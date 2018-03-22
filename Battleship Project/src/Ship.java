import java.io.Serializable;
import java.util.ArrayList;

public class Ship implements Serializable
{
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;

	private int length;
	private String team;
	private ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
	private int orientation;
	private String shipClass;

	public Ship(String team, Coordinate coordinate, int orientation, int length, String shipClass)
	{
		this.team = team;
		this.orientation = orientation;
		this.length = length;
		this.shipClass = shipClass;
		coords.add(coordinate);
		buildArray();
	}

	public void setOrientation(int orientation)
	{
		this.orientation = orientation;
		buildArray();
	}

	public void buildArray()
	{
		if(orientation == HORIZONTAL)
		{
			for(int i = 1; i < length; i++)
			{
				coords.add(new Coordinate(coords.get(0).getX() + i, coords.get(0).getY()));
			}
		}
		else
		{
			for(int i = 1; i < length; i++)
			{
				coords.add(new Coordinate(coords.get(0).getX(), coords.get(0).getY() + i));
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

	public String getShipClass()
	{
		return shipClass;
	}

	public String toString()
	{
		return team + "'s " + shipClass + " is at " + coords.toString();
	}

}

class Carrier extends Ship
{
	private static String shipClass = "Carrier";
	private static int length = 5;

	public Carrier(String t, Coordinate c, int o)
	{
		super(t, c, o, length, shipClass);
	}

}

class Warship extends Ship
{
	private static String shipClass = "Warship";
	private static int length = 3;

	public Warship(String t, Coordinate c, int o)
	{
		super(t, c, o, length, shipClass);
	}

}

class Submarine extends Ship
{
	private static String shipClass = "Submarine";
	private static int length = 3;

	public Submarine(String t, Coordinate c, int o)
	{
		super(t, c, o, length, shipClass);
	}

}

class Cruiser extends Ship
{
	private static String shipClass = "Cruiser";
	private static int length = 3;

	public Cruiser(String t, Coordinate c, int o)
	{
		super(t, c, o, length, shipClass);
	}

}

class Destroyer extends Ship
{
	private static String shipClass = "Destroyer";
	private static int length = 2;

	public Destroyer(String t, Coordinate c, int o)
	{
		super(t, c, o, length, shipClass);
	}

}