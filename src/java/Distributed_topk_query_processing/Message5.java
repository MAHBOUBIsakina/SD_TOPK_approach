/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

import peersim.core.Node;

/**
 *
 * @author sakina
 */
public class Message5 {
    final Message5Value []value;
    
    final Node sender;
    
    final double calcul_time;

    
    public Message5(Message5Value []val, Node node, double time){
        this.value = val;
        this.sender = node;
        this.calcul_time = time;
    }
    
    public Node getSender() {
        return this.sender;
    }
    
    public Message5Value []getValue() {
        return this.value;
    }
    
    public double getCalcul_time(){
        return this.calcul_time;
    }
    
}
