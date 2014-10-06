/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package flickrviewer.gui;

import flickrviewer.FlickrViewer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import static flickrviewer.gui.ComponentDecorator.*;
import flickrviewer.util.FileUtils;

/**
 * Panel pro výběr uživatele.
 * @author Martin
 */
public class UserSelectPanel extends FlickrPanel {
    
    private JTextField userName;
    
    public UserSelectPanel() {
        // setLayout(new FlowLayout(FlowLayout.CENTER));
        setLayout(new GridBagLayout());
        decoratePanel(this);
        
        GridBagConstraints cnstrs = new GridBagConstraints();
        cnstrs.insets = new Insets(5, 5, 5, 5);
        
        add(decorateLabel(new JLabel("Select your username: ")), cnstrs);
        
        String lastUserNameValue = FileUtils.readString(FlickrViewer.getDataDirectory() + "/username");
        
        userName = decorateTextField(new JTextField(lastUserNameValue));
        userName.setPreferredSize(new Dimension(200, 30));
        add(userName, cnstrs);
        
        JButton okButton = decorateButton(new JButton("OK"));
        okButton.setPreferredSize(new Dimension(50, 30));
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
        
        FileUtils.writeString(FlickrViewer.getDataDirectory() + "/username", flickrUsername);
        
        FlickrPanel newPanel = new SetsPanel(flickrUsername);
        flickrFrame.changePanel(newPanel);
    }
    
}
