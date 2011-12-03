package domain.search.spelling;
import domain.index.spimi.DefaultInvertedIndex;
import domain.index.spimi.IInvertedIndex;

public class fromIndex {

	public static void main(String[] args) {
		IInvertedIndex i = DefaultInvertedIndex.readFromFile("index.txt");
		for (String s : i) {
			System.out.println(i + " " + Soundex.soundex(s));
		}
	}
}
