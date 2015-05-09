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
	private Boolean loginSuccess = false;
	
	Klient(String login, String password, Socket gniazdo)
	{
		this.gniazdoKlienta = gniazdo;
		this.login = login;
		this.password = password;
		
	}
	
	Klient(Socket gniazdo, Connection polaczenieZBaza)
	{
		this.gniazdoKlienta = gniazdo;
		this.polaczenieZBaza = polaczenieZBaza;
		
	}
	
	public Socket getGniazdo()
	{
		return gniazdoKlienta;
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

	@Override
	public void run()
	{
		try
		{
			
			this.czytelnik = new ObjectInputStream(gniazdoKlienta.getInputStream());
			this.pisarz = new ObjectOutputStream(gniazdoKlienta.getOutputStream());
			pisarz.flush();
			RamkaKlienta ramka = null;
			while((ramka = (RamkaKlienta) czytelnik.readObject()) !=null)
			{
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
						
				}

			}
		
		}
		catch(Exception e)
		{
		 e.printStackTrace();	
		}
	}
	
	
}
