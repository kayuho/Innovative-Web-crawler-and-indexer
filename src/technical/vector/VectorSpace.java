package technical.vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class VectorSpace {
	
	private HashMap<Integer, DocumentVector> vectorMap;
	private double tfOfDocA;
	private double tfOfDocB;
	
	public VectorSpace(){
		vectorMap = new HashMap<Integer, DocumentVector>();
		tfOfDocA = 0;
		tfOfDocB = 0;
	}
	
	public void add(int docID, DocumentVector docVec){
		vectorMap.put(docID, docVec);
	}
	
	public HashMap<Integer, DocumentVector> getVectorMap(){
		return vectorMap;
	}
	
	public DocumentVector sumOfVectors(DocumentVector docVectorA, DocumentVector docVectorB){
		
		DocumentVector sumVectors = new DocumentVector();
		HashSet<Integer> dimensions = new HashSet<Integer>();
		dimensions.addAll(docVectorA.getDimensionMap().keySet());
		dimensions.addAll(docVectorB.getDimensionMap().keySet());

		for(int dimension: dimensions){
			tfOfDocA = docVectorA.getDimensionMap().get(dimension);
			tfOfDocB = docVectorB.getDimensionMap().get(dimension);
			double sum = tfOfDocA + tfOfDocB;
			sumVectors.add(dimension, sum);
		}
		return sumVectors;
	}
	
	public DocumentVector diffOfVectors(DocumentVector docVectorA, DocumentVector docVectorB){
		DocumentVector diffVectors = new DocumentVector();
		HashSet<Integer> dimensions = new HashSet<Integer>();
		dimensions.addAll(docVectorA.getDimensionMap().keySet());
		dimensions.addAll(docVectorB.getDimensionMap().keySet());

		for(int dimension: dimensions){
			tfOfDocA = docVectorA.getDimensionMap().get(dimension);
			tfOfDocB = docVectorB.getDimensionMap().get(dimension);
			double diff = tfOfDocA - tfOfDocB;
			diffVectors.add(dimension, diff);
		}
		return diffVectors;
	}
	
	public double distanceBetween(DocumentVector docVectorA, DocumentVector docVectorB){
		return docVectorA.distanceTo(docVectorB);
	}
	
	public boolean hasDocument(int docID){
		return vectorMap.containsKey(docID);
	}
	
	public double cosineSimilarity(DocumentVector docVector1, DocumentVector docVector2){
		
		double sum = 0;
		
		DocumentVector dvNormalize1 = docVector1.normalizedLength();
		DocumentVector dvNormalize2 = docVector2.normalizedLength();
		
		Set<Integer> dimensionOfdv1 = dvNormalize1.getDimensionMap().keySet();
		dimensionOfdv1.retainAll(dvNormalize2.getDimensionMap().keySet());
		
		for(int dimension : dimensionOfdv1){
			sum +=  (dvNormalize1.getValue(dimension) * dvNormalize2.getValue(dimension) );
		}
		
		return Math.acos(sum);
	}
	
}
