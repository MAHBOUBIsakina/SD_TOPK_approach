
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


public class DataNode implements Node{
    static private int pointer = 0;
    static private int nMasters = 0;
    
    static public int NBR_MASTERS;
    
    
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
        
        return result;
    }
    
    
    public void setData(List_EncryptedItems [] list){
        this.datalist = new LinkedHashMap<String,EncryptedItem>();
        
        
        for(int i = 0; i < list.length; i++) {
            EncryptedItem temp = new EncryptedItem();
            temp.min_bound = list[i].min_bound;
            temp.max_bound = list[i].max_bound;
            temp.score = new byte[list[i].score.length];
            System.arraycopy(list[i].score, 0, temp.score, 0, list[i].score.length);
            this.datalist.put(list[i].dataID, temp);
            }      
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
        
        return items_to_return;
    }
    
    
    public ArrayList getDataHighThanTH(double th){
        ArrayList items_to_return = new ArrayList();
        
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
        
        return items_to_return;
        
    }
    
    public Message5Value []getTop_kCondidateScores (Set candidate){
        Message5Value [] result = new Message5Value[candidate.size()];
        Iterator it = candidate.iterator();
        int l=0;
        while (it.hasNext()) {
            
            String next = (String) it.next();
               if (datalist.get(next) != null) {
                    result[l] = new Message5Value();
                    result[l].dataID = next;
                    int leng = datalist.get(next).score.length;
                    result[l].score = new byte[leng];
                    
                    for (int j = 0; j < leng; j++) {
                        result[l].score[j] = datalist.get(next).score[j];
                    }
              }else{
                    System.out.println(" the data item doesn'i eist in the list");
                }
            l++;
        }
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
