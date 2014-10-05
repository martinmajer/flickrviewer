/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;

/**
 *
 * @author Martin
 */
public class ComponentDecorator {
    
    private static final Font FONT = new Font("Arial", Font.PLAIN, 14);
    
    public static JPanel decoratePanel(JPanel panel) {
        panel.setBackground(Color.BLACK);
        return panel;
    }
    
    public static JLabel decorateLabel(JLabel label) {
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        
        label.setFont(FONT);
        
        return label;
    }
    
    public static JTextField decorateTextField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(new EmptyBorder(5, 5, 5, 5));
        field.setUI(new MyTextUI());
        
        field.setFont(FONT);
        
        return field;
    }
    
    public static JButton decorateButton(JButton button) {
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setUI(new MyButtonUI());
        
        button.setFont(FONT);
        
        return button;
    }
    
    
    public static class MyTextUI extends MetalTextFieldUI {
        
    }
    
    
    public static class MyButtonUI extends MetalButtonUI {    
        
        @Override
        protected Color getSelectColor() {
            return Color.GRAY;
        }
        
        @Override
        protected Color getFocusColor() {
            return Color.GRAY;
        }
        
    }
    
}
