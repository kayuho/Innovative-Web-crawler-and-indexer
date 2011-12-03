package domain.collection;


import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import technical.helpers.BenchmarkRow;
import technical.helpers.Pair;
import technical.helpers.Property;

import domain.GenericPosting;
import domain.Document;
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
		TreeMap<GenericPosting, LinkedList<Document>> data = index.getDocBI();
		System.out.println("Tf-Idf is being calculated");
		LinkedBlockingQueue<Pair<GenericPosting, LinkedList<Document>>> workToDo = new LinkedBlockingQueue<Pair<GenericPosting, LinkedList<Document>>>();
		
		for (GenericPosting gd : data.keySet()) {
			Pair<GenericPosting, LinkedList<Document>> pair = new Pair<GenericPosting, LinkedList<Document>>(gd, data.get(gd));
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
