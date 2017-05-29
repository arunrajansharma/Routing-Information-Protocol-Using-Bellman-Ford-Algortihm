# Routing-Information-Protocol-Using-Bellman-Ford-Algortihm
This repository contains implementation of Routing Information Protocol over a small network topology built using Mininet and Quagga.


/*
Author : Arun Rajan, Stony Brook University
*/
Every node is running the 'mini RIP Algorithm'.

***  Setup :
Every node has a server thread that accepts connection on port 4444. At the same time, each node also acts as client by openning sockets on the connected nodes.

The information about on which server a socket should be opened is given in the "client.txt". 

In the beginning, each node has information about just its directly connected neighbors. This information is stored in the "NodeName_initialConfig.txt"
For example, "H1_initialConfig.txt" etc.

For output, there are two streams, one writes to the console and the second writes to file, named after the node's name. For example, "H1_output.txt" etc.


As usual, "start.py" and "topo.py" contains code for setting up the network emulator.


**** Working 
First of all, each node reads from its initial Config file and pushes that information on the outbound sockets. Each node on receiving this information puts it in its view of the graph. 

Bellman Ford Algorithm is executed on the instance of this view of the graph and results is written to Output.txt files.
After writing the result (application level routing table), the information is passed to the connected nodes.




**** Validity 
I ran the algorithm with positive weights to check the correctness. It is giving the correct results. Also, the algorithm is converging for negative weights.


**** How to run :
1. In any folder, first run the "start.py" script to set the network up.
2. Now open 6 terminals, each for each node. 
	>using miniNext/util/mx run the DriverProgram.java
3. The program will prompt to enter the name of the host you are runnnig it.
	enter "H1", "R1" etc.
4. Enter the port number as 4444 but don't hit the enter yet. If you do, one of the socket might get timeout. Hence, it is good practice to type 4444 in all the terminals together and then hit "enter" at the same time.

5. Wait and Watch. 









