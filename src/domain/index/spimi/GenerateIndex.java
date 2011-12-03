package domain.index.spimi;

import java.util.ArrayList;

import technical.helpers.Benchmark;
import technical.helpers.Constants;
import technical.helpers.Property;
import technical.helpers.Utils;

import domain.collection.CollectionFactory;
import domain.index.DiskReaderThread;
import domain.index.IndexerThread;
import domain.index.reuters.XMLSharding;


public class GenerateIndex {
	
	
	private static final int NUMBER_OF_INDEXER_THREADS = Property.getInt("numOfParserThreads");
	private static final String DEFAULT_DIR = Constants.basepath + "/reut";
	private static final String DEFAULT_EXTENSION = ".xml";

	
	public static void main(String[] args) {


		Benchmark benchmark = new Benchmark();
		benchmark.startTimer("total");
		String directory = (args.length > 0) ? args[0] : DEFAULT_DIR;
		String extension = (args.length > 1) ? args[1] : DEFAULT_EXTENSION;
		
		//Open all files
		ArrayList<IndexerThread> pool = new ArrayList<IndexerThread>();

		if (Property.getBoolean("forceSharding"))
			XMLSharding.preprocess(directory, directory + "/fragment");
		if(Property.getBoolean("forcePreIndexing")){
		DiskReaderThread.addCollection(Utils.getAllFiles(directory, extension, false));

		//Launch two disk reader thread!
		DiskReaderThread d1 = new DiskReaderThread();
		DiskReaderThread d2 = new DiskReaderThread();
		d1.start();
		d2.start();
		
		benchmark.startTimer("starting-thread");
		for (int x=0; x<NUMBER_OF_INDEXER_THREADS; x++) {
			String tName = "Indexer-" + x;
			IndexerThread t1 = new IndexerThread(tName);
			t1.start();
			pool.add(t1);
		}
		
		benchmark.stopTimer("starting-thread");
		
		//Now IO has to finish
		try {
			d1.join();
			d2.join();
			System.out.println("Done reading files from disk");
			IndexerThread.signalNoMoreDocumentsAreExpected();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		

		for (IndexerThread t : pool) {
			try {
				benchmark.startTimer("waiting-thread");
				benchmark.startTimer("waiting-thread" + t.getName());
					t.join();
				benchmark.stopTimer("waiting-thread" + t.getName());
				benchmark.stopTimer("waiting-thread");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Get rid of the pool and allow garbage collection of their resource
		pool = null;
		System.gc();
		}
		//Time to write the index to a file
		benchmark.startTimer("writing-to-file");
		if(Property.getBoolean("forcePreIndexing")) SPIMIReconciliation.reconciliate();
		//Signal the corpus we are done reconciling.
		if(Property.getBoolean("forceIndexing")) CollectionFactory.getCorpus().closeIndex();
		benchmark.stopTimer("writing-to-file");

		benchmark.stopTimer("total");

		
		// Display some statistics
		System.out.println("The inverted index has been generated");
		
		benchmark.reportOnAllTimer();
	}

}
