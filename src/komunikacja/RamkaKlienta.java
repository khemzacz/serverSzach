package komunikacja;

import java.io.Serializable;

public class RamkaKlienta implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4038219745714368545L;
	private int rodzaj;
	private String wiadomosc1;
	private String wiadomosc2;
	
	public RamkaKlienta(int r, String w1, String w2)
	{
		this.rodzaj=r;
		this.wiadomosc1=w1;
		this.wiadomosc2=w2;
	}
	
	public int getRodzaj()
	{
		return rodzaj;
	}
	
	public String getW1()
	{
		return wiadomosc1;
	}
	
	public String getW2()
	{
		return wiadomosc2;
	}

}
