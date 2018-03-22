import java.io.Serializable;

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
				Integer.parseInt(Character.toString(notation.charAt(1))) - 1);
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
}
