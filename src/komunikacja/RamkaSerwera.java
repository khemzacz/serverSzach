package komunikacja;

import java.io.Serializable;
import java.util.ArrayList;

public class RamkaSerwera implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -443743683776580613L;
	private int rodzaj;
	private String wiadomosc1;
	private String wiadomosc2;
	private ArrayList<String> klienci;
	
	
	public RamkaSerwera(int r, String w1, String w2)
	{
		this.rodzaj=r;
		this.wiadomosc1=w1;
		this.wiadomosc2=w2;
		this.klienci=new ArrayList<String>();
	}
	
	public RamkaSerwera(ArrayList<String> klienci)
	{
		this.rodzaj=3;
		this.klienci = new ArrayList<String>(klienci);
	}
	
	public ArrayList <String> getClientList()
	{
		return klienci;
	}
	
	public void addClientToList(String klient)
	{
		klienci.add(klient);
	}
	
	public String getW1()
	{
		return wiadomosc1;
	}
	
	public String getW2()
	{
		return wiadomosc2;
	}
	
	public int getRodzaj()
	{
		return rodzaj;
	}
	
	
}
