package serwerczatu;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Klient
{
	private Socket gniazdoKlienta;
	private String login;
	private String password;
	private InputStream czytelnik;
	private PrintWriter pisarz;
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
		try
		{
			this.czytelnik = gniazdo.getInputStream();
			try(Scanner in = new Scanner(czytelnik))
			{
				String kom1=in.nextLine();
				if (kom1.equals("loginAttempt"))
				{
					this.weryfikacja(in.nextLine(),in.nextLine());
				}
				else if (kom1.equals("registerAttempt"))
					this.rejestracja(in.nextLine(),in.nextLine());
			}
			
		}
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
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
								pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
								pisarz.println("zalogowano");
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
					pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
					pisarz.println("bledne_dane");
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
						pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
						pisarz.println("zajete");
						pisarz.flush();
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
	
	
}
