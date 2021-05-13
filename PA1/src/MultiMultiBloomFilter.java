import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

/**
 * 
 * Bloom filter implementation of a Bloom filter with multiple tables each corresponding to their 
 * own random hash function.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class MultiMultiBloomFilter {
	
	private int filterSize, dataSize, numHashes, setSize;
	private BitSet bFilter[];
	private int abValues[][];
	private Random rand;


	/**
	 * creates a Bloom filter with multiple tables one for each hash function. Sets all bits in bFilter
	 * to 0.
	 * @param setSize
	 * @param bitsPerElement
	 */
	public MultiMultiBloomFilter(int setSize, int bitsPerElement){
		rand = new Random(setSize);
		filterSize = setSize * bitsPerElement;
		this.setSize = setSize;
		numHashes = bitsPerElement;
		abValues = new int[numHashes][2];
		this.setSize = primeOverM(setSize);
		setHashes();
		bFilter = new BitSet[bitsPerElement];
		for(int i = 0; i < numHashes; i++) {
			bFilter[i] = new BitSet(this.setSize);
		}
	}
	
	/**
	 * Adds  the  strings to  the  filter.   Type  of  this  method  is  void.   
	 * This  method should be case-insensitive.  For example, it should not distinguish between “Galaxy” 
	 * and “galaxy”.
	 * @param s
	 */
	public void add(String s) {
		if(s == null) {
			return;
		}
		//make string not case sensitive
		String str = s.toLowerCase();
		
		int hash = 0;
		for(int i = 0; i < numHashes; i++) {
			hash = hash(str,i);
			bFilter[i].set(Math.abs(hash));
		}
	}
	
	/**
	 * Returns true if s appears in the filter; otherwise returns false. This method 
	 * must also be case-insensitive.
	 * @param s
	 * @return
	 */
	public boolean appears(String s) {
		if(s == null) {
			return false;
		}
		//make string not case sensitive
		s = s.toLowerCase();
		
		int hash = 0;
		for(int i=0; i < numHashes;i++)
		{
			hash = hash(s,i);
			if(!bFilter[i].get(Math.abs(hash))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the size of the filter.
	 * @return
	 */
	public int filterSize() {
		return filterSize;
	}
	
	/**
	 * Returns the number of elements added to the filter.
	 * @return
	 */
	public int dataSize() {
		return dataSize;
	}

	/**
	 * Returns the number of hash function used.
	 * @return
	 */
	public int numHashes() {
		return numHashes;
	}
	
	/**
	 * Gets the next prime higher than n. Used to increase filterSize to out prime selection.
	 * @param n
	 * @return
	 */
	public int primeOverM(int n) {
		int nextPrime = 0;
		
		for(int i = n + 1; i < n * 2; i++) {
			for(int j = 2; j < i/2; j++) {
				if(i % j == 0) {
					break;
				}else if(j == (i/2)-1) {
					nextPrime = i;
				}
			}
			if(nextPrime != 0) {
				return nextPrime;
			}
		}
	    return nextPrime;
	}
	
	/**
	 * Sets the hash function variables used when computing hash values
	 */
	public void setHashes() {
		for(int i = 0; i < numHashes; i++) {
			abValues[i][0] = (int) (rand.nextInt()* setSize);
			abValues[i][1] = (int) (rand.nextInt()* setSize);
		}
	}
	
	/**
	 * Generates the hash values of a given string s at index of i in bFilter.
	 * @param s
	 * @param i
	 * @return
	 */
	public int hash(String s, int i) {
		
		return (abValues[i][0]*s.hashCode()+abValues[i][1])%(setSize);
	}
}
