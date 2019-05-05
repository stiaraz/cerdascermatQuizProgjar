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

import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class handler extends Thread {
    public MulticastSocket s;
    public List<soal> listQuestion = new ArrayList<soal>(100);
    public MulticastServer serv = new MulticastServer();
    public GUIServer gui;
    
    public handler(MulticastSocket s, List<soal> list, MulticastServer serv, GUIServer gui){
        this.s = s;
        this.serv = serv;
        this.listQuestion = list;
        this.gui = gui;
    }
    
    public void run(){
        serv.clearScoreSheet();
        
        int count = 1;
        serv.sendMessage("*CLEAR");
        serv.sendMessage("Quiz akan segera dimulai 5 detik"+
                "\n\n1. Jawaban yang diterima hanyalah jawaban dari peserta yang paling cepat"+
                "\n2. Score akhir adalah banyak soal yang dijawab dengan benar"
                + "\n3. Jadilah yang tercepat dan sedikit cerdas");
        
        try {
            sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        serv.sendMessage("*START");
        for(final soal i: listQuestion){
            serv.sendMessage("*CLEAR");
            serv.sendMessage(i.ID+"\n");
            serv.sendMessage(i.deskripsi+"\n");
            
            int z = 0;
            String abj;
            for(final String x: i.optionList){
                if(z == 0){
                    abj = "A.";
                }else if(z == 1){
                    abj = "B.";
                }else if(z == 2){
                    abj = "C.";
                }else{
                    abj = "D.";
                }
                
                serv.sendMessage(abj+" "+x+"\n");
                z++;
            }
            
            gui.updateLog("Soal ke "+count+" dikeluarkan\n");
            count++;
            
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            serv.jawabFlag = 0;
        }
        
        serv.sendMessage("*CLEAR");
        serv.sendMessage("QUIZ SUDAH SELESAI\nXD XD XD");
        try {
            sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        serv.sendMessage("*DISABLE");
        
        serv.sendMessage("Menghitung score anda....\n");
        
        serv.countScore();
    }

   
}
