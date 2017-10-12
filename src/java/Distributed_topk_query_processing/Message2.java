
package distributed_approach;

import peersim.core.Node;

public class Message2 {
    
    final Message2Value []value;
    
    final Node sender;
    
    final double calcul_time;
    
    public Message2(Message2Value []val, Node node, double time) {
        this.value = val;
        this.sender = node;
        this.calcul_time = time;
    }
    
    public Node getSender() {
        return this.sender;
    }
    
    public Message2Value []getValue() {
        return this.value;
    }
    
    public double getCalcul_time(){
        return this.calcul_time;
    }
    
    public void printMessage2(){
        System.out.println("Message 2 contains ");
        for (int i = 0; i < this.value.length; i++) {
            System.out.println("id = "+ value[i].id + " min bound = "+value[i].min_value);
        }
    }
    
}
