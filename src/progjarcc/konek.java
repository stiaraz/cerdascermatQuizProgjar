/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progjarcc;

/**
 *
 * @author Owner
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class konek {
    private static final String username="root";
    private static final String pass="";
    private static final String conn_string="jdbc:mysql://localhost:3306/fp_progjar";
    public static int numRow;
    public List<soal> listQuestion = new ArrayList<soal>(100);
    private GUIServer gui;
    
    public konek(GUIServer gui){
        this.gui = gui;
    }
    
    public void fetch(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection conn = null;
        
        
            conn = DriverManager.getConnection(conn_string,username, pass);
            System.out.print("Database sudah terkoneksi !\n");
            
            Statement stmt = null;
            stmt = conn.createStatement();

        
            String query = "SELECT * FROM soal";
            ResultSet rset = null;
            rset = stmt.executeQuery(query);
            
            if(listQuestion.isEmpty()){
                while(rset.next()){
                    String deskripsi = rset.getString("deskripsi");
                    String kunci = rset.getString("true_opt");
                    int id = rset.getInt("id");

                    String a = rset.getString("opt_a");
                    String b = rset.getString("opt_b");
                    String c = rset.getString("opt_c");
                    String d = rset.getString("opt_d");

                    listQuestion.add(new soal(deskripsi, kunci, id, a, b,c,d));
                }

                gui.updateLog(listQuestion.size() + " soal berhasil diambil\n");
            }else{
                gui.updateLog("Daftar pertanyaan sudah diambil\n");
            }
            
            rset.close();
            stmt.close();
            conn.close();
            
        }catch(SQLException e){
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        
    }
}
