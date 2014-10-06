/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;

/**
 *
 * @author Martin
 */
public class SetsLayout implements LayoutManager {

    
    private static final int GAP = 10;
    
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
        
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int height = 0;
            
            int left = GAP, top = GAP;
            int rowHeight = 0;
            for (Component c: parent.getComponents()) {
                if (!c.isVisible()) continue;
                
                Dimension d = c.getPreferredSize();
                
                if (left + d.width + GAP >= parent.getWidth()) {
                    left = GAP;
                    top += rowHeight + GAP;
                    rowHeight = 0;
                }
                
                left += d.width + GAP;
                
                if (d.height > rowHeight) rowHeight = d.height;
            }
            
            height = top + rowHeight + GAP;
            return new Dimension(parent.getWidth(), height);
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int left = GAP, top = GAP;
            int rowHeight = 0;
            for (Component c: parent.getComponents()) {
                if (!c.isVisible()) continue;
                
                Dimension d = c.getPreferredSize();
                c.setSize(d.width, d.height);
                
                if (left + d.width + GAP >= parent.getWidth()) {
                    left = GAP;
                    top += rowHeight + GAP;
                    rowHeight = 0;
                }
                
                c.setLocation(left, top);
                left += d.width + GAP;
                
                if (d.height > rowHeight) rowHeight = d.height;
            }
        }
    }
    
}
