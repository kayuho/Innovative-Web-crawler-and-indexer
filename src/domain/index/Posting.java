package domain.index;

import java.util.TreeSet;

public class Posting implements Comparable<Posting> {

	private int postingId;
	private TreeSet<Integer> positions;
	private int occurence; // it will no longer be used when positions are working since the size of it will give us the occurrence
	private String term;
	
	public Posting(String term, int postingId, int occurence) {
		super();
		this.postingId = postingId;
		this.occurence = occurence;
		this.term = term;
	}

	public int getPostingId() {
		return postingId;
	}

	public void setDocumentId(int documentId) {
		this.postingId = documentId;
	}

	public int getOccurence() {
		return occurence;
	}

	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}
	
	public void add(int add) {
		occurence += add;
	}
	
	@Override
	public int hashCode() {
		return getPostingId();
	}
	@Override
	public boolean equals(Object o) {
		if ((o instanceof Posting) == false)
			return false;
		Posting otherPosting = (Posting) o;
		if (otherPosting.getPostingId() == getPostingId())
			return true;
		else
			return false;
	}
	
	
	public static Posting fromString(String term, String input) {
		String[] parts =input.split(",");
		int documentId = Integer.parseInt(parts[0], Character.MAX_RADIX);
		int occurence = Integer.parseInt(parts[1], Character.MAX_RADIX);
		return new Posting(term, documentId, occurence);
	}
	
	public String toString() {
		return Integer.toString(getPostingId(), Character.MAX_RADIX) + "," + Integer.toString(getOccurence(), Character.MAX_RADIX);
	}

	@Override
	public int compareTo(Posting p) {
		return new Integer(this.getPostingId()).compareTo(p.getPostingId());
	}

	public String getTerm() {
		return term;
	}

	public TreeSet<Integer> getPositions() {
		return positions;
	}

	
	public void setPositions(TreeSet<Integer> positions) {
		this.positions = positions;
	}
}
