package domain.index.reuters;

import domain.GenericPosting;
import domain.index.DiskReaderThread;

public class ReutersDocument extends GenericPosting {

	
	public ReutersDocument(int id, String title, String text) {
		super(id, title);
		super.setContent(text);
	}
	

	@Override
	public String getText() {
		if (content != null) return content;
		//Make article act as a proxy here
		return DiskReaderThread.getArticleById(getId()).getText();
	}

}
