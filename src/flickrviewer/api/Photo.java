/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.api;

import flickrviewer.async.AsyncJob;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Fotka na Flickr.
 * @author Martin
 */
public class Photo {
    
    /** ID alba. */
    public long id;
    
    /** Titulek fotky. */
    public String title;
    
    /** Popis fotky. */
    public String description;
    
    /** Malá velikost. */
    public String large1024url;
    
    /** Střední velikost. */
    public String large1600url;
    
    /** Velká velikost. */
    public String large2048url;
    
    /** Originální velikost. */
    public String originalUrl;
    
    
    
    // public transient volatile Image image;
    // public transient volatile AsyncJob loadingJob;
    
    public volatile SoftReference<BufferedImage> image;
    public transient volatile AsyncJob loadingJob;
    
    
}
