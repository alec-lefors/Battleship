import java.util.ArrayList;

public class Ship
{
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;
	private static final String[][] CLASSES = {
			{"Carrier", "5"}, 
			{"Battleship", "4"},
			{"Cruiser", "3"},
			{"Submarine", "3"},
			{"Destroyer", "2"}
		};
	public static final String[] CARRIER = CLASSES[0];
	public static final String[] BATTLESHIP = CLASSES[1];
	public static final String[] CRUISER = CLASSES[2];
	public static final String[] SUBMARINE = CLASSES[3];
	public static final String[] DESTROYER = CLASSES[4];
	
	private int length;
	private String team;
	private ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
	private int orientation;
	private String shipClass;
	
	public Ship(String[] s, String t, Coordinate c, int o)
	{
		shipClass = s[0];
		length = Integer.parseInt(s[1]);
		team = t;
		orientation = o;
		buildArray(c, orientation);
	}

	public void buildArray(Coordinate c, int o)
	{
		if(o == HORIZONTAL)
		{
			for(int i = 0; i < length; i++)
			{
				coords.add(new Coordinate(c.getX() + i, c.getY()));
			}
		}
		else
		{
			for(int i = 0; i < length; i++)
			{
				coords.add(new Coordinate(c.getX(), c.getY() + i));
			}
		}
	}
	
	public boolean isMatch(Coordinate c)
	{
		for(int i = 0; i < coords.size(); i++)
		{
			if(coords.get(i).equalsTo(c)) return true;
		}
		return false;
	}
	
	public String toString()
	{
		return team + "'s " + shipClass + " is at " + coords.toString();
	}
	
}
