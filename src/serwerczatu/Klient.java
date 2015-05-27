package serwerczatu;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import komunikacja.*;

public class Klient extends Thread
{
	private Socket gniazdoKlienta;
	private String login = new String("");
	private String password;
	private ObjectInputStream czytelnik;
	private ObjectOutputStream pisarz;
	private Connection polaczenieZBaza;
	ArrayList <Klient> klienci;
	private Boolean loginSuccess = false;
	private Boolean inGame=false;
	private String przeciwnik;
	
	
	/*Klient(String login, String password, Socket gniazdo)
	{
		this.gniazdoKlienta = gniazdo;
		this.login = login;
		this.password = password;
		
	}*/
	
	Klient(Socket gniazdo, Connection polaczenieZBaza,ArrayList<Klient> klienci)
	{
		this.gniazdoKlienta = gniazdo;
		this.polaczenieZBaza = polaczenieZBaza;
		this.klienci = klienci;
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
	
	public void setInGame(Boolean inGame)
	{this.inGame=inGame;}
	
	public Boolean getInGame()
	{ return inGame; }
	
	public void setPrzeciwnik(String przeciwnik)
	{this.przeciwnik = przeciwnik;}
	
	public String getPrzeciwnik()
	{return przeciwnik;}
	
	public ObjectOutputStream getPisarz()
	{
		return pisarz;
	}
	
	public void weryfikacja(String login, String password)
	{
		for(int i=klienci.size()-1;i>=0;i--)
		{
			if (klienci.get(i).getLogin().equals(login))
			{
				try 
				{
					pisarz.writeObject(new RamkaSerwera(1,"zajete",""));
					pisarz.flush();
					return;
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				return;
			}
		}
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
		if(login.length()<3 || password.length()<3)
		{			
			try 
			{
				pisarz.writeObject(new RamkaSerwera(1,"too_short",""));
				pisarz.flush();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return;
		}
		
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
			try 
			{
				pisarz.writeObject(new RamkaSerwera(1,"zarejestrowano",""));
				pisarz.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			
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
				klienci.get(i).getPisarz().writeObject(pakiet);
				klienci.get(i).getPisarz().flush();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void wiadomoscDoUsera(String nadawca, String odbiorca, String wiadomosc)
	{
		for (int i = klienci.size()-1;i>=0;i--)
		{
			if (klienci.get(i).getLogin().equals(odbiorca))
			{
				try 
				{
					klienci.get(i).getPisarz().writeObject(new RamkaSerwera(5, nadawca,odbiorca,wiadomosc));
					klienci.get(i).getPisarz().flush();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				break;
			}
		}
		
	}
	
	public void zaprosGraczaDoGry(String zapraszajacy, String zapraszany)
	{
		for (int i=klienci.size()-1;i>=0;i--)
		{
			if(klienci.get(i).getLogin().equals(zapraszany))
			{
				try
				{
					klienci.get(i).getPisarz().writeObject(new RamkaSerwera(6,zapraszajacy,zapraszany));
					klienci.get(i).getPisarz().flush();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}

		
	}
	
	public void utworzGre(String klient1, String klient2) // akceptujacy, zapraszajacy
	{
		this.setInGame(true);
		this.setPrzeciwnik(klient2);
		for(int i=klienci.size()-1;i>=0;i--)
		{
			if(klienci.get(i).getLogin().equals(klient2))
				{
					try 
					{
						klienci.get(i).getPisarz().writeObject(new RamkaSerwera(8,klient1,""));
						klienci.get(i).getPisarz().flush();
						klienci.get(i).setInGame(true);
						klienci.get(i).setPrzeciwnik(klient1);
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
		}
	}
	
	public void obslugaRuchu(RamkaKlienta ramka) // DO Zrobienia!!!
	{
		//System.out.println("odebrano ruch!"); // potwierdzam odebrało 
		for (int i =klienci.size()-1;i>=0;i--)
		{
			if (klienci.get(i).getLogin().equals(ramka.getW2()))
			{
				try {
					klienci.get(i).getPisarz().writeObject(new RamkaSerwera(9,ramka.getW1(),ramka.getW2(),ramka.getPW(),ramka.getPK(),ramka.getKW(),ramka.getKK()));
					klienci.get(i).getPisarz().flush();
				}  //informacje o ruchu
				catch (IOException e)
				{
					e.printStackTrace();
				} 
			}
		}
	}
	
	public void logOut()
	{
		try 
		{
			pisarz.writeObject(new RamkaSerwera(99,"",""));
			pisarz.flush();
			
			gniazdoKlienta.close();
			klienci.remove(this); // serwer mówi dobranoc :D
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void rageQuit()
	{
		for(int i = klienci.size()-1;i>=0;i--)// obsluga rozlaczenia podczas gry DO-ZROBIENIA
		{
			if (klienci.get(i).getLogin().equals(przeciwnik))
			{
				try
				{
					klienci.get(i).getPisarz().writeObject(new RamkaSerwera(10,"",""));
					klienci.get(i).getPisarz().flush();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				klienci.get(i).setInGame(false);
				klienci.get(i).setPrzeciwnik("");
				this.setInGame(false);
				this.setPrzeciwnik("");
				break;
			}
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
						this.wiadomoscDoUsera(ramka.getW1(),ramka.getW2(),ramka.getW3());
						break;
					case 6: //zaproszenie do Gry gracza
						this.zaprosGraczaDoGry(ramka.getW1(),ramka.getW2()); // kto, kogo
						break;
					case 7: //przyjete zaproszenie -> utwórz gre 
						this.utworzGre(ramka.getW1(),ramka.getW2()); // akceptujacy, zapraszajacy
						break;
					case 8: // Pakiet z ruchem
						this.obslugaRuchu(ramka);
						break;
					case 9: // umyślne opuszczenie rozgrywki;
						this.rageQuit();
						break;
					case 10: //
						
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
			try 
			{
				if(inGame)
				{
					for(int i = klienci.size()-1;i>=0;i--)// obsluga rozlaczenia podczas gry DO-ZROBIENIA
					{
						if (klienci.get(i).getLogin().equals(przeciwnik))
						{
							klienci.get(i).getPisarz().writeObject(new RamkaSerwera(10,"",""));
							klienci.get(i).getPisarz().flush();
							klienci.get(i).setInGame(false);
							klienci.get(i).setPrzeciwnik("");
							break;
						}
					}
				}
				gniazdoKlienta.close();
				//klienci.get(klienci.indexOf(this)).stop();//loginSuccess = false;
				System.out.println("Rozłączono: "+ this.getLogin());
				klienci.remove(this);
				

			} catch (IOException e1) {
				System.out.println("ngnrtr");
				//e1.printStackTrace();
			}
			//e.printStackTrace();	
		}
	}
	
	
}
