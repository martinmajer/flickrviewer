/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.FlickrAPI;
import flickrviewer.api.FlickrException;
import flickrviewer.api.Photo;
import flickrviewer.api.PhotoSet;
import flickrviewer.async.AsyncJob;
import flickrviewer.async.AsyncLoader;
import static flickrviewer.gui.ComponentDecorator.*;
import java.util.List;

/**
 * Panel pro prohlížení fotek.
 * @author Martin
 */
public class SlideshowPanel extends FlickrPanel {
    
    protected PhotoSet set;
    protected List<Photo> photos;
    
    private FlickrPanel previousPanel;
    
    public SlideshowPanel(PhotoSet set, FlickrPanel previousPanel) {
        this.set = set;
        this.previousPanel = previousPanel;
        
        decoratePanel(this);
        showPreloader();
        
        AsyncLoader.getInstance().load(new LoadPhotos());
    }
    
    private class LoadPhotos implements AsyncJob {

        @Override
        public Object loadData() throws FlickrException {
            return FlickrAPI.getInstance().getPhotos(set);
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if (data != null) {
                photos = (List<Photo>)data;
                loadSlideshow();
            }
            else {
                flickrFrame.changePanel(previousPanel);
            }
        }
        
    }
    
    public void loadSlideshow() {
        hidePreloader();
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
