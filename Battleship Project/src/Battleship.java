import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

/**
 * 
 * Battleship Network Protocol (BNP)
 * 
 * HIT <Coordinate>
 * MISS <Coordinate>
 * SUNK <Ship>
 * FIRE <Coordinate>
 * TEAM <String>
 * QUIT
 * 
 * @author Alec and Logan
 *
 */

public class Battleship extends Thread implements ActionListener
{

	private static volatile ArrayList<Ship> myShips = new ArrayList<Ship>();
	private static ArrayList<JButton> myGrid = new ArrayList<JButton>();
	private static ArrayList<JButton> theirGrid = new ArrayList<JButton>();
	private static ArrayList<Coordinate> misses = new ArrayList<Coordinate>();
	private static ArrayList<Coordinate> hits = new ArrayList<Coordinate>();
	private static String teamName;
	private static String serverAddress;
	private static int PORT;
	private static ServerSocket server;
	private static Socket connection;
	private static ObjectInputStream input;
	private static ObjectOutputStream output;
	private static JTextArea consoleLabel = new JTextArea("Welcome to Battleship.");
	private static boolean quit = false;
	private static String enemyTeam = "";
	private static boolean yourTurn;
	private static TitledBorder theirBorder;
	private static JPanel theirBoard = new JPanel();
	private static ArrayList<JButton> menuButtons = new ArrayList<JButton>();
	private static int orientation = Ship.HORIZONTAL;
	private static Object currentSelection = "";
	private static Coordinate coordSelection;
	private static JPanel options = new JPanel();
	private static JScrollPane console = new JScrollPane(consoleLabel);
	private static JScrollBar scroll = console.getVerticalScrollBar();
	private static JTextField chatText = new JTextField(20);
	private static JButton sendChat = new JButton("Send");
	
	private static JButton addCarrier = new JButton("Carrier");
	private static JButton addWarship = new JButton("Battleship");
	private static JButton addSubmarine = new JButton("Submarine");
	private static JButton addCruiser = new JButton("Cruiser");
	private static JButton addDestroyer = new JButton("Destroyer");
	private static JButton finishedButton = new JButton("Finished");
	
	private static JButton setOrientation = new JButton("Horizontal");
	private static volatile Boolean readyUp = false;
	private static volatile Boolean enemyReady = false;
	private static volatile Boolean gameIsReady = false;
	

	public static void main(String[] args) throws IOException
	{
		startGame();
		Thread GUI = new Thread()
		{
			public void run()
			{
				new Battleship();
			}
		};
		network.start();
		GUI.start();
	}
	
	private static Thread network = new Thread()
	{
		public void run()
		{
			runNetwork();
		}
	};
	
