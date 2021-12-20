//Used https://www.geeksforgeeks.org/different-ways-reading-text-file-java/ as guidance for implementation of Scanner object
//Used https://java2blog.com/filenotfoundexception-java/ as guidance for the error handling of the input
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Parser{
  public static void main(String[] args) throws Exception {

    // User I/O
    Scanner myScanner = new Scanner(System.in);
    // Boolean to mark the last file given by user
    boolean last_file = false;
    // array list of strings to hold all file names
    ArrayList<String> file_list = new ArrayList<String>();
    // String to hold current file name
    String fileName;
    // until the last file is reached prompt user to input more files
    while(!last_file){
      System.out.print("Enter the name of the input file w/ the full path: ");
      fileName = myScanner.next();
      //add file to array of file names
      file_list.add(fileName);
      //prompt user to quit
      System.out.print("Was that the last file? [Y/N]");
      String more_files = myScanner.next();
      if(more_files.equals("Y")){
        last_file = true; //break out of while loop
      }
    }
    myScanner.close();

    System.out.println();
    //create new BlockChain 😎 🤓
    BlockChain moneyMovesBaby = new BlockChain();
    //Here, for loop to iterate over file_list and create a Block object for each ?
    for (String filePath : file_list) {
      //System.out.println("filePath: "+  filePath);
      ArrayList<Leaf> input = new ArrayList<Leaf>();
      ArrayList<Record> input2 = new ArrayList<Record>();
      // pass the path to the file as a parameter
      File file = new File(filePath);
      FileInputStream fis = null;
      //error catching if a file does not exist or we cannot find the file
      try{
        //reads the contents of file as a stream of bytes
          fis = new FileInputStream(file);
      }
      catch (FileNotFoundException e){ //prints that there is an error in reading the file and exits the program
        System.out.println("Error in reading of file. Verify the path exists and is correct ");
        System.exit(1);
      }
      Scanner sc = new Scanner(file);

      //reads the contents of the file and creates the MerkleTree
      while (sc.hasNextLine()){
        String str = sc.nextLine();
        // Split the string into the 40 character(byte) address and the non-negative balance
        String parts[] = str.split(" ");
        // Initiate a leaf object using its constructor
        Leaf in = new Leaf(parts[0], Integer.parseInt(parts[1]));
        Record in2 = new Record(parts[0], Integer.parseInt(parts[1]));
        // Append the leaf to the array of leaves soon to become merkle tree
        input.add(in);
        input2.add(in2);
      }
      // Initiate the Merkle Tree using the arraylist of Leaves
      MerkleTree tree = new MerkleTree(input);
      //builds the merkle tree and pritns the root hash
      String rootHash = tree.build() ;
      //System.out.println("rootHash: \n" + rootHash);

      //create Block here
      moneyMovesBaby.addBlock(rootHash, input2);
      }
      //if(moneyMovesBaby.validateBlockchain()){
      //  System.out.println("BlockChain is validated");
      //}
      System.out.println();
      System.out.println();
      moneyMovesBaby.printBlockChain();
      System.out.println();
      System.out.println();

      // String to hold output file.
      String outputFile = file_list.get(0);
      char[] ch = outputFile.toCharArray();
      int fileNameStart = outputFile.lastIndexOf('/');
      int fileNameEnd = outputFile.lastIndexOf('.');
      String outputFileFormatted="";
      for (int k = fileNameStart+1; k < fileNameEnd; k++) {
        outputFileFormatted += ch[k] ;
      }
      outputFileFormatted+=".block.out";
      //System.out.println("Final Ouput File Name: " + outputFileFormatted);
      moneyMovesBaby.writeFile(outputFileFormatted);

      //verify a block


      // //get balance via address
      // String address_1 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
      // int balance_1 = moneyMovesBaby.retrieveBalance(address_1);
      // System.out.println("balance_1 " + balance_1);
      // System.out.println();
      //
      // //Proof of Membership
      // try{
      //   Block last_block = moneyMovesBaby.blocks.get(moneyMovesBaby.blocks.size()-1);
      //   moneyMovesBaby.proofOfMembership(last_block, address_1);
      //   Block second_last = moneyMovesBaby.blocks.get(moneyMovesBaby.blocks.size()-2);
      //   moneyMovesBaby.proofOfMembership(second_last, address_1);
      //   //error testing with proof of membership
      //   /*Block third_last = moneyMovesBaby.blocks.get(moneyMovesBaby.blocks.size()-3);
      //   moneyMovesBaby.proofOfMembership(third_last);*/
      // }
      // catch(ArrayIndexOutOfBoundsException exc){
      //   System.out.println("The index of the block you have provided does not exist on the current blockchain: ");
      //   System.out.println(exc);
      // }
      // catch(IndexOutOfBoundsException exc){
      //   System.out.println("There are not enough blocks on the current blockchain");
      //   System.out.println(exc);
      // }
    }
}
