import java.awt.Color;
import java.io.Serializable;

import javax.swing.JButton;

public class Coordinate implements Serializable
{
	private Integer xaxis;
	private Integer yaxis;
	private String battleshipNotation;

	public Coordinate(int x, int y)
	{
		xaxis = x;
		yaxis = y;
		battleshipNotation = Character.toString((char) (xaxis + 65)) + (yaxis + 1);
	}

	public String toString()
	{
		return battleshipNotation;
	}

	public int getX()
	{
		return xaxis;
	}

	public int getY()
	{
		return yaxis;
	}

	public static Coordinate toCoordinate(String notation)
	{
		return new Coordinate(((int) notation.charAt(0)) - 65,
				Integer.parseInt(notation.substring(1)) - 1);
	}

	public boolean equalsTo(Object obj)
	{
		if(obj instanceof Coordinate && ((Coordinate) obj).xaxis.equals(this.xaxis)
				&& ((Coordinate) obj).yaxis.equals(this.yaxis))
		{
			return true;
		}
		return false;
	}
	
	public static int convertToBtn(Coordinate coords)
	{
		int i = 0;
		for(int x = 0; x < 10; x++)
		{
			for(int y = 0; y < 10; y++)
			{
				if(coords.getX() == x && coords.getY() == y) return i;
				i++;
			}
		}
		return 99;
	}
}
