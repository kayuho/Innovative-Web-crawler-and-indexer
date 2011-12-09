package domain.collection;


import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import technical.helpers.BenchmarkRow;
import technical.helpers.Pair;
import technical.helpers.Property;

import domain.collection.documents.GenericDocument;
import domain.index.Posting;
import domain.index.spimi.DefaultInvertedIndex;
import domain.index.weight.ThreadTf_Idf;


public class RankedCollection extends Collection {
	
	private DefaultInvertedIndex index;  
	private Stack<ThreadTf_Idf> threads = new Stack<ThreadTf_Idf>();
	private static final int THREADS_NUM = Property.getInt("numOfRankingThreads");
	public RankedCollection() {
		super();
	}
	
	public void closeIndex(){
		generateTFIDFVector();
		super.closeIndex();
	}

	private void generateTFIDFVector() {
		BenchmarkRow bench = new BenchmarkRow("Generating Tf-Idf");
		bench.start();
		
		index = DefaultInvertedIndex.readFromFile("index.txt");
		if (!index.validate()) throw new RuntimeException("The index is not valid");
		TreeMap<GenericDocument, LinkedList<Posting>> data = index.getDocBI();
		System.out.println("Tf-Idf is being calculated");
		LinkedBlockingQueue<Pair<GenericDocument, LinkedList<Posting>>> workToDo = new LinkedBlockingQueue<Pair<GenericDocument, LinkedList<Posting>>>();
		
		for (GenericDocument gd : data.keySet()) {
			Pair<GenericDocument, LinkedList<Posting>> pair = new Pair<GenericDocument, LinkedList<Posting>>(gd, data.get(gd));
			workToDo.add(pair);
		}
		
		for (int i = 0; i < THREADS_NUM; i++){
			ThreadTf_Idf thread = new ThreadTf_Idf(workToDo, index, this.size());
			threads.add(thread);
			thread.start();
		}
		for (ThreadTf_Idf t : threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Computing TF-IDF finished");
		bench.stop();
		System.out.println(bench.toString());
	}


}
