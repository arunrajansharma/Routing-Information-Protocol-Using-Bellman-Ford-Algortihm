/* Author - Arun Rajan
   Stony Brook University 
*/

import java.util.*;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

import java.io.DataInputStream;
import java.io.DataOutputStream;



public class DriverProgram{
	public static List<Socket> inBoundSocketList = new ArrayList<Socket>();
	public static List<Socket> outBoundSocketList = new ArrayList<Socket>();
	private static int serverPort;
	private static int clientPort;
	private static String myHostName;

	private static long sysTime ;
        
	// Graph Information(In-memory)
	private static Set<String> vertices = new HashSet<>();
	private static Map<String,Integer> edges = new LinkedHashMap<>();
	
	// Result from BF algo
	private static Map<String,Integer>  distanceFromSrc = new HashMap<>();
	private	static Map<String,String> nextHop = new HashMap<>();
        
	private static void checkAndProcess(){
		//First [just once] we will push each node's distance information to all the client socket(outBoundSocket)
		// Then we go in a loop, every 15 seconds, listening, processing and calling BellmanFord() medthod

                BellmanFordAlgorithm();
		
		// Please note we will encode each edge information using $ sign
		String outBoundData="";
		for(Map.Entry<String,Integer> singleEdge: edges.entrySet()){
			outBoundData = singleEdge.getKey()+" "+singleEdge.getValue()+"$"+outBoundData;
		}
		
		//System.out.println(outBoundData);
		//String[] strArr = outBoundData.split("\\$");
		//for(String s: strArr)
		//	System.out.println(s);
                  
		
		try{   //writing once on the outbound sockets
			for(Socket sock: outBoundSocketList){
				DataOutputStream out = new DataOutputStream(sock.getOutputStream());
				out.writeUTF(outBoundData);
				out.flush();
			}
 		
			Thread.currentThread().sleep(5000);
	
			//here comes the awaited loop
			int iter = 1;
		        while(true){
			       	
				System.out.println("Iteration "+iter);
				iter++;
				for(Socket sock: inBoundSocketList){
					DataInputStream in = new DataInputStream(sock.getInputStream());
					String inBoundData = in.readUTF();
					String[] strArr = inBoundData.split("\\$");

					//printing the data
					System.out.println("Getting data from a socket");
					System.out.println(inBoundData);
					
				//check if the edge is present or not, if not add. If yes, then check the weight
					for(String dataUnit : strArr){
						String[] dataUnitInfo = dataUnit.split(" ");
						String edge = dataUnitInfo[0];
						String vertex_1 = edge.split("#")[0];
						String vertex_2 = edge.split("#")[1];
						int weight = Integer.parseInt(dataUnitInfo[1]);
						if(!edges.containsKey(edge)){
							edges.put(edge,weight);
						}
						else{
							if(weight<edges.get(edge))
								edges.put(edge,weight);
						}
						
						// trying to get unique vertices
							if(!vertices.contains(vertex_1))
								vertices.add(vertex_1);	
							if(!vertices.contains(vertex_2))
								vertices.add(vertex_2);
					}
	
					
				}
			
				BellmanFordAlgorithm();
				outBoundData="";
				for(Map.Entry<String,Integer> singleEdge: edges.entrySet()){
					outBoundData = singleEdge.getKey()+" "+singleEdge.getValue()+"$"+outBoundData;
				}
				for(Socket sock: outBoundSocketList){
					DataOutputStream out = new DataOutputStream(sock.getOutputStream());
					out.writeUTF(outBoundData);
					out.flush();
				}
				Thread.currentThread().sleep(10000);
				
		
		       }
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
		
		System.out.println("Check Point");
		
	   
     		
      
	}
	
	private static void BellmanFordAlgorithm(){

		System.out.println("Starting Bellman Ford Algorithm at "+myHostName);
		int V = vertices.size();
		int E = edges.size();
		
		System.out.println("*******************Map Information ******************");
		for(Map.Entry<String,Integer> singleEdge : edges.entrySet()){
			System.out.println(singleEdge.getKey()+" "+singleEdge.getValue());
		}
		System.out.println("Vertices[Start]...");

		for (String v : vertices)
			System.out.println(v);
		
		System.out.println("Vertices[End]...");
		

 		//Flushing the Data Structures 
		distanceFromSrc.clear();
		nextHop.clear();

		
		//Initialization
		for(String v : vertices)
			distanceFromSrc.put(v,Integer.MAX_VALUE);

		distanceFromSrc.put(myHostName,0);
		nextHop.put(myHostName,myHostName);
		
		// Run the edges V-1 times
		for(int i=1;i<V;i++){
			for(Map.Entry<String,Integer> singleEdge : edges.entrySet()){
				String[] strArr = singleEdge.getKey().split("#");
				String src = strArr[0];
				String dest = strArr[1];
				int weight = singleEdge.getValue();
				if(distanceFromSrc.containsKey(src) && distanceFromSrc.containsKey(dest) 
					&& distanceFromSrc.get(src)!=Integer.MAX_VALUE 
					&& (distanceFromSrc.get(src) + weight < distanceFromSrc.get(dest))){

						distanceFromSrc.put(dest,(distanceFromSrc.get(src) + weight));
						nextHop.put(dest,src);
				}
			
			}
		}

		// call some utilit function to write result in a file, for now just print
		for(Map.Entry<String,Integer> entry : distanceFromSrc.entrySet())
		{
			  if(myHostName.equals(entry.getKey())){
					System.out.println(myHostName+" "+0);
				}
			 else{
				System.out.println(entry.getKey()+"  "+entry.getValue());
	    		     }
		}
		System.out.println("Next Hop Information");
		 //next Hop Information
		for(Map.Entry<String,String> entry : nextHop.entrySet())
		{
			  if(myHostName.equals(entry.getKey())){
					System.out.println(myHostName+" "+myHostName);
				}
			 else{
				System.out.println(entry.getKey()+"  "+entry.getValue());
	    		     }
		}
		
		System.out.println();
		// File Print
		System.out.println("Writing the same output to "+myHostName+"_output.txt");
		try
		{       FileWriter fw = new FileWriter(myHostName+"_output.txt",true);
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			for(Map.Entry<String,Integer> entry : distanceFromSrc.entrySet())
			{
				if(myHostName.equals(entry.getKey())){
					pw.println(myHostName+" "+0);
				}
				else{
					pw.println(entry.getKey()+"  "+entry.getValue());
				    }
				
			}
			pw.println();
			for(Map.Entry<String,String> entry : nextHop.entrySet())
			{
			  if(myHostName.equals(entry.getKey())){
					pw.println(myHostName+" "+myHostName);
				}
			 else{
				      pw.println(entry.getKey()+"  "+entry.getValue());
	    		     }
			}
			
			pw.println("Time Elapsed :"+((System.nanoTime()-sysTime)/1e6));
			pw.flush();
			pw.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("************************BF finished*******************");		

	}
	 
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the hostname on which you are running  H1, H2 etc");
		myHostName = sc.nextLine();
		System.out.println("Enter the port number, it would be same on the client Server Pair");
		serverPort = sc.nextInt();
		clientPort = serverPort;
		System.out.println(serverPort+clientPort);
		
		System.out.println("Creating a Server Thread on "+serverPort);
		ServerThread serverthread = new ServerThread(serverPort);
		Thread st = new Thread(serverthread);
		st.start();
		try{
			Thread.currentThread().sleep(10000);
		}
		catch(Exception ex){
		ex.printStackTrace();
		}

		System.out.println("The system will read the client.txt file for client sockets");
		try{
			BufferedReader br = new BufferedReader(new FileReader("client.txt"));
			
			String line;
			while((line=br.readLine())!=null){
				String[] lineArr = line.split(" ");
				if(lineArr[0].equals(myHostName)){
					String host = lineArr[1];
					System.out.println(host);
					Socket socket = new Socket(host,clientPort);
					outBoundSocketList.add(socket);	
				}
			}// while ends here
			
			br.close();


			// will open new reading stream from different file
			System.out.println("The System will read the topology files, that has edges and weight mentioned");
			System.out.println();
			System.out.println("Building Graph now");
			br = new BufferedReader(new FileReader(myHostName+"_initialConfig.txt"));
			while((line=br.readLine())!=null){
				String[] strArr = line.split(" ");
				String edge = strArr[0];
				String vertex_1 = edge.split("#")[0];
				String vertex_2 = edge.split("#")[1];
				int weight = Integer.parseInt(strArr[1]);
				
				// trying to get unique vertices
				if(!vertices.contains(vertex_1))
					vertices.add(vertex_1);	
				if(!vertices.contains(vertex_2))
					vertices.add(vertex_2);

				// trying to add edges 
				if(!edges.containsKey(edge))
					edges.put(edge,weight);	
			}
			
	
			 System.out.println("The System is going to remove old file - "+myHostName+"_output.txt");
			 File f = new File(myHostName+"_output.txt");
			 f.delete();
			 
			 FileWriter fw = new FileWriter(myHostName+"_output.txt",true);
			 PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
 			 pw.println("Starting Calculating Time from now :");
			 sysTime = System.nanoTime();
			 pw.println(sysTime/1e6+" Nano Seconds");
			 pw.flush();
			 pw.close();
			
		
			
		}
		
		catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		 
		 
                 checkAndProcess();
                
		
		
		

	}
}
