package technical;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


import domain.Document;


/**
 * 
 * Represents a single machine
 * 
 * @author Andreas
 *
 */
public class Machine {
	private int count = 0;
	public int latestBlockID = 0;
	public HashMap<Integer, Block> blocks = new HashMap<Integer, Block>();
	public int machineID;
	public int availableMemorySpace;
	
	public Block getBlockWithID(int blockID) {
		return blocks.get(blockID);
	}	
	
	/**
	 * only add block if the machine has low memory
	 * 
	 * @param dictionary
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public boolean addBlock(HashMap<String, LinkedList<Document>> dictionary) throws IOException, ClassNotFoundException {
		if((availableMemorySpace = MEM()) < 100 && ++count > 5000){
//			System.out.println("Memory available left: " + availableMemorySpace);
			count = 0;
			
				++latestBlockID;
				blocks.put(latestBlockID, new Block(latestBlockID));
				getBlockWithID(latestBlockID).setBlock(dictionary);
				getBlockWithID(latestBlockID).WriteBlockToDisk();
				return true;
		} else { 
			if(!blocks.isEmpty()) getBlockWithID(latestBlockID).setBlock(dictionary);
			return false;
		}
	}
	
	/**
	 * This gives us the amount available of main memory left at our disposal.
	 *<br> 
	 *<br> Just a side note: Runtime.freeMemory() doesn't state the amount of memory that's left of allocating, it's just the amount of memory that's free within the currently allocated memory (which is initially smaller than the maximum memory the VM is configured to use), but grows over time.

When starting a VM, the max memory (Runtime.maxMemory()) just defines the upper limit of memory that the VM may allocate (configurable using the -Xmx VM option). The total memory (Runtime.totalMemory()) is the initial size of the memory allocated for the VM process (configurable using the -Xms VM option), and will dynamically grow every time you allocate more than the currently free portion of it (Runtime.freeMemory()), until it reaches the max memory.
	 * 
	 * @return
	 */
	private static int MEM(){
	    return (int)(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory())/1024/1024;
	}

}

