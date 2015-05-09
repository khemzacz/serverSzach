package serwerczatu;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerwerCzatu 
{
	ArrayList <Klient> klienci= new ArrayList<Klient>();
	ArrayList <Thread> watkiKlientow = new ArrayList<Thread>();
	Klient klient;
	Thread watek;
	//ObslugaKlientow obslugaKlientow = new ObslugaKlientow();
	Connection polaczenieZBaza;
	
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
	public void serwerStart()
	{
		try {
			ServerSocket serwerSock = new ServerSocket(5000); // tworzy serwer monitorujacy port 5000
			while(true)
			{
				Socket gniazdoKlienta = serwerSock.accept(); // program oczekuje az klient przylaczy sie do portu
				// jesli jakis klient sie polaczy, metoda zwroci obiekt klasy SOcket repzentujacy utworzone 
				// polaczenie
				klient = new Klient(gniazdoKlienta, polaczenieZBaza);
				klienci.add(klient);
				watek = new Thread(klient);
				watkiKlientow.add(watek);
				watek.start();

				System.out.println("mamy polaczenie");	
			}
		}
		catch(Exception ex)
		{ex.printStackTrace();}
		
	}
	
	
}
