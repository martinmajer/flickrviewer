/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.util;

import flickrviewer.FlickrViewer;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 *
 * @author Martin
 */
public class PhotoDownloader {
 
    /**
     * Stáhne obrázek ze zadané adresy.
     * @param url
     * @return 
     */
    public static Image download(String url) {
        System.out.println("PhotoDownloader: " + url);
        
        try {
            String ext = (url.lastIndexOf('.') > 0) ? url.substring(url.lastIndexOf('.')) : "";
            File cached = new File(FlickrViewer.getImageCacheDirectory() + "/" + md5(url) + ext);
            if (cached.exists()) {
                return ImageIO.read(cached);
            }
            else {
               BufferedImage img = ImageIO.read(new URL(url)); 
               ImageIO.write(img, "jpeg", cached);
               return img;
            }
        }
        catch (IOException e) {
            return null;
        }
    }
    
    private static String md5(String str) {
        try {
            return new HexBinaryAdapter().marshal(MessageDigest.getInstance("MD5").digest(str.getBytes()));
        } 
        catch (NoSuchAlgorithmException ex) { return null; }
    }
    
}
