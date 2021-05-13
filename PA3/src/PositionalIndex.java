import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * This class creates reverted index
 * 
 * @author Brad Warren	bawarren@iastate.edu
 *
 */
public class PositionalIndex {

	private int numDocs, termsIdx = 0;
	private Hashtable<String, Hashtable<String,Postings>> termPostings; //term postings of a term and each document it appears in
	private Hashtable<String, Integer> terms = new Hashtable<>(); //terms corresponding to a number
	private Hashtable<String, Double> l2Norms = new Hashtable<>(); //l2norms of each document
	private Hashtable<String, ArrayList<String>> docTerms = new Hashtable<String, ArrayList<String>>(); //list of terms in each document
	private String[] docs; //list of documents
	
	/**
	 * Creates a positionalIndex object. Create termPostings, terms, l2Norms, docTerms and docs.
	 * @param folder
	 * @throws FileNotFoundException
	 */
	public PositionalIndex(String folder) throws FileNotFoundException {
		File files = new File(folder);
		File[] listOfFiles = files.listFiles();
		numDocs = listOfFiles.length;
		termPostings = new Hashtable<String, Hashtable<String, Postings>>();
		docs = new String[numDocs];

		int i = 0;
		for(File f : listOfFiles) {//for each file
			docs[i] = f.getName();
			docTerms.put(docs[i], new ArrayList<String>());
			traverseFile(f,i);
			l2Norms.put(docs[i],0.0);
			i++;
		}
		
		//Compute l2Norm of each document
		for(int j = 0; j < numDocs; j++) {
			double w = 0;
			for(int k = 0; k < docTerms.get(docs[j]).size(); k++) {
				 w = w + Math.pow(weight(docTerms.get(docs[j]).get(k), docs[j]),2);
			}
			l2Norms.replace(docs[j], Math.sqrt(w)); //replaces temp value to actual value
		}
	}
	
	/**
	 * Traverses the file and adds a posting for each term in the file
	 * @param file
	 * @param i
	 * @throws FileNotFoundException
	 */
	public void traverseFile(File file, int i) throws FileNotFoundException {
		String name = file.getName();
		int position = 1;
		Scanner sc = new Scanner(file);
		
		while(sc.hasNextLine()) { //until file ends
			String line = new String(sc.nextLine());
			line = line.toLowerCase();
			line = line.replaceAll("[,\"?\\[\\]{}:;()]", " "); // removing unwanted characters
			Scanner s = new Scanner(line);
			
			while(s.hasNext()) { //until line ends
				String temp = s.next();
				if(temp.contains(".")) {//checks if "." belongs to a number
					try {
						double dub = Double.parseDouble(temp);
					}catch(NumberFormatException e) {
						temp = temp.replaceAll("\\.","");
					}
				}
				if(!termPostings.containsKey(temp)) { //add term 
					termPostings.put(temp, new Hashtable<String, Postings>());
					termPostings.get(temp).put(name, new Postings(i,name));
					termPostings.get(temp).get(name).add(position);
					docTerms.get(name).add(temp);
					terms.put(temp, termsIdx);
					termsIdx++;
				}else { //add term to already existing term list
					Postings o = termPostings.get(temp).get(name);
					if(o != null) { //if ArrayList for document already exists
						termPostings.get(temp).get(name).add(position);
					}else { //if ArrayList for document does not exist
						docTerms.get(name).add(temp);
						termPostings.get(temp).put(name, new Postings(i,name));
						termPostings.get(temp).get(name).add(position);
					}
				}
				position++;
			}
			s.close();
		}
		sc.close();

	}
	
	/**
	 * Returns the number of times a term appears in a document
	 * @param term
	 * @param Doc
	 * @return
	 */
	public int termFrequency(String term, String Doc) {
		int docNum = hasFile(term,Doc);
		if(docNum == -1) {
			return 0;
		}

		return termPostings.get(term).get(Doc).freq();
	}
	
	/**
	 * Returns the number of documents a term appears in
	 * @param term
	 * @return
	 */
	public int docFrequency(String term) {
		return termPostings.get(term).size();
	}
	
	/**
	 * Prints out the documents that contain a term
	 * @param t
	 */
	public void postingList(String t) {
		int size = termPostings.get(t).size();
		ArrayList<String> st = new ArrayList<String>(size);

		termPostings.get(t).forEach((k, v) -> {
			st.add(k);
		});
				
		System.out.print("\n[");
		for(int i = 0; i < termPostings.get(t).size(); i++) {
			String posArray = termPostings.get(t).get(st.get(i)).positions().toString().replaceAll("[\\[\\]]", "");
			System.out.print("<" + st.get(i) + ": "
					+ posArray + ">");
			if(i != termPostings.get(t).size()-1) {
				System.out.print(", ");
			}
		}
		System.out.print("]\n");
	}
	
