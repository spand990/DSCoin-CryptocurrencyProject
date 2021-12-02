package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.TreeNode;
import HelperClasses.MerkleTree;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans ;
  public int numsPending=0;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Pair<String , TransactionBlock> pr = mycoins.get(0);
    mycoins.remove(0);

    Members mb = DSobj.memberlist[0];
    for (int m = 0 ; m < DSobj.memberlist.length ; m++){
      if(DSobj.memberlist[m].UID.equals(destUID)){
        mb = DSobj.memberlist[m];
      }
    }
    Transaction tobj = new Transaction();
    tobj.coinID = pr.get_first();
    tobj.coinsrc_block = pr.get_second();
    tobj.Destination = mb;
    tobj.Source = this;
    DSobj.pendingTransactions.AddTransactions(tobj);
    numsPending++; 
    this.in_process_trans[numsPending] = tobj; 
    
  }
/*
  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Pair<String , TransactionBlock> pr = mycoins.get(0);
    mycoins.remove(0);
    Members mb = DSobj.memberlist[0];
    for (int m = 0 ; m < DSobj.memberlist.length ; m++){
      if(DSobj.memberlist[m].UID.equals(destUID)){
        mb = DSobj.memberlist[m];
      }
    }
    Transaction tobj = new Transaction();
    tobj.coinID = pr.get_first();
    tobj.coinsrc_block = pr.get_second();
    tobj.Destination = mb;
    tobj.Source = this;
    DSobj.pendingTransactions.AddTransactions(tobj);
    in_process_trans[numsPending + 1] = tobj; 
    numsPending++;
  }
*/
  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {  
    int condition  = 1;
    TransactionBlock curr_block = DSObj.bChain.lastBlock;
    ArrayList<Pair<String, String>> arr2 = new ArrayList<Pair<String, String>>();
    int index =0;
    while(curr_block != null && condition == 1){
      Pair<String, String> pr = new Pair<String, String>(curr_block.dgst,curr_block.previous.dgst+"#"+curr_block.trsummary+"#"+curr_block.nonce );
      arr2.add(0,pr);
      for(int i = 0; i< curr_block.trarray.length;i++){
        if(curr_block.trarray[i]==tobj ){
          condition = 0;
          index = i;
          break;
        }
        else if(curr_block.trarray[i] != null && tobj !=null){
          if(curr_block.trarray[i].coinID.equals(tobj.coinID) && curr_block.trarray[i].Source == tobj.Source && curr_block.trarray[i].Destination == tobj.Destination && curr_block.trarray[i].coinsrc_block == tobj.coinsrc_block){
            condition = 0;
            index = i;
            break;
          }
        }
      }
      if(condition == 0){
        break;
      }
      curr_block = curr_block.previous;
    }
    if(curr_block == null){
      throw new MissingTransactionException();
    }
    int a =0;
    while(a<tobj.Destination.mycoins.size() && tobj.Destination.mycoins.get(a) !=null   &&  Integer.valueOf(tobj.Destination.mycoins.get(a).get_first())<Integer.valueOf(tobj.coinID)){
      a++;
    }
    tobj.Destination.mycoins.add(a,new Pair<String,TransactionBlock>(tobj.coinID,curr_block));
    arr2.add(0,new Pair<String, String>(curr_block.previous.dgst,null));
		TreeNode curr = curr_block.Tree.rootnode;
    int inv = curr_block.trarray.length;
		int d = (int)((Math.log(inv)/Math.log(2))+1);
		int d0 = d-1;
		for(int i = 0 ; i < d-2 ; i++ ){
			int nodes = (int)(Math.pow(2,d0));
			int half = (int)(nodes / 2);
			int rem  = (index+1) %  nodes;
			if(rem == 0){
				rem = nodes;
			}
			if(rem > half){
				curr = curr.right;
				d0 = d0 -1 ;

			}
			else{
				curr = curr.left;
				d0 = d0 - 1;
			}
		}
    if(index %2 == 0){
      curr = curr.left;
    }
    else{
      curr = curr.right;
    }
    ArrayList<Pair<String, String>> arr1 = new ArrayList<Pair<String, String>>();
    while(curr != null){
			if(curr.parent == null){ 
				Pair<String , String> pr_e= new Pair<String , String>(curr.val , null);
				arr1.add(pr_e);
			}
			else{
				Pair<String , String> pr_e = new Pair<String , String>(curr.parent.left.val , curr.parent.right.val);
				arr1.add(pr_e);
			}
			curr = curr.parent;
		}
    int j =0;
    for(int i =0; i< in_process_trans.length; i++){
      if(tobj == in_process_trans[i]){  //Comparing two transactions here
        continue;
      }
      else if(in_process_trans[i]!=null && tobj != null){
        if(in_process_trans[i].coinID.equals(tobj.coinID) && in_process_trans[i].Source == tobj.Source && in_process_trans[i].Destination == tobj.Destination && in_process_trans[i].coinsrc_block == tobj.coinsrc_block){
          continue;
        }
      }
      in_process_trans[j] = in_process_trans[i];
      if(in_process_trans[i]==null){
        break;
      }
      j++;
    }
    in_process_trans[in_process_trans.length-1]=null;    
    numsPending--;
    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(arr1,arr2);
    
  }

  public void MineCoin(DSCoin_Honest DSObj) {
      try {
        int trc = DSObj.bChain.tr_count;
        int count = 0 ;
        Transaction[] lined_transactions = new Transaction[trc];
        TransactionBlock tB_init = DSObj.bChain.lastBlock;
        while(count < trc-1 ){
          Transaction t = DSObj.pendingTransactions.RemoveTransaction();
          int p = 0;

          while( checkTr(t , tB_init) == false){
            t = DSObj.pendingTransactions.RemoveTransaction();
          }
          for (int i = 0 ; i < count ; i++){
            if(lined_transactions[i].coinID.equals(t.coinID)){
              p = 1;
              break;
            }
          }
          if(p == 0){
            lined_transactions[count] = t;
            count += 1;
          }
        } 
        DSObj.latestCoinID = String.valueOf(Integer.valueOf(DSObj.latestCoinID) + 1);
        Transaction minerRewardTransaction = new Transaction();
        minerRewardTransaction.coinID = DSObj.latestCoinID;
        minerRewardTransaction.Source = null;
        minerRewardTransaction.Destination = this ; 
        minerRewardTransaction.coinsrc_block = null;    
        lined_transactions[trc-1] = minerRewardTransaction;
        TransactionBlock tB_new = new TransactionBlock(lined_transactions);
        DSObj.bChain.InsertBlock_Honest(tB_new);      
        Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(DSObj.latestCoinID , tB_new);
        this.mycoins.add(pr);   
          
      }
      catch (EmptyQueueException e) {
        System.out.println(e);
      }
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    
    try {
      int trc = DSObj.bChain.tr_count;
      int count = 0 ;
      Transaction[] lined_transactions = new Transaction[trc];
      TransactionBlock tB_init = DSObj.bChain.FindLongestValidChain();
      while(count < trc-1 ){
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        int p = 0;
        while(!checkTr(t,tB_init)){
          t = DSObj.pendingTransactions.RemoveTransaction();
        }
        for (int i = 0 ; i < count ; i++){
          if(lined_transactions[i].coinID.equals(t.coinID)){
            p = 1;
            break;
          }
        }
        if(p == 0){
          lined_transactions[count] = t;
          count += 1;
        }
      } 
      DSObj.latestCoinID = String.valueOf(Integer.valueOf(DSObj.latestCoinID) + 1); 
      Transaction minerRewardTransaction = new Transaction();
      minerRewardTransaction.coinID = DSObj.latestCoinID;
      minerRewardTransaction.Source = null;
      minerRewardTransaction.Destination = this ; 
      minerRewardTransaction.coinsrc_block = null;    
      lined_transactions[trc-1] = minerRewardTransaction;
      TransactionBlock tB_new = new TransactionBlock(lined_transactions);
      DSObj.bChain.InsertBlock_Malicious(tB_new);      
      Pair<String, TransactionBlock> pr = new Pair<String, TransactionBlock>(DSObj.latestCoinID , tB_new);
      this.mycoins.add(pr);   
       
    }
    catch (EmptyQueueException e) {
      System.out.println(e);
    }
  } 

  public boolean checkTr(Transaction t , TransactionBlock tBlock){
    if(t.coinsrc_block == null){
      return true;
    }
    TransactionBlock tb = tBlock;
    while(tb != t.coinsrc_block){
      for(int j = 0 ; j< tb.trarray.length ; j++){
        if(t.coinID.equals(tb.trarray[j].coinID) ){
          return false;    
        }
      }
      tb = tb.previous;
    }
    for(int i = 0; i < t.coinsrc_block.trarray.length ; i++){
      if(t.coinID.equals(t.coinsrc_block.trarray[i].coinID)){
        if(t.Source.equals(t.coinsrc_block.trarray[i].Destination)){
          return true;
      }
      return false;
    }
  }
  return false;
  }

}

