import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
// import java.io.FileWriter;
// import java.io.BufferedWriter ;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import javafx.util.Pair;


/**
 * BlockChain class is a model of the blockhain structures exhibited by cryptocurrency technologies
 */

public class BlockChain {
    //chain of blocks
    ArrayList<Block> blocks;
    // difficulty target
    double target;

    //empty constructor s
    public BlockChain(){
      this.blocks = new ArrayList<Block>();
      this.target = 0.5;

    };

    //constructor
    public BlockChain(BlockChain blockchain){
      ArrayList<Block> blocks2 = new ArrayList<Block>();
      for(int i = 0; i<blockchain.blocks.size();i++){
        blocks2.add(blockchain.blocks.get(i));
      }
      this.blocks = blocks2;
      this.target = blockchain.target;

    };

    // Adds a block to the BlockChain
    public void addBlock(String roothash, ArrayList<Record> data){
        //If blockChain is empty, create the Genesis Block. Time to get rich. Get yo money up not your funny up ya diggggg!
        if(blocks.size() == 0){
            Block newBlock = new Block("0", roothash, this.target, data) ;
            if(verifyBlock(newBlock)){
              blocks.add(newBlock);
            } else {
              //System.out.println("Failed: Creating a new block to Verify");
              addBlock(roothash, data);
            }
        } else{
        //generate previousHash using the previous Block
        Block previousBlock = blocks.get(blocks.size()-1);
        String previousHash = generatePreviousHash(previousBlock);

        //generate new block and append it to the blockchain
        Block newBlock = new Block(previousHash, roothash, this.target, data) ;
        if(verifyBlock(newBlock)){
          blocks.add(newBlock);
        } else {
          //System.out.println("Failed: Creating a new block to Verify");
          addBlock(roothash, data);
        }
      }
    };

    //generate the previous hash given a block
    public String generatePreviousHash(Block previousBlock){
        // Collect information of the previous block so that the previousHash can be generated
        String previousHash = previousBlock.previousHash;
        String rootHash = previousBlock.rootHash;
        long timestamp = previousBlock.timestamp;
        double target= previousBlock.target;
        int nonce = previousBlock.nonce;

        // Concatenate all of the previous Block's fields into a string
        String data_soup = previousHash+rootHash+timestamp+target+nonce;

        // Hash the string
        // Calculate the hash of the prehash string
        String newPreviousHash;
        try{
            newPreviousHash = SHA256.toHexString(SHA256.getSHA(data_soup));
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
            newPreviousHash = "ERROR HASHING";
            return newPreviousHash ;
        }
        return newPreviousHash ;
    }

    //Print the blockchain bottom first
    public void printBlockChain(){
        for (Block each_block : blocks) {
            each_block.printBlock(true);
        }
    }


    //send blockchain to file top up first
    public boolean writeFile(String filename){
        Path path = Paths.get(filename);
        try {
            String output= "";
            //iterate over array list of blocks and for each, add the formatted info to output string
            for(int i = blocks.size()-1 ; i>-1; i--){
                output= output + "BEGIN BLOCK\n";
                output= output +"BEGIN HEADER\n";
                output= output +"previous hash:   " + blocks.get(i).previousHash + "\n";
                output= output +"merkleroot hash: " + blocks.get(i).rootHash+ "\n";
                output= output +"timestamp:       " + blocks.get(i).timestamp + "\n";
                output= output +"target:          " + blocks.get(i).target+ "\n";
                output= output +"nonce:           " + blocks.get(i).nonce+ "\n";
                output= output +"END HEADER\n";
                for(int j = 0; j < blocks.get(i).data.size(); j++){
                    output= output +blocks.get(i).data.get(j).address + " " + blocks.get(i).data.get(j).balance+ "\n";
                }
                output= output +"END BLOCK\n\n";
            }
            //System.out.println("output: " + output);
            Files.write(path, output.getBytes());
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return false;

        }
        return true;
      }

