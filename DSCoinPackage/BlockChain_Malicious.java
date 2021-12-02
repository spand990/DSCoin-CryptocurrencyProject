package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {

    CRF obj = new CRF(64);
    String start_4 = tB.dgst.substring(0,4);
    String init = "";
    if(tB.previous == null){
      init = start_string;
    }
    else{
      init = tB.previous.dgst;
    }

    String dgst_calculated = obj.Fn(init + "#" + tB.trsummary + "#" + tB.nonce);
    MerkleTree mt = new MerkleTree();
    String correct_summary = mt.Build(tB.trarray);

    for (int i = 0; i < tB.trarray.length ; i++){
      Transaction t = tB.trarray[i];
      if(!tB.checkTransaction(t)){
        return false;
      }
    }
    if(!dgst_calculated.equals(tB.dgst) || !start_4.equals("0000") || !correct_summary.equals(tB.trsummary)){
      return false;
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain () {

    int len = lastBlocksList.length;
    int[] number_valid = new int[len];
    TransactionBlock[] block_ls = new TransactionBlock[len];

    for(int i = 0 ; i < len ; i++){
      number_valid[i] = 0;
    }
    for(int i = 0 ; i < len ; i++){
      block_ls[i] = null;
    }

    for (int i = 0; i < len ; i++){
      TransactionBlock bl = lastBlocksList[i];
      if(bl == null){
        break;
      }
      TransactionBlock last_valid_block = null;
      while(bl !=null){
        if(this.checkTransactionBlock(bl) == true){
          number_valid[i] += 1;
          if(last_valid_block == null){
            last_valid_block = bl;  
          }
        }
        else{
          number_valid[i] = 0;
          last_valid_block = null; 
        }
        bl = bl.previous;
      }
      block_ls[i] = last_valid_block;      
    }

    int maximum = 0;
    for(int i = 0 ; i < len ; i++){
      if(number_valid[i] > number_valid[maximum]){
        maximum = i;
      }
    }
    return block_ls[maximum];
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {

    CRF obj = new CRF(64);
    TransactionBlock last_block = FindLongestValidChain();
    TransactionBlock blocklast = last_block;
    tr_count = newBlock.trarray.length;
    String start = "";
    if(last_block == null){
      start = start_string;
    }else{
      start = last_block.dgst;
    }
    
    newBlock.previous = last_block;    
    int i = 1000000001;
    String chec = "0000";
    newBlock.nonce = String.valueOf(i);
    String dg  = obj.Fn(start.concat("#" + newBlock.trsummary + "#" + newBlock.nonce));
    while( !dg.substring(0,4).equals(chec)){
      i++;
      newBlock.nonce = String.valueOf(i);
      dg  = obj.Fn(start.concat("#" + newBlock.trsummary + "#" + newBlock.nonce ));
    }
    newBlock.dgst = dg; 
    int l = lastBlocksList.length;

    int c = 0;
    int zo = 0;
    while(lastBlocksList[zo]!=null){
      if(lastBlocksList[zo]==blocklast){
        c = 1;
        lastBlocksList[zo]=newBlock;
        break;
      }
      zo++;
    }
    if(c == 0){
      lastBlocksList[zo] = newBlock;
    }
  }

}
