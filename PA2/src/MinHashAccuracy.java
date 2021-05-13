import java.io.FileNotFoundException;

/**
 * MinHashAccuracy that tests the how accurately MinHash matrix can be used to estimate Jaccard Similarity
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class MinHashAccuracy {
	
	/**
	 * Creates an instance of MinHashAccuracy
	 */
	public MinHashAccuracy() {
		
	}
	
	/**
	 * From folder name and permutations we construct a minHashSimilarities instance and use this to
	 * determine the number of pairs whose approximateJaccard and exactJaccard have a difference greater than s.
	 * @param folder
	 * @param permutations
	 * @param s
	 * @throws FileNotFoundException
	 */
	public void accuracy(String folder, int permutations, double s) throws FileNotFoundException{
		MinHashSimilarities minHashSim = new MinHashSimilarities(folder, permutations);
		String[] docs = minHashSim.getMinHash().allDocs();
		int count = 0;
		for(int i = 0; i < docs.length; i++) {
			if(i == docs.length) {
				break;
			}
			for(int j = i+1; j < docs.length - i; j++) {
				double approx = 0, exact = 0;
				approx = minHashSim.approximateJaccard(docs[i], docs[j]);
				exact = minHashSim.exactJaccard(docs[i], docs[j]);
				if(Math.abs(approx - exact) > s){
					count++;
				}
			}
		}
		System.out.println("Number of pairs where Jaccard difference > error = " + count);
	}
	
//	public static void main(String args[]) throws FileNotFoundException {
//		MinHashAccuracy b = new MinHashAccuracy();
//		b.accuracy("space", 600, .04);
//	}
}
