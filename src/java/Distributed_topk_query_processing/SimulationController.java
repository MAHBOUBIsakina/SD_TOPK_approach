/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

//import data_encryption.DataEncryption;
//import static data_encryption.DataEncryption.ieme_nbr_premier;
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

/**
 *
 * @author sakina
 */
public class SimulationController implements Control{
    //--------------------------------------------------------------------------
    //Parameters
    //--------------------------------------------------------------------------
    private static int DELAY = 50;
    /**
     * The protocol for Step 1.
     * @config
     */
    private static final String PAR_PROT_STEP1 = "step1Prtcl";

    /**
     * The protocol for Step 2.
     * @config
     */
    private static final String PAR_PROT_STEP2 = "step2Prtcl";

    /**
     * The protocol for Step 3.
     * @config
     */
    private static final String PAR_PROT_STEP3 = "step3Prtcl";


    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /** The name of this observer in the configuration */
    private final String name;

    /** Step 1 Protocol id */
    private final int step1pid;

    /** Step 2 Protocol id */
    private final int step2pid;

    /** Step 3 Protocol id */
    private final int step3pid;
    
    //node number
    
    int node_nbr;
    
    
    //secret key
    
    static MyBlowfish bf;
    static double simulation_time = 0;
    //byte[] secretkey;
    byte[] key_xor;


    //--------------------------------------------------------------------------
    // Constructor
    //--------------------------------------------------------------------------

    /**
     * Standard constructor that reads the configuration parameters.
     * Invoked by the simulation engine.
     * @param name the configuration prefix for this class
     */
    public SimulationController(String name) {
        this.name = name;
        this.step1pid = Configuration.getPid(name + "." + PAR_PROT_STEP1);
        this.step2pid = Configuration.getPid(name + "." + PAR_PROT_STEP2);
        this.step3pid = Configuration.getPid(name + "." + PAR_PROT_STEP3);
        
        //System.out.println(" SimulationController constructor le bf est "+bf);
        
        //this.secretkey = bf.getSecretKeyInBytes();
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


    //--------------------------------------------------------------------------
    // Methods
    //--------------------------------------------------------------------------

    /**
    * Print statistics over a vector. The vector is defined by a protocol,
    * specified by {@value #PAR_PROT}, that has to  implement
    * {@link SingleValue}.
    * Statistics printed are: min, max, number of samples, average, variance,
    * number of minimal instances, number of maximal instances (using 
    * {@link IncrementalStats#toString}).
    * @return true if the standard deviation is below the value of
     * {@value #PAR_ACCURACY}, and the time of the simulation is larger then zero
     * (i.e. it has started).
     */
    public boolean execute() {
        long  x = Runtime.getRuntime().freeMemory();
        
        System.out.println("===========================SimulationController controller start encryption step =====================");
        
        int max_values_score=1000;
        double a= 865;//Math.random()*1000;
        System.out.println("    "+a);
        int e=ieme_nbr_premier(1000);
        System.out.println("    "+e);
        int Bucket_size=Configuration.getInt("BUCKET_SIZE");//il faut que numR soit un multiple de size
        int K=Configuration.getInt("K");
        int numC=Configuration.getInt("SIZE"),numR=Configuration.getInt("NBR_ELEMENTS");
        FirstProposition fp;
        try {
            fp = new FirstProposition(numC,numR,max_values_score,Bucket_size);
            System.out.println("creation de la base est terminée");
            fp.db.sortLists();
            //fp.db.printDatabase();
            System.out.println("sort fini");
            fp.encode_using_xor(key_xor);        
            System.out.println("chiffrement XOR FINI");
            bf = new MyBlowfish();
            bf.generateKey();
            fp.encode_using_Blowfish(this.getBf());
            System.out.println("CHIFFREMENT bLOWFISH fini");
            fp.baquetization_equal_packets(Bucket_size,a,e);
            //fp.db1.printDatabase();
            //fp.db = null;
            System.out.println("baquetization fini");
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
                    //System.out.println("id = "+ list[i].dataID + " minscore = "+ list[i].min_bound );
                }
                System.out.println();
            ((DataNode) Network.get(j)).setData(list);
            
            
            System.out.println("plaintext ID length === "+ (fp.db.lists[1].elements[199].dataID).length());
            System.out.println("encrypted ID length === "+ (fp.db1.listsCR[1].elementsCR[199].dataID).length());
            System.out.println("encrypted score length === "+ (fp.db1.listsCR[1].elementsCR[1].score).length);
        }
        System.out.println("===========================SimulationController controller end encryption step =====================");
        } catch (IOException ex) {
             
         }
        
        
        
