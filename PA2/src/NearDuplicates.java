import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Puts together MinHash and LSH to detect near duplicates in a document collection
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class NearDuplicates {

	private LSH lsh;
	private MinHashSimilarities mhs;
	private double s;
	
	/**
	 * Uses folder and permutations to construct an instance of minHashSimilarities and uses minhash of 
	 * minHashSimilarities and bands to construct LSH.
	 * @param folder
	 * @param permutations
	 * @param s
	 * @param bands
	 * @throws FileNotFoundException
	 */
	public NearDuplicates(String folder, int permutations, double s) throws FileNotFoundException {
		mhs = new MinHashSimilarities(folder, permutations);
		double  r = 1;
		boolean p = false;
		double b;
		while(true) {
			for(b = 1; b < 150; b++) {
				if(Math.abs((double)Math.pow((double)1/b,(double) 1/r) - .7) <= .02) {
					if(Math.abs(b*r - permutations) <= 1 || Math.abs((double)(double)permutations/(double)r - b) <= 1 
							|| Math.abs((double)(double)permutations/(double)b - r) <= 1) {
						p = true;
						break;					
					}
				}
			}
			if(p == true) {
				break;
			}
			r++;
			if(r == 100000) {
				System.out.println("stuck in endless loop: could not find possible r and b values");
			}
		}
		
		lsh = new LSH(mhs.getMinHash().minHashMatrix(),mhs.getMinHash().allDocs(), (int)b);
	}
	
	/**
	 * Gets the near duplicates of a document by first finding the duplicates with LSH and then using 
	 * minHashSimilarities to calculate distance from threshold.
	 * @param docName
	 * @return
	 */
	public ArrayList<String> nearDuplicateDetector(String docName){
		ArrayList<String> nearDups = lsh.nearDuplicatesOf(docName);
		
		for(int i = 0; i < nearDups.size(); i++) {
			if(mhs.approximateJaccard(docName, nearDups.get(i)) < s) {
				nearDups.remove(i);
			}
		}
		return nearDups;
	}
//	public static void main(String args[]) throws FileNotFoundException {
//		NearDuplicates l = new NearDuplicates("temp",600,.9);
//		
//		ArrayList<String> p = l.nearDuplicateDetector("space-985.txt");
//		for(String k : p) {
//			System.out.println(k);
//		}
//	}
}
