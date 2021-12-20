import java.util.ArrayList;
// 2.2 Block Structure
// The block consists of a header followed by the ledger of addresses. The header format is as follows:
// • hash of the header of the previous block (zero for the initial genesis block)
// • hash of the root of the Merkle tree stored in the current block
// • a timestamp as an integer number of seconds since 1970-01-01 00:00:00 UTC (that is, Unix time)
// • difficulty target
// • nonce


public class Block {
    // hash of the header of the previous block (zero for the initial genesis block)
    String previousHash;
    // hash of the root of the Merkle tree stored in the current block
    String rootHash;
    // a timestamp as an integer number of seconds since 1970-01-01 00:00:00 UTC (that is, Unix time)
    long timestamp;
    // difficulty target
    double target;
    // nonce ( number only used once)
    int nonce;
    //Account and Balance Data
    ArrayList<Record> data;

    // Full Block Constructor
    public Block(String previousHash, String rootHash, double target, ArrayList<Record> data){
        this.previousHash = previousHash;
        this.rootHash = rootHash;
        this.timestamp = System.currentTimeMillis() / 1000L ;
        //0.50 50%
        this.target = target;
        // You will need to find a nonce such that the nonce concatenated with the root hash of the
        // Merkle tree is hashed by SHA-256 to a value less than or equal to the specified target.
        // ?Probability or hash target ?
        this.nonce = (int) Math.floor(Math.random()*100000);
        this.data = data;

    }

    public void printBlock(boolean fullLedger){
        // This output must be in the format specified below.
        // • The print output of every block begins with a line saying BEGIN BLOCK
        // • The header is printed as follows: A line saying BEGIN HEADER, then each header component own
        // its own separate line, and a line saying END HEADER
        // • The addresses and balances in the format of the input file from Homework 3, if specified.
        // [[[ HFK: I <== don’t get the if specified part of this. ]]]
        // • A line saying END BLOCK
        // • A blank line
        System.out.println("BEGIN BLOCK");
        System.out.println("BEGIN HEADER");
        System.out.println("previous hash:   " + this.previousHash);
        System.out.println("merkleroot hash: " + this.rootHash);
        System.out.println("timestamp:       " + this.timestamp );
        System.out.println("target:          " + this.target);
        System.out.println("nonce:           " + this.nonce);
        System.out.println("END HEADER");
        if(fullLedger){
          for(int i = 0; i < this.data.size(); i++){
            System.out.println(this.data.get(i).address + " " + this.data.get(i).balance);
          }
        }
        System.out.println("END BLOCK");
        System.out.println();

    }
}

// The block header contains three sets of block metadata. It is an 80-byte long string, and it is comprised of
// the 4-byte long Bitcoin version number, 32-byte previous block hash, 32-byte long Merkle root, 4-byte long
// timestamp of the block, 4-byte long difficulty target for the block, and the 4-byte long nonce used by miners.