        System.out.println("========================= Step 1 Starting ====================");

        //int delay = CommonState.r.nextInt(5000);
        double begin = System.currentTimeMillis();
        Node master = Network.get(0); //Network.prototype;
        
        Message1 msg = new Message1(K,master);
        for (int i = 0; i < Network.size(); i++) {
            

            Node n = Network.get(i);
           // System.err.println("master 0 send message to  "+n.getIndex()+" at " + CommonState.getTime());
            EDSimulator.add(DELAY, msg, n, this.step1pid);
            //System.err.println("le temps aprés l'envoi du message est " + CommonState.getTime());

            //System.out.println(master.getIndex() + " send message to " + n.getIndex() + " Start Step 1 with K = " + K);
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 1= " + simulation_time);
        simulation_time = simulation_time + (end - begin);
        System.err.println("simulation time after starting step 1= " + simulation_time);
        
       
        /* Terminate if accuracy target is reached */
        return (false);
    }

    public void startStep2(double threshold, double time) {
        System.err.println("le temps de la première etape = "+ time);
        System.out.println("========================= Step 2 Starting ====================");
        double begin = System.currentTimeMillis();
        MasterDataNode master = (MasterDataNode)Network.get(0);
        
        
        double th = threshold;
         Message1 msg = new Message1(th,master);
        for (int i = 0; i < Network.size(); i++) {
           

            Node n = Network.get(i);
            
            EDSimulator.add(DELAY, msg, n, this.step2pid);
           
            //System.err.println("le temps écoulé pour envoyer le 2 ème message " + CommonState.getTime());
            
            //System.out.println(master.getIndex() + " send message to " + n.getIndex() + " Start Step 2 with th = " + th);
            
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 2 = " + simulation_time);
        simulation_time = simulation_time + time +(end - begin);
        System.err.println("simulation time after starting step 2 = " + simulation_time);
    }
    
    public void startStep3(Set condidate, double time) {
        System.err.println("le temps de la deuxième etape = "+ time);
        System.out.println("========================= Step 3 Starting ====================");
        MasterDataNode master = (MasterDataNode)Network.get(0);
        
        
        double begin = System.currentTimeMillis();
        Set top_k_condidate = condidate;
        Message4 msg = new Message4(top_k_condidate,master);
        for (int i = 0; i < Network.size(); i++) {
            

            Node n = Network.get(i);
            EDSimulator.add(DELAY, msg, n, this.step3pid);
            
            
            
            //System.out.println(master.getIndex() + " send message to " + n.getIndex());
                    //+ " Start Step 3 with top_k_condidate = " + top_k_condidate);
        }
        double end = System.currentTimeMillis();
        System.err.println("simulation time before starting step 3= " + simulation_time);
        simulation_time = simulation_time + time +(end - begin);
        System.err.println("simulation time after starting step 3= " + simulation_time);
            
    }
    
    
    public ListElement[] startLastStep(HashMap<String,CryptedScore[]> candidate, double time) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        System.err.println("le temps de la troisième etape = "+ time);
        System.out.println("========================= Last Step Starting ====================");
        simulation_time = simulation_time + time;
        System.err.println("le temps de simulation avant le dechiffrement et l'extraction des k elements est "+ simulation_time);
        double begin = System.currentTimeMillis();
        
        int k = Configuration.getInt("K");
        ListElement [] final_result = new ListElement [k];
        for (int i = 0; i < k; i++) {
            final_result[i]=new ListElement();
            final_result[i].dataID="";
            final_result[i].score=0;
        }
        System.out.println("the number of elements to decrypt "+candidate.size());
        HashMap<String,double[]> result = new HashMap<String,double[]>();
        node_nbr = Configuration.getInt("SIZE");
        double global_score;
        double min;
        int index_min;
        double min_glb_scr=0;
        Set listKeys=candidate.keySet();  // Obtenir la liste des clés
        Iterator it=listKeys.iterator();
        // Parcourir les clés et afficher les entrées de chaque clé;
        int i=0;
        while(it.hasNext())
            {
            String key= (String)it.next();
            int spos = 0;
            byte [] ciphertext=(new sun.misc.BASE64Decoder().decodeBuffer(key));
            //System.out.println("l'element chiffré est "+answers_client.elementAt(i).dataID);
            byte [] ciphertext1=new byte [ciphertext.length];
            for (int pos = 0; pos < ciphertext.length; ++pos) {
                //System.out.println(" key_xor = ="+key_xor);
                ciphertext1[pos] = (byte) (ciphertext[pos] ^ key_xor[spos]);
                ++spos;

                if (spos >= key_xor.length) {
                    spos = 0;
                }
            }
            String dataID=new String(ciphertext1, "UTF-8");
            result.put(dataID, new double[node_nbr]);
            //System.out.println(" identifiant " + key + "is crypted to " + dataID);
            for(int l=0;l<candidate.get(key).length;l++){
                //System.out.println("---------------bf = "+this.getBf()+"the score is "+
                    //(candidate.get(key)[k].score.length));

            result.get(dataID)[l]= this.bf.decryptInDouble(candidate.get(key)[l].score);
        }  

                //System.out.println ("data item " + dataID + " have the scores " + Arrays.toString(result.get(dataID)));
        }

                
        listKeys=result.keySet();  // Obtenir la liste des clés
    	Iterator it2=listKeys.iterator();
    		// Parcourir les clés et afficher les entrées de chaque clé;
                while(it2.hasNext())
    		{
                    String key= (String)it2.next();
                    //System.out.println("hhhhhhhhhhhh "+Arrays.toString(result.get(key)));
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
                System.out.println ("the k top-k elements are  " );
                for (int j = 0; j < final_result.length; j++) {
                    System.out.println ("  "+final_result[j].dataID+ "     "+final_result[j].score);
                }
                
               
               
                simulation_time = simulation_time + time + (end - begin);
                System.out.println("le temps de simulation final est "+ simulation_time);
        return final_result;
    }
    
   private double computeOverallScore (double [] localScores)
  {
    
   double overallScore =0;
//    for (int k=0; k<numColumns; ++k)
//      overallScore = overallScore + Math.pow(2,(k+1))*localScores [k];
//
//    return overallScore;
//    for (int k=0; k<numColumns; ++k)
//      overallScore = overallScore +(k+1)*localScores[k];
//
//    return overallScore;
 for (int k=0; k<localScores.length; ++k)
      overallScore = overallScore + localScores[k];

    return overallScore;

  }
   
   
//   private double computeOverallScore (double [] localScores){
//        System.out.println("//////////////localScores[0] = "+localScores[0]+"localScores[1] = "+localScores[1]+"localScores[2] = "+localScores[2]
//        +"localScores[3] = "+localScores[0]*3600+"localScores[4] = "+localScores[4]*60);
//        double overallScore =0;
//        overallScore = localScores[0] + localScores[1] + localScores[2] + localScores[3]*3600 + localScores[4]*60 ;//+ localScores[5];
////        for (int k=0; k<localScores.length; ++k)
////            overallScore = overallScore + localScores[k];
//
//        return overallScore;    
//
//    }
//    
  
    
    
    
}
