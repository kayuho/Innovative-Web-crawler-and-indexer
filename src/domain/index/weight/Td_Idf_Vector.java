package domain.index.weight;

import java.text.DecimalFormat;
import java.util.TreeMap;

public class Td_Idf_Vector {

	private TreeMap<String, Double> vr = new TreeMap<String, Double>();



	public TreeMap<String, Double> getVector() {
		return vr;
	}
	
	public Td_Idf_Vector() {
		
	}
	public void setVector(TreeMap<String, Double> vector) {
		this.vr = vector;
	}
	
	public String toString(){
		String result = "";
		DecimalFormat df = new DecimalFormat("0.00");
		for (int i = 0; i < vr.size(); i++){
			result = result +  vr.firstEntry().getKey() + "," + df.format(vr.pollFirstEntry().getValue())+ " ";
		}
		return result;
	}
	
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Td_Idf_Vector){
			Td_Idf_Vector v = (Td_Idf_Vector)o;
			if (v.getVector().size()!=this.vr.size()){
				return false;
			} else {
			for (String s : vr.keySet()){
				for (String s2 : v.getVector().keySet()){
					if (!s.equals(s2) || v.getVector().get(s2)!=this.vr.get(s)){
						return false;
					}
				}
				
			}
			return true;
			}
		} else{
			return false;
		}
	}
	
}
