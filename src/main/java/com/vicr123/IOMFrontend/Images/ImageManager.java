package com.vicr123.IOMFrontend.Images;

import com.vicr123.IOMFrontend.IOMFrontendPlugin;
import org.bukkit.Bukkit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    public static class ImageData {
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getBlockWidth() {
            return (int) Math.ceil(getImageWidth() / 128.0);
        }

        public int getBlockHeight() {
            return (int) Math.ceil(getImageHeight() / 128.0);
        }

        private String imageUrl;
        private int imageHeight;
        private int imageWidth;
    }

    public ImageData getImageData(String hash) throws IOException {
        var image = ImageIO.read(getImage(hash));

        var d = new ImageData();
        d.setImageUrl("http://localhost:" + plugin.getConfig().getInt("port")  + "/images/" + hash);
        d.setImageWidth(image.getWidth());
        d.setImageHeight(image.getHeight());
        return d;
    }

    public String putImage(byte[] imageData) throws IOException, NoSuchAlgorithmException {
        if (imageData.length > 4 && imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50 && imageData[2] == (byte) 0x4E && imageData[3] == (byte) 0x47) {
            //This is already a PNG file
            return putFile(imageData);
        } else if (isSvg(imageData)) {
            ProcessBuilder builder = new ProcessBuilder("inkscape", "--export-type=png", "--pipe", "--export-filename=-");
            builder.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process proc = builder.start();
            proc.getOutputStream().write(imageData);
            proc.getOutputStream().flush();
            proc.getOutputStream().close();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while (proc.isAlive()) {
                os.write(proc.getInputStream().readAllBytes());
            }
            os.write(proc.getInputStream().readAllBytes());

            if (proc.exitValue() != 0) throw new IllegalArgumentException("SVG could not be converted");
            return putFile(os.toByteArray());

        } else {
            throw new IllegalArgumentException("Unknown file type");
        }
    }

    private String putFile(byte[] fileData) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(fileData);
        byte[] hexDigest = digest.digest();

        StringBuilder hexBuilder = new StringBuilder(hexDigest.length * 2);
        for (byte b : hexDigest) hexBuilder.append(String.format("%02x", b));

        String hash = hexBuilder.toString();

        File file = new File(imageDirectory, hash + ".png");
        FileOutputStream output = new FileOutputStream(file);
        output.write(fileData);
        output.close();

        return hash;
    }

    private boolean isSvg(byte[] imageData) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(imageData));

            return doc.getElementsByTagName("svg").getLength() != 0;

//            return doc.getDocumentElement().getTagName().equals("svg");
        } catch (SAXException | IOException | ParserConfigurationException e) {
            return false;
        }
    }
}
