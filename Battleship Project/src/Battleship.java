 import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Battleship
{
	
	private static ArrayList<Ship> myShips = new ArrayList<Ship>();
	private static String teamName;

	public static void main(String[] args)
	{
		new Battleship();
		Coordinate myCoord = new Coordinate(8, 8);
		Ship ship1 = new Ship(Ship.BATTLESHIP, teamName, new Coordinate(1, 4), Ship.VERTICAL);
		Ship ship2 = new Ship(Ship.CRUISER, teamName, new Coordinate(5, 4), Ship.HORIZONTAL);
		Ship ship3 = new Ship(Ship.DESTROYER, teamName, new Coordinate(7, 8), Ship.HORIZONTAL);
		myShips.add(ship1);
		myShips.add(ship2);
		myShips.add(ship3);
		printShips();
		System.out.println(myCoord);
		System.out.println(hitOrMiss(myCoord));
	}
	
	public Battleship()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		JPanel startGame = new JPanel();
		startGame.setLayout(new BoxLayout(startGame, BoxLayout.PAGE_AXIS));
		JLabel portLabel = new JLabel("Port");
		JTextField ipAddy = new JTextField(15);
		JLabel ipLabel = new JLabel("IP Address");
		JTextField portNum = new JTextField(5);
		JLabel nameLabel = new JLabel("Team Name");
		JTextField name = new JTextField(10);
		startGame.add(ipLabel);
		startGame.add(ipAddy);
		startGame.add(portLabel);
		startGame.add(portNum);
		startGame.add(nameLabel);
		startGame.add(name);
		do
		{
			int result = JOptionPane.showConfirmDialog(null, startGame, "Battleship", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.CANCEL_OPTION) System.exit(0);
		} while(ipAddy == null || portNum == null || name == null);
		teamName = name.getText();
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

}
