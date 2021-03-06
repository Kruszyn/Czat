import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Thread run, listen;
	private Client client;
	
	private boolean running = false;
	
	public ClientWindow(String name, String address, int port) {
		setTitle("Chat Client");
		client = new Client(name, address, port);
		boolean connect = client.openConnection(address);
		if(!connect){
			System.err.println("Connection failed");
			console("Connectrion failed");
		}
		createWindow();
		console("Nawiązuje połączenie z " + address + ": " + port + ", użytkownik: " + name );
		String connection = "/c/" + name;
		client.send(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();
	}
	
	private void createWindow(){
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{35,815,30,5};
		gbl_contentPane.rowHeights = new int[]{30,480,40};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setFont(new Font("Arial", Font.PLAIN, 12));
		history.setEditable(false);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 1;
		scrollConstraints.gridy = 1;
		scrollConstraints.gridwidth = 2;		
		scrollConstraints.insets = new Insets(10,0,0,0);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					send(txtMessage.getText());
				}
			}
		});
		txtMessage.setText("\r\n");
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 1;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);	
		txtMessage.requestFocus();
	}
	
	public void send(String message){
		if(message.equals(""))return;
		message = client.getName() + ": " + message; 
		message = "/m/" + message;
		client.send(message.getBytes());
		txtMessage.setText("");
	}
	
	public void console(String message){
		history.append(message + System.lineSeparator());
		history.setCaretPosition(history.getDocument().getLength());
	}

	public void run() {
		listen();
	}

	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")){
						String text = message.split("/m/|/e/")[1];
						console(text);
					}
		
				}
			}
		};
		listen.start();
	}
}