	private static Thread game = new Thread()
	{
		public void run()
		{
			sendMessage(new Team(teamName));
			try {
				Thread.sleep(700);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			theirBorder.setTitle(enemyTeam + "'s Board");
			theirBoard.repaint();
			sendChat.setEnabled(true);
			placeShips();
			toConsole("Waiting on " + enemyTeam + ".");
			while(!enemyReady) {}
			nextTurn();
		}
	};
	
	public static void nextTurn()
	{
		toConsole(yourTurn ? "It is your turn." : "It is " + enemyTeam + "'s turn.");
		if(yourTurn)
		{
			for(int i = 0; i < myGrid.size(); i++) theirGrid.get(i).setEnabled(true);
		}
		else
		{
			for(int i = 0; i < myGrid.size(); i++) theirGrid.get(i).setEnabled(false);
		}
	}
	
	public static void setMyGrid(boolean tf)
	{
		for(int i = 0; i < myGrid.size(); i++)
		{
			myGrid.get(i).setEnabled(tf);
		}
	}
	
	public static void placeShips()
	{
		toConsole("Place your ships.");
		for(int i = 0; i < menuButtons.size(); i++) menuButtons.get(i).setEnabled(true);
		setMyGrid(true);
		while(myShips.size() != 5) {}
		finishedButton.setEnabled(true);
		toConsole("Press finished when you are done.");
		while(!readyUp) {}
		finishedButton.setVisible(false);
		for(int i = 0; i < menuButtons.size(); i++) menuButtons.get(i).setEnabled(false);
		setMyGrid(false);
		for(int i = 0; i < menuButtons.size(); i++) menuButtons.get(i).setVisible(false);
	}

	public Battleship()
	{
		JFrame frame = new JFrame(teamName);
		JPanel myBoard = new JPanel();
		JPanel boards = new JPanel();
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder border = BorderFactory.createTitledBorder(blackline, "Menu");
		TitledBorder myBorder = BorderFactory.createTitledBorder(blackline, "My Board");
		theirBorder = BorderFactory.createTitledBorder(blackline, "Opponent's Board");
		myBoard.setBorder(myBorder);
		theirBoard.setBorder(theirBorder);
		options.setBorder(border);
		myBoard.setLayout(new GridLayout(11, 11));
		theirBoard.setLayout(new GridLayout(11, 11));
		boards.setLayout(new BoxLayout(boards, BoxLayout.PAGE_AXIS));
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setMinimumSize(new Dimension(500, 500));
		options.add(console);
		options.add(chatText);
		chatText.addActionListener(this);
		chatText.setActionCommand("Chat");
		sendChat.addActionListener(this);
		sendChat.setActionCommand("Chat");
		options.add(sendChat);
		sendChat.setEnabled(false);
		finishedButton.addActionListener(this);
		finishedButton.setActionCommand("Finished");
		finishedButton.setEnabled(false);
		menuButtons.add(addCarrier);
		menuButtons.add(addWarship);
		menuButtons.add(addSubmarine);
		menuButtons.add(addCruiser);
		menuButtons.add(addDestroyer);
		menuButtons.add(setOrientation);
		for(int i = 0; i < menuButtons.size(); i++)
		{
			menuButtons.get(i).addActionListener(this);
			menuButtons.get(i).setActionCommand(menuButtons.get(i).getName());
			options.add(menuButtons.get(i));
			menuButtons.get(i).setEnabled(false);
		}
		options.add(finishedButton);
		
		makeButtons(theirGrid);
		theirBoard.add(new JLabel(""));
		for(Integer i = 1; i <= 10; i++) theirBoard.add(new JLabel("<html><b>" + i.toString() + "</b>") {{setVerticalAlignment(JLabel.CENTER); setHorizontalAlignment(JLabel.CENTER);}});
		for(int i = 0; i < theirGrid.size(); i++)
		{
			if((i) % 10 == 0) theirBoard.add(new JLabel("<html><b>" + Character.toString((char) (i / 10 + 65)) + "</b>") {{setVerticalAlignment(JLabel.CENTER); setHorizontalAlignment(JLabel.CENTER);}});
			theirBoard.add(theirGrid.get(i));
			theirGrid.get(i).setEnabled(false);
		}
		
		makeButtons(myGrid);
		myBoard.add(new JLabel(""));
		for(Integer i = 1; i <= 10; i++) myBoard.add(new JLabel("<html><b>" + i.toString() + "</b>") {{setVerticalAlignment(JLabel.CENTER); setHorizontalAlignment(JLabel.CENTER);}});
		for(int i = 0; i < myGrid.size(); i++)
		{
			if((i) % 10 == 0) myBoard.add(new JLabel("<html><b>" + Character.toString((char) (i / 10 + 65)) + "</b>") {{setVerticalAlignment(JLabel.CENTER); setHorizontalAlignment(JLabel.CENTER);}});
			myBoard.add(myGrid.get(i));
			myGrid.get(i).setEnabled(false);
		}
		consoleLabel.setEditable(false);
		console.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleLabel.setPreferredSize(new Dimension(140, 400));
		consoleLabel.setLineWrap(true);
		consoleLabel.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)consoleLabel.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		boards.add(theirBoard);
		boards.add(myBoard);
		frame.add(options, BorderLayout.LINE_END);
		frame.add(boards, BorderLayout.CENTER);
		frame.setVisible(true);
		while(!gameIsReady) {}
		System.out.println(gameIsReady);
		game.start();
	}
	
