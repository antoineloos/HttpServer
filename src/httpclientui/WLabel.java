/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpclientui;

import javafx.scene.control.Label;

/**
 *
 * @author Epulapp
 */
public class WLabel extends Label {

    public WLabel(String text) {
        super(text);
        this.setStyle("-fx-text-fill: white;"+"-fx-font: 16px \"Tahoma\";"+"");
    }
    
}
