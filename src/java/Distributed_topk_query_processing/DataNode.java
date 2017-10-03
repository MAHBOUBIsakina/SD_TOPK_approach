/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import peersim.config.Configuration;
import peersim.core.Cleanable;
import peersim.core.CommonState;
import static peersim.core.Fallible.DEAD;
import static peersim.core.Fallible.DOWN;
import static peersim.core.Fallible.OK;
import peersim.core.Node;
import static peersim.core.Node.PAR_PROT;
import peersim.core.Protocol;
import peersim.core.Fallible;

/**
 *
 * @author sakina
 */
public class DataNode implements Node{
    static private int pointer = 0;
    static private int nMasters = 0;
    
    static public int NBR_MASTERS;
    
    //public List_EncryptedItems[] data;
    
    public LinkedHashMap<String,EncryptedItem> datalist;
    
    
    protected Protocol[] protocol = null;
    
    
    protected int failstate = Fallible.OK;
    
    
    private int index;
    
    
     private long ID;
     
     private static long counterID = -1;
    
    public DataNode(String prefix){
        NBR_MASTERS = Configuration.getInt("NBR_MASTERS");
                
        String[] names = Configuration.getNames(PAR_PROT);//collects all the protocols on configuration file
        CommonState.setNode(this);//set the current node
        ID=nextID();
        protocol = new Protocol[names.length];//table which contains all the protocols in th node
        for (int i=0; i < names.length; i++) {
            //System.out.println("name prtcl = "+names[i]);
                CommonState.setPid(i);//each prorocol has the same ID in all the nodes, ID = protocol order on configuration file
                Protocol p = (Protocol) Configuration.getInstance(names[i]); //return protocol which havs the name in name[i] 
                //System.out.println("prtcl is "+p);
                protocol[i] = p; 
        }
        this.datalist = null;
    }
    
    
    
    
    private long nextID() {

            return counterID++;
    }

    
    public Object clone() {
        DataNode result = null;
        
        if(nMasters < NBR_MASTERS) {
            result = new MasterDataNode("");
            nMasters++;
        } else {
            try { result=(DataNode)super.clone(); }
            catch( CloneNotSupportedException e ) {} // never happens
        }
        
        result.protocol = new Protocol[protocol.length];
        CommonState.setNode(result);
        result.ID=nextID();
        for(int i=0; i<protocol.length; ++i) {
                CommonState.setPid(i);
                result.protocol[i] = (Protocol)protocol[i].clone();
        }
        //try {
            // TODO get the data;
           // result.data = this.initializeData();
            
        //} catch (IOException ex) {
        //    Logger.getLogger(DataNode.class.getName()).log(Level.SEVERE, null, ex);
        //}
        return result;
    }
    
    private List_EncryptedItems[] initializeData() throws IOException {
        int nbr = Configuration.getInt("NBR_ELEMENTS");
        
        List_EncryptedItems[] data = new List_EncryptedItems[nbr];
        
        InputStream flux = new FileInputStream ("/home/sakina/Desktop/crypted_DB.txt"); 
        InputStreamReader lecture=new InputStreamReader(flux);
        BufferedReader buff=new BufferedReader(lecture); 
        String value;
        String []line;
        String [] crypted_score;
        int current_line = 0;
        while(current_line < pointer){
            buff.readLine();
            current_line++;
        }
        for(int i = 0; i < nbr; i++) {
            value=buff.readLine();
            //System.out.println(" value = " + value);
            line = value.split("/");
            //System.out.println(" line = " + line[1]);
            data[i] = new List_EncryptedItems();
            
            data[i].dataID = line[0];
            
            crypted_score = line[1].split(" ");
            
            //System.out.println("crypted score "+ crypted_score[0]);
            data[i].score = new byte[crypted_score.length];
                
            for (int j = 0; j < crypted_score.length; j++) {
                data[i].score[j] =(byte) Integer.parseInt(crypted_score[j]) ;
            }
            
            data[i].min_bound = Double.parseDouble(line[2]);
            
            data[i].max_bound = Double.parseDouble(line[3]);
            
            String score="";
                for (int l = 0; l < data[i].score.length; l++) {
                    score = score + data[i].score[l] + " ";
                   // h++;
                    //System.out.println(fp.db1.listsCR[j].elementsCR[i].score[l]);
                }
            
            System.out.println(data[i].dataID + "/" + score + "/" + data[i].min_bound + " / " + data[i].max_bound);
            
        }
        
       
           
        
         buff.close();
        
        // update pointer
        pointer += nbr;
        
        return data;
    }
    
    
    public void setData(List_EncryptedItems [] list){
        this.datalist = new LinkedHashMap<String,EncryptedItem>();
        
        
        for(int i = 0; i < list.length; i++) {
            //temp.dataID = list[i].dataID;
            EncryptedItem temp = new EncryptedItem();
            temp.min_bound = list[i].min_bound;
            temp.max_bound = list[i].max_bound;
            temp.score = new byte[list[i].score.length];
            System.arraycopy(list[i].score, 0, temp.score, 0, list[i].score.length);
            //System.out.println("temp.id = "+ list[i].dataID + " temp.minscore = "+ temp.min_bound );
            this.datalist.put(list[i].dataID, temp);
            //System.out.println("temp.id = "+ list[i].dataID + " temp.minscore = "+ this.datalist.get(list[i].dataID).min_bound);
            
        }
//        System.out.println();
//        Iterator it = this.datalist.keySet().iterator();
//        while(it.hasNext()) {            
//            String next =it.next().toString();
//            System.out.println("id = "+ next + " minscore = "+ this.datalist.get(next).min_bound );
//        }
        
    }
    
