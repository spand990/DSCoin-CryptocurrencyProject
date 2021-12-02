package DSCoinPackage;
import java.util.*;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions =0;
  public ArrayList<Transaction> queue = new ArrayList<Transaction>();

  public void AddTransactions (Transaction transaction) {
    queue.add(transaction);
    int sz = queue.size();
    if(numTransactions == 0){
      firstTransaction = queue.get(0);
    }
    lastTransaction = queue.get(sz-1);
    numTransactions++;  
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    
    if(numTransactions == 0){
      throw new EmptyQueueException();
    }
    else {
      Transaction t = queue.get(0);
      queue.remove(0);
      if(numTransactions == 1){
        firstTransaction = null;
        lastTransaction = null;
      }
      else{
        firstTransaction = queue.get(0);
      }
      numTransactions--;
      return t;
    }   
  }

  public int size() {
    return numTransactions;
  }
}
