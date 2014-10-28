/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer;

import flickrviewer.gui.*;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Hlavní třída programu.
 * @author Martin
 */
public class FlickrViewer {
    
    /** API klíč. */
    public static final String API_KEY = "156f279670a45dc11dfbd1d6c7486256";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean isCommandLine = false; // @todo
        
        initAppDir();
        setImageCache();
        
        if (!isCommandLine) {
            setLookAndFeel();
            
            JFrame flickrFrame = new FlickrFrame();
            flickrFrame.setVisible(true);
        }
    }
    
    public static String getDataDirectory() {
        return System.getProperty("user.home")  + "/.flickrviewer";
    }
    
    public static String getImageCacheDirectory() {
        return getDataDirectory() + "/cache";
    }
    
    private static void initAppDir() {
        String appDirPath = getDataDirectory();
        System.out.println("FlickrViewer: application data " + appDirPath);
        File appDir = new File(appDirPath);
        if (!appDir.exists() || appDir.isFile()) {
            if (appDir.isFile()) appDir.delete();
            appDir.mkdir();
        }
    }
    
    private static void setImageCache() {
        File imageCacheDir = new File(getImageCacheDirectory());
        if (!imageCacheDir.isDirectory()) imageCacheDir.mkdir();
        // ImageIO.setCacheDirectory(imageCacheDir);
        ImageIO.setUseCache(false);
        System.out.println("FlickrViewer: image cache " + ImageIO.getCacheDirectory());
    }
    
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } 
        catch(Exception e) {}
    }
    
}
