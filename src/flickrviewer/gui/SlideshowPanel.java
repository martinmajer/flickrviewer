/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;
import flickrviewer.async.*;
import flickrviewer.util.PhotoDownloader;

import static flickrviewer.gui.ComponentDecorator.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import org.imgscalr.Scalr;

/**
 * Panel pro prohlížení fotek.
 * @author Martin
 */
public class SlideshowPanel extends FlickrPanel implements KeyListener {
    
    
    private static final int PRELOAD_COUNT = 2;
    
    
    protected PhotoSet set;
    protected List<Photo> photos;
    
    private FlickrPanel previousPanel;
    
    protected int currentIndex;
    
    protected ImagePanel currentPanel;
    private Map<Photo, SoftReference<BufferedImage>> scaledImages = new ConcurrentHashMap();
    
    
    private ExecutorService scalrThreadExecutor;
    
    
    public SlideshowPanel(PhotoSet set, FlickrPanel previousPanel) {
        this.set = set;
        this.previousPanel = previousPanel;
        
        decoratePanel(this);
        showPreloader();
        
        setFocusable(true);
        addKeyListener(this);
        
        scalrThreadExecutor = Executors.newSingleThreadExecutor();
        
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
        
        if (photo.image == null || photo.image.get() == null) {
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
            showImagePanel(photo);
            
            System.out.println("Photo " + currentIndex);
            
            // načtení následujících fotek
            if (index > currentIndex) {
                int nextIndex = index + 1;
                while (nextIndex < photos.size() && nextIndex - index <= PRELOAD_COUNT) {
                    Photo next = photos.get(nextIndex);
                    if ((next.image == null || next.image.get() == null) && next.loadingJob == null) {
                        AsyncLoader.getInstance().load(new LoadPhoto(next, nextIndex, false));
                    }
                    nextIndex++;
                }
            }
            // načtení předchozích fotek
            else if (index < currentIndex) {
                int prevIndex = index - 1;
                while (prevIndex >= 0 && index - prevIndex <= PRELOAD_COUNT) {
                    Photo prev = photos.get(prevIndex);
                    if ((prev.image == null || prev.image.get() == null) && prev.loadingJob == null) {
                        AsyncLoader.getInstance().load(new LoadPhoto(prev, prevIndex, false));
                    }
                    prevIndex--;
                }
            }
            
            currentIndex = index;
            
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
            photo.image = new SoftReference((BufferedImage)PhotoDownloader.download(photo.originalUrl));
            return true;
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if ((Boolean)data == true) {
                if (showAfterLoaded) showPhoto(index);
            }
            photo.loadingJob = null;
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
    
    
    
    private void showImagePanel(Photo photo) {
        removeAll();
        if (currentPanel != null) {
            currentPanel.image = null;
        }
        
        setLayout(new BorderLayout());
        currentPanel = new ImagePanel(photo);
        add(currentPanel);
        revalidate();
        repaint();
    }
    
    
    protected class ImagePanel extends JPanel {
        
        public Photo photo;
        public Image image;
        
        public ImagePanel(Photo photo) {
            this.photo = photo;
            this.image = photo.image.get();
        }
        
        @Override
        public void paint(Graphics g) {
            int thisWidth = getWidth();
            int thisHeight = getHeight();
            
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, thisWidth, thisHeight);
            
            if (image == null) return;
            
            Graphics2D g2 = (Graphics2D)g;
            
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);
            
            final int renderedWidth;
            final int renderedHeight;
            
            float imageRatio = (float)imageWidth / (float)imageHeight;
            float thisRatio = (float)thisWidth / (float)thisHeight;
            
            if (imageRatio > thisRatio) {
                renderedWidth = thisWidth;
                renderedHeight = (int)(thisWidth / imageRatio);
            }
            else if (imageRatio < thisRatio) {
                renderedHeight = thisHeight;
                renderedWidth = (int)(thisHeight * imageRatio);
            }
            else {
                renderedWidth = thisWidth;
                renderedHeight = thisHeight;
            }
            
            int left = (thisWidth - renderedWidth) / 2;
            int top = (thisHeight - renderedHeight) / 2;
            
            if (scaledImages.get(photo) == null || scaledImages.get(photo).get() == null || scaledImages.get(photo).get().getWidth(null) != renderedWidth || scaledImages.get(photo).get().getHeight(null) != renderedHeight) {
                scaledImages.remove(photo);
                scalrThreadExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        BufferedImage scaled = Scalr.resize((BufferedImage)image, Scalr.Method.QUALITY, Scalr.Mode.FIT_EXACT, renderedWidth, renderedHeight);
                        scaledImages.put(photo, new SoftReference(scaled));
                        repaint();
                    }
                });
            }
            
            if (scaledImages.get(photo) != null && scaledImages.get(photo).get() != null) g.drawImage(scaledImages.get(photo).get(), left, top, null);
            else g.drawImage(this.image, left, top, renderedWidth, renderedHeight, null);
        }
        
    }
    
    
}
