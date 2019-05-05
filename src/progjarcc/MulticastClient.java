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
import java.io.IOException;
import sun.net.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastClient {
    private static int port = 5000;
    //private static String host = "10.151.253.239";
   // private static InetAddress host; 
    private static String group = "224.0.0.3";
    private static MulticastSocket s;
    private static DatagramPacket pack;
    private static DatagramPacket notif;
    private static byte[] buf;
    private static GUIClient gui;
    
    private static int ttl = 1;
    public static String nama;
    public static void main(String[] args){
        try {
              
            s = new MulticastSocket(port);
            s.joinGroup(InetAddress.getByName(group));
            //host= InetAddress.getByName("10.151.253.239");
            gui = new GUIClient(s);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    gui.setVisible(true);
                }
            });
                
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        gui.showAskUsername();
            
        start();
    }
    
    public void setUsername(String param){
        this.nama = param;
    }
    
    public String getNama(){
        return this.nama;
    }
    
    public static void start(){
//            gui.updateChatbox("Anda bergabung dengan username " + nama);
        
        String msg = "";
            
        msg = "***NEW#" + nama;
            
        buf = new byte[1024];
        pack = new DatagramPacket(buf, buf.length);
            
        try {
            notif = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(group), port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        try {
            s.send(notif, (byte) ttl);
            gui.updateChatbox("Anda sudah bergabung di room dengan username "+nama+"\n");
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            do{              
                
                try {
                    s.receive(pack);
                } catch (IOException ex) {
                    Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                msg = new String(pack.getData(),0,pack.getLength());
                if(msg.startsWith("***")) continue;
                
                if(msg.equals("*CLEAR")){
                    gui.clearChatBox();
                    continue;
                }
                
                if(msg.equals("*START")){
                    gui.enableOption();
                }
                
                if(msg.equals("*DISABLE")){
                    gui.disableOption();
                }
                
                if(msg.startsWith("*ANS")) continue;
                    
                gui.updateChatbox(msg);
                 
            }while(!msg.equals("*CLOSE-CLIENT"));
            
        try {
            s.leaveGroup(InetAddress.getByName(group));
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
            gui.updateChatbox("You left the group");
            s.close();
    }
    
    public void sendMessage(String message){
        message = message;
        
        try {
            notif = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
        } catch (UnknownHostException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            s.send(notif,(byte)ttl);
        } catch (IOException ex) {
            Logger.getLogger(MulticastClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
