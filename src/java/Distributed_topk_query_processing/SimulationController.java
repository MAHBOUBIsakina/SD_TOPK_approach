
package distributed_approach;

import data_encryption.FirstProposition;
import data_encryption.ListElement;
import data_encryption.MyBlowfish;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;


public class SimulationController implements Control{
   
    private static int DELAY = 50;
    
    private static final String PAR_PROT_STEP1 = "step1Prtcl";

    
    private static final String PAR_PROT_STEP2 = "step2Prtcl";

   
    private static final String PAR_PROT_STEP3 = "step3Prtcl";


    private final String name;

    
    private final int step1pid;

    
    private final int step2pid;

    
    private final int step3pid;
    
    
    int node_nbr;
    
    static MyBlowfish bf;
    static double simulation_time = 0;
    byte[] key_xor;


    public SimulationController(String name) {
        this.name = name;
        this.step1pid = Configuration.getPid(name + "." + PAR_PROT_STEP1);
        this.step2pid = Configuration.getPid(name + "." + PAR_PROT_STEP2);
        this.step3pid = Configuration.getPid(name + "." + PAR_PROT_STEP3);
        
        String key_string="huyfhksdlkopijuhygtfreazdfdgoizeuydfuyifkiudf";
        this.key_xor=key_string.getBytes();
        
    }
    
    public MyBlowfish getBf (){
       
        return bf;
    }
    
    
    
    public int ieme_nbr_premier(int max){
        int divis, nbr, compt = 1 ;
         boolean Est_premier;
         
         for( nbr = 3; compt < max; nbr += 2 )
         { Est_premier = true;
           for (divis = 2; divis<= nbr/2; divis++ )
             if ( nbr % divis == 0 )
             { Est_premier = false;
                break;
             }
           if (Est_premier)
           {
               compt++;
               
           }
         }
         return nbr;
    }


