/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

/**
 *
 * @author sakina
 */
public class Bounds {
    double []min_bound;
    double []max_bound;
    
    public Bounds(int size){
        this.max_bound = new double[size];
        this.min_bound = new double[size];
    }
}
