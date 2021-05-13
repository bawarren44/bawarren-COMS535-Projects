import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Uses a Bloomfilter in order to store record of DiffFile.txt. Used to check performance compared to 
 * if hashtable was used in EmpericalComparision.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class BloomDifferential {
	
	private BloomFilterFNV bFilter;
	private File difFile,dataFile;
	private int difFileSize,bitsPerElement;
	
	/**
	 * Sets values of the Bloomfilter to records in DiffFile.txt.
	 * 
	 * @param difFileName
	 * @param dataFileName
	 * @throws FileNotFoundException
	 */
	public BloomDifferential(int bitsPerElement, String difFileName, String dataFileName) throws FileNotFoundException{
		this.bitsPerElement = bitsPerElement;
		difFile = new File(difFileName);
		dataFile = new File(dataFileName);
		difFileSize = getDifSize();
		bFilter = createFilter();
		
	}
	
	/**
	 * Returns a Bloom Filter corresponding to the records in the differential fileDiffFile.txt.
	 * @throws FileNotFoundException
	 */
	public BloomFilterFNV createFilter() throws FileNotFoundException {
		 BloomFilterFNV b = new BloomFilterFNV(difFileSize,bitsPerElement);
		 
		 Scanner sc = new Scanner(difFile);
		 while(sc.hasNextLine()) {
			 String record = sc.nextLine();
			 Scanner s = new Scanner(record);
			 String key = "";
			 int count = 0;
			 while(s.hasNext()) {
				 if(count == 4) {
					 s.close();
					 b.add(key.trim());
					 break;
				 }
				 key += s.next() + " ";
				 count++;
			 }
		 }
		 sc.close();
		 return b;
	}
	
	/**
	 * Gets a key as parameter and returns the record corresponding to the record 
	 * by consulting the Bloomfilter first.
	 * @param key
	 * @return
	 * @throws FileNotFoundException
	 */
	public String retrieveRecord(String key) throws FileNotFoundException {
		if(key == null) {
			return null;
		}
		
		if(bFilter.appears(key)) {
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
	 * Gets the size of the DiffFile.txt
	 * @return
	 * @throws FileNotFoundException
	 */
	public int getDifSize() throws FileNotFoundException {
		Scanner sc = new Scanner(difFile);
		int count = 0;
		while(sc.hasNextLine()) {
			count++;
			sc.nextLine();
		}
		sc.close();
		return count;
		
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
