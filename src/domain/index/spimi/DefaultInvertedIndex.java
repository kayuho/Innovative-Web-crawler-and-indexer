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

import domain.GenericPosting;
import domain.Document;
import domain.collection.CollectionFactory;


public class DefaultInvertedIndex implements IInvertedIndex {

	private AbstractMap<String, ArrayList<Document>> map = new TreeMap<String, ArrayList<Document>>();
	
	boolean add(String token, ArrayList<Document> documentList) {
		if (token == null ) return false;
		if (map.containsKey(token)) {
			//add this document to the list of document that contains this token
			map.put(token, mergeTwoPostingList(map.get(token), documentList));
			return true;
		} else {
			map.put(token, documentList);
			return false;
		}
	
	}
	
	private ArrayList<Document> mergeTwoPostingList(ArrayList<Document> a, ArrayList<Document> b) {
		for (Document p1:a) {
			int idxInB = b.indexOf(p1);
			if (idxInB == -1)
				b.add(p1);
			else
				b.get(idxInB).add(p1.getOccurence());
		}
		return b;
	}
	@Override
	public boolean add(String token, int documentNumber) {
		// If this token is already in our index
		if (map.containsKey(token)) {
			//add this document to the list of document that contains this token
			int idx = map.get(token).indexOf(new Document(token, documentNumber,-1));
			if (idx >-1) {
				map.get(token).get(idx).add(1);
				return false;
			}
			else {
				map.get(token).add(new Document(token, documentNumber,1)); // if this is a new document/token pair
				return true;
			}
		}
		else {
			// Not already present, create a new list of document name
			ArrayList<Document> documentList = new ArrayList<Document>();
			// Add this document to the list
			documentList.add(new Document(token, documentNumber,1));
			// Add this list of document to the index
			map.put(token, documentList);
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
	public AbstractSet<Document> getSet(String token) {
		List<Document> c = map.get(token);
		if (c == null)
			return new HashSet<Document>();

		HashSet<Document> r = new HashSet<Document>(map.get(token).size());
		for (Document n : map.get(token))
			r.add(n);
		return r;
	}
	
	public int getIdf(String term) {
		return map.get(term).size();
	}
	
	/**
	 * Validate the index to make sure it holds the following properties for every term
	 * - The list of PostingList is not null
	 * - The list of PostingList contains only unique document id
	 * @return result of the validation
	 */
	public boolean validate() {
		HashSet<Integer> docSet = new HashSet<Integer>();
		for (String term : map.keySet()) {
			List<Document> pl = map.get(term);
			if (pl == null) return false;
			docSet.clear();
			for (Document p : pl) {
				if (docSet.contains(p.getDocumentId()))
					return false;
				else
					docSet.add(p.getDocumentId());
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
				for (Document i : this.getSet(token))
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
			TreeMap<String, ArrayList<Document>> newMap = new TreeMap<String, ArrayList<Document>>();

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
				Document[] postingList = new Document[st.countTokens()-1];
				int i=0;
				while (st.hasMoreTokens()) {
					if (firstToken==true) {
						firstToken = false;
						term = st.nextToken();
					}
					else {
						postingList[i++] = Document.fromString(term, st.nextToken());
					}
				}
				
				newMap.put(term, new ArrayList<Document>(Arrays.asList((postingList))));
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
	public HashSet<Document> getAll() {
		HashSet<Document> all = new HashSet<Document>();
		for (String s : map.keySet()) {
			all.addAll(map.get(s));
		}
		return all;
	}
	
	public TreeMap<GenericPosting, LinkedList<Document>> getDocBI() {
		TreeMap<GenericPosting, LinkedList<Document>> answer = new TreeMap<GenericPosting, LinkedList<Document>>();
		
		BenchmarkRow benR = new BenchmarkRow("Document based index");
		benR.start();
		for (Collection<Document> col : map.values()) {
			for (Document document : col) {
				GenericPosting doc = CollectionFactory.getCorpus().findArticle(document.getDocumentId());
				if (doc!=null){
					if (answer.containsKey(doc))
						answer.get(doc).add(document);
					else {
						LinkedList<Document> ll = new LinkedList<Document>();
						ll.add(document);
						answer.put(doc, ll);
					}
				}
			}
		}
		benR.stop();
		System.out.println("Document based index took " + benR.getDuration() + " to run");
		return answer;
	}
}
