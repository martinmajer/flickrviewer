/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;

/**
 *
 * @author Martin
 */
public class SetsPanel extends FlickrPanel {
    
    private FlickrAPI api;
    
    public SetsPanel() {
        api = FlickrAPI.getInstance();
        
        api.getSets(flickrviewer.FlickrViewer.MY_USER_ID);
    }
    
    
}
