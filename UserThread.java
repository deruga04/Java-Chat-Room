import java.io.*;
import java.net.*;

class UserThread extends Thread
{

	private DataInputStream in = null;
	private PrintStream out = null;
	private Socket clientSocket = null;
	private final UserThread[] threads;
	private int maxClients;

	public UserThread(Socket clientSocket, UserThread[] threads)
	{
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClients = threads.length;
	}

	public void run()
	{
		int maxClientsCount = this.maxClients;
		UserThread[] threads = this.threads;

		try
		{
			/*
			 * Create input and output streams for this client.
			 */
			in = new DataInputStream(clientSocket.getInputStream());
			out = new PrintStream(clientSocket.getOutputStream());
			printWelcomeMessage();
			out.println("Enter your name.");
			String name = in.readLine().trim();
			out.println("Hello " + name + " to our chat room.\nTo leave enter /quit in a new line");
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] != null && threads[i] != this)
				{
					threads[i].out.println("*** A new user " + name + " entered the chat room !!! ***");
				}
			}
			while (true)
			{
				String line = in.readLine();
				if (line.startsWith("/quit"))
				{
					break;
				}
				for (int i = 0; i < maxClientsCount; i++)
				{
					if (threads[i] != null)
					{
						threads[i].out.println("<" + name + "&gr; " + line);
					}
				}
			}
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] != null && threads[i] != this)
				{
					threads[i].out.println("*** The user " + name + " is leaving the chat room !!! ***");
				}
			}
			out.println("*** Bye " + name + " ***");

			/*
			 * Clean up. Set the current thread variable to null so that a new
			 * client could be accepted by the server.
			 */
			for (int i = 0; i < maxClientsCount; i++)
			{
				if (threads[i] == this)
				{
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
		System.out.println("***************************");
		System.out.println("Welcome to the chat room!");
		System.out.println("@join - enter the server");
		System.out.println("@leave - exit the server");
		System.out.println("@list - users currently in the room");
		System.out.println("***************************");
	}
}