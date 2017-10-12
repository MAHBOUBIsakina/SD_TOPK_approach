
package distributed_approach;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import peersim.config.Configuration;

public class MasterDataNode extends DataNode {
    private HashMap<String,Bounds> dataCollection;
    private HashMap<String,CryptedScore[]> candidatecollection; 
    
    private int nbrResponses;
    
    double TH;
    
    private double max_time = 0;
    
    private double process_time= 0;
    
    
    
    public MasterDataNode(String prefix) {
        super(prefix);
        dataCollection = new HashMap<>();
        candidatecollection = new HashMap(); 
        nbrResponses = 0;
    }
    
    public void setMax_time(double max_time) {
        this.max_time = max_time;
    }

    public double getMax_time() {
        return max_time;
    }

    public double getProcess_time() {
        return process_time;
    }

    public void setProcess_time(double process_time) {
        this.process_time = process_time;
    }
    
    public void dataCollectionReinitialzation(){
        nbrResponses = 0;
    }
    
    public HashMap getCollectedData() {
        return this.dataCollection;
    }
    
    public void putStep1Result(Message2Value[] data, int node_index) {
        int nbr_node = Configuration.getInt("SIZE");
        
        
        for(int i = 0; i < data.length; i++) {
            if (this.dataCollection.containsKey(data[i].id)) {
                this.dataCollection.get(data[i].id).min_bound[node_index]=data[i].min_value;
                
            }
            else{
                Bounds  temp = new Bounds(nbr_node);
                temp.min_bound[node_index] = data[i].min_value;
                this.dataCollection.put(data[i].id, temp);
            }
        }
        this.nbrResponses++;
        
    }
    
    public void putStep2Result(ArrayList data , int node_index) {
        if (this.dataCollection.isEmpty()) {
            System.out.println("data collection is empty");
        }
        int nbr_node = Configuration.getInt("SIZE");
        int i = 0;
        Iterator it = data.iterator();
        while(it.hasNext()){
            String next = ((Message3Value)it.next()).id;
            if (!(this.dataCollection.containsKey(next))) {
                
                Bounds  temp = new Bounds(nbr_node);
                temp.min_bound[node_index] = ((Message3Value)data.get(i)).min_bound;
                temp.max_bound[node_index] = ((Message3Value)data.get(i)).max_bound;
                this.dataCollection.put(next, temp);
                
            }
            else{

                    if (this.dataCollection.get(next).min_bound[node_index] == 0) {
                        this.dataCollection.get(next).min_bound[node_index] = ((Message3Value)data.get(i)).min_bound;
                    }
                    
                    
                    if (this.dataCollection.get(next).max_bound[node_index] == 0) {
                        this.dataCollection.get(next).max_bound[node_index] = ((Message3Value)data.get(i)).max_bound;
                    }


            }   
            i++;
        }
       
        this.nbrResponses++;
    }
    
    
    public int getNbrResponses() {
        return this.nbrResponses;
    }
    
