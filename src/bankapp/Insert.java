/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bankapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sakib
 */


public class Insert {
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    JPanel downpanel = new JPanel();
    JButton btn = new JButton("Submit");
    JButton clr = new JButton("Reset");
    
    Connection connect = null;
    Statement statement = null;
    ResultSet rs = null;
    ResultSetMetaData rsMetaData = null;
    int columnCount = 0;
    JTextField[] texts;
    JComboBox[] boxes;
    String tbname;
    JTable tabel;
    
    ArrayList<String> cols = new ArrayList<String>();
    ArrayList<String[]> fklist = new ArrayList<String[]>();
    ArrayList<ElementInfo> elementlist = new ArrayList<ElementInfo>();
    
    public Insert(Connection conn,final String t,final JTable tb){
        try {
            connect = conn;
            tbname = t;
            tabel = tb;
            
            
            DatabaseMetaData dbmetadata = connect.getMetaData();
            
            
            rs = dbmetadata.getColumns(null, null, tbname , null);
            while(rs.next()){
                String name = rs.getString("COLUMN_NAME");
                cols.add(name);
            }
            //System.out.println(Arrays.toString(cols.toArray()));
            
            ResultSet rs = dbmetadata.getImportedKeys(connect.getCatalog(), null, tbname);
            while(rs.next()){
                String[] fkeys = new String[4];
                fkeys[0] = rs.getString("FKTABLE_NAME");
                fkeys[1] = rs.getString("FKCOLUMN_NAME");
                fkeys[2] = rs.getString("PKTABLE_NAME");
                fkeys[3] = rs.getString("PKCOLUMN_NAME");
                fklist.add(fkeys);
                //System.out.println(Arrays.toString(fklist.get(i++)));
            }
            
            
            for(String colname: cols){
                JLabel label = new JLabel(colname);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(label);
                boolean found = false;
                for(String[] fkey: fklist){
                    if(colname.matches(fkey[1])){
                        //System.out.print(fkey[1]+"->");
                        //System.out.println(Arrays.toString(fkey));
                        JComboBox cbox = new JComboBox();
                        cbox.setPreferredSize(new Dimension(200,30));
                        initComBox(cbox,fkey[2],fkey[3]);
                        
                        panel.add(cbox);
                        elementlist.add(new ElementInfo(fkey, cbox));
                        found = true;
                        break;
                    }
                }
                if(!found){
                    JTextField tf = new JTextField();
                    tf.setPreferredSize(new Dimension(200,30));
                    panel.add(tf);
                    elementlist.add(new ElementInfo(colname, tf));
                }
            }
            
            
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    
                    try{

                        String query = "INSERT INTO "+tbname+" VALUES(";
                        for (int columnIndex = 1; columnIndex < cols.size(); columnIndex++){
                            query += "?,";
                        }
                        query += "?)";
                        System.out.println(query);
                        PreparedStatement pStatement = connect.prepareStatement(query);;
                        for (int columnIndex = 1; columnIndex <= cols.size(); columnIndex++){
                            String input;
                            if(elementlist.get(columnIndex-1).isBox()){
                                input = elementlist.get(columnIndex-1).getBox().getSelectedItem().toString();
                            }
                            else{
                                input = elementlist.get(columnIndex-1).getText().getText().toString();
                            }
                            pStatement.setString(columnIndex, input);
                            System.out.println("pStatement.setString("+columnIndex+","+input+");");
                        }

                        pStatement.executeUpdate();


                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    finally{
                        
                        loadTab();
                        //frame.dispose();
                    }
                }

                
            });
            
            
            
            downpanel.setLayout(new GridLayout(1,3,10,30));
            downpanel.add(new JLabel());
            downpanel.add(btn);
            downpanel.add(new JLabel());
            downpanel.setBorder(new EmptyBorder(30,0,15,0));
            frame.add(downpanel,BorderLayout.SOUTH);
            
            
            panel.setLayout(new GridLayout(cols.size(),2,10,30));
            panel.setBorder(new EmptyBorder(20, 10, 20, 100));
            frame.add(panel,BorderLayout.CENTER);
            frame.setTitle(tbname);
            frame.pack();
            frame.setVisible(true);
            frame.setLayout(new BorderLayout());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initComBox(JComboBox box, String pktab, String pkcol) {
        try {
            ArrayList<String> ls = new ArrayList<String>();
            Statement st = connect.createStatement();
            ResultSet pkres= st.executeQuery("SELECT DISTINCT "+pkcol+" FROM "+pktab);
            //System.out.println("SELECT "+pkcol+" FROM "+pktab);
            while (pkres.next()) {
              ls.add(pkres.getString(1));
            }
            //System.out.println(Arrays.toString(ls.toArray()));
            box.setModel(new DefaultComboBoxModel( ls.toArray()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    
   
    
    private void loadTab() {
        try {
            String query = "SELECT * FROM "+tbname;
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(query);
            tabel.setModel(restab(result));
        } catch (Exception e) {
        }
    }
    public DefaultTableModel restab(ResultSet rs) throws SQLException{
        DefaultTableModel tableModel = new DefaultTableModel();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++){
            
            tableModel.addColumn(metaData.getColumnLabel(columnIndex));
        }

        //Create array of Objects with size of column count from meta data
        Object[] row = new Object[columnCount];

        //Scroll through result set
        while (rs.next()){
            //Get object from column with specific index of result set to array of objects
            for (int i = 0; i < columnCount; i++){
                row[i] = rs.getObject(i+1);
            }
            //Now add row to table model with that array of objects as an argument
            tableModel.addRow(row);
        }
        return tableModel;

       }
}


