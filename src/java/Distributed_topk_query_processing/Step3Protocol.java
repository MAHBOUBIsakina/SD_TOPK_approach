/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

/**
 *
 * @author sakina
 */
public class Step3Protocol implements EDProtocol{
    public Step3Protocol(String prefix){   
    }
    
    public Object clone(){
        return new Step3Protocol("") ;
    }
    
    public void processEvent(Node node, int pid, Object event) {
        
        
        MasterDataNode master = (MasterDataNode) Network.get(0);
        
        if (event instanceof Message4) {
            //System.out.println("event = Message4");
            Message4 msg = (Message4) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                Message5Value [] result = ((DataNode)node).getTop_kCondidateScores(msg.getValue());
                double end = System.currentTimeMillis();
                Message5 reply = new Message5(result, node, (end-begin));
                
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
                System.out.println("----------------latency between "+ node.getIndex()+" and "+ master.getIndex()+ " is "+((Transport)node.getProtocol(FastConfig.getTransport(pid))).getLatency(node, master));
                
                System.out.println("Data sent from " + node.getIndex() + " to " + master.getIndex());
            }  
        
        }
        else{
            if (event instanceof Message5) {
                //System.out.println("event = Message5");
                if(!(node instanceof MasterDataNode)) return;
                Message5 msg = (Message5) event;
                System.out.println("le temps de calcul pour le noeud "+ msg.getSender().getIndex()+ " est "+ msg.getCalcul_time());
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                double begin = System.currentTimeMillis();
                master.putStep3Result((Message5Value[])msg.getValue());
                double end = System.currentTimeMillis();
                master.setProcess_time(master.getProcess_time()+(end-begin));
                if(master.getNbrResponses() == Network.size()) { // Everyone responded
                    //master.printcandidateCollection();
                    // Process data
                    System.out.println("le temps maximal pris par le master est "+ master.getMax_time());
                    System.out.println("le temps de la 3ere étape chez le serveur est "+(end-begin) );
                    System.out.println("Everyone responded! process data and start Step3 ");
                    //Set topKCondidate = master.getKCondidateSet();
                    
                    
                    
                    //this.startStep2(master);
                        SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                        
                    try {
                        controller.startLastStep(master.getCandidatecollection(), master.getProcess_time()+master.getMax_time());
                        master.setMax_time(0);
                        master.setProcess_time(0);
                    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                    }
                }
            }
            //System.out.println("event = Message5");
        }
        
        
    }
}