	public static void startGame()
	{
		JPanel startGame = new JPanel();
		startGame.setLayout(new BoxLayout(startGame, BoxLayout.PAGE_AXIS));
		JTextField ipAddy = new JTextField(15);
		JTextField portNum = new JTextField(5);
		ipAddy.setText("localhost");
		portNum.setText("25552");
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
		try
		{
			toConsole("Attempting to connect...");
			connection = new Socket(serverAddress, PORT);
			toConsole("Connected to " + connection.getInetAddress().getHostAddress());
			yourTurn = true;
			setupStreams();
			whilePlayingGame();
		}
		catch(EOFException eof)
		{
			toConsole("Server ended the connection.");
		}
		catch(IOException io)
		{
			try
			{
				toConsole("No server found, creating server...");
				server = new ServerSocket(PORT);
				while(true)
				{
					try
					{
						waitForConnection();
						setupStreams();
						yourTurn = false;
						game.start();
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
				}
			}
			catch(IOException io2)
			{
				toConsole("Client ended the connection.");
			}
		}
		finally
		{
			closeConnections();
		}
	}
	
	public static void closeConnections()
	{
		toConsole("Closing connections...");
		try
		{
			output.close();
			input.close();
			connection.close();
			toConsole("Connection closed.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void waitForConnection() throws IOException
	{
		toConsole("Waiting for a connection...");
		connection = server.accept();
		toConsole("Now conected to " + connection.getInetAddress().getHostAddress());
	}
	
	public static void setupStreams() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}
	
	public static void whilePlayingGame() throws IOException
	{
		do
		{
			try
			{
				workWithMessage((BNP) input.readObject());
			}
			catch(ClassNotFoundException e)
			{
				toConsole("Something invalid tried to go through!");
			}
			gameIsReady = true;
		}while(!quit);
	}

	private static void sendMessage(BNP msg)
	{
		try
		{
			output.writeObject(msg);
			output.flush();
		}
		catch(IOException e)
		{
			toConsole("Something invalid tried to send!");
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
			System.out.println(enemyTeam + " fired at " + coords);
			if(hitOrMiss(coords))
			{
				sendMessage(new Hit(coords));
				toConsole(enemyTeam + " hit " + coords);
				myGrid.get(Coordinate.convertToBtn(coords)).setBackground(new Color(224, 44, 44));
			}
			else
			{
				yourTurn = true;
				sendMessage(new Miss(coords));
				toConsole(enemyTeam + " missed " + coords);
				myGrid.get(Coordinate.convertToBtn(coords)).setBackground(new Color(237, 237, 237));
			}
			nextTurn();
		}
		else if(msg.getMessageType().equals("MISS"))
		{
			yourTurn = false;
			Coordinate coords = (Coordinate) msg.getMessage();
			misses.add(coords);
			toConsole("You missed " + coords);
			theirGrid.get(Coordinate.convertToBtn(coords)).setBackground(new Color(237, 237, 237));
			nextTurn();
		}
		else if(msg.getMessageType().equals("HIT"))
		{
			yourTurn = true;
			Coordinate coords = (Coordinate) msg.getMessage();
			hits.add(coords);
			toConsole("You hit " + coords);
			theirGrid.get(Coordinate.convertToBtn(coords)).setBackground(new Color(224, 44, 44));
			nextTurn();
		}
		else if(msg.getMessageType().equals("SUNK"))
		{
			Ship ship = (Ship) msg.getMessage();
			System.out.println("Sunk " + ship);
		}
		else if(msg.getMessageType().equals("TEAM"))
		{
			enemyTeam = (String) msg.getMessage();
			toConsole("Playing " + enemyTeam);
		}
		else if(msg.getMessageType().equals("READY"))
		{
			enemyReady = true;
			if(!readyUp) toConsole(enemyTeam + " is ready.");
		}
		else if(msg.getMessageType().equals("CHAT"))
		{
			String message = (String) msg.getMessage();
			toConsole(message);
		}
		else if(msg.getMessageType().equals("QUIT"))
		{
			closeConnections();
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Horizontal") || e.getActionCommand().equals("Vertical"))
		{
			orientation = orientation == Ship.VERTICAL ? Ship.HORIZONTAL : Ship.VERTICAL;
			setOrientation.setText(orientation == Ship.HORIZONTAL ? "Horizontal" : "Vertical");
		}
		else if(e.getActionCommand().equals("Carrier") || e.getActionCommand().equals("Battleship") || e.getActionCommand().equals("Submarine") || e.getActionCommand().equals("Cruiser") || e.getActionCommand().equals("Destroyer"))
		{
			currentSelection = e.getActionCommand();
			if(currentSelection.equals("Carrier")) currentSelection = new Carrier(teamName);
			if(currentSelection.equals("Battleship")) currentSelection = new Warship(teamName);
			if(currentSelection.equals("Submarine")) currentSelection = new Submarine(teamName);
			if(currentSelection.equals("Cruiser")) currentSelection = new Cruiser(teamName);
			if(currentSelection.equals("Destroyer")) currentSelection = new Destroyer(teamName);
		}
		else if(e.getActionCommand().equals("Finished"))
		{
			readyUp = true;
			sendMessage(new Ready());
		}
		else if(e.getActionCommand().equals("Quit"))
		{
			sendMessage(new Quit());
			closeConnections();
		}
		else if(e.getActionCommand().equals("Chat"))
		{
			sendMessage(new Chat(teamName, chatText.getText()));
			toConsole("You: " + chatText.getText());
			chatText.setText("");
		}
		else if(!(currentSelection instanceof String))
		{
			if(readyUp)
			{
				coordSelection = Coordinate.toCoordinate(e.getActionCommand());
				sendMessage(new Fire(coordSelection));
			}
			else
			{
				coordSelection = Coordinate.toCoordinate(e.getActionCommand());
				Ship newShip = (Ship) currentSelection;
				newShip.setCoords(coordSelection);
				newShip.setOrientation(orientation);
				if(myShips.size() == 0)
				{
					myShips.add(newShip);
				}
				else
				{
					for(int i = 0; i < myShips.size(); i++)
					{
						if(myShips.get(i).getShipClass().equals(newShip.getShipClass()))
						{
							myShips.remove(i);
							break;
						}
					}
					myShips.add(newShip);
				}
				paintMyGrid();
			}
		}
	}
	
	public void paintMyGrid()
	{
		int i = 0;
		for(int x = 0; x < 10; x++)
		{
			for(int y = 0; y < 10; y++)
			{
				Coordinate coords = new Coordinate(x, y);
				JButton btn = myGrid.get(i);
				ArrayList<Coordinate> list = new ArrayList<Coordinate>();
				for(int o = 0; o < myShips.size(); o++)
				{
					if(myShips.get(o).isMatch(coords) || list.contains(coords))
					{
						btn.setBackground(new Color(40, 40, 40));
						list.add(coords);
					}
					else
					{
						btn.setBackground(new Color(89, 152, 255));
					}
				}
				i++;
			}
		}
	}
	
	
	public static void toConsole(String msg)
	{
		System.out.println(msg);
		consoleLabel.setText(consoleLabel.getText() + "\n~ " + msg);
	}
	
	public void makeButtons(ArrayList<JButton> grid)
	{
		for(int x = 0; x < 10; x++)
		{
			for(int y = 0; y < 10; y++)
			{
				String name = new Coordinate(x, y).toString();
				JButton newBtn = new JButton();
				newBtn.setActionCommand(name);
				newBtn.addActionListener(this);
				newBtn.setFocusPainted(false);
				newBtn.setBackground(new Color(89, 152, 255));
				newBtn.setOpaque(true);
				newBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				grid.add(newBtn);
			}
		}
	}
}