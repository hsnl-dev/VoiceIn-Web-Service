/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.kits.voicein.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author Henry
 */
public class ImageProceesor {

    private BufferedImage oriBri;
    private BufferedImage proBri;
    private final int type;
    private float compressionQuality = 1.0f;
    private int compressionMode = ImageWriteParam.MODE_EXPLICIT;

    public ImageProceesor(BufferedImage fin) throws IOException {

        oriBri = fin;
        type = oriBri.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : oriBri.getType();
    }

    public ImageProceesor resize(int imgHeight, int imgWidth) {
        // if image proccessor did not process before use ori, or otherwise use processedimage
        BufferedImage preImage = proBri != null ? proBri : oriBri;
        BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(preImage, 0, 0, imgHeight, imgWidth, null);
        g.dispose();
        this.proBri = resizedImage;
        return this;
    }

    public void saveFileWithJPGCompress(File outFile) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();
        ImageWriteParam obj = writer.getDefaultWriteParam();
        obj.setCompressionMode(this.compressionMode);
        obj.setCompressionQuality(this.compressionQuality);
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(outFile);
            ImageOutputStream ios = ImageIO.createImageOutputStream(o);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(this.proBri, null, null), obj);
        } finally {
            if (o != null) {
                try {
                    o.close();
                } catch (IOException ex) {
                    Logger.getLogger(ImageProceesor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

}
