/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author sakib
 */
public class ElementInfo {
    String name;
    String[] names;
    JComboBox box;
    JTextField text;
    boolean boxexists;

    public ElementInfo(String name, JTextField text) {
        this.name = name;
        this.text = text;
        boxexists = false;
    }

    public ElementInfo(String[] names, JComboBox box) {
        this.names = names;
        this.box = box;
        boxexists = true;
    }

    public String getName() {
        return name;
    }

    public String[] getNames() {
        return names;
    }

    public JComboBox getBox() {
        return box;
    }

    public JTextField getText() {
        return text;
    }
    
    public boolean isBox(){
        if(boxexists)
            return true;
        else
            return false;
    }
    
    public String getInput(){
        if(boxexists)
            return this.box.getModel().getSelectedItem().toString();
        else
            return this.text.getText().toString();
    }
    
    
}
