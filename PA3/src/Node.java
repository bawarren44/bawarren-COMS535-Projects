import java.util.ArrayList;

/**
 * Node object used inside PageRank class as vertexes of the graph
 * 
 * @author Brad Warren	bawarren@iastate.edu
 *
 */
public class Node implements Comparable<Node>{

	private String page;
	private ArrayList<String> references = new ArrayList<>();
	private int forwardEdges;
	private int number;
	private Double pageRank = 0.0;
	
	/**
	 * Constructs a Node object
	 * @param page
	 * @param num
	 */
	public Node(String page, int num) {
		number = num;
		this.page = page;
		forwardEdges = 0;
	}
	
	/**
	 * Returns the page name
	 * @return
	 */
	public String getPage() {
		return page;
	}
	
	/**
	 * Adds a new edge to the references list
	 * @param page
	 */
	public void addEdge(String page) {
		references.add(page);
		forwardEdges++;
	}
	
	/**
	 * Returns the number of edges leading from vertex
	 * @return
	 */
	public int getForwardEdges() {
		return forwardEdges;
	}
	 
	/**
	 * Returns a list of references from vertex
	 * @return
	 */
	public ArrayList<String> getReferences(){
		return references;
	}
	
	/**
	 * Returns the number corresponding to the vertex
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Returns the page rank of the vertex
	 * @return
	 */
	public double getPageRank() {
		return pageRank;
	}
	
	/**
	 * Sets the page rank of the vertex
	 * @param i
	 */
	public void setPageRank(double i) {
		pageRank = i;
	}

	@Override
	public int compareTo(Node o) {
		return pageRank.compareTo(o.getPageRank());
	}
}
