package domain.search;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import technical.helpers.BenchmarkRow;
import technical.helpers.Property;

import domain.GenericPosting;
import domain.Document;
import domain.collection.CollectionFactory;
import domain.index.IndexerThread;
import domain.index.spimi.DefaultInvertedIndex;
import domain.search.booleantree.InfixToPostfix;
import domain.search.booleantree.InvalidQueryException;
import domain.search.booleantree.QueryTree;
import domain.search.booleantree.QueryTreeBuilder;

public class QueryProcessor {

	private static DefaultInvertedIndex index = DefaultInvertedIndex.readFromFile("index.txt");

	private static Set<Document> matchingDocId;
	private static Iterator<Result> matchedIterator;
	private static BenchmarkRow matchingTime;
	private static BenchmarkRow pullingArticletime = new BenchmarkRow(null);

	private static String queryPositiveTerms;

	public static long getMatchingTime() {
		return matchingTime.getDuration();
	}

	public static long getPullingTime() {
		return pullingArticletime.getDuration();
	}

	public static boolean performBufferedQuery(String query) throws InvalidQueryException {
		matchingDocId = findMatchingPostingId(query);
		if (matchingDocId == null || matchingDocId.isEmpty() == true)
			return false;
		matchedIterator = generateRankResult(queryPositiveTerms, matchingDocId).iterator();
		return true;
	}

	private static Collection<Result> generateRankResult(String queryPositiveTerms, Set<Document> matchingDocument) {
		if (Property.getBoolean("enable_ranking") == false) {
			HashSet<Result> resultSet = new HashSet<Result>();
			for (Document p : matchingDocument) 
				resultSet.add(new Result(CollectionFactory.getCorpus().findArticle(p.getDocumentId()), 1));
			return resultSet;
		}
		else {
			TreeSet<Result> results = new TreeSet<Result>();
			// Looking to rank each document in regards to query positive terms.
			for (Document p : matchingDocument) {
				Result result = makeRank(CollectionFactory.getCorpus().findArticle(p.getDocumentId()),queryPositiveTerms);
				results.add(result);

			}
			System.out.println("Done ranking " +matchingDocument.size() +":"+ results.size() + " results");
			return results;
		}
	}

	//This methods applies Okapi BM25
	private static Result makeRank(GenericPosting abstractDocument, String queryPositiveTerms) {
		double N = CollectionFactory.getCorpus().size();	//corpus size
		double k1 = 1.5;
		double b = 0.75;
		double avgDl = CollectionFactory.getCorpus().getAllLength()/N;
		double result =0;
		for (String term : queryPositiveTerms.split(" ")) {
			double numberOfDocumentContainingT = index.getSet(term).size();
			double idfQI = Math.log((N - numberOfDocumentContainingT + 0.5)/(numberOfDocumentContainingT+0.5));
			double termFrequencyInDocument = 0;
			// Looking for termFrequencyInDocument
			for (Document p : index.getSet(term))
				if (p.getDocumentId() == abstractDocument.getId())
					termFrequencyInDocument = p.getOccurence();
			
			double top = termFrequencyInDocument*(k1+1);
			double bottom = termFrequencyInDocument+k1*(1-b+b*(abstractDocument.getLengthInWords()/avgDl));
			result += idfQI*(top/bottom);
		}
		return new Result(abstractDocument, result);
	}

	public static boolean hasNext() {
		return matchedIterator.hasNext();
	}
	
	public static Result next() {
		pullingArticletime.start();
		Result a;
		if (matchedIterator.hasNext())
			a = matchedIterator.next();
		else
			a = null;
		pullingArticletime.stop();
		return a;
	}

	public static int size() {
		if (matchingDocId == null)
			return 0;
		return matchingDocId.size();
	}

	private static String compressQuery(String query) {
		query = query.trim();
		
		query = query.replace("( ", "(");
		query = query.replace(" )", ")");
		query = query.replace(" NOT", " not");
		query = query.replace("NOT ", "not ");
		query = query.replace(" AND", " and");
		query = query.replace("AND ", "and ");
		query = query.replace(" OR", " or");
		query = query.replace("OR ", "or ");
		query = query.replace(" and not ", "^-");
		query = query.replace(" and not", "^-");
		query = query.replace(" or not ", "+-");
		query = query.replace(" or not", "+-");
		query = query.replace(" and ", "^");
		query = query.replace(" and", "^");
		query = query.replace("(not", "(-");

		query = query.replace(" or", "+");
		query = query.replace("^ ", "^");
		query = query.replace("- ", "-");
		query = query.replace("+ ", "+");
		query = query.replace(" not ", "^-");
		query = query.replace(" not", "^-");
		query = query.replace("not ", "-");

		if (Property.getBoolean("DefaultToOr") == true)
			//Setting the default boolean operator between words to OR
			query = query.replace(" ", "+");
		else
			//Setting the default boolean operator between words to AND
			query = query.replace(" ", "^");

		IndexerThread tt = new IndexerThread("Query compressor");
		StringTokenizer st = new StringTokenizer(query,"^-+()", true);
		String compressedQuery = "";
		while (st.hasMoreTokens()) {	
			String token = st.nextToken();
			if (token.equals("^") || token.equals("-") || token.equals("+") || token.equals("(") || token.equals(")"))
				compressedQuery = compressedQuery+token;
			else
				compressedQuery = compressedQuery+tt.compressToken(token);
		}
		return compressedQuery;
	}
	private static Set<Document> findMatchingPostingId(String query) throws InvalidQueryException {
		matchingTime = new BenchmarkRow(null);
		matchingTime.start();
		String compressedQuery = compressQuery(query);
		System.out.println(compressedQuery);
		String postfixRepresentation = InfixToPostfix.doTrans(compressedQuery);
		QueryTree qt = QueryTreeBuilder.getTree(postfixRepresentation);
		Set<Document> resultSet = qt.getResult(index);
		queryPositiveTerms = qt.getAllMatchedTerms();
		matchingTime.stop();
		return resultSet;
	}

	public static DefaultInvertedIndex getIndex() {
		return index;
	}


}
