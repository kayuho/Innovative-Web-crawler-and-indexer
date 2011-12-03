package technical.helpers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class Utils {

	/**
	 * Look for files matching an extension in a given folder
	 * @param directoryPath path to look for
	 * @param extension extension to filter for
	 * @param relativePath if true will return the path of the file relative to the directoryPath. False will return a full path
	 * @return array of string, one string per found file
	 */
	public static List<String> getAllFiles(String directoryPath, final String extension, boolean relativePath) {
		File dir = new File(directoryPath);

		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		    	if (name.startsWith("."))
		    		return false;
		    	if (name.endsWith(extension))
		    		return true;
		    	return false;
		    }
		};
		
		String[] r = dir.list(filter);
		if (relativePath == false)
			for (int i=0; i<r.length; i++)
				r[i] = dir.getAbsolutePath() + "/" + r[i];

		List<String> result = new ArrayList<String>();
		if (r != null) {
			result = Arrays.asList(r);
		}
		return result;
	}
	
	static Pattern noTags = java.util.regex.Pattern.compile("\\<.*?\\>");
	public static String removeTags(String input) {
		return noTags.matcher(input).replaceAll("");
	}

	static Pattern noEntities = java.util.regex.Pattern.compile("\\&.*?\\;");
	public static String removeEntities(String input) {
		return noEntities.matcher(input).replaceAll("");
	}
	
	static Pattern noNumber = java.util.regex.Pattern.compile("[^a-z|A-Z]");
	public static String noNumber(String input) {
		return noNumber.matcher(input).replaceAll("");
	}

	
	static Pattern noSpecialChar = java.util.regex.Pattern.compile("[^a-z|0-9|A-Z]");
	public static String removeSpecialChar(String input) {
		return noSpecialChar.matcher(input).replaceAll("");
	}
	
	public static void main(String[] args) {
		List<String> files = Utils.getAllFiles("reuters21578", ".sgm", false);
		for (String f : files) {
			System.out.println(f);
		}
		
		System.out.println("H@#$ELLO world === " + removeSpecialChar("123H@#$ELLO world"));
	}
	
	public static String padWithZero(int number, int totalLength) {
		if (number < 10)
			return "00" + number;
		if (number < 100)
			return "0" + number;
		return String.valueOf(number);
	}
	
	private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
	
    public static String MD5(String text) { 
    	try{
		    MessageDigest md;
		    md = MessageDigest.getInstance("MD5");
		    byte[] sha1hash = new byte[40];
		    md.update(text.getBytes("iso-8859-1"), 0, text.length());
		    sha1hash = md.digest();
		    return convertToHex(sha1hash);
    	} catch(NoSuchAlgorithmException e) {
    		e.printStackTrace();
    	} catch(UnsupportedEncodingException e) {
		    e.printStackTrace();
        }
    	return null;
	}
    
    public static boolean serialize(Object o, String path){
	    	try{
		    	FileOutputStream fos = new FileOutputStream(Constants.basepath + path);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(o);
				out.close();
				return true;
		    } catch (IOException e) {
		    	e.printStackTrace();
			}
				return false;
    	}
    
    public static Object loadSerialized(String path){
    	try{
    		FileInputStream fis = new FileInputStream(Constants.basepath + path);
			ObjectInputStream in = new ObjectInputStream(fis);
			Object o = in.readObject();
			in.close();
			return o;
	    } catch (IOException e) {
	    	return null;
		} catch (ClassNotFoundException e){
			e.printStackTrace();
			return e;
		}
	}
    
    public static boolean fileExists(String path){
    	if((new File(Constants.basepath + path)).exists())
			return true;
    	else
    		return false;
    }
}
