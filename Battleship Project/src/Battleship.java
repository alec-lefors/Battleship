import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

/**
 * 
 * Battleship Network Protocol (BNP)
 * 
 * HIT <Corrdinate>
 * MISS <Coordinate>
 * SUNK <Ship>
 * FIRE <Coordinate>
 * QUIT
 * 
 * @author Alec and Logan
 *
 */

public class Battleship extends Thread implements ActionListener
{

	private static ArrayList<Ship> myShips = new ArrayList<Ship>();
	private static String teamName;
	private static String serverAddress;
	private static int PORT;
	private static ServerSocket server;
	private static Socket connection;
	private static ObjectInputStream input;
	private static ObjectOutputStream output;
	private static JLabel consoleLabel = new JLabel("Welcome to Battleship.");
	private static JTextField messageType = new JTextField();
	private static JTextField message = new JTextField();
	private static boolean quit = false;

	public static void main(String[] args) throws IOException
	{
		startGame();
		Thread network = new Thread()
		{
			public void run()
			{
				runNetwork();
			}
		};
		Thread GUI = new Thread()
		{
			public void run()
			{
				printShips();
				new Battleship();
			}
		};
		network.start();
		GUI.start();
	}

	public Battleship()
	{
		JFrame frame = new JFrame(teamName);
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		JButton sendBtn = new JButton("Send");
		sendBtn.addActionListener(this);
		frame.add(messageType);
		frame.add(message);
		frame.add(sendBtn);
		statusPanel.add(consoleLabel);
		frame.add(statusPanel);
		frame.setVisible(true);
	}
	
	public static void startGame()
	{
		JPanel startGame = new JPanel();
		startGame.setLayout(new BoxLayout(startGame, BoxLayout.PAGE_AXIS));
		JTextField ipAddy = new JTextField(15);
		JTextField portNum = new JTextField(5);
		ipAddy.setText("localhost");
		JTextField name = new JTextField(10);
		startGame.add(new JLabel("IP Address"));
		startGame.add(ipAddy);
		startGame.add(new JLabel("Port"));
		startGame.add(portNum);
		startGame.add(new JLabel("Team Name"));
		startGame.add(name);
		do
		{
			int result = JOptionPane.showConfirmDialog(null, startGame, "Battleship", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.CANCEL_OPTION) System.exit(0);
		}
		while(ipAddy.getText() == "" || portNum.getText() == "" || name.getText() == "");
		teamName = name.getText();
		PORT = Integer.parseInt(portNum.getText());
		serverAddress = ipAddy.getText();
	}
	
	public static void runNetwork()
	{
		runtime:
		try
		{
			System.out.println("Attempting to connect...");
			consoleLabel.setText("Attempting to connect...");
			connection = new Socket(serverAddress, PORT);
			System.out.println("Connected to " + connection.getInetAddress().getHostName());
			consoleLabel.setText("Connected to " + connection.getInetAddress().getHostName());
			setupStreams();
			whilePlayingGame();
		}
		catch(EOFException eof)
		{
			System.out.println("Server ended the connection.");
			consoleLabel.setText("Server ended the connection.");
		}
		catch(IOException io)
		{
			try
			{
				System.out.println("No server found, creating server...");
				consoleLabel.setText("No server found, creating server...");
				server = new ServerSocket(PORT);
				while(true)
				{
					try
					{
						waitForConnection();
						setupStreams();
						whilePlayingGame();
					}
					catch(EOFException eof)
					{
						eof.printStackTrace();
					}
					finally
					{
						closeConnections();
					}
					break runtime;
				}
			}
			catch(IOException io2)
			{
				System.out.println("Client ended the connection.");
				consoleLabel.setText("Client ended the connection.");
			}
		}
		finally
		{
			closeConnections();
		}
	}
	
	public static void closeConnections()
	{
		System.out.println("Closing connections...");
		consoleLabel.setText("Closing connections...");
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void waitForConnection() throws IOException
	{
		System.out.println("Waiting for a connection...");
		consoleLabel.setText("Waiting for a connection...");
		connection = server.accept();
		System.out.println("Now conected to " + connection.getInetAddress().getHostName());
		consoleLabel.setText("Now conected to " + connection.getInetAddress().getHostName());
	}
	
	public static void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		System.out.println("Streams are set up.");
		consoleLabel.setText("Streams are set up.");
	}
	