    public Message2Value[] getKElements() {
        int nb_element_per_node = Configuration.getInt("NBR_ELEMENTS");
        
        int bucket_size = Configuration.getInt("BUCKET_SIZE");
        
        int k = Configuration.getInt("K");
        
        if (k>nb_element_per_node) {
            System.out.println("k is bigger than NBR_ELEMENTS");
            return null;
        }
        
        int nb_items_to_return; 
                
        if (k%bucket_size==0) {
            nb_items_to_return = k;
        }
        else{
            nb_items_to_return = ((k/bucket_size)+1)*bucket_size;
        }
        
        Message2Value []items_to_return = new Message2Value[nb_items_to_return];
        Iterator it = datalist.keySet().iterator();
        int i = 0;
        while (it.hasNext() && (i<nb_items_to_return)) {
            
            String next = (String) it.next();
            items_to_return[i] = new Message2Value();
            items_to_return[i].id = next;
            items_to_return[i].min_value = this.datalist.get(next).min_bound;
            i++;
        }
        
        
        
        
        // calculate a number close to k
       
        
        return items_to_return;
    }
    
    
    public ArrayList getDataHighThanTH(double th){
        //int bucket_size = Configuration.getInt("BUCKET_SIZE");
        
        //int nb_element_per_node = Configuration.getInt("NBR_ELEMENTS");
        ArrayList items_to_return = new ArrayList();
        
        //int i = 0;
        int k = 0;
        Message3Value temp=null;
        Iterator it = datalist.keySet().iterator();
        String next = (String) it.next();
        while (it.hasNext() && this.datalist.get(next).min_bound>th) {
            temp =  new Message3Value();
            temp.id = next;
            temp.min_bound = this.datalist.get(next).min_bound;
            temp.max_bound = this.datalist.get(next).max_bound;
            items_to_return.add(temp);
            k++;
            next = (String) it.next();
        }
        System.out.println("#########################the last min ="+temp.min_bound+" stop position =  "+k);
//        int nb_items_to_return = k*bucket_size;
//        
//        Message3Value []items_to_return = new Message3Value[nb_items_to_return];
//        //System.out.println("getDataHighThanTH methode  i= "+ i + " nb_items_to_return= "+ nb_items_to_return + " th = "+ th);
//        
//        for (i = 0; i < nb_items_to_return; i++) {
//            
//            items_to_return[i] = new Message3Value();
//            items_to_return[i].id = this.data[i].dataID;
//            items_to_return[i].min_bound = this.data[i].min_bound;
//            items_to_return[i].max_bound = this.data[i].max_bound;
//            //System.out.println("****************"+items_to_return[i].id + "   " + items_to_return[i].min_bound +
//                    //"   " + items_to_return[i].max_bound);
//        }
        
        return items_to_return;
        
    }
    
    public Message5Value []getTop_kCondidateScores (Set candidate){
        //System.out.println("------------------------------start get top k condidate methode  ");
        //Set topk_candidate = candidate;
        Message5Value [] result = new Message5Value[candidate.size()];
        Iterator it = candidate.iterator();
        int l=0;
        while (it.hasNext()) {
            
            String next = (String) it.next();
            //System.out.println("data item "+ next +" is sdfffffffffff");
            //System.out.println("data item "+ next +" is ");
            //for (int i = 0; i < data.length; i++) {
                if (datalist.get(next) != null) {
                    result[l] = new Message5Value();
                    result[l].dataID = next;
                    int leng = datalist.get(next).score.length;
                    result[l].score = new byte[leng];
                    
                    for (int j = 0; j < leng; j++) {
                        result[l].score[j] = datalist.get(next).score[j];
                    }
                    //break;
                }else{
                    System.out.println(" the data item doesn'i eist in the list");
                }
            //}
            //System.out.println("data item "+ next +" is sdfffffffffff");
            l++;
        }
        //System.out.println("------------------------------finish get top k condidate methode ");
        return result;
    }
    
    
    public int getFailState() { return failstate; }

    // ------------------------------------------------------------------

    public boolean isUp() { return failstate==OK; }

    // -----------------------------------------------------------------

    public Protocol getProtocol(int i) { return protocol[i]; }

    //------------------------------------------------------------------

    public int protocolSize() { return protocol.length; }

    //------------------------------------------------------------------

    public int getIndex() { return index; }

    //------------------------------------------------------------------

    public void setIndex(int index) { this.index = index; }

    //------------------------------------------------------------------

    /**
    * Returns the ID of this node. The IDs are generated using a counter
    * (i.e. they are not random).
    */
    public long getID() { return ID; }
   
    
    
    
   

   
    
    public String toString() 
    {
            StringBuffer buffer = new StringBuffer();
            buffer.append("ID: "+ID+" index: "+index+"\n");
            for(int i=0; i<protocol.length; ++i)
            {
                    buffer.append("protocol["+i+"]="+protocol[i]+"\n");
            }
            return buffer.toString();
    }
    
    
        public void setFailState(int failState) {

            // after a node is dead, all operations on it are errors by definition
            if(failstate==DEAD && failState!=DEAD) throw new IllegalStateException(
                    "Cannot change fail state: node is already DEAD");
            switch(failState)
            {
                    case OK:
                            failstate=OK;
                            break;
                    case DEAD:
                            //protocol = null;
                            index = -1;
                            failstate = DEAD;
                            CommonState.setNode(this);
                            for(int i=0;i<protocol.length;++i) {
                                    CommonState.setPid(i);
                                    if(protocol[i] instanceof Cleanable)
                                            ((Cleanable)protocol[i]).onKill();
                            }
                            break;
                    case DOWN:
                            failstate = DOWN;
                            break;
                    default:
                            throw new IllegalArgumentException(
                                    "failState="+failState);
            }
    }

    // -----------------------------------------------------------------

    
}
