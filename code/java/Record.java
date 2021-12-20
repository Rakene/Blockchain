// The Record class holds the address and balance data
public class Record{
  // address string from the data
  String address;
  // balance integer from the data
  int balance;

// Constructor that takes an address and balance
  public Record(String address, int balance){
    this.address = address;
    this.balance = balance;
  }

// Function that converts a Record object into a Leaf object
  public Leaf RecordtoLeaf(){
    Leaf ret = new Leaf(this.address, this.balance);
    return ret;
  }
}
