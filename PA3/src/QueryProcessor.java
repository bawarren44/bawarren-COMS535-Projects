import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class takes in the folder name as input and outputs the top K documents corresponding to
 * the query given by the user.
 * 
 * @author Brad Warren	bawarren@iastate.edu
 *
 */
public class QueryProcessor {

	private PositionalIndex PI;
	
	/**
	 * Creates a QueryProcessor object
	 * @param folder
	 * @throws FileNotFoundException
	 */
	public QueryProcessor(String folder) throws FileNotFoundException {
		PI = new PositionalIndex(folder);
	}
	
	/**
	 * Returns the top k documents from a given query
	 * @param query
	 * @param k
	 * @return
	 */
	public ArrayList<String> topKDocs(String query, int k){
		ArrayList<Document> docs = new ArrayList<Document>(k);
		int topIdx = 0;
		
		double temp = 0;
		for(int i = 0; i < PI.getDocNum(); i++) {
			String doc = PI.getDoc(i);
			if(topIdx <= k-1) { //set all indices of doc
				docs.add(new Document(doc, PI.Relevance(query, doc)));
				topIdx++;
			}else {
				Collections.sort((List<Document>) docs); //sorts list
				temp = PI.Relevance(query, doc);
				if(docs.get(0).getRelevance() < temp) {
					docs.set(0, new Document(doc, temp));
				}
			}			
		}
		
		Collections.sort((List<Document>) docs);
		ArrayList<String> documents = new ArrayList<String>(k);
		
		for(int i = k-1; i >= 0; i--) { //reverse inputs documents into list (because it is backwards)
			documents.add(docs.get(i).getDoc());
//			System.out.println(docs.get(i).getDoc());
//			System.out.println(PI.TPScore(query, docs.get(i).getDoc()));
//			System.out.println(PI.VSScore(query, docs.get(i).getDoc()));
//			System.out.println(docs.get(i).getRelevance());
		}
		
		return documents;
	}
	
	/**
	 * Document class to store each documents relevance score (to later compare)
	 */
	public class Document implements Comparable<Document> {
		private String docName;
		private Double relevance;
		
		/**
		 * Creates a Document object
		 * @param doc
		 * @param relevance
		 */
		public Document(String doc, double relevance) {
			docName = doc;
			this.relevance = relevance;
		}
		
		/**
		 * Returns document name
		 * @return
		 */
		public String getDoc() {
			return docName;
		}
		
		/**
		 * Returns relevance of a document
		 * @return
		 */
		public double getRelevance() {
			return relevance;
		}

		@Override
		public int compareTo(Document o) {
			return relevance.compareTo(o.getRelevance());
		}
	}
	
//	public static void main(String args[]) throws FileNotFoundException {
//		QueryProcessor p = new QueryProcessor("IR");
//		System.out.println(p.topKDocs("announcer Yankee broadcaster contract premiered", 10));
//	}
}
