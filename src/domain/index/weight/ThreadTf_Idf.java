package domain.index.weight;


import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import technical.helpers.Pair;

import domain.index.Posting;
import domain.collection.documents.GenericDocument;
import domain.collection.documents.RankedDocument;
import domain.index.spimi.DefaultInvertedIndex;

public class ThreadTf_Idf extends Thread {

	private LinkedBlockingQueue<Pair<GenericDocument, LinkedList<Posting>>> workToDo;
	private DefaultInvertedIndex index;
	private double corpusSize = 0;

	public ThreadTf_Idf (LinkedBlockingQueue<Pair<GenericDocument, LinkedList<Posting>>> workToDo, DefaultInvertedIndex d, int corpusSize){
		this.workToDo = workToDo;
		this.index = d;
		this.corpusSize = (double) corpusSize;
	}



	public void run(){
		while (workToDo.size() > 0) {
			Pair<GenericDocument, LinkedList<Posting>> pair;
			try {
				pair = workToDo.take();
				GenericDocument gendoc = pair.getFirst();
				RankedDocument wd = new RankedDocument(gendoc.getId(), gendoc.getTitle());
				gendoc = null;
				wd.setVector(getTFIDFVector(pair.getSecond()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Td_Idf_Vector getTFIDFVector(LinkedList<Posting> list) {
		Td_Idf_Vector vector = new Td_Idf_Vector();
		if (list != null){
			for (Posting p : list){
				vector.getVector().put(p.getTerm(), tf_idf(p));
			}
		} 
		return vector;
	}

	private Double tf_idf(Posting p) {
		double tf = (double)p.getOccurence();//(1.0+Math.log(doc.getOccurence()));
		double idf = Math.log(corpusSize/(double)index.getIdf(p.getTerm()));
		return tf*idf;
	}

}
