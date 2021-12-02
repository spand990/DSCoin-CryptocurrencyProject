package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public  void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    tr_count = newBlock.trarray.length;
    String start = "";
    if(lastBlock == null){
      start = this.start_string;
      newBlock.previous = null;
    }
    else{
      start = lastBlock.dgst;
      newBlock.previous = lastBlock;   
    }
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
    this.lastBlock = newBlock; 
  }
}