    public boolean execute() {
        long  x = Runtime.getRuntime().freeMemory();
        
        System.out.println("===========================SimulationController controller starts encryption step =====================");
        
        int max_values_score=1000;
        double a= 865;
        System.out.println("    "+a);
        int e=ieme_nbr_premier(1000);
        System.out.println("    "+e);
        int Bucket_size=Configuration.getInt("BUCKET_SIZE");//it is necessary that numR be a multiple of size
        int K=Configuration.getInt("K");
        int numC=Configuration.getInt("SIZE"),numR=Configuration.getInt("NBR_ELEMENTS");
        FirstProposition fp;
        try {
            fp = new FirstProposition(numC,numR,max_values_score,Bucket_size);
            System.out.println("Database creation is done");
            fp.db.sortLists();
            System.out.println("Database sort is done ");
            fp.encode_using_xor(key_xor);        
            bf = new MyBlowfish();
            bf.generateKey();
            fp.encode_using_Blowfish(this.getBf());
            System.out.println("Database encryption is done");
            fp.baquetization_equal_packets(Bucket_size,a,e);
            System.out.println("Bucketization is done");
            List_EncryptedItems temp = null;
            for (int j = 0; j < fp.db1.m; j++) {
                List_EncryptedItems [] list = new List_EncryptedItems[fp.db1.n];
                for (int i = 0; i < fp.db1.n; i++) {
                    temp = new List_EncryptedItems();
                    temp.dataID = fp.db1.listsCR[j].elementsCR[i].dataID;
                    temp.score = new byte[fp.db1.listsCR[j].elementsCR[i].score.length];
                    System.arraycopy(fp.db1.listsCR[j].elementsCR[i].score, 0, temp.score, 0, fp.db1.listsCR[j].elementsCR[i].score.length); //h++;
                    temp.min_bound = fp.db1.listsCR[j].elementsCR[i].PID;
                    temp.max_bound = fp.score_sup.get(fp.db1.listsCR[j].elementsCR[i].dataID)[j];
                    list[i] = temp;
                    }
                System.out.println();
            ((DataNode) Network.get(j)).setData(list);
        }
        System.out.println("===========================SimulationController controller ends encryption step =====================");
        } catch (IOException ex) {
             
         }
        
        
        
        System.out.println("========================= Step 1 Starting ====================");

        double begin = System.currentTimeMillis();
        Node master = Network.get(0);
        
        Message1 msg = new Message1(K,master);
        for (int i = 0; i < Network.size(); i++) {
           Node n = Network.get(i);
            EDSimulator.add(DELAY, msg, n, this.step1pid);
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 1= " + simulation_time);
        simulation_time = simulation_time + (end - begin);
        System.err.println("simulation time after starting step 1= " + simulation_time);
        
       
        return (false);
    }

    public void startStep2(double threshold, double time) {
        System.out.println("========================= Step 2 Starting ====================");
        double begin = System.currentTimeMillis();
        MasterDataNode master = (MasterDataNode)Network.get(0);
        double th = threshold;
        Message1 msg = new Message1(th,master);
        for (int i = 0; i < Network.size(); i++) {
           Node n = Network.get(i);
           EDSimulator.add(DELAY, msg, n, this.step2pid);
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 2 = " + simulation_time);
        simulation_time = simulation_time + time +(end - begin);
        System.err.println("simulation time after starting step 2 = " + simulation_time);
    }
    
    public void startStep3(Set condidate, double time) {
        System.out.println("========================= Step 3 Starting ====================");
        MasterDataNode master = (MasterDataNode)Network.get(0);
        double begin = System.currentTimeMillis();
        Set top_k_condidate = condidate;
        Message4 msg = new Message4(top_k_condidate,master);
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i);
            EDSimulator.add(DELAY, msg, n, this.step3pid);
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 3= " + simulation_time);
        simulation_time = simulation_time + time +(end - begin);
        System.err.println("simulation time after starting step 3= " + simulation_time);
            
    }
    
    
    public ListElement[] startLastStep(HashMap<String,CryptedScore[]> candidate, double time) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        System.out.println("========================= Last Step Starting ====================");
        simulation_time = simulation_time + time;
        System.err.println("simulation time before decrypting data items = "+ simulation_time);
        double begin = System.currentTimeMillis();
        int k = Configuration.getInt("K");
        ListElement [] final_result = new ListElement [k];
        for (int i = 0; i < k; i++) {
            final_result[i]=new ListElement();
            final_result[i].dataID="";
            final_result[i].score=0;
        }
        System.out.println("the number of data items to decrypt = "+candidate.size());
        HashMap<String,double[]> result = new HashMap<String,double[]>();
        node_nbr = Configuration.getInt("SIZE");
        double global_score;
        double min;
        int index_min;
        double min_glb_scr=0;
        Set listKeys=candidate.keySet();  
        Iterator it=listKeys.iterator();
        int i=0;
        while(it.hasNext())
            {
            String key= (String)it.next();
            int spos = 0;
            byte [] ciphertext=(new sun.misc.BASE64Decoder().decodeBuffer(key));
            byte [] ciphertext1=new byte [ciphertext.length];
            for (int pos = 0; pos < ciphertext.length; ++pos) {
                ciphertext1[pos] = (byte) (ciphertext[pos] ^ key_xor[spos]);
                ++spos;
                if (spos >= key_xor.length) {
                    spos = 0;
                }
            }
            String dataID=new String(ciphertext1, "UTF-8");
            result.put(dataID, new double[node_nbr]);
            for(int l=0;l<candidate.get(key).length;l++){
               result.get(dataID)[l]= this.bf.decryptInDouble(candidate.get(key)[l].score);
        }  

        }

                
        listKeys=result.keySet();  
    	Iterator it2=listKeys.iterator();
    		    while(it2.hasNext())
    		{
                    String key= (String)it2.next();
                    global_score=computeOverallScore(result.get(key));
                    if (global_score > min_glb_scr){
                        min=final_result[0].score;
                        index_min=0;
                        for (int j = 0; j < k; j++) {
                            if(final_result[j].score<min){
                                min = final_result[j].score;
                                index_min=j;
                            }
                        }
                        if (global_score > min){
                            final_result[index_min].score=global_score;
                            final_result[index_min].dataID=key;
                        }
                        min=final_result[0].score;
                        for (int j = 0; j < k; j++) {
                            if(final_result[j].score<min){
                                min = final_result[j].score;
                            }
                        }
                        min_glb_scr = min;

                    }
                }
                double end = System.currentTimeMillis();
                System.out.println ("the k top-k queried data items are  " );
                for (int j = 0; j < final_result.length; j++) {
                    System.out.println ("  "+final_result[j].dataID+ "     "+final_result[j].score);
                }
                simulation_time = simulation_time + time + (end - begin);
                System.out.println("the final simulation time is "+ simulation_time);
        return final_result;
    }
    
   private double computeOverallScore (double [] localScores){
        double overallScore =0;
        for (int k=0; k<localScores.length; ++k)
        overallScore = overallScore + localScores[k];

        return overallScore;

  }
    
}
