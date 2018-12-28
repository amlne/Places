package com.example.Places.stockage;

import java.io.*;

public class GestionnaireImages {

    public static String appPath;
    public static File directory;

    public static void init() throws Exception {
        File f = new File(".");
        appPath = f.getCanonicalPath();
        directory = new File(appPath + "/src/main/webapp/uploadspictures/");
        if (!directory.exists())
            directory.mkdirs();
    }

    public static boolean handlePicture(InputStream file, String idimages) {
        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            OutputStream out = new FileOutputStream(new File(directory.getAbsolutePath() + "/" + idimages));
            while ((read = file.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}