package serwerczatu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class test {

	public static Connection polaczenieZBaza;
	
	
	public static void main(String[] args) 
	{
		try 
		{
			Class.forName("org.postgresql.Driver");
			String url= "jdbc:postgresql://127.0.0.1:5432/postgres";
			String username = "postgres";
			String password = "kefir1000";
			try 
			{
				polaczenieZBaza = DriverManager.getConnection(url,username,password);
				System.out.println("Panie! Wyglada na to ze mamy polaczenie z baza!");
				SerwerCzatu serwer = new SerwerCzatu(polaczenieZBaza);
				serwer.serwerStart();
			}
			catch (SQLException e)
			{
				System.out.println("nie polaczono z baza");
			}
		} 
		catch (ClassNotFoundException e1)
		{
			System.out.println("problemy ze sterownikiem bazy");
		}


		
	}

}