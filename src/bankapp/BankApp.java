/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author sakib
 */
public class BankApp {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        try {
            String dbName = "busbays";
            String user = "root";
            String password = "";
            String url = "jdbc:mysql://localhost/" + dbName;
             
            Class.forName("com.mysql.jdbc.Driver");
            Connection connect = DriverManager.getConnection(url, user, password);
            
            Home home = new Home(connect,dbName);
            home.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
