package technical.vector;

import java.util.HashMap;
import java.util.Map;

public class DocumentVector {
	
	private Map<Integer, Double> dimensionMap;
	private double sum;
	
	public DocumentVector(){
		dimensionMap = new HashMap<Integer, Double>();
	}
	
	public double length(){
		sum = 0;
		for(double value : dimensionMap.values() ){
			sum += Math.pow(value,2);
		}
		return Math.sqrt(sum);
	}
	//Euclidean Distance
	public double distanceTo(DocumentVector document){
		sum = 0;
		double anotherDimension = 0;
		for(int key : dimensionMap.keySet()){
			if(document.getDimensionMap().containsKey(key))
				anotherDimension = document.getDimensionMap().get(key);
			else 
				anotherDimension = 0;
			sum += Math.pow( (dimensionMap.get(key) - anotherDimension), 2 );
		}
		
		for(int key : document.getDimensionMap().keySet() ){
			if( dimensionMap.containsKey(key) )
				sum += Math.pow(dimensionMap.get(key),2);
		}
		return Math.sqrt(sum);
	}
	
	public Map<Integer, Double> getDimensionMap(){
		return dimensionMap;
	}
	
	public void add(int dimension , double tf){
		dimensionMap.put(dimension, tf);
	}
	
	public double getValue(int dimension){
		return dimensionMap.containsKey(dimension) ? dimensionMap.get(dimension):0.0;
	}
	
	public DocumentVector normalizedLength(){
		DocumentVector normalizeVector = new DocumentVector();
		double scalar = 1/this.dimensionMap.size();
		for(int key : dimensionMap.keySet()){
			double value = dimensionMap.get(key)*scalar;
			normalizeVector.add(key,value);
		}
		return normalizeVector;
	}

}
