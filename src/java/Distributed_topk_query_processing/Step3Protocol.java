
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

public class Step3Protocol implements EDProtocol{
    public Step3Protocol(String prefix){   
    }
    
    public Object clone(){
        return new Step3Protocol("") ;
    }
    
    public void processEvent(Node node, int pid, Object event) {
        
        
        MasterDataNode master = (MasterDataNode) Network.get(0);
        
        if (event instanceof Message4) {
            Message4 msg = (Message4) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                Message5Value [] result = ((DataNode)node).getTop_kCondidateScores(msg.getValue());
                double end = System.currentTimeMillis();
                Message5 reply = new Message5(result, node, (end-begin));
                
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
            }  
        
        }
        else{
            if (event instanceof Message5) {
                if(!(node instanceof MasterDataNode)) return;
                Message5 msg = (Message5) event;
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                double begin = System.currentTimeMillis();
                master.putStep3Result((Message5Value[])msg.getValue());
                double end = System.currentTimeMillis();
                master.setProcess_time(master.getProcess_time()+(end-begin));
                if(master.getNbrResponses() == Network.size()) { 
                    System.out.println("Everyone responded! process data and start Step3 ");
                    SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                        
                    try {
                        controller.startLastStep(master.getCandidatecollection(), master.getProcess_time()+master.getMax_time());
                        master.setMax_time(0);
                        master.setProcess_time(0);
                    } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
                    }
                }
            }
      }
        
        
    }
}
