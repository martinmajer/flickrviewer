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
import java.awt.GridBagLayout;
import javax.swing.*;

import static flickrviewer.gui.ComponentDecorator.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Panel s přehledem alb.
 * @author Martin
 */
public class SetsPanel extends FlickrPanel {
    
    private List<PhotoSet> sets;
    
    public SetsPanel() {
        decoratePanel(this);
        showPreloader();
        
        AsyncLoader.getInstance().load(new AsyncJob() {
            @Override
            public Object loadData() throws FlickrException {
                return FlickrAPI.getInstance().getSets(flickrviewer.FlickrViewer.MY_USER_ID);
            }

            @Override
            public void done(Object data, FlickrException ex) {
                hidePreloader();
                sets = (List<PhotoSet>)data;
                showSets();
            }
        });
    }
    
    
    private void showSets() {
        final JPanel innerPanel = decoratePanel(new JPanel());
        // innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        innerPanel.setLayout(new SetsLayout());
        
        for (PhotoSet set: sets) {
            innerPanel.add(createSetComponent(set));
        }
        
        setLayout(new BorderLayout(0, 0));
        final JScrollPane scrollPane = new JScrollPane(innerPanel);
        scrollPane.setBorder(null);
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
    
    private JComponent createSetComponent(PhotoSet set) {
        JButton setButton = decorateButton(new JButton(set.title));
        setButton.setPreferredSize(new Dimension(128, 128));
        
        return setButton;
    }
    
    
    
}
