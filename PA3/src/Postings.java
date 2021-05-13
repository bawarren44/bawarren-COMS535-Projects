import java.util.ArrayList;

/**
 * Postings class used as individual postings of a document corresponding to a term
 * 
 * @author Brad Warren 	bawarren@iastate.edu
 *
 */
public class Postings {
		
	private int frequency;
	private int docNum;
	private String docName;
	private ArrayList<Integer> positions;

	/**
	 * Constructs a posting object 
	 * @param docNum
	 * @param docName
	 */
	public Postings(int docNum, String docName) {
		frequency = 0;
		this.docNum = docNum;
		this.docName = docName;
		this.positions = new ArrayList<Integer>();
	}

	/**
	 * Adds a position of a term to the positions list
	 * @param position
	 */
	public void add(int position) {
		positions.add(position);
		frequency++;
	}

	/**
	 * Returns the size of the positions list
	 * @return
	 */
	public int size() {
		return positions.size();
	}

	/**
	 * Returns the number corresponding to the document
	 * @return
	 */
	public int docNum() {
		return docNum;
	}

	/**
	 * Returns the name of the document
	 * @return
	 */
	public String docName() {
		return docName;
	}

	/**
	 * Returns the position list of the term in the document
	 * @return
	 */
	public ArrayList<Integer> positions(){
		return positions;
	}
	
	/**
	 * Returns the frequency of the document
	 * @return
	 */
	public int freq() {
		return frequency;
	}
}
