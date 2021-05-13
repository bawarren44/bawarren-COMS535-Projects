import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Invokes MinHash to compute/estimate similarities. 
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class MinHashSimilarities {

	MinHash minHash;
	private int[][] termDocMatrix, minHashMatrix;
	
	/**
	 * Creates an instance of MinHash, and calls the methods termDocumentMatrix and minHashMatrix 
	 * to store the respective matrices.
	 * @param folder
	 * @param numPermutations
	 * @throws FileNotFoundException
	 */
	public MinHashSimilarities(String folder, int numPermutations) throws FileNotFoundException {
		minHash = new MinHash(folder,numPermutations);
		termDocMatrix = minHash.termDocumentMatrix();
		minHashMatrix = minHash.minHashMatrix();
	}
	
	/**
	 * Gets names of two files (in the document collection) file1 and file2 as parameters and returns 
	 * the exact Jaccard Similarity of the files. Use the termDocumentMatrix computed (by the constructor) 
	 * for this. Return type is double.
	 * @param file1
	 * @param file2
	 * @return
	 */
	public double exactJaccard(String file1, String file2) {
		ArrayList<Integer> file1Terms = new ArrayList<>();
		ArrayList<Integer> file2Terms = new ArrayList<>();
		
		int f1 = minHash.getDoc(file1), f2 = minHash.getDoc(file2);
		if(file1.equals(file2)){
			return 1.0;
		}
		
		for(int i = 0; i < termDocMatrix[f1].length; i++) {
			if(termDocMatrix[f2][i] != 0) {
				file2Terms.add(i);
			}
			if(termDocMatrix[f1][i] != 0) {
				file1Terms.add(i);
			}
		}
		
		
		ArrayList<Integer> union = new ArrayList<>();
		ArrayList<Integer> intersection = file1Terms;
		
		union.addAll(file1Terms);
		union.addAll(file2Terms);
		intersection.retainAll(file2Terms);

		return (double)((double)intersection.size()/(double)union.size());
	}
	
	/**
	 * Estimates and returns the Jaccard similarity of documents file1 and file2 by comparing 
	 * the MinHash signatures of file1 and file2. Use the MinHashMatrix computed by the constructor. 
	 * Return type is double.
	 * @param file1
	 * @param file2
	 * @return
	 */
	public double approximateJaccard(String file1, String file2) {
		if(file1.equals(file2)) {
			return 1.0;
		}
		int[] sig1 = minHashSig(file1);
		int[] sig2 = minHashSig(file2);
		int same = 0;
		for(int i = 0; i < sig1.length; i++) {
			if(sig1[i]==sig2[i]) {
				same++;
			}
		}
		
		
		return (double)((double)same/(double)sig1.length);
	}
	
	/**
	 * Returns the MinHash the minhash signature of the document named fileName. 
	 * Return type is an array of ints.
	 * @param fileName
	 * @return
	 */
	public int[] minHashSig(String fileName) {
		int docIdx = minHash.getDoc(fileName);
		int[] sig = new int[minHash.numPermutations()];
		for (int j = 0; j < minHash.numPermutations(); j++) {
			sig[j] = minHashMatrix[j][docIdx];
		}

		return sig;
	}
	
	/**
	 * Gives the minHashMatrix of the current minHash used to construct MinHashSimilarities
	 * @return
	 */
	public MinHash getMinHash() {
		return minHash;
	}
}
