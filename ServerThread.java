/*
Author - Arun Rajan
Stony Brook University
*/
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.Socket;

public class ServerThread implements Runnable
{	private ServerSocket serversocket;
	private int port;
	
	public ServerThread(int port){
		this.port = port;
	}
	
	public void run(){
			try{ 
				serversocket = new ServerSocket(port); 
			}
			catch(Exception ex)
			{	 ex.printStackTrace();
				return;
			}
				
			System.out.println("Server socket got created at "+port);
		while(true){
			try{	
				System.out.println("Server Log : Waiting for a client to connect");
				Socket socketCli = serversocket.accept();
				DriverProgram.inBoundSocketList.add(socketCli);
				System.out.println("Server Log : Connection accepted");
			}
			catch(SocketTimeoutException ex){
				System.out.println("Server Log :Socket time out at "+port);
				break;
			}
			catch(Exception ex){
				ex.printStackTrace();
				break;
			}
		}// while ends here
	}// run ends here
	
}
