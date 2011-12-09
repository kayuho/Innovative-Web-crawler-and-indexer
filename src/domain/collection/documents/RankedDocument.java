package domain.collection.documents;

import domain.index.weight.Td_Idf_Vector;



public class RankedDocument extends GenericDocument{
	
	private Td_Idf_Vector vector;
	
	public RankedDocument(int id, String title) {
		super(id, title);
	}

	public Td_Idf_Vector getVector() {
		return vector;
	}

	public void setVector(Td_Idf_Vector vector) {
		this.vector = vector;
	}
	
	public boolean equals(Object o) {
		if (o instanceof RankedDocument) {
			RankedDocument document = (RankedDocument) o;
			if (getId() == document.getId())
				return true;
			else
				return (getVector().equals(document.getVector()));
		}
		else
			return false;
	}
	
	public static RankedDocument create(GenericDocument posting) {
		return new RankedDocument(posting.getId(), posting.getTitle());
	}
}
