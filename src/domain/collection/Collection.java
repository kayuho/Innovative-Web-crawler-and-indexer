package domain.collection;

import technical.helpers.*;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeMap;

import domain.collection.documents.GenericDocument;

public class Collection {

	protected TreeMap<Integer, GenericDocument> docMap;
	private static Class<? extends GenericDocument> factory = GenericDocument.class;
	private boolean rom=false;
	
	Collection() {
		super();
		System.out.println("Collection is being generated");
		if (CollectionFactory.collection != null)
			throw new RuntimeException("already one collection exists in CollectionFactory");
	}

	public static void setNewDocumentFactory(Class<? extends GenericDocument> factory) {
		Collection.factory = factory;
	}
	
	public synchronized void addArticle(GenericDocument d) {
		if (docMap == null)
			docMap = new TreeMap<Integer, GenericDocument>();
		if (rom==true)
			throw new RuntimeException("read-only collection");
		// Check if this document already exists
		if (!docMap.values().contains(d)){
			docMap.put(d.getId(), d);
	    	}
	}
	
	public void closeIndex(){
		writeToDisk();
		rom=true;
	}
	
	protected void writeToDisk() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(Constants.basepath + "/articles.txt"));
			for (Integer i : docMap.keySet()) {
				GenericDocument a = docMap.get(i);
				out.write(a.toString() + "\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFromDisk() {
		Collection newCollectionInstance = CollectionFactory.getCorpus();
		newCollectionInstance.docMap = new TreeMap<Integer, GenericDocument>();
		try {
			LineNumberReader in = new LineNumberReader(new FileReader(Constants.basepath + "/articles.txt"));
			String line;
			line = in.readLine();
			while (line != null && line.length()>0) {
				try {
					Method factoryMethod = factory.getDeclaredMethod("fromString", String.class);
					GenericDocument genPost = (GenericDocument) factoryMethod.invoke(null, line);
					newCollectionInstance.docMap.put(genPost.getId(), genPost);
					} 
				catch (IllegalAccessException e) {e.printStackTrace();} 
				catch (InvocationTargetException e){e.printStackTrace();}
				catch (NumberFormatException e) {e.printStackTrace();} 
				catch (SecurityException e) {e.printStackTrace();} 
				catch (NoSuchMethodException e) {e.printStackTrace();} 
				catch (IllegalArgumentException e) {e.printStackTrace();} 
					
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		newCollectionInstance.rom=true;
		
	}
	
	public int getAllLength() {
		if (docMap == null) readFromDisk();
		int value=0;
		for (GenericDocument a : docMap.values()) {
			value+=a.getLengthInWords();
		}
		return value;
	}

	public GenericDocument findArticle(int documentId) {
		if (docMap == null) readFromDisk();
		return docMap.get(documentId);
	}

	public int size() {
		if (docMap == null) readFromDisk();
		return docMap.size();
	}
}