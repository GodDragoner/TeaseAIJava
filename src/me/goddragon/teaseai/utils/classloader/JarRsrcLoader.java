package me.goddragon.teaseai.utils.classloader;


import me.goddragon.teaseai.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JarRsrcLoader {

    public JarRsrcLoader() {
    }

    public static void main(String[] args) throws IllegalArgumentException, SecurityException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JarRsrcLoader.ManifestInfo mi = getManifestInfo();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));

        File lib = FileUtils.getLibFolder();

        //Create lib folder
        if(!lib.exists()) {
            lib.mkdir();
        }

        File[] libFiles = lib.listFiles();

        URL[] rsrcUrls = new URL[mi.rsrcClassPath.length + libFiles.length];

        for (int i = 0; i < mi.rsrcClassPath.length; ++i) {
            String rsrcPath = mi.rsrcClassPath[i];

            if (rsrcPath.endsWith("/")) {
                rsrcUrls[i] = new URL("rsrc:" + rsrcPath);
            } else {
                rsrcUrls[i] = new URL("jar:rsrc:" + rsrcPath + "!/");
            }
        }

        for(int x = 0; x < libFiles.length; x++) {
            File libFile = libFiles[x];
            rsrcUrls[rsrcUrls.length - x - 1] = libFile.toURI().toURL();
        }

        ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, getParentClassLoader());
        Thread.currentThread().setContextClassLoader(jceClassLoader);

        Class c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
        Method main = c.getMethod("main", args.getClass());
        main.invoke(null, (Object) args);
    }

    private static ClassLoader getParentClassLoader() {
        return ClassLoader.getPlatformClassLoader();
    }

    private static JarRsrcLoader.ManifestInfo getManifestInfo() throws IOException {
        Enumeration resEnum = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");

        while (resEnum.hasMoreElements()) {
            try {
                URL url = (URL) resEnum.nextElement();
                InputStream is = url.openStream();
                if (is != null) {
                    ManifestInfo result = new ManifestInfo();
                    Manifest manifest = new Manifest(is);
                    Attributes mainAttribs = manifest.getMainAttributes();

                    result.rsrcMainClass = mainAttribs.getValue("Rsrc-Main-Class");
                    String rsrcCP = mainAttribs.getValue("Rsrc-Class-Path");

                    if (rsrcCP == null) {
                        rsrcCP = "";
                    }

                    result.rsrcClassPath = splitSpaces(rsrcCP);
                    if (result.rsrcMainClass != null && !result.rsrcMainClass.trim().equals("")) {
                        return result;
                    }
                }
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

        System.err.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Rsrc-Class-Path)");
        return null;
    }

    private static String[] splitSpaces(String line) {
        if (line == null) {
            return null;
        } else {
            List result = new ArrayList();

            int lastPos;
            for (int firstPos = 0; firstPos < line.length(); firstPos = lastPos + 1) {
                lastPos = line.indexOf(32, firstPos);
                if (lastPos == -1) {
                    lastPos = line.length();
                }

                if (lastPos > firstPos) {
                    result.add(line.substring(firstPos, lastPos));
                }
            }

            return (String[]) result.toArray(new String[result.size()]);
        }
    }

    private static class ManifestInfo {
        String rsrcMainClass;
        String[] rsrcClassPath;

        private ManifestInfo() {
        }
    }
}
