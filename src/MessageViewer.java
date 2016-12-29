import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.ScrollPane;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MessageViewer extends JFrame {

	private static final long serialVersionUID = 1L;
	private ScrollPane contentPane;
	static String message;
	//static JTextArea textArea = new JTextArea();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MessageViewer frame = new MessageViewer(message);
					frame.setVisible(true);	
							
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param message2 
	 */
	public MessageViewer(String message2) {
		JTextArea textArea = new JTextArea();
		setTitle("ProKSy Traffic Viewer");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 462, 433);
		
		contentPane = new ScrollPane();
		//contentPane.setLayout(new BorderLayout(0, 0));
		textArea.setLineWrap(true);
		textArea.setBackground(ProKSy.mycolor);
		textArea.setText(message2);
		textArea.setEditable(false);
		
		
		contentPane.add(textArea, BorderLayout.NORTH);
		setContentPane(contentPane);
		pack();
	}

}
