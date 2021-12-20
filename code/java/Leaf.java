import java.security.NoSuchAlgorithmException;

// The Leaf class holds the address and balance data, the hash of address concatenated with balance,
// and the index of its children nodes if it has any
public class Leaf{
  // address string from the data
  String address;
  // balance integer from the data
  int balance;
  // The hash of the address and balance concatenated together
  String hash;
  // The index of the left child Leaf
  int left;
  // The index of the right child Leaf
  int right;

// Constructor that takes an address and balance, then calculates the hash
  public Leaf(String address, int balance){
    this.address = address;
    this.balance = balance;
    this.left = -1;
    this.right = -1;
    String prehash = address+balance;

    // Calculate the hash of the prehash string
    try{
      this.hash = SHA256.toHexString(SHA256.getSHA(prehash));
    }
    catch (NoSuchAlgorithmException e) {
      System.out.println("Exception thrown for incorrect algorithm: " + e);
    }
  }

// Constructor that takes a string for the hash, then calculates the hash
  public Leaf(String prehash){
    this.address = "NA";
    this.balance = -1;
    this.left = -1;
    this.right = -1;

    // Calculate the hash of the prehash string
    try{
      this.hash = SHA256.toHexString(SHA256.getSHA(prehash));
    }
    catch (NoSuchAlgorithmException e) {
      System.out.println("Exception thrown for incorrect algorithm: " + e);
    }
  }

// Constructor for an empty leaf
  public Leaf(){
    this.address = "NA";
    this.balance = -1;
    this.left = -1;
    this.right = -1;
    this.hash = "";
  }

}
