package technical;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


import domain.Document;


/**
 * 
 * Our blocks may on live on the same machine as we do. Instead we can have our a of our block's IDs and traverse as follows:
 * <br>1. For each block ID:int machine_index = getMachineIDForBlock(int blockID);
 * <br>2. Go to machine #machine_index
 * <br>3. On that machine, do:  Block doc = getBlockWithID(block_id);
 * 
 * <br><br><b> Goal: This class holds a list of all machines </b>
 * 
 * @author Andreas
 */
public class Server {
	private static Server currentInstance;
	HashMap<Integer, Machine> machines = new HashMap<Integer, Machine>();
	HashMap<Integer, Integer> blockToMachineMap = new HashMap<Integer, Integer>();
	
	public static Server getInstance(){
		if(currentInstance == null) currentInstance = new Server();
		return currentInstance;
	}
	
	private Server(){};
	
	public Machine getMachineWithId(int machineID) {
		return machines.get(machineID);
	}
	
	public int getMachineIDForBlock(int blockID) {
		Integer machineID = blockToMachineMap.get(blockID);
		return machineID == null ? -1 : machineID;
	}
	
	public Block getBlockWithID(int blockID) {
		Integer machineID = blockToMachineMap.get(blockID);
		if (machineID == null) {
			return null;
		}
		Machine machine = getMachineWithId(machineID);
		if (machine == null) {
			return null;
		}
		return machine.getBlockWithID(blockID);
	}

	public boolean addBlock(HashMap<String, LinkedList<Document>> dictionary) throws IOException, ClassNotFoundException {
		if(machines.isEmpty()) machines.put(new Integer(1), new Machine());
			return machines.get(1).addBlock(dictionary);
	}
}
