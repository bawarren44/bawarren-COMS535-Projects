import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;

/**
 * Constructs minHash getting a termDocMatrix, minHashMatrix using a folder of documents and
 * number of permutations
 * 
 * @author Brad Warren	bawarren@iastate.edu
 *
 */
public class MinHash {

	public String[] allDocs;
	public int[][] minHashMatrix, termDocMatrix;
	public Integer[][] permutations;
	private int numTerms, numPermutations;
	private HashMap<String,Integer> U = new HashMap<String,Integer>();

	
	/**
	 * Constructs a MinHash from a folder of files, with a given number of permutations.
	 * @param folder
	 * @param numPermutations
	 * @throws FileNotFoundException
	 */
	public MinHash(String folder, int numPermutations) throws FileNotFoundException {
		this.numPermutations = numPermutations;
		File files = new File(folder);
		File[] listOfFiles = files.listFiles();
		allDocs = new String[files.list().length];
		minHashMatrix = new int[numPermutations][allDocs.length];
		
		int i = 0;
		//sets termDocMatrix and allDocs
		for(File file : listOfFiles) {
			String str = file.getPath().substring(file.getPath().indexOf('/', 0)+1, file.getPath().length());
			allDocs[i] = str;
			initializeTerms(file);
			i++;
		}
		
		numTerms = U.size();
		U.clear();
		ArrayList<String> terms = new ArrayList<String>(numTerms);
		termDocMatrix = new int[allDocs.length][numTerms];
		for(int j = 0; j < allDocs.length; j++) {
			Arrays.fill(termDocMatrix[j], 0);
		}
		
		i = 0;
		for(File file : listOfFiles) {
			getTerms(file, termDocMatrix[i],terms);
			i++;
		}

		//set permutations
		permutations = new Integer[numPermutations][numTerms];
		List<Integer> temp = new ArrayList<>();
		for(int j = 0; j < terms.size(); j++) {
			temp.add(j);
		}
		
		for(int j = 0; j < numPermutations; j++) {
			permutations[j] = (Integer[])fisherYatesShuffle(temp,numTerms);
		}



		//make minHashMatrix
		for(i = 0; i < numPermutations(); i++) {
			for(int j = 0; j < allDocs.length; j++) {
				ArrayList<Integer> minTracker = new ArrayList<>();
				for(int k = 0; k < terms.size(); k++) {
					if(termDocMatrix[j][k] != 0) {
						minTracker.add(k);
					}
				}
				Collections.sort(minTracker);
				try{
					minHashMatrix[i][j] = findMin(minTracker,i); 
				}catch(Exception e){
					minHashMatrix[i][j] = 0;
				}
			}
		}
		terms.clear();
		permutations = null;		
	}
	
	/**
	 * Returns an array of Strings consisting of all the names of files in the document collection
	 * @return
	 */
	public String[] allDocs() {
		return allDocs;
	}
	
	/**
	 * Returns the MinHash Matrix of the collection. Return type is 2D array of ints.
	 * @return
	 */
	public int[][] minHashMatrix(){
		return minHashMatrix;
	}
	
	/**
	 *  Return the term document matrix of the collection. Return type is 2D
	 * @return
	 */
	public int[][] termDocumentMatrix(){
		return termDocMatrix;
	}
	
	/**
	 * Returns the size of union of all documents (after preprocessing). Note that each document
	 * is a multiset of term.
	 * @return
	 */
	public int numTerms() {
		return numTerms;
	}
	
	/**
	 * Returns the number of permutations used to construct the MinHash matrix.
	 * @return
	 */
	public int numPermutations() {
		return numPermutations;
	}
	
	/**
	 * Responsible for setting the terms into termdocmatrix and getting the initial number of terms.
	 * @param file
	 * @param terms
	 * @param globalTerms
	 * @throws FileNotFoundException
	 */
	public void initializeTerms(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()) {
			String line = new String(sc.nextLine());
			line = line.toLowerCase();
			line = line.replaceAll("[.,;:']", " "); // removing unwanted characters
			Scanner s = new Scanner(line);
			while(s.hasNext()) {
				String temp = s.next();
				if(!(temp.length() <= 2) && !temp.equals("the")
//						) {
						&& !temp.contains(">") && 
						!temp.contains("<") && !temp.contains("?") && !temp.contains("*") 
						&& !temp.contains("(") && !temp.contains(")") && !temp.contains("[")
						&& !temp.contains("]") && !temp.contains("-") && !temp.contains("_")) { //restrictions on the strings we want
					if(!U.containsKey(temp)) {
						U.put(temp, U.size());
					}
				}
			}
			s.close();
		}
		sc.close();
	}
	
	/**
	 * Responsible for setting the terms into termdocmatrix and getting the initial number of terms.
	 * @param file
	 * @param terms
	 * @param globalTerms
	 * @throws FileNotFoundException
	 */
	public void getTerms(File file, int[] terms, ArrayList<String> globalTerms) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()) {
			String line = new String(sc.nextLine());
			line = line.toLowerCase();
			line = line.replaceAll("[.,;:']", " "); // removing unwanted characters
			Scanner s = new Scanner(line);
			while(s.hasNext()) {
				String temp = s.next();
				if(!(temp.length() <= 2) && !temp.equals("the")
//						) {
						&& !temp.contains(">") && 
						!temp.contains("<") && !temp.contains("?") && !temp.contains("*") 
						&& !temp.contains("(") && !temp.contains(")") && !temp.contains("[")
						&& !temp.contains("]") && !temp.contains("-") && !temp.contains("_")) { //restrictions on the strings we want
					if(!globalTerms.contains(temp)) {
						globalTerms.add(temp);
						terms[globalTerms.size()-1]++;
					}else {
						terms[globalTerms.indexOf(temp)]++;
					}
				}
			}
			s.close();
		}
		sc.close();
	}
	
	/**
	 * Used to create the permutations. Returns a single permutation at a time.
	 * @param temp2
	 * @param n
	 * @return
	 */
	public Integer[] fisherYatesShuffle(List<Integer> temp2, int n) {
		Integer[] arr = new Integer[temp2.size()];
		arr = temp2.toArray(arr);
		Random rand = new Random();
		for(int i = n-1; i > 0; i--) {
			int j = rand.nextInt(i+1);
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
		return arr;
	}
	
	/**
	 * Returns the document index in the allDocs array
	 * @param doc
	 * @return
	 */
	public int getDoc(String doc) {
		for(int i = 0; i < allDocs.length; i++) {
			if(allDocs[i].equals(doc)) {
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Returns the lowest value in the permutation arrays 
	 * @param terms
	 * @param perm
	 * @return
	 */
	public int findMin(ArrayList<Integer> terms, int perm) {
		List<Integer> ints = new ArrayList<>();
		for(int i = 0; i < terms.size(); i++) {
			ints.add(permutations[perm][terms.get(i)]);
		}
		Collections.sort(ints);
		return ints.get(0);
	}
}
