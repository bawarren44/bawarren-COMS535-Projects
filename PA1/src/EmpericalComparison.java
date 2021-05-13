import java.io.FileNotFoundException;

/**
 * Class used to empirically compare BloomDifferential and NaiveDifferential.
 * Should see from output that BloomDifferential is faster while NaiveDifferential
 * uses less memory.
 * 
 * @author Brad Warren bawarren@iastate.edu
 *
 */
public class EmpericalComparison {

	private static String keys[];
	
	
	public static void main(String args[]) throws FileNotFoundException {
		//selected keys for testing
		keys = new String[4];
		keys[0] = "are folded inseparably together";
		keys[1] = "are_VERB abundant_ADJ about_ADP the_DET";
		keys[2] = "are trying the last"; //second to last in database.txt
		keys[3] = "are probably various ,";
		
		long startTime = 0, endTime = 0, bDifMemory = 0, nDifMemory = 0, bstartMemory = 0,
				bendMemory = 0, nstartMemory = 0, nendMemory = 0;
				
		
		//time and memory usage to construct bloomDifferential
		bstartMemory = Runtime.getRuntime().freeMemory();
		startTime = System.nanoTime();
		BloomDifferential bDif = new BloomDifferential(10,"DiffFile.txt","database.txt");
		endTime = System.nanoTime();
		bendMemory = Runtime.getRuntime().freeMemory();
		bDifMemory = (bstartMemory - bendMemory);
		long bDifConstruction = (endTime - startTime)/1000000;
		
		//time and memory usage to construct naiveDifferential
		nstartMemory = Runtime.getRuntime().freeMemory();
		startTime = System.nanoTime();
		NaiveDifferential nDif = new NaiveDifferential("DiffFile.txt","database.txt");
		endTime = System.nanoTime();
		nendMemory = Runtime.getRuntime().freeMemory();
		nDifMemory = (nstartMemory - nendMemory);
		long nDifConstruction = (endTime - startTime)/1000000;
		
		
		//time takes to get keys from files for bloomDifferential
		startTime = System.nanoTime();
		System.out.println(bDif.retrieveRecord(keys[0]));
		System.out.println(bDif.retrieveRecord(keys[1]));
		System.out.println(bDif.retrieveRecord(keys[3]));
		endTime = System.nanoTime();
		long bDifInBloomTime = (endTime - startTime)/1000000;
		
		startTime = System.nanoTime();
		System.out.println(bDif.retrieveRecord(keys[2])+"\n\n");
		endTime = System.nanoTime();
		long bDifOutBloomTime = (endTime - startTime)/1000000;
		
		
		//time takes to get keys from files for naiveDifferential
		startTime = System.nanoTime();
		System.out.println(nDif.retrieveRecord(keys[0]));
		System.out.println(nDif.retrieveRecord(keys[1]));
		System.out.println(nDif.retrieveRecord(keys[3]));
		endTime = System.nanoTime();
		long nDifInBloomTime = (endTime - startTime)/1000000;
		
		startTime = System.nanoTime();
		System.out.println(nDif.retrieveRecord(keys[2]) + "\n\n");
		endTime = System.nanoTime();
		long nDifOutBloomTime = (endTime - startTime)/1000000;

		
		//Side by side comparison
		System.out.println("Time comparison:");
		System.out.println("BloomDifferential construction time: " + bDifConstruction + " ms");
		System.out.println("NaiveDifferential construction time: " + nDifConstruction + " ms\n");

		System.out.println("BloomDifferential find 3 record in DiffFile.txt time: " + bDifInBloomTime + " ms");
		System.out.println("NaiveDifferential find 3 record in DiffFile.txt time: " + nDifInBloomTime + " ms\n");

		System.out.println("BloomDifferential find 2nd to last record in database.txt time: " + bDifOutBloomTime + " ms");
		System.out.println("NaiveDifferential find 2nd to last record in database.txt time: " + nDifOutBloomTime + " ms\n\n");
		
		System.out.println("Memory comparison:");
		System.out.println("BloomDifferential memory usage: " + bDifMemory/(1024*1024) + " mb");
		System.out.println("NaiveDifferential memory usage: " + nDifMemory/(1024*1024)+ " mb");
	}
}
