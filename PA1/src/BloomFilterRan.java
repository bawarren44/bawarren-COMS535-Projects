import java.util.BitSet;
import java.util.Random;


/**
 * 
 * Bloom filter implementation of a Bloom filter with one table and k random hash functions
 * of my choice.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class BloomFilterRan {
	
	private int filterSize, dataSize, numHashes;
	private BitSet bFilter;
	private int abValues[][];
	private Random rand = new Random(100000000);
	
	/**
	 * creates a Bloom filter with one table and k hash functions of my choice. 
	 * @param setSize
	 * @param bitsPerElement
	 */
	public BloomFilterRan(int setSize, int bitsPerElement){
		filterSize = setSize * bitsPerElement;
		numHashes = (int) Math.ceil(Math.log(2) * (filterSize/setSize));
		filterSize = primeOverM(filterSize);
		bFilter = new BitSet(filterSize);
		abValues = new int[numHashes][2];
		setHashes();
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
			bFilter.set(Math.abs(hash));
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
		String str = s.toLowerCase();
		
		int hash = 0;
		for(int i=0; i < numHashes;i++)
		{
			hash = hash(str, i);
			if(!bFilter.get(Math.abs(hash))) {
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
			abValues[i][0] = (int) (rand.nextInt() * filterSize);
			abValues[i][1] = (int) (rand.nextInt() * filterSize);
		}
	}
	
	/**
	 * Generates the hash values of a given string s.
	 * @param s
	 * @return
	 */
	public int hash(String s, int i) {
		return ((abValues[i][0]*s.hashCode()+abValues[i][1])%filterSize);
	}
}
