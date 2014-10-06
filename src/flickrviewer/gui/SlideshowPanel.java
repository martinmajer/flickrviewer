/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.PhotoSet;
import static flickrviewer.gui.ComponentDecorator.*;

/**
 * Panel pro prohlížení fotek.
 * @author Martin
 */
public class SlideshowPanel extends FlickrPanel {
    
    protected PhotoSet set;
    
    private FlickrPanel previousPanel;
    
    public SlideshowPanel(PhotoSet set, FlickrPanel previousPanel) {
        this.set = set;
        this.previousPanel = previousPanel;
        
        decoratePanel(this);
    }
    
    
    @Override
    public boolean escapeAction() {
        if (previousPanel != null) {
            flickrFrame.changePanel(previousPanel);
            return true;
        }
        else return false;
    }
    
}
