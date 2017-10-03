package data_encryption;




import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Database {
  public List [] lists;
  public int m;
  public int n;
  public DataItemPositions[] dataPositions;
  private int maxIntRandom = 10000;
  public int ma_val_scr;
  Map<String,int[]> positions_elem=new HashMap<String,int[]>();

  public Database(int in_m, int in_n,int max_values_score) throws FileNotFoundException, IOException
  {
    m = in_m;
    n = in_n;
    lists = new List[m];
    ma_val_scr=max_values_score;
    
    
     int i, j;
    for (i=0; i<m; ++i) {
      lists[i] = new List();
      lists[i].elements = new ListElement[n];
      for (j=0; j<n; ++j)
      {
        lists[i].elements[j] = new ListElement();
      }
    }
    InputStream flux=new FileInputStream("/home/sakina/Desktop/sakina/Desktop/Netbeans Projects/resultat100million1.txt"); 
    InputStreamReader lecture=new InputStreamReader(flux);
    BufferedReader buff=new BufferedReader(lecture);
    String ligne;

    //remplir les listes par des données synthétique   
    for (i=0; i<m; ++i) {
      lists[i] = new List();
      lists[i].elements = new ListElement[n];
      for (j=0; j<n; ++j)
      {
        lists[i].elements[j] = new ListElement();
        lists[i].elements[j].dataID=""+j;
        lists[i].elements[j].score=Double.parseDouble(ligne=buff.readLine());
      }
    }

//remplir la base de données avec des données réelles 
//      for (int k = 0; k < m; k++) {
//        lists[k] = new List();
//        lists[k].elements = new ListElement[n];
//      }
//        
//        String valeur;
//        //lists[i] = new List();
//        //lists[i].elements = new ListElement[n];
//        j=0;
//        for (i=0; i<n; ++i) {
//    
//        valeur=buff.readLine();
//        String[] data=valeur.split(" ");
//        //System.out.println("      "+Arrays.toString(data));
//        
//     // for (j=0; j<n; ++j)
////    //  {
//        lists[0].elements[i] = new ListElement();
//        lists[0].elements[i].dataID=""+j;
//        lists[0].elements[i].score=Double.parseDouble(data[1]);
//        
//        lists[1].elements[i] = new ListElement();
//        lists[1].elements[i].dataID=""+j;
//        lists[1].elements[i].score=Double.parseDouble(data[2]);
//        
//        lists[2].elements[i] = new ListElement();
//        lists[2].elements[i].dataID=""+j;
//        lists[2].elements[i].score=Double.parseDouble(data[3]);
//        
//        lists[3].elements[i] = new ListElement();
//        lists[3].elements[i].dataID=""+j;
//        lists[3].elements[i].score=Double.parseDouble(data[4]);
//       
//        lists[4].elements[i] = new ListElement();
//        lists[4].elements[i].dataID=""+j;
//        lists[4].elements[i].score=Double.parseDouble(data[5]);
//        
////        lists[5].elements[i] = new ListElement();
////        lists[5].elements[i].dataID=""+j;
////        lists[5].elements[i].score=Double.parseDouble(data[5]);
////      //}
//        j++;
//   }
// fin de remplissage


     buff.close(); 

    dataPositions = new DataItemPositions [n];
    for (j=0; j<n; ++j) {
      dataPositions[j] = new DataItemPositions();
      dataPositions[j].dataID = ""+j;
      dataPositions[j].positionsInLists = new int [m];
    }
    
  }
  
 public void sortLists() {
    
     for (int i = 0; i < n; i++) {
         positions_elem.put(lists[0].elements[i].dataID, new int [m]);
     }
     
	 for (int i=0; i<m; ++i) {
            long debut=System.currentTimeMillis();
             if((i==0)||(i==1)){
                 QSort(0,n-1,i); 
             }else{
                 QSort(0,n-1,i); 
                    }
            //tri_entier(i);
            long fin=System.currentTimeMillis();
            System.out.println("liste ["+i+"] est triée dans "+(fin-debut));
         }
 }
 
 
  public int  partition( int  G,  int  D  ,int k){  
      String dataID ;
   int  i, j ,x=0,y=0;
   double piv;
   ListElement temp,piv_score;
    
   piv  =  lists[k].elements[G].score ;
   piv_score=lists[k].elements[G];
   i  =  G + 1 ;
   j  =  D ;
   while (i < j) {
        while(i < j && lists[k].elements[j].score <= piv){ 
            
            if(x==0){
                dataID = ""+lists[k].elements[j].dataID;
                positions_elem.get(dataID)[k]=j;
                x=1;
            }
            j--;
        
        }
        while(i < j && lists[k].elements[i].score >= piv){ 
            
            if(y==0){
                dataID =""+ lists[k].elements[i].dataID;
                positions_elem.get(dataID)[k] =i;
                y=1;
            }
            i++;
        }
        temp = lists[k].elements[i];
        lists[k].elements[i]= lists[k].elements[j];
        lists[k].elements[j] = temp;
        dataID = ""+lists[k].elements[i].dataID;
        positions_elem.get(dataID)[k] =i;
         dataID =""+ lists[k].elements[j].dataID;
        positions_elem.get(dataID)[k] =j;
        
    }
   
   if (lists[k].elements[i].score < piv) i--;
    lists[k].elements[G] = lists[k].elements[i];
     dataID =""+ lists[k].elements[G].dataID;
        positions_elem.get(dataID)[k] =G;
    lists[k].elements[i] = piv_score;
         dataID =""+ lists[k].elements[i].dataID;
        positions_elem.get(dataID)[k] =i;
    return i;
 }
  
  
  
  
  public void  QSort  ( int  G,  int  D,int k  )
 {  
   int  i ;
  if( D > G )
  {
    i  =  partition ( G,D,k);
    QSort ( G,i - 1,k );
    
    QSort ( i + 1,D,k );
    
  }
 }


 public void printDatabase() {
   int i, j;
   double temp;

   String dataID;
   for (j=0; j<n; ++j) {
     System.out.print((j+1) + "  ->  " );
     for (i=0; i<m; ++i){
       temp = Math.round ((lists[i].elements[j].score) * 1000);
       temp = temp / 1000;
       dataID = lists[i].elements[j].dataID;
       System.out.print (dataID + "   :   " + temp + "        ");
     }

     System.out.println();
   }
 }


 public void printPositions() {
   int i, j;
   double temp;
   for (j=0; j<n; ++j) {
     System.out.print (j + " : ");
     for (i=0; i<m; ++i){
       temp = Math.round ((dataPositions[j].positionsInLists[i]) * 1000);
       temp = temp / 1000;
       temp = temp + 1;
       System.out.print(temp + " , ");
     }
     System.out.println();
   }
 }

public void tri_entier (int h){
    tab_tri[] tab= new tab_tri[2011];
    for (int i = 0; i < tab.length; i++) {
        tab[i]=new tab_tri();
    }
    for (int i = 0; i < n; i++) {
        
        tab[(int)lists[h].elements[i].score].value++;
        tab[(int)lists[h].elements[i].score].ids.add(lists[h].elements[i].dataID);
    }
    int c=0;
    for (int i = (tab.length-1); i >=0 ; i--) {
        //System.out.println("bbbbb"+ i);
        for (int j = 0; j < tab[i].value; j++) {
            lists[h].elements[c].score=i;
            lists[h].elements[c].dataID=tab[i].ids.elementAt(j);
            positions_elem.get(lists[h].elements[c].dataID)[h]=c;
            c++;
        }
    }
}
}



