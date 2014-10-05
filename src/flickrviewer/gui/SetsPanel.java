/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.api.*;
import java.util.List;

/**
 *
 * @author Martin
 */
public class SetsPanel extends FlickrPanel {
    
    private FlickrAPI api;
    
    public SetsPanel() {
        api = FlickrAPI.getInstance();
        
        /*List<PhotoSet> sets = api.getSets(flickrviewer.FlickrViewer.MY_USER_ID);
        System.out.println(sets);*/
    }
    
    
}
