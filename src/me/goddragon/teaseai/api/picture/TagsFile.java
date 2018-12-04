package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public class TagsFile {

    //Maps all known files to the folders
    private static HashMap<File, TagsFile> tagFiles = new HashMap<>();

    private File tagsFile;
    private final ArrayList<String> lines = new ArrayList<>();
    private final List<File> taggedFiles = new ArrayList<>();

    public TagsFile(File tagsFile) {
        if (tagsFile.getName().equalsIgnoreCase("imagetags.txt")) {
            this.tagsFile = tagsFile;
        } else {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File " + tagsFile.getName());
        }

        loadCache();
    }

    public void loadCache() {
        lines.clear();
        taggedFiles.clear();

        try {
            FileInputStream fstream = new FileInputStream(tagsFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            File[] files = tagsFile.getParentFile().listFiles();

            while ((strLine = br.readLine()) != null) {
                lines.add(strLine);

                for (File file : files) {
                    if (strLine.contains(file.getName())) {
                        taggedFiles.add(file);
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

    public boolean addTags(HashSet<PictureTag> tagsToAdd, File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
            return false;
        } else {
            try {
                StringBuffer inputBuffer = new StringBuffer();

                boolean added = false;

                //Read line by line
                for (String strLine : (List<String>) lines.clone()) {
                    if (strLine.contains(image.getName())) {
                        HashSet<PictureTag> tags = new HashSet<>();

                        for (PictureTag thisTag : PictureTag.values()) {
                            if (strLine.contains(thisTag.getTagName())) {
                                tags.add(thisTag);
                            }
                        }

                        for (PictureTag t : tagsToAdd) {
                            tags.add(t);
                        }

                        String newstrLine = image.getName();

                        for (DressState state : DressState.values()) {
                            if (strLine.contains(state.getTagName())) {
                                newstrLine += " " + state.getTagName();
                            }
                        }

                        for (PictureTag tag : tags) {
                            newstrLine += " " + tag.getTagName();
                        }

                        //File was already tagged before and we just added the new tags to it so we need to set this to true
                        added = true;

                        lines.set(lines.indexOf(strLine), newstrLine);
                        strLine = newstrLine;
                    }

                    inputBuffer.append(strLine);
                    inputBuffer.append('\n');
                }


                //Check whether we already set all tags or whether the image is a new one and we need to append it as a new line
                if (!added) {
                    String strLine = image.getName();
                    for (PictureTag tag : tagsToAdd) {
                        strLine += (" " + tag.getTagName());
                    }
                    
                    if (!tagsToAdd.isEmpty()) {
                        inputBuffer.append(strLine);
                        inputBuffer.append('\n');
                        lines.add(strLine);
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

    public boolean setDressState(DressState dressState, File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
            return false;
        } else {
            try {
                StringBuffer inputBuffer = new StringBuffer();

                boolean added = false;

                String replaceLine = null;
                String newLine = null;

                for (String strLine : lines) {
                    if (strLine.contains(image.getName())) {
                        HashSet<PictureTag> tags = new HashSet<>();

                        for (PictureTag thisTag : PictureTag.values()) {
                            if (strLine.contains(thisTag.getTagName())) {
                                tags.add(thisTag);
                            }
                        }

                        newLine = image.getName();
                        if (dressState != null) {
                            newLine += (" " + dressState.getTagName());
                        }

                        for (PictureTag tag : tags) {
                            newLine += (" " + tag.getTagName());
                        }

                        replaceLine = strLine;
                        strLine = newLine;

                        added = true;
                    }

                    inputBuffer.append(strLine);
                    inputBuffer.append('\n');
                }
    
                if (added) {
                    lines.set(lines.indexOf(replaceLine), newLine);
                } else {
                    if (dressState == null) {
                        return false;
                    }

                    String strLine = image.getName();
                    strLine += " " + dressState.getTagName();
                    inputBuffer.append(strLine);
                    inputBuffer.append('\n');
                    lines.add(strLine);
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

    public boolean setTags(HashSet<PictureTag> tagsToSet, File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
            return false;
        } else {
            try {
                StringBuffer inputBuffer = new StringBuffer();

                boolean replaced = false;

                String replaceLine = null;
                String newLine = null;

                //Read line by line
                for (String strLine : lines) {
                    if (strLine.contains(image.getName())) {
                        newLine = image.getName();

                        for (DressState state : DressState.values()) {
                            if (strLine.contains(state.getTagName())) {
                                newLine += (" " + state.getTagName());
                            }
                        }

                        for (PictureTag tag : tagsToSet) {
                            newLine += (" " + tag.getTagName());
                        }

                        replaced = true;
                        replaceLine = strLine;
                        strLine = newLine;
                    }

                    inputBuffer.append(strLine);
                    inputBuffer.append('\n');
                }

                if (replaced) {
                    lines.set(lines.indexOf(replaceLine), newLine);
                }

                if (!replaced) {
                    String strLine = image.getName();

                    for (PictureTag tag : tagsToSet) {
                        strLine += (" " + tag.getTagName());
                    }

                    if (!strLine.equals(image.getName())) {
                        inputBuffer.append(strLine);
                        inputBuffer.append('\n');
                        lines.add(strLine);
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

    public boolean deleteTags(File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
            return false;
        } else {
            try {
                StringBuffer inputBuffer = new StringBuffer();

                String lineRemove = null;

                for (String strLine : lines) {
                    if (!strLine.contains(image.getName())) {
                        inputBuffer.append(strLine);
                        inputBuffer.append('\n');
                    } else {
                        lineRemove = strLine;
                    }
                }

                lines.remove(lineRemove);

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

    public HashSet<PictureTag> getTags(File image) {
        HashSet<PictureTag> toReturn = new HashSet<>();

        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
        } else {
            String line = getImageLine(image);

            if (line == null) {
                return toReturn;
            }

            for (PictureTag thisTag : PictureTag.values()) {
                if (line.contains(thisTag.getTagName())) {
                    toReturn.add(thisTag);
                }
            }
        }

        return toReturn;
    }

    public DressState getDressState(File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
        } else {
            String line = getImageLine(image);

            if (line == null) {
                return null;
            }

            for (DressState dressState : DressState.values()) {
                if (line.contains(dressState.getTagName())) {
                    return dressState;
                }
            }
        }

        return null;
    }

    public String getImageLine(File image) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
        } else {
            for (String strLine : lines) {
                if (strLine.contains(image.getName())) {
                    return strLine;
                }
            }
        }

        return null;
    }

    public boolean isFileTagged(File isTagged) {
        if (tagsFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid Tags File");
            return false;
        } else {
            for (String strLine : lines) {
                if (strLine.contains(isTagged.getName())) {
                    return true;
                }

            }
        }

        return false;
    }

    public List<File> getTaggedFiles() {
        return taggedFiles;
    }

    public File getTagsFile() {
        return tagsFile;
    }

    public static TagsFile getTagsFile(File folder) {
        if (tagFiles.containsKey(folder)) {
            return tagFiles.get(folder);
        }
        TagsFile toReturn = null;

        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        });

        if (files.length == 0) {
            TeaseLogger.getLogger().log(Level.WARNING, "No tag file found in folder " + folder.getAbsolutePath());
            toReturn = new TagsFile(PictureHandler.getOrCreateFile(folder.getPath() + File.separator + "ImageTags.txt"));
        } else if (files.length == 1 && files[0].getName().equalsIgnoreCase("imagetags.txt")) {
            toReturn = new TagsFile(files[0]);
        } else {
            TeaseLogger.getLogger().log(Level.WARNING, "Found multiple txt files in folder " + folder.getAbsolutePath());

            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equalsIgnoreCase("imagetags.txt")) {
                    toReturn = new TagsFile(files[j]);
                }
            }

            if (toReturn == null) {
                TeaseLogger.getLogger().log(Level.WARNING, "Unable to identify tag file out of " + files.length + " txt files");
                toReturn = new TagsFile(PictureHandler.getOrCreateFile(folder.getPath() + File.separator + "ImageTags.txt"));
            }
        }

        if (toReturn != null) {
            tagFiles.put(folder, toReturn);
        }

        return toReturn;
    }
}
