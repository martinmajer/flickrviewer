/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.api;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Třída zajišťující práci s Flickr API.
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
    
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) { return ""; }
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
        
        System.out.println("FlickrAPI: " + urlString);
        
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
            Object result = jsonParser.parse(response);
            jsonParser.reset();
            return result;
        }
        catch (ParseException e) {
            e.printStackTrace(System.err);
            jsonParser.reset();
            return null;
        }
    }
    
    
    /**
     * Vrátí ID uživatele podle uživatelského jména.
     * 
     * https://www.flickr.com/services/api/flickr.people.findByUsername.html
     * @param username
     * @return 
     */
    public String getUserId(String username) {
        String response = call("flickr.people.findByUsername", "username=" + urlEncode(username));
        System.out.println(response);
        JSONObject json = (JSONObject)jsonParse(response);
        String stat = (String)json.get("stat");
        
        if ("ok".equals(stat)) {
            return (String)((JSONObject)json.get("user")).get("id");
        }
        
        return null;
    }
    
    
    /**
     * Vrátí seznam alb zadaného uživatele.
     * 
     * https://www.flickr.com/services/api/flickr.photosets.getList.html
     * @param userId ID uživatele
     * @return 
     */
    public List<PhotoSet> getSets(String userId) {
        String response = call("flickr.photosets.getList", "user_id=" + urlEncode(userId) + "&primary_photo_extras=url_q");
        JSONObject json = (JSONObject)jsonParse(response);
        if (json == null) return null;
        
        JSONArray photosets = (JSONArray)((JSONObject)json.get("photosets")).get("photoset");
        
        List<PhotoSet> sets = new ArrayList();
        
        for (int i = 0; i < photosets.size(); i++) {
            JSONObject photoset = (JSONObject)photosets.get(new Integer(i));
            
            PhotoSet set = new PhotoSet();
            set.id = Long.parseLong(photoset.get("id").toString());
            set.title = ((JSONObject)photoset.get("title")).get("_content").toString();
            set.description = ((JSONObject)photoset.get("title")).get("_content").toString();
            set.coverUrl = ((JSONObject)photoset.get("primary_photo_extras")).get("url_q").toString();
            
            sets.add(set);
        }
        
        return sets;
    }
    
    
}
