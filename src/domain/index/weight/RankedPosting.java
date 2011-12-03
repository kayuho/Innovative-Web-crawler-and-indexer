package domain.index.weight;

import domain.GenericPosting;



public class RankedPosting extends GenericPosting{
	
	private Td_Idf_Vector vr;
	
	public RankedPosting(int id, String title) {
		super(id, title);
	}

	public Td_Idf_Vector getVector() {
		return vr;
	}

	public void setVector(Td_Idf_Vector vr) {
		this.vr = vr;
	}
	
	
}
