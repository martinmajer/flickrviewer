/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.util;

import java.io.*;

/**
 * Třída pro jednoduchou práci se soubory.
 * @author Martin
 */
public class FileUtils {
    
    /** Zapíše řetězec do souboru. */
    public static boolean writeString(File f, String s) {
        try {
            try (PrintWriter writer = new PrintWriter(f)) {
                writer.write(s);
            }
            
            return true;
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    /** Zapíše řetězec do souboru. */
    public static boolean writeString(String filename, String s) {
        return writeString(new File(filename), s);
    }
    
    /** Načte řetězec ze souboru. */
    public static String readString(File f) {
        try {
            StringBuilder strBuilder;
            
            try (Reader reader = new FileReader(f)) {
                strBuilder = new StringBuilder();
                char[] buffer = new char[1024];
                int readLen;
                while ((readLen = reader.read(buffer)) != -1) {
                    strBuilder.append(buffer, 0, readLen);
                }
            }
            
            return strBuilder.toString();
        }
        catch (IOException e) {
            return null;
        }
    }
    
    /** Načte řetězec ze souboru. */
    public static String readString(String filename) {
        return readString(new File(filename));
    }
    
}
