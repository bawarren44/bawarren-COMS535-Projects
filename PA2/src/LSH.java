import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * This class implements locality sensitive hashing to detect near duplicates of a document.
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class LSH {

	private ArrayList<ArrayList<ArrayList<Integer>>> hTables;
	private int A,B,N; //2N???
	private String[] docNames;
	
	/**
	 * Constructs an instance of LSH, where docNames is an array of Strings consisting of names 
	 * of documents/files in the document collection, and minHashMatrix is the MinHash matrix of 
	 * the document collection and bands is the number of bands to be used to perform locality sensitive hashing.
	 * @param minHashMatrix
	 * @param docNames
	 * @param bands
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		//Assume ith column of minHashMatrix is the minHash signature of docNames[i]
		this.docNames = docNames;
		int k = minHashMatrix.length;
		N =minHashMatrix[0].length;
		hTables  = new ArrayList<>(bands);
		boolean rwhole = false;
		double rr = ((double)(double)k / (double)bands);
		if(rr %1 == 0) {
			rwhole = true;
		}
		
		//initialization of hTables
		for(int i = 0; i < bands; i++) {
			hTables.add(new ArrayList<>(N));
			for(int j = 0; j < N; j++) {
				hTables.get(i).add(new ArrayList<>(docNames.length));
			}
		}

		int r = k/bands; //last band has +1 if rwhole = false
//		System.out.println(r);
		Random rand = new Random(N);
		A = rand.nextInt()*N;
		B = rand.nextInt()*N;
		
		
		// Split each minhashsig into bands or r length
		//hash each band and place into the table corresponding to the band
		//i.e T1[h(Mi^1)] -> store the minhashmatrix index
		ArrayList<Integer> tuple = new ArrayList<>();
		
		boolean nextBand = false;
		for(int i = 0; i < docNames.length; i++) {
			int bandIdx = 0;
			for(int j = 0; j < k; j++) {
				tuple.add(minHashMatrix[j][i]);
				if(rwhole == true && tuple.size() == r) {
					hTables.get(bandIdx).get(Math.abs(hash(tuple))).add(i);
					tuple.clear();
					nextBand = true;
				}
				if(rwhole == false && tuple.size() == r && bandIdx != bands -1) {
					hTables.get(bandIdx).get(Math.abs(hash(tuple))).add(i);
					tuple.clear();
					nextBand = true;
				}
				if(rwhole == false && j == k-1 && tuple.size() != 0) {
					hTables.get(bandIdx).get(Math.abs(hash(tuple))).add(i);
					tuple.clear();
					nextBand = true;
				}
				if(nextBand == true) {
					nextBand = false;
					bandIdx++;
				}
			}
		}
		
		
	}
	
	/**
	 * Takes name of a document docName as parameter and returns an array list of names of the near 
	 * duplicate documents. Return type is ArrayList of Strings
	 * @param docName
	 * @return
	 */
	public ArrayList<String> nearDuplicatesOf(String docName){
		ArrayList<String> retVal = new ArrayList<String>(); 
		int docIdx = 0;
		for(int i = 0; i < docNames.length; i++) { //Gets our document index
			if(docNames[i].equals(docName)) {
				docIdx = i;
			}
		}
		
		//retieves those document names from LSH that are in the same rows as our document index
		for(int i = 0; i < hTables.size(); i++) {
			for(int j = 0; j < hTables.get(i).size(); j++) {
				if(hTables.get(i).get(j).size() > 0) {
					if(hTables.get(i).get(j).contains(docIdx)) {
						Iterator iter = hTables.get(i).get(j).iterator();
						while(iter.hasNext()) {
							int next = (int)iter.next();
							if(!retVal.contains(docNames[next])) {
								if(next != docIdx) {
									retVal.add(docNames[next]);
								}	
							}						
						}
					}
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Hash function to hash each tuple 
	 * @param tuple
	 * @return
	 */
	public int hash(ArrayList<Integer> tuple) {
		int hash = tuple.hashCode();
		
		hash = (A*hash) + B;
		hash = hash % (N);

		return hash;
	}
}
