package technical.cluster;

import java.util.TreeSet;

import technical.vector.DocumentVector;

public class ClusterOFDocuments {
	private int clusterID;
	private DocumentVector centroidDocument;
	private TreeSet<DocumentVector> documents;
	private double rss;
	
	public ClusterOFDocuments(){
		rss = 0;
		documents = new TreeSet<DocumentVector>();
	}
		
	public void setId(int clusterID){
		this.clusterID = clusterID;
	}
	
	public int getId(){
		return clusterID;
	}
	
	public void setCentroid(DocumentVector centroidDocument){
		this.centroidDocument = centroidDocument;
	}
	
	public DocumentVector getCentroid(){
		return centroidDocument;
	}
	
	public void add(DocumentVector doc){
		documents.add(doc);
	}
	
	public TreeSet<DocumentVector> getDocumentSet(){
		return documents;
	}
	
	public void findCentroid(){
		DocumentVector centroid = new DocumentVector();
		for(DocumentVector docVector : documents ){
			for(int dimension: docVector.getDimensionMap().keySet()){
				double value = centroid.getValue(dimension) + docVector.getDimensionMap().get(dimension);
				centroid.add(dimension, value );
			}
		}
		centroidDocument = centroid;
	}
	
	public void addRSS(double rss){
		this.rss += rss;
	}
	
	public double getRSS(){
		return rss;
	}

	
}
