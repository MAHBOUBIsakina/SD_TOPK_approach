
package distributed_approach;

import peersim.config.Configuration;


public class DataItem {
    String id;
    Bounds [] bound;
    
    public DataItem(){
        int node_nbr = Configuration.getInt("SIZE");
        bound = new Bounds[node_nbr];
    }
    
}