    public void printdataCollection(){
        Set keys = this.dataCollection.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()){
                    String key = (String) it.next(); 
                    Bounds value = this.dataCollection.get(key); 
                    System.out.print("the data item is "+key + "   ");
                    for (int i = 0; i < this.dataCollection.get(key).max_bound.length; i++) {
                        System.out.print(" min bound "+ this.dataCollection.get(key).min_bound[i]+"  max bound " 
                                + this.dataCollection.get(key).max_bound[i]);
                    }
                    System.out.println();
                }
    }
    
    public double THcalcul(){
        System.out.println("THcalcul methode");
        int nbr_node = Configuration.getInt("SIZE");
        int k = Configuration.getInt("K");
        double [] k_high_scores = new double [k];
        double global_score;
        double min_glb_scr=0;
        int minIndex = 0;
        double min=0;
        Set keys = this.dataCollection.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()){
                    String key = (String) it.next(); 
                    global_score = computeOverallScore(this.dataCollection.get(key).min_bound); 
                    if (global_score > min_glb_scr){
                        k_high_scores[minIndex] = global_score;
                        min=k_high_scores[0];
                        minIndex = 0;
                        for (int h=1; h<k; ++h){
                            if (k_high_scores[h]< min) {
                                min = k_high_scores[h];
                                minIndex = h;
                            }
                        }
                        min_glb_scr = min;
                    }
                    
                }
                dataCollectionReinitialzation();
       
        this.TH=min_glb_scr/nbr_node;
        System.out.println("TH =  " +this.TH);
            return this.TH;
    }
   
    private double computeOverallScore (double [] localScores){
    
        double overallScore =0;
        for (int k=0; k<localScores.length; ++k)
            overallScore = overallScore + localScores[k];

        return overallScore;    

    }
   
    public Set getKCondidateSet() throws UnsupportedEncodingException, IOException{
       int k = Configuration.getInt("K");
        double [] k_high_scores = new double [k];
        double global_score;
        double min_glb_scr=0;
        int minIndex = 0;
        double min=0;
        Set keys = this.dataCollection.keySet();
                Iterator it1 = keys.iterator();
                while (it1.hasNext()){
                    String key = (String) it1.next(); 
                    global_score = computeOverallScore(this.dataCollection.get(key).min_bound);
                    if (global_score > min_glb_scr){
                        k_high_scores[minIndex] = global_score;
                        min=k_high_scores[0];
                        minIndex = 0;
                        for (int h=1; h<k; ++h){
                            if (k_high_scores[h]< min) {
                                min = k_high_scores[h];
                                minIndex = h;
                            }
                        }
                        min_glb_scr = min;
                    }
                }
                System.out.println("number of data items before filtering "+this.dataCollection.size());
                keys = this.dataCollection.keySet();
                Object [] array1 = keys.toArray();
                double th2 = min_glb_scr;
                System.out.println("th2 = "+ th2);
                keys = this.dataCollection.keySet();
                Object [] array = keys.toArray();
                for (int i = 0; i < array.length; i++) {
                    String key = array[i].toString();
                     double maxOverallScore =0;
                        for (int h=0; h<this.dataCollection.get(key).max_bound.length; ++h){
                            if (this.dataCollection.get(key).max_bound[h] == 0) {
                                maxOverallScore = maxOverallScore + this.TH ;
                            }else{
                            maxOverallScore = maxOverallScore + this.dataCollection.get(key).max_bound[h];
                        }}
                        if (maxOverallScore < th2){
                           this.dataCollection.remove(key);
                    }
                }  
                keys = this.dataCollection.keySet();
                System.out.println("number of data items after filtering "+this.dataCollection.size());
                dataCollectionReinitialzation();
            return keys;
    }
    
    
    public void putStep3Result(Message5Value[] data){
        int nbr_node = Configuration.getInt("SIZE");
        
        
        for(int i = 0; i < data.length; i++) {
            if (!(this.candidatecollection.containsKey(data[i].dataID))) {
                
                CryptedScore[]  temp = new CryptedScore[nbr_node];
                temp[0] = new CryptedScore();
                temp[0].score = new byte[data[i].score.length];
                for (int j = 0; j < data[i].score.length; j++) {
                    temp[0].score[j] = data[i].score[j];
                }
                this.candidatecollection.put(data[i].dataID, temp);
            }
            else{
                int j = 0;
                CryptedScore [] k = this.candidatecollection.get(data[i].dataID);
                int leng = k.length;
                while(j<leng){
                    if(k[j] != null){
                        j++;
                    }else{
                        break;
                    }
                    
                }
                if (j<leng) {
                    this.candidatecollection.get(data[i].dataID)[j] = new CryptedScore();
                    this.candidatecollection.get(data[i].dataID)[j].score = new byte[data[i].score.length];
                        for (int kk = 0; kk < data[i].score.length; kk++) {
                        candidatecollection.get(data[i].dataID)[j].score[kk] = data[i].score[kk];
                    }
                }
                
            }
            
        }
        
        this.nbrResponses++;
    }
    
    public void printcandidateCollection(){
        Set keys = this.candidatecollection.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()){
                    String key = (String) it.next(); 
                    CryptedScore[] value = this.candidatecollection.get(key); 
                    System.out.print("the data item is "+key + "  its scores are ");
                    for (int i = 0; i < this.candidatecollection.get(key).length; i++) {
                        System.out.print(  Arrays.toString(this.candidatecollection.get(key)[i].score));
                    }
                    System.out.println();
                }
    }
    
    public HashMap<String,CryptedScore[]> getCandidatecollection (){
        return this.candidatecollection;
    }
    
     
    
    
}
