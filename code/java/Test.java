import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

public class Test{
  public static void main(String[] args) throws Exception {

    // User I/O
    Scanner myScanner = new Scanner(System.in);
    // Boolean to mark the last file given by user
    boolean last_file = false;
    // String to hold current file name
    String fileName = "";
    // until the last file is reached prompt user to input more files
    while(!last_file){
      System.out.print("Enter the full path where the test.txt file will be created: ");
      fileName = myScanner.next();
      System.out.print("Test Results are in the Test.txt file\n");
      last_file = true;
    }
    myScanner.close();
    //create new BlockChain 😎 🤓
    BlockChain testBlockChain = new BlockChain();
    //create new test object
    Test test = new Test();
    //create arraylists of Leafs and Records
    ArrayList<Leaf> input = new ArrayList<Leaf>();
    ArrayList<Record> input2 = new ArrayList<Record>();
    //create a bunch of Records as test data
    Record testRecord1 = new Record("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 11111);
    Record testRecord2 = new Record("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", 22222);
    Record testRecord3 = new Record("cccccccccccccccccccccccccccccccccccccccc", 33333);
    Record testRecord4 = new Record("dddddddddddddddddddddddddddddddddddddddd", 44444);
    Record testRecord5 = new Record("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", 55555);
    //Add the test Records to the Leaf and Record arraylists
    input2.add(testRecord1);
    input2.add(testRecord2);
    input2.add(testRecord3);
    input2.add(testRecord4);
    input2.add(testRecord5);

    input.add(testRecord1.RecordtoLeaf());
    input.add(testRecord2.RecordtoLeaf());
    input.add(testRecord3.RecordtoLeaf());
    input.add(testRecord4.RecordtoLeaf());
    input.add(testRecord5.RecordtoLeaf());

    // Initiate the Merkle Tree using the arraylist of Leaves
    MerkleTree tree = new MerkleTree(input);
    //builds the merkle tree and pritns the root hash
    String rootHash = tree.build() ;

    //create Blocks here
    testBlockChain.addBlock(rootHash, input2);
    testBlockChain.addBlock(rootHash, input2);
    testBlockChain.addBlock(rootHash, input2);

    //Tests and writes output to file
    test.writeFile(fileName, testBlockChain, rootHash, input2);

  }

// Empty Constructor
  public Test(){

  };

// Generates a bad Block with an incorrect previousHash
  public void badBlock1(BlockChain blockchain, String roothash, ArrayList<Record> data){
    //Not using correct previousHash
    String previousHash = "random string";
    //generate new block and append it to the blockchain
    Block newBlock = new Block(previousHash, roothash, blockchain.target, data);
    //Checks for correct nonce
    if(blockchain.verifyBlock(newBlock)){
      blockchain.blocks.add(newBlock);
    } else {
      badBlock1(blockchain, roothash, data);
    }

  }

// Generates a bad Block with an incorrect nonce
  public void badBlock2(BlockChain blockchain, String roothash, ArrayList<Record> data){
    //generate previousHash using the previous Block
    Block previousBlock = blockchain.blocks.get(blockchain.blocks.size()-1);
    String previousHash = blockchain.generatePreviousHash(previousBlock);
    //generate new block and append it to the blockchain
    Block newBlock = new Block(previousHash, roothash, blockchain.target, data);
    //Makes sure a bad nonce is used
    if(blockchain.verifyBlock(newBlock)){
      badBlock2(blockchain, roothash, data);
    } else {
      blockchain.blocks.add(newBlock);
    }
  }

// Generates a bad Block with an incorrect rootHash
  public void badBlock3(BlockChain blockchain, String roothash, ArrayList<Record> data){
    //generate previousHash using the previous Block
    Block previousBlock = blockchain.blocks.get(blockchain.blocks.size()-1);
    String previousHash = blockchain.generatePreviousHash(previousBlock);
    //generate new block and append it to the blockchain
    Block newBlock = new Block(previousHash, "Random rootHash", blockchain.target, data);
    //Checks for good nonce
    if(blockchain.verifyBlock(newBlock)){
      blockchain.blocks.add(newBlock);
    } else {
      badBlock3(blockchain, roothash, data);
    }
  }

  // Tests validateBlock and validateBlockchain functions and Writes results to test.txt
  public boolean writeFile(String filename, BlockChain blockchain, String roothash, ArrayList<Record> data){
    //Creating several blockchains to add bad blocks to
    BlockChain testBC1 = new BlockChain(blockchain);
    BlockChain testBC2 = new BlockChain(blockchain);
    BlockChain testBC3 = new BlockChain(blockchain);
    //Adding bad blocks to blockchains
    badBlock1(testBC1, roothash, data);
    badBlock2(testBC2, roothash, data);
    badBlock3(testBC3, roothash, data);

    //Edits the filename and creates a path
    filename += "/Test.txt";
      Path path = Paths.get(filename);
      try {
          String output= "";
          output= output + "This file shows the results of our tests on our Blockchain \n\n";

          output= output + "Testing the validateBlock function on a good block : ";
          if(blockchain.validateBlock(blockchain.blocks.get(blockchain.blocks.size()-1))){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }

          output= output + "Testing the validateBlockchain function on a good Blockchain : ";
          if(blockchain.validateBlockchain()){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }

          output= output + "Testing the validateBlockchain function on a Blockchain with a block with a bad previousHash value : ";
          if(!testBC1.validateBlockchain()){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }


          output= output + "Testing the validateBlock function on a block with a nonce that does not hash below the target value : ";
          if(!testBC2.validateBlock(testBC2.blocks.get(testBC2.blocks.size()-1))){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }


          output= output + "Testing the validateBlockchain function on a Blockchain with a block with a nonce that does not hash below the target value : ";
          if(!testBC2.validateBlockchain()){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }


          output= output + "Testing the validateBlock function on a block with an incorrect rootHash : ";
          if(!testBC3.validateBlock(testBC3.blocks.get(testBC3.blocks.size()-1))){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }

          output= output + "Testing the validateBlockchain function on a Blockchain with a block with an incorrect rootHash : ";
          if(!testBC3.validateBlockchain()){
            output= output + "Passed\n";
          } else {
            output= output + "Failed\n";
          }


          //Writes output to Test.txt
          Files.write(path, output.getBytes());
      }
      catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
          return false;

      }
      return true;
    }

}
