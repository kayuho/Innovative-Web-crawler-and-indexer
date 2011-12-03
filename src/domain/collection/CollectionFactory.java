package domain.collection;

import technical.helpers.Property;

public class CollectionFactory {

	static Collection collection;

	public synchronized static Collection getCorpus(){
		if (collection == null) {
			if (Property.getBoolean("weightedCorpus") == true){
				collection = new RankedCollection();
			} else{
				collection = new Collection();
			}
		}
		return collection;
	}
}
