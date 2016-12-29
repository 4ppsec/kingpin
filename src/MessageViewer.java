import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.ScrollPane;
import javax.swing.JFrame;
import javax.swing.JTextArea;

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
		setBounds(100, 100, 380, 240);
		
		contentPane = new ScrollPane();
		textArea.setLineWrap(true);
		textArea.setBackground(ProKSy.mycolor);
		textArea.setText(message2);
		textArea.setEditable(false);
		
		contentPane.add(textArea, BorderLayout.NORTH);
		setContentPane(contentPane);
		this.setPreferredSize(textArea.getPreferredSize());
	}

}
