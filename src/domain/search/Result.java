package domain.search;


import java.util.HashSet;

import domain.GenericPosting;
import domain.Document;

public class Result implements Comparable<Result> {

	private GenericPosting result;
	private double rank;
	private HashSet<Document> matchesFor;
	
	public Result(GenericPosting result, double rank) {
		super();
		this.result = result;
		this.rank = rank;
	}

	public Result(GenericPosting result, double rank, Document p) {
		super();
		this.result = result;
		this.rank = rank;
		matchesFor = new HashSet<Document>();
		matchesFor.add(p);
	}

	public GenericPosting getResult() {
		return result;
	}
	public double getRank() {
		return rank;
	}
	
	
	@Override
	/**
	 * USed for insertion in a sorted array.. the better the result the first you are
	 */
	public int compareTo(Result other) {
		if (other.rank == this.rank)
			return  new Integer(this.result.getId()).compareTo(other.getResult().getId());
		return Double.compare(other.rank,this.rank);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Result))
			return false;
		return (((Result) o).getResult().getId() == getResult().getId());
	}
	
	public void addPosting(Document document) {
		if (matchesFor == null)
			matchesFor = new HashSet<Document>();
		matchesFor.add(document);
	}
	
	
}