	/**
	 * Returns the weight of a term with a document
	 * @param t
	 * @param d
	 * @return
	 */
	public double weight(String t, String d) {
		int i = hasFile(t,d);
		int freq = 0;
		if(i == -1) { //if d is not a document
			Scanner sc = new Scanner(d);
			while(sc.hasNext()) {
				if(t.equals(sc.next())) {
					freq++;
				}
			}
			sc.close();
			return freq;
		} else { //if d is a document
			double firstHalf = (double)Math.sqrt(termPostings.get(t).get(d).freq());
			double secondHalf = (double)Math.log10((double)numDocs/(double)docFrequency(t));
			return firstHalf*secondHalf;
		}
	}
	
	/**
	 * Returns the TPScore of a document against a query
	 * @param query
	 * @param doc
	 * @return
	 */
	public double TPScore(String query, String doc) {
		Scanner sc = new Scanner(query);
		ArrayList<String> terms = new ArrayList<String>();
		
		while(sc.hasNext()) { //retrieves the terms from the query
			String temp = sc.next();
			terms.add(temp);
		}
		sc.close();
		
		double dist = 17;
		
		if(terms.size() == 1) { //returns size of 1/17
			return 1/dist;
		}
		
		int[] docNums = new int[terms.size()];
		Arrays.fill(docNums, -1);
		for(int i = 0; i < terms.size(); i++) {
			int docNum = hasFile(terms.get(i), doc);
			if(i != -1) {
				docNums[i] = docNum;
			}
		}
		
		int docNum1 = docNums[0];
		
		//Goes through the terms inside the documents corresponding to the query and reduces the size of dist
		for(int i = 0; i < terms.size() - 1; i ++) {
			int docNum2 = docNums[i + 1];
			boolean more = true;
			int index1 = 0, index2 = 0;
			while(more) {
				if(docNum1 == -1 || docNum2 == -1) {
					break;
				}
				int num1 = termPostings.get(terms.get(i)).get(doc).positions().get(index1);
				int num2 = termPostings.get(terms.get(i + 1)).get(doc).positions().get(index2);
				if(num1 > num2) {
					index2++;
				}else {
					if(num1 < num2 && num2 - num1 < dist) {
						dist = num2 - num1;
					}
					index1++;
				}
				
				if(index1 == termPostings.get(terms.get(i)).get(doc).positions().size() ||
						index2 == termPostings.get(terms.get(i+1)).get(doc).positions().size()) {
					more = false;
				}
			}
			docNum1 = docNum2;
		}
		return (double)((double)terms.size()/(double)dist);
	}
	
	/**
	 * Returns the VSScore of a document against a query TODO
	 * @param query
	 * @param doc
	 * @return
	 */
	public double VSScore(String query, String doc) {
		Scanner sc = new Scanner(query);
		ArrayList<String> terms = new ArrayList<String>();
		while(sc.hasNext()) {
			String temp = sc.next();
			terms.add(temp);
		}
		sc.close();
		
		ArrayList<Double> qVector = new ArrayList<Double>();
		double l2normQ = 0;
		for(int i = 0; i < terms.size(); i++) { //computes l2norm of vector q
			qVector.add(weight(terms.get(i),query));
			l2normQ = l2normQ + Math.pow(qVector.get(i), 2);
		}
		
		l2normQ = Math.sqrt(l2normQ);
		
		double dotProduct = 0;
		for(int i = 0; i < qVector.size(); i++) {
			dotProduct = dotProduct + (qVector.get(i) * weight(terms.get(i), doc));
		}
		
		return (double) (dotProduct/(double)(l2normQ * l2Norms.get(doc)));
	}
	
	/**
	 * Returns the Relevance of a document against the query given
	 * @param query
	 * @param doc
	 * @return
	 */
	public double Relevance(String query, String doc) {
		query = query.toLowerCase();
		double firstHalf = 0.6*TPScore(query,doc);
		double secondHalf = 0.4*VSScore(query, doc);
		return (double)(firstHalf + secondHalf);
	}
	
	/**
	 * Looks through the documents corresponding to the term until the document number is found
	 * @param term
	 * @param docName
	 * @return
	 */
	public int hasFile(String term, String docName) { //slow 
		if(termPostings.get(term) == null) {
			return -1;
		}
		
		if(termPostings.get(term).get(docName) == null) {
			return -1;
		}else {
			return 1;
		}
	}
	
	/**
	 * Returns the number of documents that are read from the folder
	 * @return
	 */
	public int getDocNum() {
		return numDocs;
	}
	
	/**
	 * Returns the document name given the index according to i
	 * @param i
	 * @return
	 */
	public String getDoc(int i) {
		return docs[i];
	}
	
//	public static void main(String args[]) throws FileNotFoundException {
//		PositionalIndex p = new PositionalIndex("IRSUB");
//		System.out.println(p.docFrequency("season"));
//		System.out.println(p.termFrequency("season", "1890_World_Series.txt"));
//		System.out.println(p.weight("season", "1890_World_Series.txt"));
//		p.postingList("season");
//		System.out.println(p.l2Norms.get("1890_World_Series.txt"));
//		System.out.println(p.VSScore("season league", "1890_World_Series.txt"));
//	}
}
