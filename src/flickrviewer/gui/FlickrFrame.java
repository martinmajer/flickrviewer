/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import sun.awt.image.SurfaceManager;

/**
 * Hlavní okno programu.
 * @author Martin
 */
public class FlickrFrame extends JFrame implements WindowListener, MouseListener {
    
    /** Aktuální panel. */
    private FlickrPanel currentPanel;
    
    /** Fullscreen? */
    private boolean isFullscreen = false;
    
    public FlickrFrame() {
        setTitle("Flickr Viewer");
        
        java.util.List<Image> icons = new ArrayList<>();
        icons.add(new ImageIcon(getClass().getClassLoader().getResource("res/icon16.png")).getImage());
        icons.add(new ImageIcon(getClass().getClassLoader().getResource("res/icon32.png")).getImage());
        setIconImages(icons);
        
        currentPanel = new UserSelectPanel();
        currentPanel.setFlickrFrame(this);
        currentPanel.initPanel();
        currentPanel.addMouseListener(this);
        
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(currentPanel);
        getContentPane().setPreferredSize(new Dimension(988, 600));
        
        pack();
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(this);
        // addKeyListener(this);
        getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                escapeAction();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && currentPanel != null) currentPanel.requestFocusInWindow();
    }
    
    public void changePanel(FlickrPanel newPanel) {
        getContentPane().remove(currentPanel);
        newPanel.initPanel();
        newPanel.setFlickrFrame(this);
        newPanel.addMouseListener(this);
        getContentPane().setPreferredSize(getContentPane().getSize());
        getContentPane().add(newPanel);
        newPanel.requestFocusInWindow();
        currentPanel.disposePanel();
        currentPanel = newPanel;
        
        pack();
        repaint();
    }
    
    /** Vrátí true, pokud je okno zobrazené na celou obrazovku. */
    public boolean isFullscreen() {
        return isFullscreen;
    }
    
    
    
    private void escapeAction() {
        if (!currentPanel.escapeAction()) {
            currentPanel.disposePanel();
            dispose();
            System.exit(0);
        }
    }
    
    

    @Override
    public void windowOpened(WindowEvent e) {
        
    }

    @Override
    public void windowClosing(WindowEvent e) {
        currentPanel.disposePanel();
        dispose();
        System.exit(0);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (!isFullscreen) {
                dispose();
                setUndecorated(true);
                device.setFullScreenWindow(this);
                isFullscreen = true;
            }
            else {
                device.setFullScreenWindow(null);
                dispose();
                setUndecorated(false);
                setVisible(true);
                isFullscreen = false;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    
}
