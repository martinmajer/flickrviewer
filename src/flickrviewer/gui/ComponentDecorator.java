/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.StyleConstants;

/**
 * Třída pro jednoduché nastavení vzhledu Swing komponent (metal look & feel).
 * @author Martin
 */
public class ComponentDecorator {
    
    private static final Font FONT = new Font("Arial", Font.PLAIN, 14);
    
    
    private static final Color COLOR_BUTTON_HOVER = new Color(96, 96, 96);
    
    
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
    
    public static JButton decorateButton(final JButton button) {
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setUI(new MyButtonUI());
        
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(FONT);
        
        button.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
            }
        });
        
        return button;
    }
    
    public static JScrollBar decorateScrollBar(JScrollBar scrollBar) {
        scrollBar.setUnitIncrement(16);
        scrollBar.setUI(new MyScrollBarUI());
        return scrollBar;
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
        
        @Override
        protected void paintFocus(Graphics g, AbstractButton b,
                              Rectangle viewRect, Rectangle textRect, Rectangle iconRect){ 
            
        }
        
    }
    
    public static class MyScrollBarUI extends MetalScrollBarUI {
        
        protected JButton createDecreaseButton( int orientation )
        {
            // return super.createDecreaseButton(orientation);
            return createZeroButton();
        }

        
        protected JButton createIncreaseButton( int orientation )
        {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }
        
        protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds ) {
            g.setColor(Color.BLACK);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
        
        protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds ) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
        }
        
    }
    
}
