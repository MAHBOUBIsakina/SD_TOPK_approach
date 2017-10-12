
package distributed_approach;


import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;


public class Step1Protocol implements EDProtocol{
    
    
    public Step1Protocol(String prefix){   
    }
    
    public Object clone(){
        return new Step1Protocol("") ;
    }
    
    @Override
 
    public void processEvent(Node node, int pid, Object event) {
        double TH;
        MasterDataNode master = (MasterDataNode) Network.get(0);
        if (event instanceof Message1) {
            Message1 msg = (Message1) event;
            if(msg.getSender() != null) {
                double begin = System.currentTimeMillis();
                Message2Value [] result = ((DataNode)node).getKElements();
                double end = System.currentTimeMillis();
                Message2 reply = new Message2(result, node, (end-begin));
                
                ((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, master, reply, pid);
                
            }             
        }
        else{
            if (event instanceof Message2) {
                if(!(node instanceof MasterDataNode)) return;
                Message2 msg = (Message2) event;
                if (msg.getCalcul_time()>master.getMax_time()) {
                    master.setMax_time(msg.getCalcul_time());
                }
                double begin = System.currentTimeMillis();
                master.putStep1Result((Message2Value[])msg.getValue(),msg.getSender().getIndex());
                
                if(master.getNbrResponses() == Network.size()) { 
                    TH=master.THcalcul();
                    double end = System.currentTimeMillis();
                    master.setProcess_time(master.getProcess_time()+(end-begin));
                    System.out.println("Everyone responded! process data and start Step2 ");
                    SimulationController controller = (SimulationController)Configuration.getInstance(Configuration.getNames("control")[0]);
                    controller.startStep2(TH,master.getProcess_time()+master.getMax_time());
                    master.setMax_time(0);
                    master.setProcess_time(0);
                }
            }
        }            
                    
        }           
                    
                    

        
        
}
