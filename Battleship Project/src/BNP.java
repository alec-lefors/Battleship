import java.io.Serializable; 

public class BNP implements Serializable
{
	private String messageType;
	private Object message;
	
	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}
	
	public void setMessage(Object message)
	{
		this.message = message;
	}
	
	public String getMessageType()
	{
		return messageType;
	}
	
	public Object getMessage()
	{
		return message;
	}
}

class Fire extends BNP
{
	public Fire(Coordinate coordinates)
	{
		setMessageType("FIRE");
		setMessage(coordinates);
	}
}

class Hit extends BNP
{
	public Hit(Coordinate coordinates)
	{
		setMessageType("HIT");
		setMessage(coordinates);
	}
}

class Miss extends BNP
{
	public Miss(Coordinate coordinates)
	{
		setMessageType("MISS");
		setMessage(coordinates);
	}
}

class Sunk extends BNP
{
	public Sunk(Ship ship)
	{
		setMessageType("SUNK");
		setMessage(ship);
	}
}

class Quit extends BNP
{
	public Quit()
	{
		setMessageType("QUIT");
		setMessage("QUIT");
	}
}