
package distributed_approach;


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
            Message1 msg = (Message1) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                ArrayList result = ((DataNode)node).getDataHighThanTH(msg.getValue());
                double end = System.currentTimeMillis();
                Message3 reply = new Message3(result, node, (end-begin));
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
            }  
        
        }
        else{
            if (event instanceof Message3) {
                if(!(node instanceof MasterDataNode)) return;
                Message3 msg = (Message3) event;
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                double begin = System.currentTimeMillis();
                master.putStep2Result((ArrayList)msg.getValue(), msg.getSender().getIndex());
                double end = System.currentTimeMillis();
                master.setProcess_time(master.getProcess_time()+(end-begin));
                if(master.getNbrResponses() == Network.size()) { 
                    Set topKCondidate;
                    try {
                        double begin1 = System.currentTimeMillis();
                        topKCondidate = master.getKCondidateSet();
                        double end1 = System.currentTimeMillis();
                        master.setProcess_time(master.getProcess_time()+(end1-begin1));
                        System.out.println("Everyone responded! process data and start Step3 ");
                        SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                        
                        controller.startStep3(topKCondidate, master.getProcess_time()+master.getMax_time());
                        master.setMax_time(0);
                        master.setProcess_time(0);
                    } catch (IOException ex) {
                        Logger.getLogger(Step2Protocol.class.getName()).log(Level.SEVERE, null, ex);
                    }
   
                }
            }
            
        }
        
        
    }
}
