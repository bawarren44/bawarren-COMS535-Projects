import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

/**
 * Like BloomDifferential, however this class uses a hashtable instead of a generated Bloom filter in order
 * to store record of DiffFile.txt. 
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class NaiveDifferential {
	 
	private Hashtable<String,Integer> bFilter = new Hashtable<String,Integer>();
	private File difFile, dataFile;
	private Random ran;
	
	/**
	 * Sets values of the hashtable to records in DiffFile.txt with random numbers assigned to them.
	 * 
	 * @param difFileName
	 * @param dataFileName
	 * @throws FileNotFoundException
	 */
	public NaiveDifferential(String difFileName, String dataFileName) throws FileNotFoundException{
		difFile = new File(difFileName);
		dataFile = new File(dataFileName);
		ran = new Random(214748364); //largest possible integer value
		createFilter();
	}
	
	/**
	 * Returns a Bloom Filter corresponding to the records in the differential fileDiffFile.txt.
	 * @throws FileNotFoundException
	 */
	public void createFilter() throws FileNotFoundException {
		Scanner sc = new Scanner(difFile);
		 while(sc.hasNextLine()) {
			 String record = sc.nextLine();
			 Scanner s = new Scanner(record);
			 String temp = "";
			 int count = 0;
			 while(s.hasNext()) {
				 temp = s.next();
				 if(count == 4) {
					 s.close();
					 break;
				 }
				 if(!bFilter.contains(s)) {
					 bFilter.put(temp, ran.nextInt());
				 }
				 count++;
			 }
		 }
		 sc.close();
	}
	
	/**
	 * Gets a key as parameter and returns the record corresponding to the record 
	 * by consulting the hashtable first.
	 * @param key
	 * @return
	 * @throws FileNotFoundException
	 */
	public String retrieveRecord(String key) throws FileNotFoundException {
		if(key == null) {
			return null;
		}
		
		Scanner sc = new Scanner(key);
		boolean check = true;
		for(int i = 0; i < 4; i++) {
			if(!bFilter.contains(sc.next())) {
				check = false;
			}
		}
		sc.close();
		
		if(check == true) {
			String record = checkFileForKey(key,difFile);
			if(record != null) {
				return record;
			}
		}else {
			String record = checkFileForKey(key,dataFile);
			if(record != null) {
				return record;
			}
		}
		return "record not found";
	}
	
	/**
	 * Checks if the file contains the specified key.
	 * @param key
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public String checkFileForKey(String key, File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()) {
			String record = sc.nextLine();
			Scanner s = new Scanner(record);
			String temp = "";
			int count = 0;
			while(s.hasNext()) {
				if(count == 4) {
					s.close();
					if(temp.trim().equals(key.trim())){
						return record.trim();
					}
					break;
				}
				temp += s.next() + " ";
				count++;
			}
		}
		sc.close();
		return null;
	}
}
