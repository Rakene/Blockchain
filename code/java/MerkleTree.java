import java.lang.Math;
import java.util.ArrayList;

// This class represents the Merkle Tree with all nodes held in an ArrayList
public class MerkleTree{
  // This is the arraylist of all nodes.  The nodes are Leaf objects
  ArrayList<Leaf> nodes;

// Constructor of the Merkle Tree
  public MerkleTree(ArrayList<Leaf> nodes){
    this.nodes = nodes;
  }
// Return Merkle Tree Root hash 
  public String treeRoot(ArrayList<Leaf> nodes){
    return nodes.get(nodes.size()-1).hash ;
  }
// This function fills the arraylist with extra empty leaves to make sure the Merkle Tree is a full binary tree
  public void fill(){
    int length = this.nodes.size();
    int height = (int) Math.ceil((Math.log(length) / Math.log(2)));
    int total = ((int) Math.pow(2, height));
    int dif = total - length;
    // Loops through to add the correct number of empty leaves to arraylist
    for(int i = dif-1; i >= 0; i--){
      Leaf in = new Leaf();
      this.nodes.add(length-i, in);
      length = this.nodes.size();
    }
  }

// This function adds all the necessary nodes to the arraylist and returns the hash of the root of the Merkle Tree
  public String build(){
    // Calls Fill to makes sure for a full bottom level of a binary tree
    fill();
    // The index of the next child node
    int count = 0;
    // The length of the arraylist
    int length = this.nodes.size();
    // How many levels of a binary tree are needed
    int height = (int) Math.ceil((Math.log(length) / Math.log(2)));

    // Nested For loops to step through each level and each node
    for(int i = 1; i <= height; i++){
      for(int j = 0; j < ((int) Math.pow(2, height-i)); j++){
        // Gets hash of children nodes and concatenates them
        String h1 = this.nodes.get(count).hash;
        String h2 = this.nodes.get(count+1).hash;
        String prehash = h1 + h2;
        // Creates new Leaf
        Leaf in = new Leaf(prehash);
        // Sets that Leaf's children
        in.left = count;
        in.right = count+1;
        // Adds that Leaf to the arraylist
        this.nodes.add(in);
        // Increment count
        count = count + 2;
      }
    }
    // Gets the length of the arraylist after adding all the nodes
    int final_length = this.nodes.size();
    // Gets the hash of the root node
    String ret = this.nodes.get(final_length-1).hash;
    // Returns the hash of the root node
    return ret;
  }

// This function is a simple way to visualize the tree.  It prints out the node's addresses in a structured way
  public void printTree(){
    // number of nodes on the current level
    int count = 0;
    // number of nodes until a new level is needed
    int boundary = 1;
    // length of the total arraylist
    int length = this.nodes.size();
    // Loops through arraylist indexes from largest to smallest
    for(int i = length-1; i>=0; i--){
      //System.out.print(this.nodes.get(i).left + " : " + this.nodes.get(i).right + "   |   ");
      System.out.print(this.nodes.get(i).address + "   |   ");
      count = count + 1;
      // Checks if the level of the tree is full of nodes
      if (count == boundary){
        System.out.println(" ");
        System.out.println(" ");
        count = 0;
        boundary = 2*boundary;
      }
    }
  }
}
