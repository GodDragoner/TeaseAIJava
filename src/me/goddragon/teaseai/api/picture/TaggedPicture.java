package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class TaggedPicture {

    private File file;
    private DressState dressState;
    private HashSet<PictureTag> tags = new HashSet<>();
    private TagsFile imageTagFile;

    public TaggedPicture(String fileName, String[] tags, File folder) {
        this.file = new File(folder.getPath() + File.separator + fileName);

        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tagged picture " + fileName + " in folder " + folder.getAbsolutePath() + " does not exist!");
            return;
        }
        if (PictureHandler.getHandler().checkDuplicate(file))
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "duplicate file " + file.getPath());
        	return;
        }
        String extension = "";
        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getPath().substring(i+1);
        }
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("gif"))
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "Invalid file extension " + extension + ". Expected format png jpg or gif!");
        	return;
        }

        for(int x = 0; x < tags.length; x++) {
            String tag = tags[x];

            //Too short to be a real tag
            if(tag.length() < 2) {
                continue;
            }

            DressState dressState = DressState.getByTag(tag);
            if(dressState != null) {
                this.dressState = dressState;
                continue;
            }

            PictureTag pictureTag = PictureTag.getByTag(folder, tag);
            if(pictureTag != null) {
                this.tags.add(pictureTag);
            }
        }
        getTagsFolder();
    }
    
    public TaggedPicture(File file)
    {
    	this.file = file;
        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "File does not exist!");
            return;
        }
        
        if (PictureHandler.getHandler().checkDuplicate(file))
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "duplicate file " + file.getPath());
        	this.file = null;
        	return;
        }
        String extension = "";

        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            extension = file.getPath().substring(i+1);
        }
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("gif"))
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "Invalid file extension " + extension + ". Expected format png jpg or gif!");
        	return;
        }
        getTagsFolder();
    	this.tags = imageTagFile.getTags(this.file);
    	if (tags == null)
    	{
    		tags = new HashSet<>();
    	}
    }

    public boolean move(String newPath)
    {
		File folder = this.file.getParentFile();
    	HashSet<PictureTag> localTags = getTags();
		deleteTags();
		File newFile = new File(newPath);
		TeaseLogger.getLogger().log(Level.INFO, "Moving file to " + newPath);
		if (!file.renameTo(newFile))
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Failed to move file to path " + newPath);
		    return false;
		}
		if (!newFile.exists())
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Failed to move file to path " + newPath);
		    return false;
		}
		this.file = newFile;
		getTagsFolder();
		setTags(localTags);
    	File newFolder = newFile.getParentFile();   	
    	
    	List<File> uniqueFolders = Arrays.asList(PictureHandler.getHandler().getFolders());
    	if (folder.equals(newFolder))
    	{
    		TeaseLogger.getLogger().log(Level.SEVERE, "File's destination location is the same as its source! " + newFolder.getPath());
    		return false;
    	}
    	else if (uniqueFolders.contains(folder) && !uniqueFolders.contains(newFolder))
    	{
    		PictureHandler.getHandler().removeUniquePicture(this.file);
    	}
    	else if (!uniqueFolders.contains(folder) && uniqueFolders.contains(newFolder))
    	{
    		PictureHandler.getHandler().addUniquePicture(this.file);
    	}
    	TeaseLogger.getLogger().log(Level.INFO, "File location after move " + this.file.getPath());
    	TeaseLogger.getLogger().log(Level.INFO, "File tags after move " + this.tags.toString());
    	return true;
    }
    
    public void delete()
    {
    	deleteTags();
    	File folder = this.file.getParentFile();
    	if (Arrays.asList(PictureHandler.getHandler().getFolders()).contains(folder))
    	{
    		PictureHandler.getHandler().removeUniquePicture(this.file);
    	}
    	this.file.delete();
    }
    
    public boolean hasTag(PictureTag pictureTag) {
        return tags.contains(pictureTag);
    }

    public boolean hasDressState(DressState dressState) {
        return this.dressState == dressState;
    }

    public File getFile() {
        return file;
    }

    public DressState getDressState() {
        return dressState;
    }
    

    public void setDressState(DressState dressState) {
        this.dressState = dressState;
        imageTagFile.setDressState(dressState , this.file);
        if (dressState == null && (tags == null || tags.size() == 0))
        {
            imageTagFile.deleteTags(file);
        }
    }

    @SuppressWarnings("unchecked")
    public HashSet<PictureTag> getTags() {
    	HashSet<PictureTag> temp = imageTagFile.getTags(file);
    	if (temp != null)
    	{
    		tags = temp;
    	}
    	if (tags == null)
    	{
    		return new HashSet<PictureTag>();
    	}
        return (HashSet<PictureTag>) tags.clone();
    }
    
    public void addTags(PictureTag ... tags)
    {
    	HashSet<PictureTag> addTags = new HashSet<PictureTag>(Arrays.asList(tags));
    	addTags(addTags);
    }
    
    public void addTags(HashSet<PictureTag> addTags)
    {
    	if (imageTagFile == null)
    	{
    		TeaseLogger.getLogger().log(Level.SEVERE, "No image tag file defined for this file");
    	}
    	else
    	{
    		imageTagFile.addTags(addTags, file);
    		tags = imageTagFile.getTags(file);
    	}
    }
    
    public void setTags(PictureTag ... tags)
    {
    	HashSet<PictureTag> setTags = new HashSet<PictureTag>(Arrays.asList(tags));
    	setTags(setTags);
    }
    
    public void setTags(HashSet<PictureTag> setTags)
    {
    	//issue in here somewhere
    	if (imageTagFile == null)
    	{
    		TeaseLogger.getLogger().log(Level.SEVERE, "No image tag file defined for this file");
    	}
    	else
    	{
    		if (setTags.equals(tags))
    		{
    			TeaseLogger.getLogger().log(Level.INFO, "Tags to set already eqauls current tags." + setTags.toString() + " " + tags.toString());
    			return;
    		}
    		//if tagslist is empty, delete tags
    		if ((tags == null || setTags.size() == 0) && dressState == null)
    		{
    			imageTagFile.deleteTags(file);
    		}
    		else
    		{
    			imageTagFile.setTags(setTags, file);
    		}
    		tags = imageTagFile.getTags(file);
    	}
    }
    
    public boolean hasTags(HashSet<PictureTag> theseTags)
    {
    	if (tags.containsAll(theseTags))
    	{
    		return true;
    	}
    	return false;
    }
    
    public boolean hasTags(PictureTag ... theseTags)
    {
    	getTags();
    	if (tags.containsAll(Arrays.asList(theseTags)))
    	{
    		return true;
    	}
    	return false;
    }
    
    public void deleteTags()
    {
    	imageTagFile.deleteTags(this.file);
    	if (tags == null)
    	{
    		tags = new HashSet<PictureTag>();
    	}
    	tags.clear();
    }
    
    private void getTagsFolder()
    {
        File parentFolder = file.getParentFile();
        if (!parentFolder.exists())
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "Parent file does not exist. Does this file??");
        }
        imageTagFile = TagsFile.getTagsFile(parentFolder);
        if (imageTagFile == null)
        {
        	TeaseLogger.getLogger().log(Level.SEVERE, "Tag file null " + this.file);
        }
    }
}
