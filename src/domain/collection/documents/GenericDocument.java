package domain.collection.documents;

public class GenericDocument implements Comparable<GenericDocument> {

	protected Integer id;
	protected String title;
	protected String content;
	protected int length = -1;


	public GenericDocument(int id, String title) {
		this.id =id;
		this.title = title.replace('\n', ' ');
	}

	public void setContent(String text) {
		this.content = text;
	}
	public GenericDocument(int id, String title, int length) {
		this.id =id;
		this.title = title.replace('\n', ' ');
		this.length = length;
	}

	public int getLengthInWords() {
		return length;
	}

	public void setLengthInWords(int length) {
		this.length = length;
	}

	public void clearContent() {
		content=null;
	}

	
	public String toString() {
		return getId() + "~" + getLengthInWords() + "~" + getTitle();
	}
	
	public static GenericDocument fromString(String input) {
		String[] parts = input.split(",");
		int id = Integer.parseInt(parts[0]);
		int length = Integer.parseInt(parts[1]);
		String title = "???";
		if (parts.length > 2)
			title = parts[2].trim();
		return new GenericDocument(id, title, length);
	}

	@Override
	public int compareTo(GenericDocument o) {
		return (new Integer(this.getId())).compareTo(o.getId());
	}
	
	public int getId() {
		return id;
	}

	public String getText() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public GenericDocument() {
		super();
	}
}