/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * Panel programu zobrazený v hlavním okně.
 * @author Martin
 */
public abstract class FlickrPanel extends JPanel {
    
    /** Hlavní okno aplikace. */
    protected FlickrFrame flickrFrame;
    
    /** Animace načítání. */
    private ImageIcon preloaderImage = null;
    
    
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
    
    protected void showPreloader() {
        removeAll();
        setLayout(new GridBagLayout());
        add(createPreloader());
        repaint();
    } 
    
    protected void hidePreloader() {
        removeAll();
        setLayout(null);
        repaint();
    }
    
    /** Vytvoří animovaný preloader. */
    protected JLabel createPreloader() {
        if (preloaderImage == null) {
            URL url = getClass().getClassLoader().getResource("res/preloader.gif");
            preloaderImage = new ImageIcon(url);
        }
        JLabel label = new JLabel(preloaderImage);
        label = ComponentDecorator.decorateLabel(label);
        return label;
    }
    
    
}
