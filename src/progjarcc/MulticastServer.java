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
import java.util.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastServer {
    private static int port = 5000;
    private static String group = "224.0.0.3";
    //private static String group = "10.151.253.239";
    private static InetAddress host; 
    private static int ttl = 1;
    private static MulticastSocket s;
    private static DatagramPacket pack;
    private static DatagramPacket update;
    private static GUIServer gui;
    private static byte[] buf;
    private static final List<Client> listClient = new ArrayList<Client>(100);
    public List<soal> listQuestion = new ArrayList<soal>(100);
    public static int jawabFlag = 0;
    
    public static void main(String args[]){
        
        try {
            s = new MulticastSocket(port);
            s.joinGroup(InetAddress.getByName(group));
            gui = new GUIServer(s);
            
            java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        gui.setVisible(true);
                    }
                });
        } catch (IOException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        buf = new byte[1024];
        update = new DatagramPacket(buf, buf.length);
        String msg="";
        String[] temp;
        
        do{
            try {
                s.receive(update);
            } catch (IOException ex) {
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            msg = new String(update.getData(),0,update.getLength());
            
            if(msg.startsWith("***")){
                msg.replace("*", "");
                temp = msg.split("#");
                
                String addr = update.getAddress().toString();
                
                listClient.add(new Client(addr, temp[1], 0));
                gui.appendUser(listClient);
                gui.updateLog("User with IP " + addr +" has joined room\n");
            }
            
            if(msg.startsWith("*ANS")){
                if(jawabFlag == 0){
                    String[] tmp;
                
                    tmp = msg.split("#");

                    for(final Client i: listClient){
                        if(i.username.equals(tmp[2])){
                            i.ansList.add(tmp[1]);
                        }
                    }

                    gui.updateLog(tmp[2] + " menjawab " + tmp[1]+"\n");
                    
                    jawabFlag = 1;
                    
                    String message = "";
                    
                    message = "*CLEAR";
                    
                    try {
                        pack = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        s.send(pack,(byte) ttl);
                    } catch (IOException ex) {
                        Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    message = "Soal sudah terjawab";
                    
                    try {
                        pack = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    try {
                        s.send(pack,(byte) ttl);
                    } catch (IOException ex) {
                        Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    continue;
                }
                
            }
            
            if(msg.startsWith("*CLOSE CLIENT")){
                String[] tmp;
                tmp = msg.split("#");
                
                for(final Client z: listClient){
                    if(z.username.equals(tmp[1])){
                        listClient.remove(z);
                        break;
                    }
                }
                
                gui.updateLog("User " + tmp[1] + " leave the room\n");
                gui.appendUser(listClient);
                
            }
        }while(true);
    }
    
    public void sendMessage(String message){
            try {
                pack = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(group), port);
            } catch (UnknownHostException ex) {
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                s.send(pack,(byte) ttl);
            } catch (IOException ex) {
                Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void countScore(){
        Iterator<soal> it1 = listQuestion.iterator();
        
        for(final Client i: listClient){
            for(final String j: i.ansList){
                if(j.equals(it1.next().optionBenar)){
                    i.score++;
                }
            }
        }
        
        String tmp = "HASIL AKHIR : \n\n";
        int x = 1;
        for(final Client i: listClient){
            tmp += x + ". " + i.username + " " + i.score + "\n";
            x++;
        }
        
        this.sendMessage("*CLEAR");
        
        this.sendMessage(tmp);
    }
    
    public void clearScoreSheet(){
        for(final Client i: listClient){
            if(!i.ansList.isEmpty()) i.ansList.clear();
            i.score = 0;
        }
    }
}
