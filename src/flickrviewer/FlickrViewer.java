/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer;

import flickrviewer.gui.*;
import java.awt.Color;
import javax.swing.*;

/**
 *
 * @author Martin
 */
public class FlickrViewer {

    /** Defaultní ID uživatele. */
    public static final String MY_USER_ID = "24890203@N06";
    
    /** API klíč. */
    public static final String API_KEY = "156f279670a45dc11dfbd1d6c7486256";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean isCommandLine = false; // @todo
        
        if (!isCommandLine) {
            //setNativeLookAndFeel();
            
            JFrame flickrFrame = new FlickrFrame();
            flickrFrame.setVisible(true);
        }
    }
    
    
    private static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch(Exception e) {}
    }
    
}
