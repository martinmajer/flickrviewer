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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
public class SlideshowPanel extends FlickrPanel implements KeyListener, MouseListener, MouseMotionListener {
    
    /** Určuje, kolik fotek se bude dopředu načítat. */
    private static final int PRELOAD_COUNT = 3;
    
    /** Aktuální album. */
    protected PhotoSet set;
    
    /** Fotky v albu. */
    protected List<Photo> photos;
    
    /** Reference na předchozí panel. */
    private FlickrPanel previousPanel;
    
    /** Index aktuální fotky. */
    protected int currentIndex;
    
    /** Aktuální panel se zobrazenou fotkou. */
    protected ImagePanel currentImagePanel;
    
    /** Mapa stažených fotek. */
    private Map<Photo, SoftReference<BufferedImage>> downloadedImages = new ConcurrentHashMap<>();
    
    /** Mapa úloh pro načítání fotek. */
    private Map<Photo, LoadPhoto> loadingJobs = new ConcurrentHashMap<>();
    
    /** Mapa zmenšených fotek (na velikost obrazovky). */
    private Map<Photo, SoftReference<BufferedImage>> scaledImages = new ConcurrentHashMap();
    
    
    /** Spouštěč vlákna pro škálování obrázků. */
    private ExecutorService scalrThreadExecutor;
    
    
    public SlideshowPanel(PhotoSet set, FlickrPanel previousPanel) {
        this.set = set;
        this.previousPanel = previousPanel;
        
        decoratePanel(this);
        showPreloader();
        
        setFocusable(true);
        
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        scalrThreadExecutor = Executors.newSingleThreadExecutor();
        
        // stažení seznamu fotek z alba
        AsyncLoader.getInstance().load(new LoadPhotos());
    }

    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int width = getWidth();
        int height = getHeight();
        
        if (y >= height / 3 && y < height * 2 / 3) {
            e.consume();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int width = getWidth();
        int height = getHeight();
        
        if (y >= height / 3 && y < height * 2 / 3) {
            if (x >= width / 2) showNext();
            else showPrevious();
            e.consume();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        changeMouseCursor(e.getX(), e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        changeMouseCursor(e.getX(), e.getY());
    }
    
    
    private void changeMouseCursor(int x, int y) {
        int height = getHeight();
        
        if (y >= height / 3 && y < height * 2 / 3) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    
    
    /** Stisk Escape. */
    @Override
    public boolean escapeAction() {
        if (previousPanel != null) {
            flickrFrame.changePanel(previousPanel);
            return true;
        }
        else return false;
    }
    
    
    /** Spustí slideshow - zobrazí první fotku. */
    public void startSlideshow() {
        showPhoto(0);
    }
    
    /** Zobrazí další fotku. */
    public void showNext() {
        if (photos != null && currentIndex < photos.size() - 1) {
            showPhoto(currentIndex + 1);
        }
    }
    
    /** Zobrazí předchozí fotku. */
    public void showPrevious() {
        if (photos != null && currentIndex > 0) {
            showPhoto(currentIndex - 1);
        }
    }
    
    /** 
     * Zobrazí fotku. 
     * @param index index fotky
     */
    public void showPhoto(int index) {
        if (photos.isEmpty()) return;
        
        Photo photo = photos.get(index);
        SoftReference imageSoftRef = downloadedImages.get(photo);
        
        // pokud nebyla fotka načtená, nebo byla odstraněná z paměti, je potřeba ji nahrát
        if (imageSoftRef == null || imageSoftRef.get() == null) {
            if (imageSoftRef != null) System.out.println("Photo lost " + index);
            
            showPreloader();
            
            if (loadingJobs.get(photo) == null) { // ještě neprobíhá načítání
                AsyncJob job = new LoadPhoto(photo, index, true);
                AsyncLoader.getInstance().load(job);
            }
            else { // probíhá načítání
                LoadPhoto job = loadingJobs.get(photo);
                job.showAfterLoaded = true; // zobrazit fotku po načtení
            }
        }
        else {
            hidePreloader();
            showImagePanel(photo); // zobrazení fotky
            
            System.out.println("Photo " + index);
            
            // načtení následujících fotek
            if (index >= currentIndex) {
                int nextIndex = index + 1;
                while (nextIndex < photos.size() && nextIndex - index <= PRELOAD_COUNT) {
                    Photo next = photos.get(nextIndex);
                    if ((downloadedImages.get(next) == null || downloadedImages.get(next).get() == null) && loadingJobs.get(next) == null) {
                        System.out.println("Preloading " + nextIndex);
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
                    if ((downloadedImages.get(prev) == null || downloadedImages.get(prev).get() == null) && loadingJobs.get(prev) == null) {
                        System.out.println("Preloading " + prevIndex);
                        AsyncLoader.getInstance().load(new LoadPhoto(prev, prevIndex, false));
                    }
                    prevIndex--;
                }
            }
            
            currentIndex = index;
            
        }
    }
    
    
    /** Zobrazí panel s fotkou. */
    private void showImagePanel(Photo photo) {
        removeAll();
        if (currentImagePanel != null) {
            currentImagePanel.image = null;
        }
        
        setLayout(new BorderLayout());
        currentImagePanel = new ImagePanel(photo);
        add(currentImagePanel);
        revalidate();
        repaint();
    }
    
    
    
    
    /** Úloha pro asynchronní načtení seznamu fotek. */
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
    
    
    
    /** Úloha pro asynchronní načítání fotek z Flickru. */
    private class LoadPhoto implements AsyncJob, AsyncJob.Priority {
        
        /** Fotka. */
        private Photo photo;
        
        /** Pořadí fotky. */
        private int index;
        
        /** Zobrazit fotku po načtení? */
        private boolean showAfterLoaded;
        
        public LoadPhoto(Photo photo, int index, boolean showAfterLoaded) {
            this.photo = photo;
            this.index = index;
            this.showAfterLoaded = showAfterLoaded;
            loadingJobs.put(photo, this);
        }
        
        @Override
        public Object loadData() throws FlickrException {
            String photoUrl = photo.large2048url; // načteme největší možnou velikost
            if (photoUrl == null || photoUrl.equals("")) photoUrl = photo.large1600url;
            if (photoUrl == null || photoUrl.equals("")) photoUrl = photo.large1024url;
            
            // fotku uložíme jako soft referenci, aby mohla být odstraněna, když začne docházet paměť
            downloadedImages.put(photo, new SoftReference((BufferedImage)PhotoDownloader.download(photoUrl)));
            return true;
        }

        @Override
        public void done(Object data, FlickrException ex) {
            if ((Boolean)data == true) {
                if (showAfterLoaded) showPhoto(index);
            }
            loadingJobs.remove(photo);
        }

        @Override
        public int getPriority() {
            return 10;
        }
        
    }
    
    
    /** Pomocná třída pro panel s fotkou. */
    protected class ImagePanel extends JPanel {
        
        public Photo photo;
        public Image image;
        
        public ImagePanel(Photo photo) {
            this.photo = photo;
            this.image = downloadedImages.get(photo).get();
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
            
            // při prvním vykreslení fotku zmenšíme přesně na velikost okna, po zmenšení ji překreslíme
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
