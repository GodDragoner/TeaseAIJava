package me.goddragon.teaseai.api.picture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.logging.Level;

import me.goddragon.teaseai.utils.TeaseLogger;

public class TagsFile {
	private File tagsFile;
	public TagsFile(File tagsFile)
	{
		if (tagsFile.getName().equalsIgnoreCase("imagetags.txt"))
		{
			this.tagsFile = tagsFile;
		}
		else
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File " + tagsFile.getName());
			tagsFile = null;
		}
	}
	

	public boolean addTags(HashSet<PictureTag> tagsToAdd, File image)
	{
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
			return false;
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				StringBuffer inputBuffer = new StringBuffer();

				boolean added = false;
				//Read line by line
				while ((strLine = br.readLine()) != null) {
					if (strLine.contains(image.getName()))
					{
						HashSet<PictureTag> tags = new HashSet<PictureTag>();
						for (PictureTag thisTag: PictureTag.values())
						{
							if (strLine.contains(thisTag.tagName()))
							{
								tags.add(thisTag);
							}
						}
						for (PictureTag t: tagsToAdd)
						{
							tags.add(t);
						}
                        String newstrLine = image.getName();
                        for (DressState state: DressState.values())
                        {
                            if (strLine.contains(state.tagName()))
                            {
                                newstrLine += (" " + state.tagName()); 
                            }
                        }
						for(PictureTag tag: tags)
						{
							newstrLine += (" " + tag.tagName()); 
						}
						added = true;
						strLine = newstrLine;
					}
		            inputBuffer.append(strLine);
		            inputBuffer.append('\n');
				}
				//Close the input stream
				br.close();
				if (!added)
				{
				    strLine = image.getName();
					for(PictureTag tag: tagsToAdd)
					{
						strLine += (" " + tag.tagName()); 
					}
		            inputBuffer.append(strLine);
		            inputBuffer.append('\n');	
				}
				
	            FileOutputStream fileOut = new FileOutputStream(tagsFile);
	            fileOut.write(inputBuffer.toString().getBytes());
	            fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean setDressState(DressState dressState, File image)
	{
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
			return false;
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				StringBuffer inputBuffer = new StringBuffer();

				boolean added = false;
				//Read line by line
				while ((strLine = br.readLine()) != null) {
					if (strLine.contains(image.getName()))
					{
						HashSet<PictureTag> tags = new HashSet<PictureTag>();
						for (PictureTag thisTag: PictureTag.values())
						{
							if (strLine.contains(thisTag.tagName()))
							{
								tags.add(thisTag);
							}
						}
						strLine = image.getName();
						if (dressState != null)
						{
						    strLine += (" " + dressState.tagName()); 
						}
						for(PictureTag tag: tags)
						{
							strLine += (" " + tag.tagName()); 
						}
						added = true;
					}
		            inputBuffer.append(strLine);
		            inputBuffer.append('\n');
				}
				//Close the input stream
				br.close();
				if (!added)
				{
				    if (dressState == null)
				    {
				        return false;
				    }
					strLine = image.getName();
                    if (dressState != null)
                    {
                        strLine += " " + dressState.tagName(); 
                    }
		            inputBuffer.append(strLine);
		            inputBuffer.append('\n');
				}
				
	            FileOutputStream fileOut = new FileOutputStream(tagsFile);
	            fileOut.write(inputBuffer.toString().getBytes());
	            fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean setTags(HashSet<PictureTag> tagsToSet, File image)
	{
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
			return false;
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				StringBuffer inputBuffer = new StringBuffer();
				
				boolean replaced = false;
				//Read line by line
				while ((strLine = br.readLine()) != null) 
				{
					if (strLine.contains(image.getName()))
					{
						String newstrLine = image.getName();
						for (DressState state: DressState.values())
						{
							if (strLine.contains(state.tagName()))
							{
								newstrLine += (" " + state.tagName()); 
							}
						}
						for(PictureTag tag: tagsToSet)
						{
							newstrLine += (" " + tag.tagName()); 
						}
						replaced = true;
						strLine = newstrLine;
					}
		            inputBuffer.append(strLine);
		            inputBuffer.append('\n');		
				}
				//Close the input stream
				br.close();
				if (!replaced)
				{
				    strLine = image.getName();
                    for(PictureTag tag: tagsToSet)
                    {
                        strLine += (" " + tag.tagName()); 
                    }
					if (!strLine.equals(image.getName()))
					{
			            inputBuffer.append(strLine);
			            inputBuffer.append('\n');	
					}
				}
				
	            FileOutputStream fileOut = new FileOutputStream(tagsFile);
	            fileOut.write(inputBuffer.toString().getBytes());
	            fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean deleteTags(File image)
	{
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
			return false;
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				StringBuffer inputBuffer = new StringBuffer();
				
				//Read line by line
				while ((strLine = br.readLine()) != null) 
				{
					if (!strLine.contains(image.getName()))
					{
			            inputBuffer.append(strLine);
			            inputBuffer.append('\n');		
					}
				}
				//Close the input stream
				br.close();
				
	            FileOutputStream fileOut = new FileOutputStream(tagsFile);
	            fileOut.write(inputBuffer.toString().getBytes());
	            fileOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public HashSet<PictureTag> getTags(File image)
	{
		HashSet<PictureTag> toReturn = null;
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;

				//Read line by line
				while ((strLine = br.readLine()) != null) {
					if (strLine.contains(image.getName()))
					{
						toReturn = new HashSet<PictureTag>();
						for (PictureTag thisTag: PictureTag.values())
						{
							if (strLine.contains(thisTag.tagName()))
							{
								toReturn.add(thisTag);
							}
						}
					}
					
				}
				//Close the input stream
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (toReturn == null)
		{
		    toReturn = new HashSet<PictureTag>();
		}
		return toReturn;
	}
	public boolean isFileTagged(File isTagged)
	{
		if (tagsFile == null)
		{
			TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
			return false;
		}
		else
		{
            try {
				FileInputStream fstream = new FileInputStream(tagsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;

				//Read line by line
				while ((strLine = br.readLine()) != null) {
					if (strLine.contains(isTagged.getName()))
					{
						return true;
					}
					
				}
				//Close the input stream
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	public static TagsFile getTagsFile(File folder)
	{
		TagsFile toReturn = null;
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });
        if (files.length == 0)
        {
        	TeaseLogger.getLogger().log(Level.WARNING, "No tag file found in this folder!");
        	toReturn = new TagsFile(PictureHandler.getOrCreateFile(folder.getPath() + "\\ImageTags.txt"));
        }
        else if(files.length == 1 && files[0].getName().equalsIgnoreCase("imagetags.txt"))
        {
        	toReturn = new TagsFile(files[0]);
        }
        else
        {
        	TeaseLogger.getLogger().log(Level.WARNING, "Found multiple txt files in the folder!");
        	for (int j = 0; j < files.length; j++)
        	{
        		if (files[j].getName().equalsIgnoreCase("imagetags.txt"))
        		{
        			toReturn = new TagsFile(files[j]);
        		}
        	}
        	if (toReturn == null)
        	{
        		TeaseLogger.getLogger().log(Level.WARNING, "None of the txt files found in the folder had the correct name!");
        		toReturn = new TagsFile(PictureHandler.getOrCreateFile(folder.getPath() + "\\ImageTags.txt"));
        	}
        }
        return toReturn;
	}
}
