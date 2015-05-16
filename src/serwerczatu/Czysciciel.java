package serwerczatu;


public class Czysciciel implements Runnable
{
	private SerwerCzatu serwer;

	
	public Czysciciel(SerwerCzatu serwer)
	{
		this.serwer = serwer; // odwołanie do obiektu serwera;
	}


	public void run() 
	{

			for (int i =serwer.klienci.size()-1;i>=0;i--)
			{
				if (!serwer.klienci.get(i).getGniazdo().isClosed())
				{
					serwer.klienci.get(i).stop(); // przestarzala metoda, ale nie mam pomyslu jak inaczej to zrealizowac :(
					serwer.klienci.remove(i);
					serwer.klienci.remove(i);
					
					
				}
				
			}

				
	}
	
	
}