	public static void whilePlayingGame() throws IOException
	{
		System.out.println("The server and client are now connected.");
		consoleLabel.setText("The server and client are now connected.");
		do
		{
			try
			{
				workWithMessage((BNP) input.readObject());
			}
			catch(ClassNotFoundException e)
			{
				System.out.println("Something invalid tried to go through!");
				consoleLabel.setText("Something invalid tried to go through!");
			}
		}while(!quit);
	}
	
	private static void sendMessage(BNP msg)
	{
		try
		{
			output.writeObject(msg);
			SwingUtilities.invokeLater(new Runnable(){
				public void run()
				{
					System.out.println(msg.getMessage());
				}
			});
			output.flush();
		}
		catch(IOException e)
		{
			System.out.println("Something invalid tried to send!");
			consoleLabel.setText("Something invalid tried to send!");
			e.printStackTrace();
		}
	}

	public static void printShips()
	{
		for(int i = 0; i < myShips.size(); i++)
		{
			System.out.println(myShips.get(i));
		}
	}

	public static boolean hitOrMiss(Coordinate c)
	{
		for(int i = 0; i < myShips.size(); i++)
		{
			if(myShips.get(i).isMatch(c)) return true;
		}
		return false;
	}
	
	public static void workWithMessage(BNP msg)
	{
		if(msg.getMessageType().equals("FIRE"))
		{
			Coordinate coords = (Coordinate) msg.getMessage();
			System.out.println("Fired at " + coords);
			if(hitOrMiss(coords))
			{
				sendMessage(new Hit(coords));
			}
			else
			{
				sendMessage(new Miss(coords));
			}
		}
		else if(msg.getMessageType().equals("MISS"))
		{
			Coordinate coords = (Coordinate) msg.getMessage();
			System.out.println("Missed " + coords);
		}
		else if(msg.getMessageType().equals("HIT"))
		{
			Coordinate coords = (Coordinate) msg.getMessage();
			System.out.println("Hit " + coords);
		}
		else if(msg.getMessageType().equals("SUNK"))
		{
			Ship ship = (Ship) msg.getMessage();
			System.out.println("Sunk " + ship);
		}
		else if(msg.getMessageType().equals("QUIT"))
		{
			quit = true;
		}
	}
	
	public void clearInputs()
	{
		messageType.setText("");
		message.setText("");
	}

	public void actionPerformed(ActionEvent e)
	{
		if("FIRE".equalsIgnoreCase(messageType.getText()))
		{
			sendMessage(new Fire(Coordinate.toCoordinate(message.getText())));
			clearInputs();
		}
		else if("QUIT".equalsIgnoreCase(messageType.getText()))
		{
			quit = true;
			clearInputs();
		}
		else if("ADD DESTROYER".equalsIgnoreCase(messageType.getText()))
		{
			String coords = message.getText();
			myShips.add(new Destroyer(teamName, Coordinate.toCoordinate(coords), Ship.HORIZONTAL));
			clearInputs();
		}
		else if("ADD SUBMARINE".equalsIgnoreCase(messageType.getText()))
		{
			String coords = message.getText();
			myShips.add(new Submarine(teamName, Coordinate.toCoordinate(coords), Ship.HORIZONTAL));
			clearInputs();
		}
		else if("ADD CARRIER".equalsIgnoreCase(messageType.getText()))
		{
			String coords = message.getText();
			myShips.add(new Carrier(teamName, Coordinate.toCoordinate(coords), Ship.HORIZONTAL));
			clearInputs();
		}
		else if("ADD WARSHIP".equalsIgnoreCase(messageType.getText()))
		{
			String coords = message.getText();
			myShips.add(new Warship(teamName, Coordinate.toCoordinate(coords), Ship.HORIZONTAL));
			clearInputs();
		}
		else if("ADD CRUISER".equalsIgnoreCase(messageType.getText()))
		{
			String coords = message.getText();
			myShips.add(new Cruiser(teamName, Coordinate.toCoordinate(coords), Ship.HORIZONTAL));
			clearInputs();
		}
		printShips();
	}
}