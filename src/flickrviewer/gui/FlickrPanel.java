/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Martin
 */
public abstract class FlickrPanel extends JPanel {
    
    
    protected FlickrFrame flickrFrame;
    
    
    public FlickrPanel() {
        
    }
    
    /** 
     * Nastaví hlavní okno.
     * @param flickrFrame 
     */
    public void setFlickrFrame(FlickrFrame flickrFrame) {
        this.flickrFrame = flickrFrame;
    }
    
    /**
     * Metoda volaná při inicializaci panelu.
     */
    public void initPanel() {
        
    }
    
    /**
     * Metoda volaná po schování panelu, určená pro uvolnění zdrojů.
     */
    public void disposePanel() {
        
    }
    
}
