/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

import java.util.ArrayList;
import peersim.core.Node;

/**
 *
 * @author sakina
 */
public class Message3 {
    final ArrayList value;
    
    final Node sender;
    
    final double calcul_time;

    
    public Message3(ArrayList val, Node node, double time){
        this.value = val;
        this.sender = node;
        this.calcul_time = time;
    }
    
    public Node getSender() {
        return this.sender;
    }
    
    public ArrayList getValue() {
        return this.value;
    }
    
    public double getCalcul_time(){
        return this.calcul_time;
    }
    
    
    public void printMessage3(){
        System.out.println("Message 2 contains ");
        for (int i = 0; i < this.value.size(); i++) {
            System.out.println("id = "+ ((Message3Value)value.get(i)).id + " min bound = "+((Message3Value)value.get(i)).min_bound + " max bound = " + ((Message3Value)value.get(i)).max_bound);
        }
    }
    
    
}
