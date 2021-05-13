import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

/**
 * Class is implementation to compute page rank of nodes/pages of a web graph
 * 
 * @author Brad Warren	bawarren@iastate.edu
 *
 */
public class PageRank {
	
	private Hashtable<String, Node> graph = new Hashtable<String, Node>(); //Graph of pages
	private Hashtable<Integer, String> numVertex = new Hashtable<Integer, String>(); //Corresponds numbers to each vertex
	private double approxParam, teleParam;
	private int verticies, numVertexIdx = 0, numEdges;

	/**
	 * Constructs PageRank object. Processes given file and creates a graph, also calculates page rank of
	 * each vertex of the graph.
	 * @param file
	 * @param approxParam
	 * @param teleParam
	 * @throws FileNotFoundException
	 */
	public PageRank(String file, double approxParam, double teleParam) throws FileNotFoundException {
		this.approxParam = approxParam;
		this.teleParam = teleParam;
		
		formGraph(file);
		pRank();
	}
	
	/**
	 * Returns page rank of the vertex specified in parameter
	 * @param vertex
	 * @return
	 */
	public double pageRankOf(String vertex) {		
		return graph.get(vertex).getPageRank();
	}
	
	/**
	 * Returns number of edges in the graph
	 * @return
	 */
	public int numEdges() {
		return numEdges;
	}
	
	/**
	 * Returns an array list of the top k pages according to their page ranks.
	 * @param k
	 * @return
	 */
	public ArrayList<String> topKPageRank(int k) {
		ArrayList<Node> topNodes = new ArrayList<Node>(k);
		double temp = 0;
		for(int i = 0; i < verticies; i++) {
			if(i<= k-1) { //set k spots of the array
				topNodes.add(graph.get(numVertex.get(i)));
			}else {
				Collections.sort((List<Node>) topNodes); //sorts the list
				temp = graph.get(numVertex.get(i)).getPageRank();
				if(topNodes.get(0).getPageRank() < temp) {
					topNodes.set(0, graph.get(numVertex.get(i)));
				}
			}
		}
		Collections.sort((List<Node>) topNodes);
		ArrayList<String> pages = new ArrayList<String>(k);
		
		//reverse inputs list intp array list of strings 
		for(int i = k-1; i >= 0; i--) {
			pages.add(topNodes.get(i).getPage());
			System.out.println(topNodes.get(i).getPage());
			System.out.println(topNodes.get(i).getPageRank());
		}
		
		return pages;
	}
	
	/**
	 * Forms the graph from the file input by traversing through the file
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void formGraph(String file) throws FileNotFoundException {
		File f = new File(file);
		Scanner sc = new Scanner(f);
		verticies = Integer.parseInt(sc.nextLine()); //number of verticies
		
		String firstPage = "";
		int idx = 0;
		while(sc.hasNextLine()) {//until file ends
			String line = sc.nextLine();
			Scanner s = new Scanner(line);
			firstPage = s.next();
			String secondPage = s.next();
			
			if(graph.containsKey(firstPage)) {//if page already exists in graph
				graph.get(firstPage).addEdge(secondPage);
				numEdges++;
				if(graph.get(secondPage) == null) {
					graph.put(secondPage, new Node(secondPage, idx));
					numVertex.put(numVertexIdx, secondPage);
					idx++;
					numVertexIdx++;
				}
			}else { //if page does not exist in file
				graph.put(firstPage, new Node(firstPage, idx));
				idx++;
				graph.get(firstPage).addEdge(secondPage);
				numVertex.put(numVertexIdx, firstPage);
				numVertexIdx++;
				if(graph.get(secondPage) == null) {
					graph.put(secondPage, new Node(secondPage, idx));
					numVertex.put(numVertexIdx, secondPage);
					idx++;
					numVertexIdx++;
				}
				numEdges++;
			}
			s.close();
		}
		sc.close();
	}
	
	/**
	 * Calculates the page rank of all of the nodes on the graph (from notes)
	 */
	public void pRank() {
		ArrayList<Double> p0 = new ArrayList<Double>(Collections.nCopies(verticies, (double)1/verticies));
		ArrayList<Double> pn = p0; 
		
		boolean converged = false;
		
		while(!converged) {
			ArrayList<Double> pn1 = A(pn);
			ArrayList<Double> pcompare = new ArrayList<Double>();
			for(int i = 0; i < pn1.size(); i++) {
				pcompare.add(Math.abs(pn1.get(i) - pn.get(i)));
			}
			if(Norm(pcompare)) {
				converged = true;
			}
			pn = pn1;
		}
		for(int i = 0; i < graph.size(); i++) {
			graph.get(numVertex.get(i)).setPageRank(pn.get(i));
		}
	}
	
	/**
	 * Helper method called by pRank as one random walk on the graph (from notes)
	 * @param pn
	 * @return
	 */
	public ArrayList<Double> A(ArrayList<Double> pn) {
		ArrayList<Double> pn1 = new ArrayList<Double>(Collections.nCopies(verticies, (double)(1-teleParam)/verticies));
		
		for(int i = 0; i < verticies; i++) {
			int links = graph.get(numVertex.get(i)).getForwardEdges();
			if(links != 0) {
				for(int j = 0; j < links; j++) {
					int q = graph.get(graph.get(numVertex.get(i)).getReferences().get(j)).getNumber();
					pn1.set(q, (pn1.get(q) + ((teleParam*(double)pn.get(i))/(double)links)));
				}
			}else {
				for(int j = 0; j < verticies; j++) {
					pn1.set(j, (pn1.get(j) + ((teleParam*(double)pn.get(j))/(double)verticies)));
				}
			}
		}
		return pn1;
	}
	
	/**
	 * Checks if l1norm is less than approximation parameter (from notes)
	 * @param p
	 * @return
	 */
	public boolean Norm(ArrayList<Double> p) {
		double sum = 0;
		for(int i = 0; i < p.size(); i++) {
			sum = sum + p.get(i);
		}
		
		if(sum <= approxParam) {
			return true;
		}else {
			return false;
		}
	}
	
//	public static void main(String args[]) throws FileNotFoundException {
//		PageRank p = new PageRank("WikiSportsGraph.txt", 0.001, .25);
////		for(int i = 0; i < p.graph.get("/wiki/Major_professional_sports_leagues_in_the_United_States_and_Canada").getForwardEdges(); i++) {
////			System.out.println(p.graph.get("/wiki/Major_professional_sports_leagues_in_the_United_States_and_Canada")
////					.getReferences().get(i));
////		}
//		System.out.println(p.topKPageRank(10));
//	}
}