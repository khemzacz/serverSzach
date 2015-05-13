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
		while (true)
		{
			for (int i =serwer.klienci.size()-1;i>=0;i--)
			{
				if (!serwer.klienci.get(i).getGniazdo().isClosed())
				{
					serwer.watkiKlientow.get(i).stop(); // przestarzala metoda, ale nie mam pomyslu jak inaczej to zrealizowac :(
					serwer.watkiKlientow.remove(i);
					serwer.klienci.remove(i);
					
					
				}
				
			}
			
			
			/*try {
				this.wait(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Blad metody Wait w czyscicielu");
				e.printStackTrace();
			}*/
		}		
	}
	
	
}
