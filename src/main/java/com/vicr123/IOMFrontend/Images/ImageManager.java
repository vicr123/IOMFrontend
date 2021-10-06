package com.vicr123.IOMFrontend.Images;

import com.vicr123.IOMFrontend.IOMFrontendPlugin;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageManager {
    File imageDirectory;
    IOMFrontendPlugin plugin;

    public ImageManager(IOMFrontendPlugin plugin) {
        this.plugin = plugin;

        imageDirectory = new File(plugin.getDataFolder().getAbsolutePath() + "/images");
        imageDirectory.mkdirs();
    }

    public InputStream getImage(String hash) throws FileNotFoundException {
        File file = new File(imageDirectory, hash + ".png");
        return new FileInputStream(file);
    }

    public String putImage(byte[] imageData) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(imageData);
        byte[] hexDigest = digest.digest();

        StringBuilder hexBuilder = new StringBuilder(hexDigest.length * 2);
        for (byte b : hexDigest) hexBuilder.append(String.format("%02x", b));

        String hash = hexBuilder.toString();

        File file = new File(imageDirectory, hash + ".png");
        FileOutputStream output = new FileOutputStream(file);
        output.write(imageData);
        output.close();

        return hash;
    }
}
