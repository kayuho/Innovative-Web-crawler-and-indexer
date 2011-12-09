package domain.collection.documents;

public class WebDocument extends RankedDocument {
	private String _url;
	
	public WebDocument(int id, String title, String content, String url) {
		super(id, title);
		super.setContent(content);
		_url = url;
	}
	
	public static WebDocument fromString(String str) {
		String[] args = str.split("~");
		int id = Integer.parseInt(args[0]);
		int length = Integer.parseInt(args[1]);
		String title = "???";
		String url = "???";
		if (args.length > 2) {
			title = args[2].trim();
			url = args[3].trim();
		}
		WebDocument document = new WebDocument(id, title, null, url);
		document.setLengthInWords(length);
		return document;
	}
	
	public void setId(int newId) {
		id = newId;
	}
	
	public String getUrl() {
		return _url;
	}
	
	public void setUrl(String url) {
		_url = url;
	}
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof WebDocument) {
			WebDocument document = (WebDocument) o;
			if (_url.equals(document.getUrl())) 
				result = true;
			else {
				if (document.getVector() != null && getVector() != null)
					result = document.getVector().equals(getVector());
			}
		}
		return result;
	}
	
	public String toString() {
		return super.toString() + "~" + _url;
	}
}
