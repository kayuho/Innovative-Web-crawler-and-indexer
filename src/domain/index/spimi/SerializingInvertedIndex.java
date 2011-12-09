package domain.index.spimi;

import technical.helpers.*;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import domain.index.Posting;

public class SerializingInvertedIndex implements IInvertedIndex{

	private Map<String, TreeMap<Integer, TreeSet<Integer>>> map;
	private String hash;
	
	public SerializingInvertedIndex(String term, HashMap<String, TreeMap<Integer, TreeSet<Integer>>> pl){
		map = pl;
		hash = Utils.MD5(term);
	}
	
	public SerializingInvertedIndex(String term){
		if(!loadFromFile(term)){
			map = new HashMap<String, TreeMap<Integer, TreeSet<Integer>>>();
			hash = Utils.MD5(term);
		}
	}
	
	/**
	 * @return the map
	 */
	public TreeMap<Integer, TreeSet<Integer>> getPostingList(String term) {
		hasValidHash(term);
		return map.get(term);
	}

	/**
	 * @param map the map to set
	 */
	public void setPostingList(Map<String, TreeMap<Integer, TreeSet<Integer>>> postingList) {
		this.map = postingList;
	}
	
	
	public boolean add(String term, int docID, int position){
		hasValidHash(term);
		//add this document to the list of document that contains this token
		if (!this.map.containsKey(term)){
			TreeSet<Integer> positions = new TreeSet<Integer>();
			positions.add(position);
			TreeMap<Integer, TreeSet<Integer>> postingList = new TreeMap<Integer, TreeSet<Integer>>();
			postingList.put(docID, positions);
			map.put(term, postingList);
			return true;
		} else if (getPostingList(term).containsKey(docID))
			if(getPostingList(term).get(docID).contains(position))
				return false;
			else{
				getPostingList(term).get(docID).add(position);
				return true;
			}
		else {
			TreeSet<Integer> positions = new TreeSet<Integer>();
			positions.add(position);
			getPostingList(term).put(docID, positions); // if this is a new document/token pair
			return true;
		}
	}
	
	public boolean addAll(String term, TreeMap<Integer, TreeSet<Integer>> pl){
		hasValidHash(term);
		if(pl instanceof TreeMap && pl.isEmpty())
			return false;
		else{
			getPostingList(term).putAll(pl);
			return true;
		}
			
			
	}
	
	public boolean remove(String term, int docID){
		hasValidHash(term);
		if(getPostingList(term).containsKey(docID)){
			getPostingList(term).remove(docID);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean loadFromFile(String term){
		hash = Utils.MD5(term);
		map = (Map<String, TreeMap<Integer, TreeSet<Integer>>>) Utils.loadSerialized("/postinglist/" + hash);
		if(map != null && !map.isEmpty())
			return true;
		else 
			return false;
	}
	
	public boolean saveToFile(){
		if(Utils.fileExists(this.hash) && !map.isEmpty()) 
			return Utils.serialize(map, "/postinglist/" + hash);
		else return false;
	}
	
	public boolean hasValidHash(String term){
		if(hash == null){
			hash = Utils.MD5(term);
			return true;
		}
		else if(hash.equals(Utils.MD5(term)))
			return true;
	    else {
		System.err.println("Something went HORRIBLY wrong! " + term + "is the term but the hash (" + hash + ") doesn't match that term.");
		System.exit(-1);
		return false;
	    }
	
	}

	@Override
	public boolean add(String token, int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AbstractSet<Posting> getSet(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<String> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashSet<Posting> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
