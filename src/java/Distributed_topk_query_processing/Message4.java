
package distributed_approach;

import java.util.Set;
import peersim.core.Node;


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
