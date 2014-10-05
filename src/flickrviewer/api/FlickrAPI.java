/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.api;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Martin
 */
public class FlickrAPI {
    
    
    private static FlickrAPI instance = null;
    
    
    private String key;
    private JSONParser jsonParser;
    
    private FlickrAPI(String key) {
        this.key = key;
        
        jsonParser = new JSONParser();
    }
    
    /**
     * Vrátí instanci API.
     * @return 
     */
    public static FlickrAPI getInstance() {
        if (instance == null) {
            instance = new FlickrAPI(flickrviewer.FlickrViewer.API_KEY);
        }
        return instance;
    }
    
    /**
     * Zavolá metodu z Flickr API
     * @param method jméno metody
     * @param params parametry v HTTP formátu
     * @return 
     */
    private String call(String method, String params) {
        String urlString = "https://api.flickr.com/services/rest/?method=" + method + "&api_key=" + key + "&format=json&nojsoncallback=1";
        if (params != null && !params.equals("")) urlString += "&" + params;
        
        System.out.println("Call " + urlString);
        
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            
            con.setRequestMethod("GET");
            con.setUseCaches(false);
            
            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseBuilder = new StringBuilder();
            
            char[] buffer = new char[1024];
            int readLen;
            while ((readLen = reader.read(buffer)) != -1) {
                responseBuilder.append(buffer, 0, readLen);
            }
            
            return responseBuilder.toString();
        } 
        catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    /**
     * Parsuje JSON text.
     * @param response
     * @return 
     */
    private Object jsonParse(String response) {
        try {
            return jsonParser.parse(response);
        }
        catch (ParseException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
    
    /**
     * Vrátí seznam alb zadaného uživatele.
     * 
     * https://www.flickr.com/services/api/flickr.photosets.getList.html
     * @param userId ID uživatele
     */
    public void getSets(String userId) {
        String response = call("flickr.photosets.getList", "user_id=" + userId);
        System.out.println(jsonParse(response));
    }
    
    
}
