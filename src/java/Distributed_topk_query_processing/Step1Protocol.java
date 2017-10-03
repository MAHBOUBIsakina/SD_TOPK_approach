/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distributed_approach;

//import distributed_top_k.step1.*;
//import distributed_top_k_final_version.core.DataNode;
//import distributed_top_k.core.MasterDataNode;
//import distributed_top_k.core.Message1;
//import distributed_top_k.core.Message2;
//import distributed_top_k.core.Message2Value;
//import distributed_top_k.core.SimulationController;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

/**
 *
 * @author sakina
 */
public class Step1Protocol implements EDProtocol{
    
    
    public Step1Protocol(String prefix){   
    }
    
    public Object clone(){
        return new Step1Protocol("") ;
    }
    
    @Override
//    public void processEvent(Node node, int pid, Object event) {
//        if (event instanceof Message1) {
//            System.out.println(" -------------------Message1-----------------");
//            Message1 msg = (Message1) event;
//        
//            MasterDataNode master = (MasterDataNode) Network.get(0);
//        
//            if(msg.getSender() != null) {
//                System.out.println(" +++++++++++++++++++++++++++++++++++");
//                //if(msg.getValue() instanceof Message2Value[]) { // The message contains elements so it is from a node to the master
//                    if(!(node instanceof MasterDataNode)) return; // impossible to communicate the data to other than the master
//                    System.out.println("///////////////////////////////////////");
//                    // Add returned data to the datacollection of the master
//                    master.put((Message2Value[])msg.getValue());
//                
//                    // Print Data sent by nodes
//                    master.printdataCollection();
//                
//                
//                
////                System.out.println("=== Dumping content of master ===");
////                Iterator<Object> itCollected = master.getCollectedData().iterator();
////                while(itCollected.hasNext()) {
////                    System.out.println(itCollected.next());
////                }
//                    System.out.println("================================");
//                
//                    if(master.getNbrResponses() == Network.size()) { // Everyone responded
//                        // Process data
//                        System.out.println("Everyone responded! process data and start Step2 ");
//                    
//                    
//                    
//                    
//                    //this.startStep2(master);
//                        SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
//                        controller.startStep2();
//                    }
//                } else { // the message contains K (so its from the master to all nodes
//                    Message2 reply = new Message2(((DataNode)node).getKElements(), node);
//                
//                    ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
//                    System.out.println("Data sent from " + node.getIndex() + " to " + master.getIndex());
//                }
//            }
//        
//        }
//        else{
//            if (event instanceof Message2) {
//                System.out.println("-------------------Message2-----------------");
//            }
//        }
    
        public void processEvent(Node node, int pid, Object event) {
            
            
            double TH;
            MasterDataNode master = (MasterDataNode) Network.get(0);
        if (event instanceof Message1) {
            System.err.println("le temps dE RECEPTION DE MSG EST " + CommonState.getTime());
            //System.out.println(" -------------------Message1-----------------");
            Message1 msg = (Message1) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                Message2Value [] result = ((DataNode)node).getKElements();
                System.out.println("'''''''''''''''''''''''''''''''''''''''''''''''''result "+result.length);
                double end = System.currentTimeMillis();
                
                Message2 reply = new Message2(result, node, (end-begin));
                
                
                //reply.printMessage2();
                
                
                System.err.println("le temps écoulé pour calculer le résultat est " + CommonState.getTime());
                
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
                System.out.println("-----------------latency between "+ node.getIndex()+" and "+ master.getIndex()+ " is "+((Transport)node.getProtocol(FastConfig.getTransport(pid))).getLatency(node, master));
                
                System.err.println("le temps écoulé pour répondre au 1ier message est " + CommonState.getTime());
                System.out.println("Data sent from " + node.getIndex() + " to " + master.getIndex());
                
            }             
        }
        else{
            if (event instanceof Message2) {
                //System.out.println(" -------------------Message2-----------------");
                if(!(node instanceof MasterDataNode)) return;
                Message2 msg = (Message2) event;
                System.out.println("le temps de calcul pour le noeud "+ msg.getSender().getIndex()+ " est "+ msg.getCalcul_time());
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                
                System.err.println("le temps écoulé pour que le master recoit le resultat est " + CommonState.getTime());
                double begin = System.currentTimeMillis();
                master.putStep1Result((Message2Value[])msg.getValue(),msg.getSender().getIndex());
                
                //master.printdataCollection();
                if(master.getNbrResponses() == Network.size()) { // Everyone responded
                    // Process data
                    

                    TH=master.THcalcul();
                    double end = System.currentTimeMillis();
                    System.out.println("le temps maximal pris par le master est "+ master.getMax_time());
                    System.out.println("le temps de la 1ere étape chez le serveur est "+(end-begin) );
                    master.setProcess_time(master.getProcess_time()+(end-begin));
                    System.out.println("Everyone responded! process data and start Step2 ");
                    
                    
                    //this.startStep2(master);
                    
                        SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                        
                        controller.startStep2(TH,master.getProcess_time()+master.getMax_time());
                        //System.err.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                        master.setMax_time(0);
                        master.setProcess_time(0);
                }
            }
        }            
                    
        }           
                    
                    

        
        
}
