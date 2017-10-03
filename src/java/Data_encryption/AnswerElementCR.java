package data_encryption;




public class AnswerElementCR {
    
    public String dataID;
    public CryptedScore [] local_scores;
    public AnswerElementCR(int numc){
        local_scores=new CryptedScore[numc];
    }
 
    
}
