import java.io.FileNotFoundException;

/**
 * This class tests whether it is faster to estimate Jaccard Similarities using MinHash matrix.
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class MinHashTime {

	/**
	 * Constructs and instance of minHashTime
	 */
	public MinHashTime() {
		
	}
	
	/**
	 * Using the folder and permutations to construct a minHashim instance (gets this time) and 
	 * then compares the time it takes to run exactJaccard vs. approximateJaccard.
	 * @param folder
	 * @param permutations
	 * @throws FileNotFoundException
	 */
	public void timer(String folder, int permutations) throws FileNotFoundException {
		double startTime = System.currentTimeMillis();
		MinHashSimilarities minHashSim = new MinHashSimilarities(folder, permutations);
		double endTime = System.currentTimeMillis();
		
		double timeOfConstruction = endTime-startTime;
		System.out.println("Time taken to construct MinHashSimilarities = " + timeOfConstruction + " ms ");
		
		String[] docs = minHashSim.getMinHash().allDocs();
		startTime = System.currentTimeMillis();
		for(int i = 0; i < docs.length; i++) {
			if(i == docs.length) {
				break;
			}
			for(int j = i+1; j < docs.length - i; j++) {
				minHashSim.exactJaccard(docs[i], docs[j]);
			}
		}
		endTime = System.currentTimeMillis();
		timeOfConstruction = endTime-startTime;
		
		System.out.println("Time taken to compute exact Jaccard between each document = " + timeOfConstruction + " ms ");

		
		startTime = System.currentTimeMillis();
		for(int i = 0; i < docs.length; i++) {
			if(i == docs.length) {
				break;
			}
			for(int j = i+1; j < docs.length - i; j++) {
				minHashSim.approximateJaccard(docs[i], docs[j]);
			}
		}
		endTime = System.currentTimeMillis();
		timeOfConstruction = endTime-startTime;
		
		System.out.println("Time taken to compute approximate Jaccard between each document = " + timeOfConstruction + " ms ");

	}
	
//	public static void main(String args[]) throws FileNotFoundException {
//		MinHashTime p = new MinHashTime();
//		p.timer("space", 600);
//	}
}
