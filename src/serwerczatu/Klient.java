package serwerczatu;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import komunikacja.*;

public class Klient implements Runnable
{
	private Socket gniazdoKlienta;
	private String login;
	private String password;
	private ObjectInputStream czytelnik;
	private ObjectOutputStream pisarz;
	private Connection polaczenieZBaza;
	private ArrayList<Klient> klienci;
	ArrayList <Thread> watkiKlientow;
	private Boolean loginSuccess = false;
	
	
	/*Klient(String login, String password, Socket gniazdo)
	{
		this.gniazdoKlienta = gniazdo;
		this.login = login;
		this.password = password;
		
	}*/
	
	Klient(Socket gniazdo, Connection polaczenieZBaza,ArrayList<Klient> klienci,ArrayList<Thread> watkiKlientow)
	{
		this.gniazdoKlienta = gniazdo;
		this.polaczenieZBaza = polaczenieZBaza;
		this.klienci = klienci;
		this.watkiKlientow = watkiKlientow;
	}
	
	public Socket getGniazdo()
	{
		return gniazdoKlienta;
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public Boolean getLoginSuccess()
	{
		return loginSuccess;
	}
	
	public void weryfikacja(String login, String password)
	{
		try
		{
			Statement stat = polaczenieZBaza.createStatement();
			ResultSet rs = stat.executeQuery("SELECT LOGIN FROM UZYSZKODNICY ");
			while (rs.next())
			{
				String tmplogin = rs.getString("LOGIN");
				if (login.equals(tmplogin))
				{
					ResultSet rs2 = stat.executeQuery("SELECT password"+  " FROM UZYSZKODNICY" +  " WHERE UZYSZKODNICY.LOGIN LIKE '"+login+"' ");
					rs2.next();
					String tmppass = rs2.getString("password");
					{
						if (password.equals(tmppass))
						{
							this.login = login;
							this.password = password;
							System.out.println("Zalogowano uzyszkodnika: "+login);
							try 
							{							
								pisarz.writeObject(new RamkaSerwera(1,"zalogowano",""));
								pisarz.flush();
								loginSuccess = true;
								//System.out.println("udalo sie");
							} 
							catch (IOException e) 
							{
								System.out.println("Nie udalo sie poslac informacji o zalogowaniu!");
								//e.printStackTrace();
							}
							
							
						}
					}
				}
				
			}
			
			if (loginSuccess != true)
			{
				try 
				{			
					pisarz.writeObject(new RamkaSerwera(1,"bledne_dane",""));
					pisarz.flush();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				
			}
			
		} 
		catch (SQLException e)
		{
			
			//e.printStackTrace();
		}
		
	}
	
	public void rejestracja(String login, String password)
	{
		try
		{
			Statement stat = polaczenieZBaza.createStatement();
			ResultSet rs = stat.executeQuery("SELECT LOGIN FROM UZYSZKODNICY ");
			while (rs.next())
			{
				String tmplogin = rs.getString("LOGIN");
				if (login.equals(tmplogin))
				{
					try 
					{	
						RamkaSerwera pakiet = new RamkaSerwera(1,"zajete","");
						pisarz.writeObject(pakiet);
						pisarz.flush();
						System.out.println("tu jestem"); // daje rade
						return;
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			
			stat.executeUpdate("INSERT INTO uzyszkodnicy (login,password,user_type) "+
			"VALUES ('"+login+"','"+password+"',1.0)");
			stat.close();
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void wyslijListeGraczy()
	{
		RamkaSerwera ramka = new RamkaSerwera(3,"","");
		for (int i = this.klienci.size()-1; i>=0 ;i--)
		{
			if(klienci.get(i).getLoginSuccess()==true)
			{
				ramka.addClientToList(klienci.get(i).getLogin());
			}
		}
		try 
		{
			pisarz.writeObject(ramka);
			pisarz.flush();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void wiadomoscGlobalna(String gracz, String wiadomosc)
	{
		for (int i =0;i<klienci.size();i++)
		{
			RamkaSerwera pakiet = new RamkaSerwera(4,gracz,wiadomosc);
			try {
				klienci.get(i).pisarz.writeObject(pakiet);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void logOut()
	{
		
		
	}
	
	
	@Override
	public void run()
	{
		try
		{
			
			this.czytelnik = new ObjectInputStream(gniazdoKlienta.getInputStream());
			this.pisarz = new ObjectOutputStream(gniazdoKlienta.getOutputStream());
			pisarz.flush();
			RamkaKlienta ramka = null;
			while(true)
			{
				ramka = (RamkaKlienta) czytelnik.readObject();
				int typ = ramka.getRodzaj();
				System.out.println(typ);
				switch (typ)
				{
					case 1:
						this.weryfikacja(ramka.getW1(),ramka.getW2());
						
						break;
					case 2:
						this.rejestracja(ramka.getW1(),ramka.getW2());
						
						break;
					case 3: //prośba o listę graczy
						this.wyslijListeGraczy();
						break;
					case 4: //Wiadomosc do kazdego
						this.wiadomoscGlobalna(ramka.getW1(),ramka.getW2());
						break;
					case 5: // wiadomosc do konkretnego gracza
						
						break;
					case 6: //prośba o liste gier
						
						break;
					case 7: //zalozenie nowej gry
						
						break;
					case 8: //dolaczenie do nowej gry
						
						break;
					case 9: // potwierdzenie startu
						
						break;
					case 10: // pakiet z ruchem
						
						break;
					case 99: // wylogowanie
						this.logOut();
						break;
						
						
				}

			}
		
		}
		catch(Exception e)
		{
			//w sumie tu moznaby dodac obsluge kasowania klientow;
			//int index = klienci.indexOf(this);
			//klie
			try {
				gniazdoKlienta.close();
				loginSuccess = false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("ngnrtr");
				//e1.printStackTrace();
			}
			e.printStackTrace();	
		}
	}
	
	
}
