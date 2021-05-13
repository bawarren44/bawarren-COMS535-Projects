import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

/**
 * 
 * For Bloom Filters (that use a random hash function), we have seen how to theoretically 
 * determine a bound on the false positives.  Thus, the performance a Bloom Filter can be 
 * captured by M/N bitsPerElement) and the false positive rate.  In this class we
 * experimentally/empirically determine the false positives for the Bloom Filters 
 * (and there) variants.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class FalsePositives {

	private static String inBloom[], outBloom[];
	private static URL url;	
	
	/**
	 * Tests the different filters with n bitsPerElement. Outputs the false positive percentage
	 * of 5000 inputs.
	 * 
	 * @param n
	 */
	public static void test(int numberOfStrings,int n) {
		//initialize Bloom filters
		BloomFilterFNV fnv = new BloomFilterFNV(numberOfStrings, n);
		BloomFilterRan ran = new BloomFilterRan(numberOfStrings, n);
		MultiMultiBloomFilter multi = new MultiMultiBloomFilter(numberOfStrings, n);
		NaiveBloomFilter naive = new NaiveBloomFilter(numberOfStrings, n);
		//values for number of false positives
		double fnvFalse = 0, ranFalse = 0, multiFalse = 0, naiveFalse = 0;
		
		//add first inBloom words to Bloom filters
		for(String word : inBloom) {
			fnv.add(word);
			ran.add(word);
			multi.add(word);
			naive.add(word);
		}
		
		//check if false outBloom words cause false positive
		for(String word : outBloom) {
			if(fnv.appears(word)) {
				fnvFalse++;
			}
			
			if(ran.appears(word)) {
				ranFalse++;
			}
			
			if(multi.appears(word)) {
				multiFalse++;
			}
			
			if(naive.appears(word)) {
				naiveFalse++;
			}
		}
		
		//calculate percentage of false positives
		float fnvPercent = (float) ((fnvFalse/numberOfStrings)*100);
		float ranPercent = (float) ((ranFalse/numberOfStrings)*100);
		float multiPercent = (float) ((multiFalse/numberOfStrings)*100);
		float naivePercent = (float) ((naiveFalse/numberOfStrings)*100);
		
		//print false positive percentages
		System.out.println("BloomFilterFNV false positives for " + n + " bitsPerElement:\t\t" + fnvPercent + "%");
		System.out.println("BloomFilterRan false positives for " + n + " bitsPerElement:\t\t" + ranPercent + "%");
		System.out.println("MultiMultiBloomFilter false positives for " + n + " bitsPerElement:\t" + multiPercent + "%");
		System.out.println("NaiveBloomFilter false positives for " + n + " bitsPerElement:\t\t" + naivePercent + "%\n\n");
	}
	
	/**
	 * Used to fill inBloom and outBloom with strings from the url
	 * @throws IOException
	 */
	public static void fill() throws IOException {
		int count = 0, count2 = 0;
		Scanner sc = new Scanner(url.openStream());
		for(int i = 0; i < 10000; i++) {
			if(i % 2 == 0) {
				outBloom[count] = sc.nextLine().trim();
				count++;
			}else {
				inBloom[count2] = sc.nextLine().trim();
				count2++;
			}
		}
	}
	
	/**
	 * Main method for testing the bloom filters
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String[] str = new String[100000];
		String[] str2 = new String[100000];
		
		for(int i = 0; i < 100000; i++) {
			str[i] = UUID.randomUUID().toString();
		}
		inBloom = str;
	
		boolean no = false;
		for(int i = 0; i < 100000; i++) {
			String g = UUID.randomUUID().toString();
			for(int j = 0; j < 100000; j++) {
				if(str[i].equals(g)) {
					no = true;
					break;
				}
			}
			str2[i] = g; 
		}
		outBloom = str2;
		test(100000,4);
		test(100000,8);
		test(100000,10);
		test(100000,16);

	}
}
