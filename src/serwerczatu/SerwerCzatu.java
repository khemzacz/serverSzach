package serwerczatu;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerwerCzatu implements Runnable
{
	//ArrayList <Klient> klienci= new ArrayList<Klient>();
	ArrayList <Klient> klienci = new ArrayList<Klient>();
	Klient klient;
	Thread watek;
	//ObslugaKlientow obslugaKlientow = new ObslugaKlientow();
	Connection polaczenieZBaza;
	//Czysciciel czysciciel = new Czysciciel(this);
	//Thread watekCzysciciela = new Thread(czysciciel);
	//Czysciciel czysciciel = new Czysciciel(this);
	
	SerwerCzatu(Connection polaczenieZBaza)
	{
		this.polaczenieZBaza= polaczenieZBaza;
	}
		
	public void rozeslijDoWszystkich(String message)
	{
		for (int i = 0 ; i < klienci.size();i++)
		{
			try{
				PrintWriter pisarz = new PrintWriter(klienci.get(i).getGniazdo().getOutputStream());
				pisarz.println(message);
				pisarz.flush();
				System.out.println("Wysylanie");
				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
		
	}
	
	
	
	public void run()
	{
		try {
			ServerSocket serwerSock = new ServerSocket(5000); // tworzy serwer monitorujacy port 5000
			while(true)
			{
				Socket gniazdoKlienta = serwerSock.accept(); // program oczekuje az klient przylaczy sie do portu
				// jesli jakis klient sie polaczy, metoda zwroci obiekt klasy SOcket repzentujacy utworzone 
				// polaczenie
				//klient = 
				//klienci.add(klient);
				klient = new Klient(gniazdoKlienta, polaczenieZBaza,klienci);
				klienci.add(klient);
				//System.out.println("tik");
				klient.start();
				//czysciciel.czysc();
				
				System.out.println("mamy polaczenie");	


			}
			
		}
		catch(Exception ex)
		{ex.printStackTrace();}

		
	}
	
	
}
