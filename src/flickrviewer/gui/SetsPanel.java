/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;
import flickrviewer.async.AsyncJob;
import flickrviewer.async.AsyncLoader;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.awt.event.*;
import java.net.MalformedURLException;

import static flickrviewer.gui.ComponentDecorator.*;
import flickrviewer.util.PhotoDownloader;
import java.util.ArrayList;

/**
 * Panel s přehledem alb.
 * @author Martin
 */
public class SetsPanel extends FlickrPanel {
    
    
    private static final int SET_BUTTON_WIDTH = 150;
    private static final int SET_BUTTON_HEIGHT = 150;
    
    
    private String username;
    private String userId;
    
    private List<PhotoSet> sets;
    private List<JButton> buttons;
    
    public SetsPanel(String username) {
        this.username = username;
        
        decoratePanel(this);
        showPreloader();
        
        AsyncLoader.getInstance().load(new LoadUserId());
    }
    
    
    private class LoadUserId implements AsyncJob {
        @Override
        public Object loadData() throws FlickrException {
            return FlickrAPI.getInstance().getUserId(username);
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if (data != null) {
                userId = (String)data;
                AsyncLoader.getInstance().load(new LoadSets());
            }
            else {
                FlickrPanel newPanel = new UserSelectPanel();
                flickrFrame.changePanel(newPanel);
            }
        }
    }
    
    private class LoadSets implements AsyncJob {
        @Override
        public Object loadData() throws FlickrException {
            return FlickrAPI.getInstance().getSets(userId);
        }

        @Override
        public void done(Object data, FlickrException ex) {
            hidePreloader();
            sets = (List<PhotoSet>)data;
            showSets();
        }
    }
    
    private class LoadSetCover implements AsyncJob {

        private PhotoSet set;
        private JButton setButton;
        
        public LoadSetCover(PhotoSet set, JButton setButton) {
            this.set = set;
            this.setButton = setButton;
        }
        
        @Override
        public Object loadData() throws FlickrException {
            Image image = PhotoDownloader.download(set.coverUrl);
            return image;
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if (data != null) {
                setButton.setText(null);
                setButton.setIcon(new ImageIcon((Image)data));
            }
        }
        
    }
    
    
    private void showSets() {
        final JPanel innerPanel = decoratePanel(new JPanel());
        // innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        innerPanel.setLayout(new SetsLayout());
        
        buttons = new ArrayList();
        
        for (PhotoSet set: sets) {
            JButton button = createSetButton(set);
            innerPanel.add(button);
            buttons.add(button);
            
            // AsyncLoader.getInstance().load(new LoadSetCover(set, button));
        }
        
        innerPanel.addMouseListener(flickrFrame);
        
        setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBorder(null);
        decorateScrollBar(scrollPane.getVerticalScrollBar());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.addComponentListener(new ComponentListener() {
            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentResized(ComponentEvent e) {
                Dimension jspSize = ((JScrollPane)e.getComponent()).getViewport().getSize();
                innerPanel.setBounds(0, 0, jspSize.width, jspSize.height);
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });
        
        add(scrollPane, BorderLayout.CENTER);
        // add(innerPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    private void buttonsMouseExited() {
        for (JButton b: buttons) {
            for (MouseListener m: b.getMouseListeners()) {
                m.mouseExited(new MouseEvent(b, 0, 0, 0, 0, 0, 1, false));
            }
        }
    }
    
    private JButton createSetButton(final PhotoSet set) {
        JButton setButton = decorateSetButton(new JButton(set.title));
        setButton.setVerticalTextPosition(SwingConstants.CENTER);
        setButton.setHorizontalTextPosition(SwingConstants.CENTER);
        setButton.setPreferredSize(new Dimension(SET_BUTTON_WIDTH, SET_BUTTON_HEIGHT));
        
        final FlickrPanel that = this;
        
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonsMouseExited();
                flickrFrame.changePanel(new SlideshowPanel(set, that));
            }
        });
        
        return setButton;
    }
    
    
    
}
