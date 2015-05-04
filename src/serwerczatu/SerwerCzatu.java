package serwerczatu;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerwerCzatu 
{
	ArrayList <Klient> klienci= new ArrayList();
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
				klienci.add(new Klient(gniazdoKlienta, polaczenieZBaza));
				//PrintWriter pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
				// do pisarza przypisuje strumien wyjsciowy z gniazda stworzonego wyzej
				
				
				//Thread t = new Thread (new ObslugaKlientow(gniazdoKlienta)); // nowy watek na podstawie
				// obiektu klasy wewnetrznej gniazdo klienta
				//t.start(); // odpalony watek
				System.out.println("mamy polaczenie");	
			}
		}
		catch(Exception ex)
		{ex.printStackTrace();}
		
	}
	
	
}
