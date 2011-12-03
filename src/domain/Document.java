package domain;

import java.util.TreeSet;

public class Document implements Comparable<Document> {

	private int documentId;
	private TreeSet<Integer> positions;
	private int occurence; // it will no longer be used when positions are working since the size of it will give us the occurrence
	private String term;
	
	public Document(String term, int documentId, int occurence) {
		super();
		this.documentId = documentId;
		this.occurence = occurence;
		this.term = term;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
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
		return getDocumentId();
	}
	@Override
	public boolean equals(Object o) {
		if ((o instanceof Document) == false)
			return false;
		Document otherPosting = (Document) o;
		if (otherPosting.getDocumentId() == getDocumentId())
			return true;
		else
			return false;
	}
	
	
	public static Document fromString(String term, String input) {
		String[] parts =input.split(",");
		int documentId = Integer.parseInt(parts[0], Character.MAX_RADIX);
		int occurence = Integer.parseInt(parts[1], Character.MAX_RADIX);
		return new Document(term, documentId, occurence);
	}
	
	public String toString() {
		return Integer.toString(getDocumentId(), Character.MAX_RADIX) + "," + Integer.toString(getOccurence(), Character.MAX_RADIX);
	}

	@Override
	public int compareTo(Document p) {
		return new Integer(this.getDocumentId()).compareTo(p.getDocumentId());
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
