import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

public class MessageViewer extends JFrame {
	@SuppressWarnings("unused")
	private static Run thread = null;
	private static final long serialVersionUID = 1L;
	private ScrollPane contentPane;
	static String message;
	static boolean intercepted;

	//static JTextArea textArea = new JTextArea();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageViewer frame = new MessageViewer(message,intercepted,thread=null);
					frame.setVisible(true);			
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static String newMessage="";
	/**
	 * Create the frame.
	 * @param message2 
	 */
	public MessageViewer(String message2,boolean intercepted,Run thread) {
		JTextArea textArea = new JTextArea();
		setTitle("ProKSy Traffic Viewer");
		setBounds(100, 100, 380, 240);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setToolTipText("Action");
		setJMenuBar(menuBar);
		
		JMenu mnaction = new JMenu("Action");
		menuBar.add(mnaction);
		
		JMenuItem mntmForward = new JMenuItem("Forward");
		mntmForward.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				 newMessage = textArea.getText();
				 thread.newMessage = newMessage;
				 setVisible(false);
				 thread.res();
			}
		});
		if (!intercepted)
			mntmForward.setEnabled(false);
		else{
			mntmForward.setEnabled(true);
		}
		mnaction.add(mntmForward);
		contentPane = new ScrollPane();
		textArea.setLineWrap(true);
		textArea.setBackground(ProKSy.mycolor);
		textArea.setText(message2.replace("\r\n", ""));
		if(!intercepted)textArea.setEditable(false);
		else {
			textArea.setEditable(true);
			textArea.setBackground(Color.WHITE);
		}
		contentPane.add(textArea, BorderLayout.NORTH);
		setContentPane(contentPane);
		this.setPreferredSize(textArea.getPreferredSize());
	}
}
