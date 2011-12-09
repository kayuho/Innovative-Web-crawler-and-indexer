package domain.crawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import domain.collection.RankedCollection;
import domain.collection.documents.RankedDocument;
import domain.collection.documents.WebDocument;
import domain.collection.documents.factory.WebFactory;
import domain.index.IndexerThread;
import domain.index.spimi.SPIMIReconciliation;

import technical.helpers.BenchmarkRow;
import technical.helpers.Property;
import websphinx.Crawler;
import websphinx.DownloadParameters;
import websphinx.Link;
import websphinx.Page;
import websphinx.Pattern;
import websphinx.Text;
import websphinx.Wildcard;

public class IRCrawler extends Crawler {

	private static final long serialVersionUID = 8328003540493734786L;
	private static Pattern _domain = new Wildcard("http://*cs.concordia.ca/*");
	private static HashMap<String, Integer> _priorityQueue = new HashMap<String, Integer>();
	private static Integer _maxId = 0;
	private IndexerThread _indexer;
	
	public IRCrawler(Link link) {
		super();
		RankedCollection.setNewDocumentFactory(RankedDocument.class);
		_indexer = new IndexerThread("Web");
		_indexer.start();
		super.addRoot(link);
	}
	
	public void postProcess() {
		while (this.getActiveThreads() > 0)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		IndexerThread.signalNoMoreDocumentsAreExpected();
		try {
			_indexer.join();
			SPIMIReconciliation.reconciliate();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean shouldVisit(Link link) {
		boolean result = false;
		
		if (_domain.found(link.getURL().toString())) {
			result = true;
			String host = link.getHost();
			if (_priorityQueue.containsKey(host)) {
				int priority = _priorityQueue.get(host) + 1;
				link.setPriority(priority);
				_priorityQueue.put(host, priority);
			} 
			else {
				System.out.println(_priorityQueue.size() + " " + host);
				_priorityQueue.put(host, 0);
				link.setPriority(0);
			}
		}
		
		link.discardContent();
		return result;
	}
	
	public void visit(Page page) {
		if ((_domain.found(page.getURL().toString()))) {
			int currentPage;
			try {
				WebDocument document = _getDocument(page);
				if (document.getText().length() > 0) {
					synchronized(_maxId) {
						currentPage = _maxId++;
					}
					document.setId(_maxId);
					_collectPage(page,Property.get("basepath") + "\\data\\" + currentPage);
					IndexerThread.addDocument(document);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private WebDocument _getDocument(Page page) throws Exception {
		String content = "";
		String title = "";
		String url = "";
		
		if (page.isHTML()) {
			String temp = "";
			Text[] words = page.getWords();
			if (words == null)
				throw new Exception("No words found");
			for (Text text : words) {
				temp += text.toText() + " ";
			}
			content = temp;
			title = page.getTitle();
			if (title == null || title.length() == 0)
				title = "Unknown";
			url = page.getURL().toString();
			return new WebDocument(0, title, content, url);
		}
		else
			return WebFactory.getDocument(page);
	}
	
	private void _collectPage(Page page, String path) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			out.write(page.getContentBytes());
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Collection error. Check that exists " + path);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void execute() {
		BenchmarkRow bench = new BenchmarkRow("Crawling+Indexing+Computing TF-IDF");
		bench.start();
		IRCrawler crawler;
		try {
			BenchmarkRow br = new BenchmarkRow("Total crawl + index time");
			br.start();
			crawler = new IRCrawler(new Link("http://users.encs.concordia.ca/~comp479_2"));
			crawler.run();
			crawler.postProcess();
			br.stop();
			System.out.println(br.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		bench.stop();
		System.out.println(bench.toString());
	}
}
