package domain.vectorspace;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import technical.vector.DocumentVector;
import technical.vector.VectorSpace;

public class VectorSpaceBuilder {
	VectorSpace vectorSpace;
	
	public VectorSpaceBuilder() {
		vectorSpace = new VectorSpace();
	}
	
	public VectorSpace Building( String indexPath) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(indexPath)));
		
		String strLine;
		int dimension = 1;
		int totalDocuments = 0;//get the total document in the corpus
		while(( strLine = reader.readLine() ) != null){
			
			LinkedList<Integer> postingList = new LinkedList<Integer>();
			
			for(int docID : postingList){
				if(vectorSpace.hasDocument(docID)){
					continue;
				}
				double tfidf = ( 1+ Math.log10(0) )*( Math.log10( totalDocuments/postingList.size() ) );
				DocumentVector docVector = new DocumentVector();
				docVector.add(dimension,tfidf);
				vectorSpace.add( docID,docVector );	
			}
			dimension++ ;
		}
		
		reader.close();
		return vectorSpace;
	}

}
