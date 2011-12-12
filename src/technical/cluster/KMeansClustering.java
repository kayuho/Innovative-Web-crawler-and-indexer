package technical.cluster;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import technical.vector.DocumentVector;
import technical.vector.VectorSpace;

public class KMeansClustering {
	private VectorSpace vectorSpace;
	private int k;
	private TreeSet<ClusterOFDocuments> clusters;
	private TreeSet<DocumentVector> documentSeeds;
	private Random randomSeedGenerator; 
	private ClusterOFDocuments docCluster;
	private double currentRSS = Double.MAX_VALUE;
	private double criteria;
	
	public KMeansClustering(VectorSpace vectorSpace, int k){
		this.k = k;
		this.vectorSpace = vectorSpace;
		clusters = new TreeSet<ClusterOFDocuments>();
		documentSeeds = new TreeSet<DocumentVector>();
		randomSeedGenerator = new Random();
		criteria = 0.8; // need to decide on the criteria
	}
	
	public TreeSet<ClusterOFDocuments> performeClusterOperation(){
		
		int maxRandomValue = vectorSpace.getVectorMap().size();
		
		//Random Seed Generator
		for(int i = 0; i<k;){
			int selectRandomSeedId = randomSeedGenerator.nextInt(maxRandomValue);
			DocumentVector seed = vectorSpace.getVectorMap().get(selectRandomSeedId);
			if(vectorSpace.getVectorMap().containsKey(selectRandomSeedId) && ( !documentSeeds.contains(seed) ) ){
				documentSeeds.add(seed);
				i++;
			}
		}
		
		for(DocumentVector clusterCentroid : documentSeeds){
			docCluster = new ClusterOFDocuments();
			docCluster.setCentroid(clusterCentroid);
			clusters.add(docCluster);
		}
		
		double checkingDistance = Double.MAX_VALUE;
		
		//add document to cluster
		do{
			for(DocumentVector currentDocVector : documentSeeds){
				
				ClusterOFDocuments tempCluster = new ClusterOFDocuments();
				double distance = 0;
				
				for(ClusterOFDocuments currentCluster : clusters){
					distance = currentCluster.getCentroid().distanceTo(currentDocVector);
					if( distance < checkingDistance ){
						checkingDistance = distance;
						tempCluster = currentCluster;
					}
				}
				
				ClusterOFDocuments realCluster = clusters.floor(tempCluster);
				realCluster.add(currentDocVector);
				realCluster.addRSS(Math.pow(distance,2));
			}
			
		}while(stoppingCriterion());
		
		return clusters;
		
	}

	private boolean stoppingCriterion(){
		double RSS = 0;
		for(ClusterOFDocuments cluster : clusters){
			cluster.findCentroid();
			RSS += cluster.getRSS();
		}
		boolean checking = ( (RSS/currentRSS) > criteria );
		currentRSS = RSS;
		return checking;
	}
}
