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
	private String wiadomosc3;
	private ArrayList<String> klienci;
	
	private int poczatkowyWiersz;
	private int poczatkowaKolumna;
	
	private int koncowyWiersz;
	private int koncowaKolumna;
	
	public RamkaSerwera(int r, String w1, String w2)
	{
		this.rodzaj=r;
		this.wiadomosc1=w1;
		this.wiadomosc2=w2;
		this.klienci=new ArrayList<String>();
	}

	public RamkaSerwera(int r, String w1, String w2, String w3)
	{
		this.rodzaj=r;
		this.wiadomosc1=w1;
		this.wiadomosc2=w2;
		this.wiadomosc3=w3;
		this.klienci=new ArrayList<String>();
	}
	
	public RamkaSerwera(ArrayList<String> klienci)
	{
		this.rodzaj=3;
		this.klienci = new ArrayList<String>(klienci);
	}
	
	public RamkaSerwera(int r, String kto, String doKogo, int pw, int pk, int kw, int kk)
	{
		this.rodzaj=r;
		this.poczatkowyWiersz=pw;
		this.poczatkowaKolumna=pk;
		this.koncowyWiersz=kw;
		this.koncowaKolumna=kk;
		this.wiadomosc1=kto;
		this.wiadomosc2=doKogo;
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
	
	public String getW3()
	{
		return wiadomosc3;
	}
	
	public int getRodzaj()
	{
		return rodzaj;
	}
	
	public int getPW()
	{
		return poczatkowyWiersz;
	}
	
	public int getPK()
	{
		return poczatkowaKolumna;
	}

	public int getKW()
	{
		return koncowyWiersz;
	}
	
	public int getKK()
	{
		return koncowaKolumna;
	}
	
}
