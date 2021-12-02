package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t) {
    int array_len = t.length;
    Transaction[] trarr = new Transaction[array_len];
    for(int i = 0; i < array_len ; i++){
      trarr[i] = t[i];
    }
    this.trarray = trarr;
    this.Tree = new MerkleTree();
    Tree.Build(trarray);
    this.trsummary = Tree.rootnode.val;
    this.previous = null;
    this.dgst = null;
    this.nonce = "1000000001";
  }

  public boolean checkTransaction (Transaction t) {
    if(t.coinsrc_block == null){
      return true;
    }
    TransactionBlock tb = this.previous;
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
