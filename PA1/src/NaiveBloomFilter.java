import java.util.Arrays;
import java.util.BitSet;

/**
 * 
 * Bloom filter implementation of a Bloom filter with a single random hash function. This class varies from
 * others because instead of a bitset we use a regular array.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class NaiveBloomFilter {

	private int filterSize, dataSize, numHashes, A, B;
	private BitSet bFilter;
	
	/**
	 * creates a Bloom filter with one array as its table and one random hash function. 
	 * 
	 * to 0.
	 * @param setSize
	 * @param bitsPerElement
	 */
	public NaiveBloomFilter(int setSize, int bitsPerElement){
		filterSize = setSize * bitsPerElement;
		numHashes = (int) Math.ceil(Math.log(2) * (filterSize/setSize));
		filterSize = primeOverM(filterSize);
		bFilter = new BitSet(filterSize);
		A = (int) (Math.random() * filterSize);
		B = (int) (Math.random() * filterSize);
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
			hash = hash(str);
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
		s = s.toLowerCase();
		
		int hash = 0;
		for(int i=0; i < numHashes;i++)
		{
			hash = hash(s);
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
	 * Generates the hash values of a given string s.
	 * @param s
	 * @return
	 */
	public int hash(String s) {
		return ((A*s.hashCode()+B)%filterSize);
	}
}
