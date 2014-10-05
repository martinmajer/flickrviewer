/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Martin
 */
public class FlickrFrame extends JFrame implements WindowListener {
    
    /** Aktuální panel. */
    private FlickrPanel currentPanel;
    
    public FlickrFrame() {
        setTitle("Flickr Viewer");
        
        currentPanel = new UserSelectPanel();
        currentPanel.setFlickrFrame(this);
        currentPanel.initPanel();
        
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(currentPanel);
        getContentPane().setPreferredSize(new Dimension(800, 600));
        
        pack();
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(this);
    }
    
    
    public void changePanel(FlickrPanel newPanel) {
        getContentPane().remove(currentPanel);
        newPanel.initPanel();
        getContentPane().add(newPanel);
        currentPanel.disposePanel();
        currentPanel = newPanel;
        
        repaint();
    }
    
    

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        currentPanel.disposePanel();
        dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        
    }

    @Override
    public void windowIconified(WindowEvent e) {
        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        
    }

    @Override
    public void windowActivated(WindowEvent e) {
        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        
    }
    
    
}
