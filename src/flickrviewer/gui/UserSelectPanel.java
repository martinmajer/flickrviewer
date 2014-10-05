/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Martin
 */
public class UserSelectPanel extends FlickrPanel {
    
    private JTextField userName;
    
    public UserSelectPanel() {
        // setLayout(new FlowLayout(FlowLayout.CENTER));
        setLayout(new GridBagLayout());
        
        GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.insets = new Insets(5, 5, 5, 5);
        
        add(new JLabel("Select your username: "), cnstrs);
        
        userName = new JTextField("");
        userName.setColumns(20);
        add(userName, cnstrs);
        
        JButton okButton = new JButton("OK");
        add(okButton, cnstrs);
        
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonClicked();
            }
        });
    }
    
    private void okButtonClicked() {
        String flickrUsername = userName.getText();
        
        FlickrPanel newPanel = new SetsPanel();
        flickrFrame.changePanel(newPanel);
    }
    
}
