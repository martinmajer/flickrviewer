/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.util;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

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
        try {
            return ImageIO.read(new URL(url));
        }
        catch (IOException e) {
            return null;
        }
    }
    
}
