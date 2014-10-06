/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.async;

import flickrviewer.api.FlickrException;

/**
 * Rozhraní pro callback určený k asynchronnímu načítání dat z FlickrAPI.
 * @author Martin
 */
public interface AsyncJob {
    
    /**
     * Načte a vrátí data z Flickr API.
     * @return data z FlickrAPI
     * @throws flickrviewer.api.FlickrException
     */
    public Object loadData() throws FlickrException;
    
    /**
     * Zavolá se po dokončení přenosu.
     * @param data data vrácená z Flickr API
     * @param ex výjimka, v případě úspěchu null
     */
    public void done(Object data, FlickrException ex);
    
    
    /** Rozhraní pro definici priority. */
    public interface Priority {
        
        public int getPriority();
        
    }
    
    
}
