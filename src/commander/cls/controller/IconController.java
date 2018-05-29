/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commander.cls.controller;

import java.nio.file.Path;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Nam
 */
public class IconController {
    
    private static IconController instance;
    
    public IconController getInstance() {
        if (instance == null) {
            instance = new IconController();
        }
        return instance;
    }
    
    private HashMap<String, ImageIcon> mapIcon;
    
//    public ImageIcon getIcon(Path path) {
//        if (!mapIcon.containsKey(typeName)) {
//            
//        } 
//    }
}
