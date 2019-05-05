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
import java.util.*;
public class Client {
    public String address;
    public String username;
    public int score;
    public List<String> ansList = new ArrayList<String>(100);
    
    public Client(String ip, String usr, int score){
        this.address = ip;
        this.username = usr;
        this.score = score;
    }
    
}

