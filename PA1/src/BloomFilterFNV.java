import java.util.BitSet;
import java.math.BigInteger;

/**
 * 
 * Bloom filter implementation of a Bloom filter using FNV hash function.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class BloomFilterFNV {
	
	private int filterSize, dataSize, numHashes;
	private BitSet bFilter;
	private BigInteger FNV_offset_basis = new BigInteger("14695981039346656037");
	private long FNV_prime = Long.parseLong("1099511628211");

	/**
	 * Creates a Bloom filter that can store asetSof cardinalitysetSize. 
	 * The size of the filter should approximately besetSize * bitsPerElement.
	 * The number of hash functions should be the optimal choice which is ln2×filterSize/setSize.
	 * @param setSize
	 * @param bitsPerElement
	 */
	public BloomFilterFNV(int setSize, int bitsPerElement){
		filterSize = setSize * bitsPerElement;
		numHashes = (int) Math.ceil(Math.log(2) * (filterSize/setSize));
		bFilter = new BitSet(filterSize);
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
			if(i < numHashes/2) {
				hash = (int) hashFNV(leftRotate(str,i));
			}else {
				hash = (int) hashFNV(leftRotate(reverse(str),i));
			}
			
			bFilter.set(Math.abs(hash));
		}
		dataSize++;
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
		for(int i = 0; i < numHashes; i++) {
			if(i < numHashes/2) {
				hash = (int) hashFNV(leftRotate(str,i));
			}else {
				hash = (int) hashFNV(leftRotate(reverse(str),i));
			}
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
	 * Method containing hash function FNV that takes a string and Hashes it.
	 * @param s - string to be hashed
	 * @return hashed string
	 */
	public long hashFNV(String s) {
		BigInteger x = FNV_offset_basis;
		for(int i = 0; i < s.length(); i++) {
			x = x.xor(BigInteger.valueOf(s.charAt(i)));
			x = x.multiply(BigInteger.valueOf(FNV_prime));
			x = x.mod(new BigInteger("18446744000000000000"));
		}

		x = x.mod(BigInteger.valueOf(filterSize));
		long result = x.longValue();
		return result;
	}
	
	/**
	 * Used to switch around hash function
	 * @param s
	 * @param i
	 * @return
	 */
	public String leftRotate(String s, int i){
		if(i > s.length()) {
			return s;
		}else {
			return s.substring(i) + s.substring(0, i);
		}
	}
	
	/**
	 * Gets reverse of string for hash functions
	 * @param s
	 * @return
	 */
	public String reverse(String s) {
		 String reverse = "";
		 
		 for(int i = s.length() - 1; i >= 0; i--){
			 reverse = reverse + s.charAt(i);
		 }
		 return reverse;
	}
	
	
}
