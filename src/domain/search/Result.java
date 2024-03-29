package domain.search;


import java.util.HashSet;

import domain.collection.documents.GenericDocument;
import domain.index.Posting;

public class Result implements Comparable<Result> {

	private GenericDocument result;
	private double rank;
	private HashSet<Posting> matchesFor;
	
	public Result(GenericDocument result, double rank) {
		super();
		this.result = result;
		this.rank = rank;
	}

	public Result(GenericDocument result, double rank, Posting p) {
		super();
		this.result = result;
		this.rank = rank;
		matchesFor = new HashSet<Posting>();
		matchesFor.add(p);
	}

	public GenericDocument getResult() {
		return result;
	}
	public double getRank() {
		return rank;
	}
	
	
	@Override
	/**
	 * Used for insertion in a sorted array.. the better the result the first you are
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
	
	public void addPosting(Posting posting) {
		if (matchesFor == null)
			matchesFor = new HashSet<Posting>();
		matchesFor.add(posting);
	}
}
