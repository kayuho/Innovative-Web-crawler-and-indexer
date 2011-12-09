package domain.index.spimi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import technical.helpers.BenchmarkRow;
import technical.helpers.Constants;

import domain.collection.CollectionFactory;
import domain.collection.documents.GenericDocument;
import domain.index.Posting;


public class DefaultInvertedIndex implements IInvertedIndex {

	private AbstractMap<String, ArrayList<Posting>> map = new TreeMap<String, ArrayList<Posting>>();
	
	boolean add(String token, ArrayList<Posting> PostingList) {
		if (token == null ) return false;
		if (map.containsKey(token)) {
			//add this Posting to the list of Posting that contains this token
			map.put(token, mergeTwoPostingList(map.get(token), PostingList));
			return true;
		} else {
			map.put(token, PostingList);
			return false;
		}
	
	}
	
	private ArrayList<Posting> mergeTwoPostingList(ArrayList<Posting> a, ArrayList<Posting> b) {
		for (Posting p1:a) {
			int idxInB = b.indexOf(p1);
			if (idxInB == -1)
				b.add(p1);
			else
				b.get(idxInB).add(p1.getOccurence());
		}
		return b;
	}
	@Override
	public boolean add(String token, int PostingNumber) {
		// If this token is already in our index
		if (map.containsKey(token)) {
			//add this Posting to the list of Posting that contains this token
			int idx = map.get(token).indexOf(new Posting(token, PostingNumber,-1));
			if (idx >-1) {
				map.get(token).get(idx).add(1);
				return false;
			}
			else {
				map.get(token).add(new Posting(token, PostingNumber,1)); // if this is a new Posting/token pair
				return true;
			}
		}
		else {
			// Not already present, create a new list of Posting name
			ArrayList<Posting> PostingList = new ArrayList<Posting>();
			// Add this Posting to the list
			PostingList.add(new Posting(token, PostingNumber,1));
			// Add this list of Posting to the index
			map.put(token, PostingList);
			return true;
		}
	}
	
	/*
	 * This method merges index b into index a.
	 */
	public synchronized void mergeWith(DefaultInvertedIndex b) {
		for (String tokenb : b) {
			this.add(tokenb, b.map.get(tokenb));
		}
	}
	
	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public AbstractSet<Posting> getSet(String token) {
		List<Posting> c = map.get(token);
		if (c == null)
			return new HashSet<Posting>();

		HashSet<Posting> r = new HashSet<Posting>(map.get(token).size());
		for (Posting n : map.get(token))
			r.add(n);
		return r;
	}
	
	public int getIdf(String term) {
		return map.get(term).size();
	}
	
	/**
	 * Validate the index to make sure it holds the following properties for every term
	 * - The list of PostingList is not null
	 * - The list of PostingList contains only unique Posting id
	 * @return result of the validation
	 */
	public boolean validate() {
		HashSet<Integer> docSet = new HashSet<Integer>();
		for (String term : map.keySet()) {
			List<Posting> pl = map.get(term);
			if (pl == null) return false;
			docSet.clear();
			for (Posting p : pl) {
				if (docSet.contains(p.getPostingId()))
					return false;
				else
					docSet.add(p.getPostingId());
			}
		}
		return true;
	}
	
	@Override
	public Iterator<String> iterator() {
		return map.keySet().iterator();
	}
	@Override
	public void clear() {
		map.clear();		
	}
	
	public synchronized void writeToFile(String path) {
		try {
			StringBuilder sb = new StringBuilder();
			FileWriter fstream = new FileWriter(Constants.basepath + "/" + path);
			// For each token of the index
			for (String token : this) {
				// Write to the index file
				sb.append(token + " ");
				for (Posting i : this.getSet(token))
					sb.append(i.toString() + " ");
				sb.append("\n");
			}
			fstream.write(sb.toString());
			// Close the index file.
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static DefaultInvertedIndex readFromFile(String path) {
		try {
			TreeMap<String, ArrayList<Posting>> newMap = new TreeMap<String, ArrayList<Posting>>();

			File inputFile = new File(Constants.basepath + "/" + path);
			BenchmarkRow timer = new BenchmarkRow(null);
			System.out.println("opening " + inputFile.getAbsolutePath());
			FileReader fstream = new FileReader(inputFile);
			BufferedReader in = new BufferedReader(fstream);
			// For each token of the index
			String line = in.readLine();

			
			String term = null;
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				boolean firstToken = true;
				Posting[] postingList = new Posting[st.countTokens()-1];
				int i=0;
				while (st.hasMoreTokens()) {
					if (firstToken==true) {
						firstToken = false;
						term = st.nextToken();
					}
					else {
						postingList[i++] = Posting.fromString(term, st.nextToken());
					}
				}
				
				newMap.put(term, new ArrayList<Posting>(Arrays.asList((postingList))));
				line = in.readLine();
			}

			in.close();
			timer.stop();
			System.out.println("index took me " + timer.getDuration()/1000.0 + "ms to open");

			DefaultInvertedIndex dii = new DefaultInvertedIndex();
			dii.map = newMap;
			return dii;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get all possible postings
	 */
	public HashSet<Posting> getAll() {
		HashSet<Posting> all = new HashSet<Posting>();
		for (String s : map.keySet()) {
			all.addAll(map.get(s));
		}
		return all;
	}
	
	public TreeMap<GenericDocument, LinkedList<Posting>> getDocBI() {
		TreeMap<GenericDocument, LinkedList<Posting>> answer = new TreeMap<GenericDocument, LinkedList<Posting>>();
		
		BenchmarkRow benR = new BenchmarkRow("Posting based index");
		benR.start();
		for (Collection<Posting> col : map.values()) {
			for (Posting Posting : col) {
				GenericDocument doc = CollectionFactory.getCorpus().findArticle(Posting.getPostingId());
				if (doc!=null){
					if (answer.containsKey(doc))
						answer.get(doc).add(Posting);
					else {
						LinkedList<Posting> ll = new LinkedList<Posting>();
						ll.add(Posting);
						answer.put(doc, ll);
					}
				}
			}
		}
		benR.stop();
		System.out.println("Posting based index took " + benR.getDuration() + " to run");
		return answer;
	}
}
