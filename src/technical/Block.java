package technical;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import domain.index.Posting;


public class Block implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Integer> blockIDs;
	private int blockID;
	private String filename;
	FileInputStream fis = null;
	FileOutputStream fos = null;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	private HashMap<String, LinkedList<Posting>> dictionary;
	
	@SuppressWarnings("unchecked")
	public HashMap<String, LinkedList<Posting>> getBlock() throws IOException, ClassNotFoundException { 
		if(dictionary != null) return dictionary;
		else {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			dictionary = (HashMap<String, LinkedList<Posting>>) in.readObject();
			return dictionary;
		}
	}
		
	public void setBlock(HashMap<String, LinkedList<Posting>> dictionary){	this.dictionary = dictionary; }
	
	public void WriteBlockToDisk() throws IOException {
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(this.dictionary);
		out.close();
		
		this.dictionary.clear();
		this.dictionary = null;
		System.gc(); // this will suggest the JVM to release objects that are not referenced: in this case it's the dictionary
	}

	public int[] getBlocks() {
		int[] temp = new int[blockIDs.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = blockIDs.get(i);
		}
		return temp;
	}
	public int getID() { return blockID; }
	
	public Block(int id) throws IOException, ClassNotFoundException {
		this.blockID = id;
		
		filename = new String("blocks/block" + blockID + ".ser");
		
//		File f = new File(filename);
//		if(f.exists()) {
//			getBlock();
//		}
		
	}
	
	public void finalize(){
		try {
			WriteBlockToDisk();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}