    // Verifies if block passes the target value by checking the number of leading 0's of its hash
    public boolean verifyBlock(Block block){
        String prehash = block.rootHash + block.nonce;
        int degree = (int)Math.ceil((Math.log(1/this.target) / Math.log(2)));
        int shift = 8-degree;
        //System.out.println("Degree : " + degree);
        // Calculate the hash of the prehash string
        try{
          byte[] hash = SHA256.getSHA(prehash);
          int test = hash[0]>>>shift;
          //String s1 = String.format("%8s", Integer.toBinaryString(test & 0xFF)).replace(' ', '0');
          //System.out.println(s1);
          if(test == 0b00000000){
            return true;
          }
        }
        catch (NoSuchAlgorithmException e) {
          System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
        return false;
    }

    // Checks if a block is valid. Checks that the nonce and rootHash is correct
    public boolean validateBlock(Block block){
      //Checks the nonce is correct using verifyBlock function
      if(!verifyBlock(block)){
        return false;
      }
      //Creates a new ArrayList of Leafs
      ArrayList<Leaf> input = new ArrayList<Leaf>();
      //Loops through the data in the block converts them to Leafs and adds them to input
      for(int i = 0; i<block.data.size(); i++){
        Leaf in = block.data.get(i).RecordtoLeaf();
        input.add(in);
      }
      // Initiate the Merkle Tree using the arraylist of Leaves
      MerkleTree tree = new MerkleTree(input);
      //builds the merkle tree and the root hash
      String rootHash = tree.build();
      //Checks if the two rootHashs are the same
      if(block.rootHash.equals(rootHash)){
        return true;
      }else{
        return false;
      }
    }

    // Checks if all the blockchain is valid by checking every block
    public boolean validateBlockchain(){
      // Loops through each block and uses validateBlock function to validate their nonce and rootHash
      for(int i = this.blocks.size()-1; i>=0; i--){
        if(!validateBlock(this.blocks.get(i))){
          return false;
        }
        // Checks each blocks previousHash
        if(i!=0){
          String pHash = generatePreviousHash(this.blocks.get(i-1));
          if(!pHash.equals(this.blocks.get(i).previousHash)){
            return false;
          }
          // Special case for Genesis Block
        }else{
          if(!this.blocks.get(i).previousHash.equals("0")){
            return false;
          }
        }
      }
      return true;
    }

    // retrieves the balance of the given string and blockchain
    public int retrieveBalance(String address){
        int balance = -1;
        //iterate over the blockchain's arrayList of blocks
        for (int i = this.blocks.size()-1; i >= 0; i--){
            //iterate over each block's data arraylist
            for (int j = 0; j < this.blocks.get(i).data.size(); j++) {
                //check if the address matches
                if(address.equals(this.blocks.get(i).data.get(j).address)){
                    //call proof of membership here with the address and block
                    return this.blocks.get(i).data.get(j).balance ;
                }
            }
        }
        //if the code reaches this retunr statement, address
        //does not exist and -1 is returned to denote error
        return balance;
    }

    //check address return boolean
    public int checkAddress(String address){
        for (int i = this.blocks.size()-1; i >= 0; i--){
            //iterate over each block's data arraylist
            for (int j = 0; j < this.blocks.get(i).data.size(); j++) {
                //check if the address matches
                if(address.equals(this.blocks.get(i).data.get(j).address)){
                    return i;
                }
            }
        }
        return -1;
    }

    //proof of membership function
    public ArrayList<String> proofOfMembership(Block block, String address){
        ArrayList<String> hash_lineage = new ArrayList<String>();
        //using the concatenated address+balance, create a merkle tree.
        ArrayList<Leaf> input = new ArrayList<Leaf>();
        for(int i = 0; i<block.data.size(); i++){
            Leaf in = block.data.get(i).RecordtoLeaf();
            input.add(in);
        }
        // Initiate the Merkle Tree using the arraylist of Leaves
        MerkleTree tree = new MerkleTree(input);
        //builds the merkle tree and pritns the root hash
        String rootHashMembership = tree.build();
        //verify that the merkle tree roothashes match
        if(block.rootHash.equals(rootHashMembership)){
            // System.out.println("PROOF OF MEMEBERSHIP DEBUG PROCESS");
            // System.out.println("rootHashMembership: " + rootHashMembership);
            // tree.printTree();
            int startIndex = -1;
            for (int i = 0; i < tree.nodes.size(); i++) {
                //if the addressed matches, we have the starting index
                if(address.equals(tree.nodes.get(i).address)){
                    startIndex = i ;
                    hash_lineage.add(tree.nodes.get(i).hash);
                    // i is odd
                    if(i%2!=0){
                        hash_lineage.add(tree.nodes.get(i-1).hash);
                    }
                    // i is even
                    else if(i%2==0){
                        hash_lineage.add(tree.nodes.get(i+1).hash);
                    }
                    //i is even
                    break;
                }
            }
            //numer of leaves of the merkle tree
            int length = (tree.nodes.size() / 2) + 1 ;
            //the number of levels of the tree
            int height = (int) Math.ceil((Math.log(length) / Math.log(2)));
            int indexToAppend = -1 ;
            //iterate over levels to add parent + parent pair
            for (int i = 0; i < height ; i++) {
                indexToAppend = (int) Math.floor(startIndex/2) + length ;
                // System.out.println("indexToAppend: "+ indexToAppend);
                hash_lineage.add(tree.nodes.get(indexToAppend).hash);
                //Only get and add the siblings/Pairs if the current node is not the root
                if(i != (height-1)){
                    // indexToAppend is odd
                    if(indexToAppend%2!=0){
                        hash_lineage.add(tree.nodes.get(indexToAppend-1).hash);
                    }
                    // indexToAppend is even
                    else if(indexToAppend%2==0){
                        hash_lineage.add(tree.nodes.get(indexToAppend+1).hash);
                    }
                }
                //set the startIndex to the index that was just appended
                startIndex = indexToAppend ;
            }

        }

        //error case merkle tree roots do not match
        else{
            //error occured, non-membership case, empty arraylist
            return hash_lineage;
        }
        System.out.println("PRINTING HASH ARRAY");
        for (int i = 0; i < hash_lineage.size(); i++) {
            System.out.println("i : "+ i + " hash: " + hash_lineage.get(i));
        }

        return hash_lineage;


    }

    // 2.4 Balance function: Has inputs of address and blockchain and returns a pair with the Integer
    // being the balance of the address and the ArrayList of Strings is the proof of Membership
    // If the integer is equal to -1 then the address is not in the blockchain
    public Pair <Integer,ArrayList<String>> balance(String address, BlockChain blockchain){
      // Index of block where the address balance is
      int index = checkAddress(address);
      //Checks if address is in the blockchain
      if(index!=-1){
        int bal = retrieveBalance(address);
        ArrayList<String> hashSet = proofOfMembership(blockchain.blocks.get(index), address);
        Pair <Integer, ArrayList<String>> ret = new Pair <Integer, ArrayList<String>>(bal, hashSet);
        return ret;
      }else{
        Pair <Integer, ArrayList<String>> ret = new Pair <Integer, ArrayList<String>>(-1, null);
        return ret;
      }
    }
}
