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
        JPanel innerPanel = decoratePanel(new JPanel());
        innerPanel.setLayout(new FlowLayout());
        
        for (PhotoSet set: sets) {
            innerPanel.add(decorateLabel(new JLabel(set.title)));
        }
        
        setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane(innerPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    
    
}
