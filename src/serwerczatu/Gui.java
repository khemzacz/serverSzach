package serwerczatu;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui extends JFrame implements Runnable{
	
	public static Connection polaczenieZBaza;
	private Thread mainServerThread;
	
	public Gui()
	{
		super("serwer");
	}

	
	public void serwerStart(JTextArea messageBox)
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
				//System.out.println("Wyglada na to ze mamy polaczenie z baza!");
				messageBox.append("podlaczono do bazy");
				SerwerCzatu serwer = new SerwerCzatu(polaczenieZBaza);
				mainServerThread = new Thread(serwer);
				mainServerThread.start();
			}
			catch (SQLException e)
			{
				messageBox.append("nie polaczono z baza");
				//System.out.println("nie polaczono z baza");
				
			}
		} 
		catch (ClassNotFoundException e1)
		{
			messageBox.append("problemy ze sterownikiem bazy");
		}		
	}


	public void run() {
		setSize(500,400);
		JPanel p = new JPanel();
		JTextArea messageBox = new JTextArea();
		p.setLayout(null);
		setLocationRelativeTo(null);
		JButton start = new JButton("Server Start");
		start.setMargin(new Insets(0,0,0,0));
		start.addActionListener ( new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				serwerStart(messageBox);
			}});
		start.setBounds(200,10,100,20);
		messageBox.setBounds(10,40,470,370);
		messageBox.setEditable(false);
		messageBox.setLineWrap(true);
		messageBox.setWrapStyleWord(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		p.add(messageBox);
		p.add(start);
		setVisible(true);
		
		add(p);
		
		
	}
}
