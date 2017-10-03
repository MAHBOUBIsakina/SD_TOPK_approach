/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;


//import distributed_top_k.core.DataNode;
//import distributed_top_k.core.MasterDataNode;
//import distributed_top_k.core.Message1;
//import distributed_top_k.core.Message3;
//import distributed_top_k.core.Message3Value;
//import distributed_top_k.core.SimulationController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
/**
 *
 * @author sakina
 */
public class Step2Protocol implements EDProtocol{
    
    
    public Step2Protocol(String prefix){   
    }
    
    public Object clone(){
        return new Step2Protocol("") ;
    }
    
    @Override
    public void processEvent(Node node, int pid, Object event) {
        
        
        MasterDataNode master = (MasterDataNode) Network.get(0);
        
        if (event instanceof Message1) {
            System.err.println("le temps écoulé pour que les neouds recoivent le message de deuxième étape est " + CommonState.getTime());
            //System.out.println("event = Message1");
            Message1 msg = (Message1) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                ArrayList result = ((DataNode)node).getDataHighThanTH(msg.getValue());
                //System.out.println("###############th = "+msg.getValue());
                //System.out.println("''''''''''''''''''''''''''''''''''''number of data that have minovl great than TH="+result.size());
                double end = System.currentTimeMillis();
                Message3 reply = new Message3(result, node, (end-begin));
                
                //reply.printMessage3();
                
                
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
                System.out.println("----------------latency between "+ node.getIndex()+" and "+ master.getIndex()+ " is "+((Transport)node.getProtocol(FastConfig.getTransport(pid))).getLatency(node, master));
                
                System.out.println("Data sent from " + node.getIndex() + " to " + master.getIndex());
            }  
        
        }
        else{
            if (event instanceof Message3) {
                if(!(node instanceof MasterDataNode)) return;
                Message3 msg = (Message3) event;
                System.out.println("le temps de calcul pour le noeud "+ msg.getSender().getIndex()+ " est "+ msg.getCalcul_time());
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                double begin = System.currentTimeMillis();
                master.putStep2Result((ArrayList)msg.getValue(), msg.getSender().getIndex());
                double end = System.currentTimeMillis();
                master.setProcess_time(master.getProcess_time()+(end-begin));
                System.err.println("******************************** le temps de collecter les données est "+ (end-begin));
                //master.printdataCollection();
                if(master.getNbrResponses() == Network.size()) { // Everyone responded
                    // Process data
                    
                    Set topKCondidate;
                    try {
                        double begin1 = System.currentTimeMillis();
                        topKCondidate = master.getKCondidateSet();
                        System.out.println("''''''''''''''''''''''''''''''''''''''''''''''the number of top-k candidate is "+topKCondidate.size());
                        double end1 = System.currentTimeMillis();
                        System.err.println("//////////////////////////////////////////// le temps d'obtention des k condidate est "+ (end1-begin1));
                        master.setProcess_time(master.getProcess_time()+(end1-begin1));
                        System.out.println("le temps maximal pris par le master est "+ master.getMax_time());
                        System.out.println("le temps de la 2ere étape chez le serveur est "+(end1-begin) );
                        System.out.println("Everyone responded! process data and start Step3 ");
                        SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                        
                        controller.startStep3(topKCondidate, master.getProcess_time()+master.getMax_time());
                        master.setMax_time(0);
                        master.setProcess_time(0);
                    } catch (IOException ex) {
                        Logger.getLogger(Step2Protocol.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    
                    //this.startStep2(master);
                    
                    
                    
                    //this.startStep2(master);
                        
                        
                }
            }
            //System.out.println("event = Message3");
        }
        
        
    }
}
