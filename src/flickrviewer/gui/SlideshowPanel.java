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
import flickrviewer.util.PhotoDownloader;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Panel pro prohlížení fotek.
 * @author Martin
 */
public class SlideshowPanel extends FlickrPanel implements KeyListener {
    
    
    private static final int PRELOAD_COUNT = 5;
    
    
    protected PhotoSet set;
    protected List<Photo> photos;
    
    private FlickrPanel previousPanel;
    
    protected int currentIndex;
    
    public SlideshowPanel(PhotoSet set, FlickrPanel previousPanel) {
        this.set = set;
        this.previousPanel = previousPanel;
        
        decoratePanel(this);
        showPreloader();
        
        setFocusable(true);
        addKeyListener(this);
        
        AsyncLoader.getInstance().load(new LoadPhotos());
    }

    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            showNext();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            showPrevious();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }
    
    
    private class LoadPhotos implements AsyncJob, AsyncJob.Priority {

        @Override
        public Object loadData() throws FlickrException {
            return FlickrAPI.getInstance().getPhotos(set);
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if (data != null) {
                photos = (List<Photo>)data;
                startSlideshow();
            }
            else {
                flickrFrame.changePanel(previousPanel);
            }
        }

        @Override
        public int getPriority() {
            return 10;
        }
        
    }
    
    
    public void startSlideshow() {
        showPhoto(0);
    }
    
    public void showNext() {
        if (photos != null && currentIndex < photos.size() - 1) {
            showPhoto(currentIndex + 1);
        }
    }
    
    public void showPrevious() {
        if (photos != null && currentIndex > 0) {
            showPhoto(currentIndex - 1);
        }
    }
    
    public void showPhoto(int index) {
        if (photos.isEmpty()) return;
        
        Photo photo = photos.get(index);
        
        if (photo.image == null) {
            showPreloader();
            if (photo.loadingJob == null) {
                AsyncJob job = new LoadPhoto(photo, index, true);
                AsyncLoader.getInstance().load(job);
            }
            else {
                LoadPhoto job = (LoadPhoto)photo.loadingJob;
                job.showAfterLoaded = true;
            }
        }
        else {
            hidePreloader();
            JLabel photoLabel = new JLabel(new ImageIcon(photo.image));
            setLayout(new GridBagLayout());
            add(photoLabel);
            
            revalidate();
            repaint();
            
            currentIndex = index;
            
            System.out.println("Photo " + currentIndex);
            
            // načtení následujících fotek
            int nextIndex = currentIndex + 1;
            while (nextIndex < photos.size() && nextIndex - currentIndex <= PRELOAD_COUNT) {
                Photo next = photos.get(nextIndex);
                if (next.image == null && next.loadingJob == null) {
                    AsyncLoader.getInstance().load(new LoadPhoto(next, nextIndex, false));
                }
                nextIndex++;
            }
            
        }
    }
    
    private class LoadPhoto implements AsyncJob, AsyncJob.Priority {

        // @todo zařídit, aby se nikdy nenačítalo víckrát najednou
        
        private Photo photo;
        private int index;
        private boolean showAfterLoaded;
        
        public LoadPhoto(Photo photo, int index, boolean showAfterLoaded) {
            this.photo = photo;
            this.index = index;
            this.showAfterLoaded = showAfterLoaded;
            photo.loadingJob = this;
        }
        
        @Override
        public Object loadData() throws FlickrException {
            photo.image = PhotoDownloader.download(photo.largeUrl);
            return true;
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if ((Boolean)data == true) {
                if (showAfterLoaded) showPhoto(index);
            }
        }

        @Override
        public int getPriority() {
            return 10;
        }
        
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
