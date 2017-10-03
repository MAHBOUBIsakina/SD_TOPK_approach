/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

import java.util.Set;
import peersim.core.Node;

/**
 *
 * @author sakina
 */
public class Message4 {
    final Set value;
    
    final Node sender;
    
    public Message4(Set val, Node node){
        this.value = val;
        this.sender = node;
    }
    
    public Node getSender() {
        return this.sender;
    }
    
    public Set getValue() {
        return this.value;
    }
}
