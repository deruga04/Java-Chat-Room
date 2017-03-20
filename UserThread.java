import java.io.*;
import java.net.*;

class UserThread extends Thread
{

	private DataInputStream in = null;
	private PrintStream out = null;
	private Socket clientSocket = null;
	private final UserThread[] threads;
	private int maxClients;
	String username;

	public UserThread(Socket clientSocket, UserThread[] threads)
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClients = threads.length;
		this.username = "";
	}

	public void run()
	{
		UserThread[] threads = this.threads;

		try
		{
			/*
			 * Create input and output streams for this client.
			 */
			in = new DataInputStream(clientSocket.getInputStream());
			out = new PrintStream(clientSocket.getOutputStream());
			String line;
			boolean joined = false;;
			
			out.println("***************************");
			out.println("Welcome to the chat room!");
			out.println("@join - enter the server");
			out.println("@leave - exit the server");
			out.println("@list - users currently in the room");
			out.println("***************************");
			
			while (!joined)
			{
				line = in.readLine();
				if (line.startsWith("@join"))
				{
					joined = true;
				}
			}
				
			out.println("Enter a display name:");
			this.username = in.readLine().trim();
			
			for (int i = 0; i < this.maxClients; i++)
			{
				if (threads[i] != null && threads[i] != this)
				{
					threads[i].out.println("*** New user " + this.username + " has joined. ***");
				}
			}
			while (true)
			{
				line = in.readLine();
				if (line.startsWith("@list"))
				{
					for (int i = 0; i < threads.length; i++)
					{
						if (threads[i] != null)
						{
							out.println("User: " + threads[i].username 
									+ " | IP: " + threads[i].clientSocket.getInetAddress() 
									+ " | Port: " + threads[i].clientSocket.getPort());
						}
					}
				}
				
				else if (line.startsWith("@leave"))
				{
					break;
				}
				else
				{
					for (int i = 0; i < this.maxClients; i++)
					{
						if (threads[i] != null)
						{
							threads[i].out.println("<" + this.username + ": " + line);
						}
					}
				}
			}
			for (int i = 0; i < this.maxClients; i++)
			{
				if (threads[i] != null && threads[i] != this)
				{
					threads[i].out.println("*** User " + this.username + " is leaving. ***");
				}
			}
			out.println("*** Bye " + this.username + " ***");

			/*
			 * Clean up. Set the current thread variable to null so that a new
			 * client could be accepted by the server.
			 */
			for (int i = 0; i < this.maxClients; i++)
			{
				if (threads[i] == this)
				{
					threads[i].clientSocket.close();
					threads[i].in.close();
					threads[i].out.close();
					threads[i] = null;
				}
			}

			/*
			 * Close the output stream, close the input stream, close the
			 * socket.
			 */
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e)
		{
		}
	}
	public static void printWelcomeMessage()
	{
		
	}
}