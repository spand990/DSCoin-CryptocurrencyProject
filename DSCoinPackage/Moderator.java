package DSCoinPackage;
import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    int tr_count = DSObj.bChain.tr_count;
    int total_members = DSObj.memberlist.length;
    int i = 100000;
    Members mod = new Members();
    mod.UID = "Moderator";
    for(int k = 0; k < coinCount ; k++){
      Transaction t = new Transaction();
      t.coinID = String.valueOf(i);
      i++; 
      t.Source = mod;
      t.Destination = DSObj.memberlist[k%total_members];
      t.coinsrc_block = null;
      DSObj.pendingTransactions.AddTransactions(t); 
    }   
    DSObj.latestCoinID = String.valueOf(i-1);  
    while(DSObj.pendingTransactions.size() >= tr_count){
      Transaction[] transactions_array = new Transaction[tr_count];
      for(int count = 0 ; count < tr_count ; count ++){
        try {
          Transaction t = DSObj.pendingTransactions.RemoveTransaction();
          transactions_array[count] = t;
        } catch (Exception e) {
          System.out.println(e);
        }  
      }
      TransactionBlock tB = new TransactionBlock(transactions_array);
      int l = tB.trarray.length;    
      for(int k1 = 0; k1 < l ; k1++){
        int ind = 0;
        for (int j = 0 ; j < DSObj.memberlist.length ; j++){
          if(DSObj.memberlist[j].UID.equals(tB.trarray[k1].Destination.UID)){
            ind = j;
          }
        }
        Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(tB.trarray[k1].coinID , tB);
        DSObj.memberlist[ind].mycoins.add(pr);
      }
      DSObj.bChain.InsertBlock_Honest(tB);
    }
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    int tr_count = DSObj.bChain.tr_count;
    int total_members = DSObj.memberlist.length;
    int i = 100000;
    Members mod = new Members();
    mod.UID = "Moderator";
    for(int k = 0; k < coinCount ; k++){
      Transaction t = new Transaction();
      t.coinID = String.valueOf(i);
      i++; 
      t.Source = mod;
      t.Destination = DSObj.memberlist[k%total_members];
      t.coinsrc_block = null;
      DSObj.pendingTransactions.AddTransactions(t); 
    }   
    DSObj.latestCoinID = String.valueOf(i-1);   
    while(DSObj.pendingTransactions.size() >= tr_count){
      Transaction[] transactions_array = new Transaction[tr_count];
      for(int count = 0 ; count < tr_count ; count ++){
        try {
          Transaction t = DSObj.pendingTransactions.RemoveTransaction();
          transactions_array[count] = t;
        } catch (Exception e) {
          System.out.println(e);
        }  
      }
      TransactionBlock tB = new TransactionBlock(transactions_array);
      int l = tB.trarray.length;    
      for(int k1 = 0; k1 < l ; k1++){
        int ind = 0;
        for (int j = 0 ; j < DSObj.memberlist.length ; j++){
          if(DSObj.memberlist[j].UID.equals(tB.trarray[k1].Destination.UID)){
            ind = j;
          }
        }
        Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(tB.trarray[k1].coinID , tB);
        DSObj.memberlist[ind].mycoins.add(pr);
      }
      DSObj.bChain.InsertBlock_Malicious(tB);
    }
  }
}

