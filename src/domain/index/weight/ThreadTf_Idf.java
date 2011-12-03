package domain.index.weight;


import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import technical.helpers.Pair;

import domain.Document;
import domain.GenericPosting;
import domain.index.spimi.DefaultInvertedIndex;

public class ThreadTf_Idf extends Thread {

	private LinkedBlockingQueue<Pair<GenericPosting, LinkedList<Document>>> workToDo;
	private DefaultInvertedIndex index;
	private double corpusSize = 0;

	public ThreadTf_Idf (LinkedBlockingQueue<Pair<GenericPosting, LinkedList<Document>>> workToDo, DefaultInvertedIndex d, int corpusSize){
		this.workToDo = workToDo;
		this.index = d;
		this.corpusSize = (double) corpusSize;
	}



	public void run(){
		while (workToDo.size() > 0) {
			Pair<GenericPosting, LinkedList<Document>> pair;
			try {
				pair = workToDo.take();
				GenericPosting gendoc = pair.getFirst();
				RankedPosting wd = new RankedPosting(gendoc.getId(), gendoc.getTitle());
				gendoc = null;
				wd.setVector(getTFIDFVector(pair.getSecond()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Td_Idf_Vector getTFIDFVector(LinkedList<Document> list) {
		Td_Idf_Vector vector = new Td_Idf_Vector();
		if (list != null){
			for (Document doc : list){
				vector.getVector().put(doc.getTerm(), tf_idf(doc));
			}
		} 
		return vector;
	}

	private Double tf_idf(Document doc) {
		double tf = (double)doc.getOccurence();//(1.0+Math.log(doc.getOccurence()));
		double idf = Math.log(corpusSize/(double)index.getIdf(doc.getTerm()));
		return tf*idf;
	}

}
