
package distributed_approach;


public class Bounds {
    double []min_bound;
    double []max_bound;
    
    public Bounds(int size){
        this.max_bound = new double[size];
        this.min_bound = new double[size];
    }
}
