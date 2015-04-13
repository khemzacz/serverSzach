package serwerczatu;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerwerCzatu 
{
	ArrayList strumienieWyjsciowe;
	public class ObslugaKlientow implements Runnable{
		BufferedReader czytelnik;
		Socket gniazdo;
		
		public ObslugaKlientow(Socket clientSocket)
		{
			try 
			{
				gniazdo = clientSocket;
				InputStreamReader isReader = new InputStreamReader(gniazdo.getInputStream());
				czytelnik = new BufferedReader(isReader);
			}
			catch(Exception ex)
			{ex.printStackTrace();}
			
		}
		
		public void run(){
			String wiadomosc;
			System.out.println("Jestem w runie");
			try{
				while ((wiadomosc = czytelnik.readLine()) !=null)
				{
					System.out.println("Odczytano: "+wiadomosc);
					rozeslijDoWszystkich(wiadomosc);
					System.out.println("Jestem w petli");
				}
			}
			catch(Exception ex)
			{ex.printStackTrace();}
		}
	}
	public void rozeslijDoWszystkich(String message)
	{
		Iterator it = strumienieWyjsciowe.iterator(); 
		while(it.hasNext())
		{
			try{
				PrintWriter pisarz = (PrintWriter) it.next();
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
		strumienieWyjsciowe = new ArrayList();
		try {
			ServerSocket serwerSock = new ServerSocket(5000);
			while(true)
			{
				Socket gniazdoKlienta = serwerSock.accept();
				PrintWriter pisarz = new PrintWriter(gniazdoKlienta.getOutputStream());
				strumienieWyjsciowe.add(pisarz);
				
				Thread t = new Thread (new ObslugaKlientow(gniazdoKlienta));
				t.start();
				System.out.println("mamy połączenie");	
			}
		}
		catch(Exception ex)
		{ex.printStackTrace();}
		
	}
	
	
}
