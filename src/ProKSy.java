import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class ProKSy {
	
	private static final String BIG_LOGO = "appsec_logo.png";
	private static final String PROKSY_LOGO = "proksy_logo.png";
	private static final String SMALL_LOGO = "proksy_ico.png";
	private static final String START_ICON = "proksy_start_16.png";
	private static final String STOP_ICON = "proksy_stop_16.png";
	private static final String SAVE_ICON = "proksy_save_16.png";
	private static final String LOAD_ICON = "proksy_load_16.png";
	private static final String EXIT_ICON = "proksy_exit_16.png";
	private static final String ABOUT_ICON = "proksy_about_16.png";
	private static final String KS_ICON = "proksy_keystore_16.png";
	private static final String CLEAR_ICON = "proksy_clear_16.png";
	public static ProKSy window;
	public static Color mycolor = null;
	protected static boolean canStart = false;
	public static Run thread = null;
	public static JFrame frame;
	
	static JTable tblLog;
	static JTable tblTraffic;
	
	// list of ProKSy parameters
	public static String lp = "";
	public static String lh = "";
	public static String rp = "";
	public static String rh = "";
	public static String ks = "";
	public static String pw = "";
	public static String cf = "";
	public static String cr = "";
	public static String sf = "";
	public static String sr = "";
	public static boolean chkreq = false;
	public static boolean chkres = false;
	public static boolean SSL = true;
	
	// Build UI
	public static boolean init() {
		// icon for ABOUT
		URL img = ProKSy.class.getResource(BIG_LOGO);
        final ImageIcon icon = new ImageIcon(img);
        URL logo = ProKSy.class.getResource(PROKSY_LOGO);
        final ImageIcon logoicon = new ImageIcon(logo);
		DefaultTableModel model = new DefaultTableModel();
		DefaultTableModel modeltr = new DefaultTableModel();
		tblTraffic = new JTable(modeltr);
		tblLog = new JTable(model);
		final JScrollPane scrollPaneTraffic = new JScrollPane(tblTraffic, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
		final JScrollPane scrollPaneLog = new JScrollPane(tblLog, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // main frame
        frame = new JFrame("ProKSy by AppSec Labs");
        ProKSy.mycolor = frame.getBackground();
        final Border borderDefault =  UIManager.getBorder("TextField.border");
		
		final JTextField txtReqFind = new JTextField();
		final JTextField txtReqRep = new JTextField();
		final JTextField txtResFind = new JTextField();
		final JTextField txtResRep= new JTextField();
		
		final JTabbedPane tabMain = new JTabbedPane(JTabbedPane.TOP);

		// configuration tab
		JPanel panConf = new JPanel();
		panConf.setLayout(null);
		
		// match & replace tab
		JPanel panMR = new JPanel();
		panMR.setLayout(null);
		
		// log tab
		JPanel panLog = new JPanel();
		panLog.setLayout(null);
		
		tabMain.addTab("Configuration", null, panConf, null);
		tabMain.addTab("Match & Replace", null, panMR, null);
		tabMain.addTab("Traffic", null, scrollPaneTraffic, null);
		tabMain.addTab("Log", null, scrollPaneLog, null);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ProKSy.class.getResource(SMALL_LOGO)));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane();
		Dimension d = new Dimension(475,275);
		frame.setPreferredSize(d);
		frame.setVisible(true);
		frame.setMinimumSize(d);
				
		// missing data border
		final Border border = BorderFactory.createLineBorder(Color.RED);
		
		// missing KeyStore password border (not mandatory)
		final Border borderWarning = BorderFactory.createLineBorder(Color.ORANGE);
		
		// default border
		frame.getContentPane().add(tabMain);
		tabMain.setBounds(10, 10, 440, 200);
		
		// traffic		
		modeltr.addColumn("");
		modeltr.addColumn("Message");
		modeltr.addColumn("From");
		modeltr.addColumn("To");
		modeltr.addColumn("Time");
		tblTraffic.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		tblTraffic.getColumnModel().getColumn(0).setPreferredWidth(15);
		tblTraffic.getColumnModel().getColumn(1).setPreferredWidth(151);
		tblTraffic.getColumnModel().getColumn(2).setPreferredWidth(73);
		tblTraffic.getColumnModel().getColumn(3).setPreferredWidth(73);
		tblTraffic.getColumnModel().getColumn(4).setPreferredWidth(120);
		tblTraffic.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		    	int selectedrow = tblTraffic.getSelectedRow();
	       		if (selectedrow>=0 && me.getClickCount() == 2) {
	       			frame.setEnabled(false);
	       			MessageViewer mv = new MessageViewer(tblTraffic.getValueAt(selectedrow, 1).toString());
	       			mv.setVisible(true);
	       			mv.addWindowListener(new WindowAdapter() {
					    @Override
					    public void windowClosing(WindowEvent windowEvent) {
					    	mv.dispose();
					    	ProKSy.frame.setEnabled(true);
					    	ProKSy.frame.setVisible(true);
					    }
					});
		        }
		    }
		});
		 
		//log
		model.addColumn("");
		model.addColumn("Message");
		model.addColumn("Time");
		tblLog.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		tblLog.getColumnModel().getColumn(0).setPreferredWidth(25);
		tblLog.getColumnModel().getColumn(1).setPreferredWidth(295);
		tblLog.getColumnModel().getColumn(2).setPreferredWidth(120);
        
		frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
            	Rectangle rect = frame.getContentPane().getBounds();
                tabMain.setBounds(Math.round(rect.x*0.97f),Math.round(rect.y*0.4f),Math.round(rect.width),Math.round(rect.height));
                tblTraffic.getColumnModel().getColumn(0).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.035f));
                tblTraffic.getColumnModel().getColumn(1).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.355f));
                tblTraffic.getColumnModel().getColumn(2).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.16f));
                tblTraffic.getColumnModel().getColumn(3).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.16f));
                tblTraffic.getColumnModel().getColumn(4).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.27f));
                
                tblLog.getColumnModel().getColumn(0).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.04f));
                tblLog.getColumnModel().getColumn(1).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.67f));
                tblLog.getColumnModel().getColumn(2).setPreferredWidth((int) Math.round(frame.getContentPane().getSize().getWidth()*0.27f));
            }
        });
		tblLog.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		    	int selectedrow = tblLog.getSelectedRow();
	       		if (selectedrow>=0 && me.getClickCount() == 2) {
	       			frame.setEnabled(false);
	       			MessageViewer mv = new MessageViewer(tblLog.getValueAt(selectedrow, 1).toString());
	       			mv.setVisible(true);
	       			mv.addWindowListener(new WindowAdapter() {
					    @Override
					    public void windowClosing(WindowEvent windowEvent) {
					    	mv.dispose();
					    	ProKSy.frame.setEnabled(true);
					    }
					});
		        }
		    }
		});
        
		// Match & Replace tab info
		txtReqFind.setEditable(false);
		panMR.add(txtReqFind);
		Checkbox justForTheFont = new Checkbox("AppSec Labs");
		Font font = justForTheFont.getFont();
		
		JLabel mrHelp = new JLabel("<html>You can match & replace traffic while ProKSy is up, at any time.<br>"
				+ "Simply modify the text feilds below with the data you want to find/replace.</html>");
		mrHelp.setFont(font);
		mrHelp.setBounds(12,5,425,50);
		panMR.add(mrHelp);
		
		txtReqFind.setBounds(137, 73, 130, 20);
		txtReqFind.setColumns(10);
		txtReqFind.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtReqFind.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtReqFind.getText().length() < 1)
					txtReqFind.setBorder(border);
			}
		});
		
		txtReqFind.getDocument().addDocumentListener(new DocumentListener() {
			  public void insertUpdate(DocumentEvent e) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cf = txtReqFind.getText();
				  else
					  cf = "";
			  }
			  @Override
			  public void changedUpdate(DocumentEvent arg0) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cf = txtReqFind.getText();
				  else
					  cf = "";
			  }
			  @Override
			  public void removeUpdate(DocumentEvent arg0) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cf = txtReqFind.getText();
				  else
					  cf = "";
			  }
		});
	
		JLabel lblReqRep = new JLabel("with:");
		lblReqRep.setFont(font);
		lblReqRep.setBounds(275, 74, 75, 16);
		panMR.add(lblReqRep);
		
		txtReqRep.setBounds(310, 73, 130, 20);
		txtReqRep.setColumns(10);
		txtReqRep.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtReqRep.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtReqRep.getText().length() < 1)
					txtReqRep.setBorder(borderWarning);
			}
		});
		
		txtReqRep.getDocument().addDocumentListener(new DocumentListener() {
			  public void insertUpdate(DocumentEvent e) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cr = txtReqRep.getText();
				  else
					  cr = "";
			  }
			  @Override
			  public void changedUpdate(DocumentEvent arg0) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cr = txtReqRep.getText();
				  else
					  cr = "";
			  }
			  @Override
			  public void removeUpdate(DocumentEvent arg0) {
				  if (txtReqFind.getText() != null && txtReqFind.getText().compareTo("") !=0)
					  cr = txtReqRep.getText();
				  else
					  cr = "";
			  }
		});
		txtReqRep.setEditable(false);
		panMR.add(txtReqRep);
		
		
		
		final Checkbox chkReq = new Checkbox("Request replace:");
		chkReq.setBounds(10, 75, 110, 16);
		chkReq.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {
	        	 if (e.getStateChange() == ItemEvent.SELECTED) {
	        		 txtReqFind.setEditable(true);
	        		 txtReqRep.setEditable(true);
	        		 chkreq = true;
	        	 }
	        	 else {
	        		 txtReqFind.setBorder(borderDefault);
	        		 txtReqFind.setEditable(false);
	        		 txtReqRep.setBorder(borderDefault);
	        		 txtReqRep.setEditable(false);
	        		 chkreq = false;
	        	 }
	         }
	     });
		panMR.add(chkReq);
		
		txtResFind.setBounds(137, 118, 130, 20);
		txtResFind.setColumns(10);
		txtResFind.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				txtResFind.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent e) {
				if (txtResFind.getText().length() < 1)
					txtResFind.setBorder(border);
			}
		});
		
		txtResFind.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
			  if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
				  sf = txtResFind.getText();
			  else
				  sf = "";
			}
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				  if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
					  sf = txtResFind.getText();
				  else
					  sf = "";
			  }
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				  if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
					  sf = txtResFind.getText();
				  else
					  sf = "";
			  }
		});
		
		txtResFind.setEditable(false);
		panMR.add(txtResFind);
		
		JLabel lblResRep = new JLabel("with:");
		lblResRep.setFont(font);
		lblResRep.setBounds(275, 118, 75, 16);
		panMR.add(lblResRep);
		
		txtResRep.setBounds(310, 118, 130, 20);
		txtResRep.setColumns(10);
		txtResRep.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				txtResRep.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent e) {
				if (txtResRep.getText().length() < 1)
					txtResRep.setBorder(borderWarning);
			}
		});
		
		txtResRep.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
					sr = txtResRep.getText();
				else
					sr = "";
			}
			@Override
			public void changedUpdate(DocumentEvent arg0){
				  if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
					  sr = txtResRep.getText();
				  else
					  sr = "";
			  }
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				  if (txtResFind.getText() != null && txtResFind.getText().compareTo("") !=0)
					  sr = txtResRep.getText();
				  else
					  sr = "";
			  }
		});
		
		txtResRep.setEditable(false);
		panMR.add(txtResRep);
		
		final Checkbox chkRes = new Checkbox("Response replace:");
		chkRes.setBounds(10, 120, 120, 16);
		chkRes.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {
	        	 if (e.getStateChange() == ItemEvent.SELECTED) {
	        		 txtResFind.setEditable(true);
	        		 txtResRep.setEditable(true);
	        		 chkres = true;
	        	 }
	        	 else {
	        		 txtResFind.setBorder(borderDefault);
	        		 txtResFind.setEditable(false);
	        		 txtResRep.setBorder(borderDefault);
	        		 txtResRep.setEditable(false);
	        		 chkres = false;
	        	 }
	         }
	     });
		panMR.add(chkRes);
		
		// configuration tab info
		JLabel lblLocalPort = new JLabel("Local Port:");
		lblLocalPort.setBounds(36, 22, 84, 16);
		panConf.add(lblLocalPort);
		
		final JTextField txtLocalPort = new JTextField();
		txtLocalPort.setBounds(115, 20, 60, 20);
		panConf.add(txtLocalPort);
		txtLocalPort.setColumns(10);
		txtLocalPort.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtLocalPort.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtLocalPort.getText().length() < 1)
					txtLocalPort.setBorder(border);
			}
		});

		JLabel lblLocalHost = new JLabel("Local Host:");
		lblLocalHost.setBounds(212, 20, 84, 20);
		panConf.add(lblLocalHost);
		
		final JTextField txtLocalHost = new JTextField();
		txtLocalHost.setBounds(288, 20, 110, 20);
		txtLocalHost.setText("127.0.0.1");
		panConf.add(txtLocalHost);
		txtLocalHost.setColumns(10);
		txtLocalHost.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtLocalHost.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtLocalHost.getText().length() < 1)
					txtLocalHost.setBorder(border);
			}
		});
		
		JLabel lblRemotePort = new JLabel("Remote Port:");
		lblRemotePort.setBounds(20, 49, 84, 16);
		panConf.add(lblRemotePort);
		
		final JTextField txtRemotePort = new JTextField();
		txtRemotePort.setBounds(115, 47, 60, 20);
		panConf.add(txtRemotePort);
		txtRemotePort.setColumns(10);
		txtRemotePort.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtRemotePort.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtRemotePort.getText().length() < 1)
					txtRemotePort.setBorder(border);
			}
		});
		
		JLabel lblRemoteHost = new JLabel("Remote Host:");
		lblRemoteHost.setBounds(198, 49, 84, 16);
		panConf.add(lblRemoteHost);

		final JTextField txtRemoteHost = new JTextField();
		txtRemoteHost.setBounds(288, 47, 110, 20);
		panConf.add(txtRemoteHost);
		txtRemoteHost.setColumns(10);
		txtRemoteHost.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtRemoteHost.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtRemoteHost.getText().length() < 1)
					txtRemoteHost.setBorder(border);
			}
		});
		
		JLabel lblKSPass = new JLabel("KS Password:");
		lblKSPass.setBounds(195, 86, 120, 16);
		panConf.add(lblKSPass);

		final JTextField txtKSPass = new JPasswordField();
		txtKSPass.setBounds(288, 84, 110, 20);
		panConf.add(txtKSPass);
		txtKSPass.setColumns(10);
		txtKSPass.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				txtKSPass.setBorder(borderDefault);
			}
			public void focusLost(FocusEvent arg0) {
				if (txtKSPass.getText().length() < 1)
					txtKSPass.setBorder(borderWarning);
			}
		});
		
		final JTextField txtKSPath = new JTextField("");
		Border borderEmpty = BorderFactory.createEmptyBorder();
		txtKSPath.setBorder(borderEmpty);
		txtKSPath.setEditable(false);
		txtKSPath.setForeground(Color.lightGray);
		txtKSPath.setBounds(15, 110, 375, 16);
		panConf.add(txtKSPath);
		JCheckBox chckbxSSL = new JCheckBox("SSL");		
		// Browse KeyStore button
		final JButton btnSelectKS = new JButton("Select KeyStore");
		btnSelectKS.setMargin(new Insets(0,0,0,0));
		btnSelectKS.setHorizontalAlignment(SwingConstants.LEFT);
		btnSelectKS.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	btnSelectKS.setBorder(borderDefault);
		    	final JFileChooser fc = new JFileChooser();
		    	fc.setCurrentDirectory(new File(System.getProperty(("user.home"))));
		        int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                try {
	                	String kspath = file.getCanonicalPath();
	                	txtKSPath.setText(kspath);
	                	
					} catch (IOException e1) {
						SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						DefaultTableModel model = (DefaultTableModel) tblLog.getModel();
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✇","Could not open file...", current_time_str});
				    	tabMain.setSelectedIndex(3);
					}
	            } else {
	            	//Open command cancelled by user
	            }
	            if (txtKSPath.getText().length() < 1)
					btnSelectKS.setBorder(border);
		    }
		});
		btnSelectKS.setBounds(75, 84, 100, 20);
		panConf.add(btnSelectKS);
		chckbxSSL.setSelected(true);
		ChangeListener changeListener = new ChangeListener() {
		      public void stateChanged(ChangeEvent changeEvent) {
		    	  if(chckbxSSL.isSelected()){
		    		  btnSelectKS.setEnabled(true);
		    		  txtKSPass.setEnabled(true);
		    		  
		    	  }
		    	  else{
		    		  btnSelectKS.setEnabled(false);
		    		  txtKSPass.setEnabled(false);
		    	  }
		    	  SSL=chckbxSSL.isSelected();
		      }
		    };
		chckbxSSL.addChangeListener(changeListener);   
		//chckbxSSL.setBounds(285, 76, 113, 25);
		chckbxSSL.setBounds(10, 84, 50, 20);
		panConf.add(chckbxSSL);
		JLabel proksy_logo = new JLabel("", logoicon, JLabel.CENTER);
		proksy_logo.setBounds(120, 115, 210, 85);
		panConf.add(proksy_logo);
		
				
		// top menu options
		final JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.RED);
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		JMenu conf = new JMenu("Settings");
		menuBar.add(conf);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenuItem mntmView = new JMenuItem("View in Separate Window");
		mntmView.addActionListener(new ActionListener() {
			 public void actionPerformed(java.awt.event.ActionEvent evt) {
			     
		       		int selectedrow = tblTraffic.getSelectedRow();
		       		if(selectedrow>=0) {
		       			frame.setEnabled(false);
		       			MessageViewer mv = new MessageViewer(tblTraffic.getValueAt(selectedrow, 1).toString());
		       			mv.setVisible(true);
		       			mv.addWindowListener(new WindowAdapter() {
						    @Override
						    public void windowClosing(WindowEvent windowEvent) {
						    	mv.dispose();
						    	ProKSy.frame.setEnabled(true);
						    }
						});
		       		}
			 }
		});
		mnView.add(mntmView);
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		
		// icons
		URL img_Start = ProKSy.class.getResource(START_ICON);
		final ImageIcon iconStart = new ImageIcon(img_Start);
        URL img_Stop = ProKSy.class.getResource(STOP_ICON);
        final ImageIcon iconStop = new ImageIcon(img_Stop);
        URL img_Save = ProKSy.class.getResource(SAVE_ICON);
        final ImageIcon iconSave = new ImageIcon(img_Save);
        URL img_Load = ProKSy.class.getResource(LOAD_ICON);
        final ImageIcon iconLoad = new ImageIcon(img_Load);
		URL img_Exit = ProKSy.class.getResource(EXIT_ICON);
        final ImageIcon iconExit = new ImageIcon(img_Exit);
        URL img_About = ProKSy.class.getResource(ABOUT_ICON);
        final ImageIcon iconAbout = new ImageIcon(img_About);
        URL img_KS = ProKSy.class.getResource(KS_ICON);
        final ImageIcon iconKS = new ImageIcon(img_KS);
        URL img_Clear = ProKSy.class.getResource(CLEAR_ICON);
        final ImageIcon iconClear = new ImageIcon(img_Clear);
        
        
        final JMenuItem menuStart = new JMenuItem("Start",iconStart);
        final JMenuItem menuStop = new JMenuItem("Stop",iconStop);
        final JMenuItem menuSave = new JMenuItem("Save",iconSave);
		final JMenuItem menuLoad = new JMenuItem("Load",iconLoad);
		final JMenuItem menuExit = new JMenuItem("Exit",iconExit);
		final JMenuItem menuAbout = new JMenuItem("About",iconAbout);
		final JMenuItem menuClear = new JMenuItem("Clear data",iconClear);
		final JMenuItem menuClearLog = new JMenuItem("Clear Log",iconClear);
		final JMenuItem menuClearTraffic = new JMenuItem("Clear Traffic",iconClear);
		final JMenuItem menuBrowseKS = new JMenuItem("Browse KeyStore",iconKS);
		
		menuStop.setEnabled(false);
		menu.add(menuStart);
		menu.add(menuStop);
		

		
		// file options
		menuStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (txtLocalPort.getText().length() < 1) {
					SSL=chckbxSSL.isSelected();
					txtLocalPort.setBorder(border);
					canStart = false;
				}
				else  {lp = txtLocalPort.getText(); canStart = true;}
				
				if (txtLocalHost.getText().length() < 2){
					txtLocalHost.setBorder(border);
					canStart = false;
				}
				else {	lh = txtLocalHost.getText(); }
				
				if (txtRemotePort.getText().length() < 1){
					txtRemotePort.setBorder(border);
					canStart = false;
				}
				else  {rp = txtRemotePort.getText(); }
				
				if (txtRemoteHost.getText().length() < 1){
					txtRemoteHost.setBorder(border);
					canStart = false;
				}
				else  {rh = txtRemoteHost.getText(); }
				
				if (txtKSPath.getText().length() < 1 ){
					btnSelectKS.setBorder(border);
					canStart = false;
				}
				else if(chckbxSSL.isSelected()) {ks = txtKSPath.getText(); }
				
				if (chckbxSSL.isSelected() && txtKSPass.getText().length() < 1 ){
					txtKSPass.setBorder(borderWarning);
				}
				else if(chckbxSSL.isSelected())  {pw = txtKSPass.getText(); }	
				
				SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				DefaultTableModel model = (DefaultTableModel) tblLog.getModel();
				if (canStart)
					try {
						doStart(menuBar, menuStart, menuStop, menuClear, menuBrowseKS, menuLoad, txtLocalHost, txtLocalPort, txtRemoteHost, txtRemotePort, txtKSPass, btnSelectKS, tabMain, chckbxSSL);
					} catch (UnrecoverableKeyException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (KeyManagementException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (NoSuchAlgorithmException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (KeyStoreException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (CertificateException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (IOException e1) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e1, current_time_str});
				    	tabMain.setSelectedIndex(3);
					}
			}
		});
		
		//stop!!
		menuStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
				thread.interrupt();
				thread.isInterupt=true;
				@SuppressWarnings("unused")
				Socket socket=null;
				if(SSL) {
					System.setProperty("javax.net.ssl.trustStore", ks);
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket sslsocket=null;
				
					try {
						sslsocket = (SSLSocket) sslsocketfactory.createSocket(lh, Integer.parseInt(lp));
						sslsocket.getOutputStream().write(-1);
						sslsocket.close();
					} catch (NumberFormatException e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (UnknownHostException e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (Exception e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					}
				}
				else{
					try{
						socket=SocketFactory.getDefault().createSocket(lh, Integer.parseInt(lp));
					} catch (NumberFormatException e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (UnknownHostException e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					} catch (Exception e2) {
						String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e2, current_time_str});
				    	tabMain.setSelectedIndex(3);
					}
				}
				menuStop.setEnabled(false);
				menuStart.setEnabled(true);
				menuClear.setEnabled(true);
				menuLoad.setEnabled(true);
				chckbxSSL.setEnabled(true);
				menuBrowseKS.setEnabled(true);
				txtKSPass.setEditable(true);
				btnSelectKS.setEnabled(true);
				txtLocalPort.setEnabled(true);
				txtLocalHost.setEnabled(true);
				txtRemotePort.setEnabled(true);
				txtRemoteHost.setEnabled(true);
				chkReq.setEnabled(true);
				chkRes.setEnabled(true);
				menuBar.setBackground(Color.RED);
			}
		});
				
		menuAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame,new About (
					    "ProKSy v0.7<br>© <a href=\"https://il.linkedin.com/in/gilad-ofir-44959919\">Gilad Ofir</a>"
					    + " and <a href=\"https://il.linkedin.com/in/talmelamed\">Tal Melamed</a>"
					    + "<br><a href=\"https://appsec-labs.com\">AppSec Labs</a>"),
					    "ProKSy",
					    JOptionPane.PLAIN_MESSAGE,
					    icon);
			}
		});
		helpMenu.add(menuAbout);	
		
		// load
		menuLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
				try (BufferedReader br = new BufferedReader(new FileReader("ProKSy.properties"))) {
				    String line;
				    while ((line = br.readLine()) != null) {
				    		if ( line.startsWith("LocalHost=") ) {
				    			txtLocalHost.setText(line.substring(10));
				    			txtLocalHost.setBorder(borderDefault);
				    		}
				    		else if ( line.startsWith("LocalPort=") ) {
				    			txtLocalPort.setText(line.substring(10));
				    			txtLocalPort.setBorder(borderDefault);
				    		}
				    		else if ( line.startsWith("RemoteHost=") ) {
				    			txtRemoteHost.setText(line.substring(11));
				    			txtRemoteHost.setBorder(borderDefault);
				    		}
				    		else if ( line.startsWith("RemotePort=") ) {
				    			txtRemotePort.setText(line.substring(11));
				    			txtRemotePort.setBorder(borderDefault);
				    		}
				    		else if ( line.startsWith("KSPath=") ) {
				    			txtKSPath.setText(line.substring(7));
				    			btnSelectKS.setBorder(borderDefault);
				    		}
				    		else { }
				    	}
				    String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✔ ", "Data loaded from properties file.", current_time_str});
			    	tabMain.setSelectedIndex(0);
				} catch (FileNotFoundException e1) {
					String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✘", e1, current_time_str});
			    	tabMain.setSelectedIndex(3);
				} catch (IOException e1) {
					String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✘", e1, current_time_str});
			    	tabMain.setSelectedIndex(3);
				}
			}
		});
		menu.add(menuLoad);

		
		// save
		menuSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
				try {
					PrintWriter writer = new PrintWriter(new File("ProKSy.properties").getPath());
					writer.println("LocalHost="+txtLocalHost.getText());
					writer.println("LocalPort="+txtLocalPort.getText());
					writer.println("RemoteHost="+txtRemoteHost.getText());
					writer.println("RemotePort="+txtRemotePort.getText());
					writer.println("KSPath="+txtKSPath.getText());
					String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✔", "Data saved.", current_time_str});
					writer.close();
				} catch (FileNotFoundException e1) {
					String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✘", e1, current_time_str});
			    	tabMain.setSelectedIndex(3);
				}
			}
		});
		menu.add(menuSave);
		menuExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(menuExit);		
		
		// settings options
		// clear log option
		conf.add(menuClearLog);
		menuClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) tblLog.getModel();
				model.setRowCount(0);
			}
		});
		
		// ClearTraffic
		conf.add(menuClearTraffic);
		menuClearTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) tblTraffic.getModel();
				model.setRowCount(0);
			}
		});
		
		// Clear all fields
		menuClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtLocalPort.setText("");
				txtLocalPort.setBorder(borderDefault);
				txtLocalHost.setText("127.0.0.1");
				txtLocalHost.setBorder(borderDefault);
				txtRemotePort.setText("");
				txtRemotePort.setBorder(borderDefault);
				txtRemoteHost.setText("");
				txtRemoteHost.setBorder(borderDefault);
				txtKSPath.setText("");
				btnSelectKS.setBorder(borderDefault);
				txtKSPass.setText("");
				txtKSPass.setBorder(borderDefault);
				txtReqFind.setText("");
				txtReqRep.setText("");
				txtResFind.setText("");
				txtResRep.setText("");
				chkReq.setState(false);
				chkRes.setState(false);
				txtReqFind.setBorder(borderDefault);
				txtReqFind.setEditable(false);
				txtReqRep.setBorder(borderDefault);
				txtReqRep.setEditable(false);
				txtResFind.setBorder(borderDefault);
				txtResFind.setEditable(false);
				txtResRep.setBorder(borderDefault);
				txtResRep.setEditable(false);
			}
		});
		conf.add(menuClear);
		
		// Browse KeySyore
		menuBrowseKS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionListener al = btnSelectKS.getActionListeners()[0];
				al.actionPerformed(null);
			}
		});
		conf.add(menuBrowseKS);
		
		frame.setJMenuBar(menuBar);
		frame.pack();
		return canStart;
	}

	// sending message to remote host
	static String SendMsgServerString(String msgSend, String Remote, int port) {
		try{
			SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			DefaultTableModel modeltr = (DefaultTableModel) ProKSy.tblTraffic.getModel();
			
			SSLSocket sslsocket=null;
			Socket socket=null;
			PrintWriter outToServer=null;
			String answer="";
			
			
			if(SSL){
				System.setProperty("javax.net.ssl.trustStore", ks);
				SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				sslsocket = (SSLSocket) sslsocketfactory.createSocket(Remote, port);
				outToServer = new PrintWriter(new OutputStreamWriter(sslsocket.getOutputStream()));
			}
			else{
				socket = (Socket)SocketFactory.getDefault().createSocket(Remote, port);
				outToServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			}
			
      outToServer.print(msgSend + '\n');
      String current_time_str = time_formatter.format(System.currentTimeMillis());
      if(SSL)
      modeltr.addRow(new Object[]{"↖", msgSend, sslsocket.getLocalSocketAddress().toString().replace("/",""), sslsocket.getRemoteSocketAddress().toString().replace("/",""), current_time_str});
      else    modeltr.addRow(new Object[]{"↖", msgSend, socket.getLocalSocketAddress().toString().replace("/",""), socket.getRemoteSocketAddress().toString().replace("/",""), current_time_str});
      
      outToServer.flush();
      if(SSL){
      answer = readAll(sslsocket);
      sslsocket.close();
      }
      else{
    	  answer = readAll(socket);
    	  socket.close();
      }
      
      
      current_time_str = time_formatter.format(System.currentTimeMillis());
      if(SSL){
      modeltr.addRow(new Object[]{"↘", answer, sslsocket.getRemoteSocketAddress().toString().replace("/",""), sslsocket.getLocalSocketAddress().toString().split("/")[1], current_time_str});
      }
      else{
    	  
    	  modeltr.addRow(new Object[]{"↘", answer, socket.getRemoteSocketAddress().toString().replace("/",""), socket.getLocalSocketAddress().toString().split("/")[1], current_time_str});
    	     
      }
      return answer;
		}
		catch (IOException e) {
			SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			DefaultTableModel model = (DefaultTableModel) tblLog.getModel();
			String current_time_str = time_formatter.format(System.currentTimeMillis());
	    	model.addRow(new Object[]{"✘", e, current_time_str});
		    return "";
		}
	}
	
	
	// SSL - read data from remote host
	public static String readAll(SSLSocket sslsocket) throws IOException {
		StringBuilder sb = new StringBuilder();
	    int count;
		try {
			BufferedInputStream  reader = new BufferedInputStream (sslsocket.getInputStream());
			count=reader.read();
			int ad = reader.available();
			sb.append((char)count);
			while(ad>0){
				count=reader.read();
				sb.append((char)count);
				ad = reader.available();
			}
		} 
	    catch(Exception e){
		   SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		   String current_time_str = time_formatter.format(System.currentTimeMillis());
		   model.addRow(new Object[]{"✘", e, current_time_str});
	    }
		return sb.toString();
	 }
	
	// TCP
	public static String readAll(Socket socket) throws IOException {
		StringBuilder sb = new StringBuilder();
	    int count;
		try {
			BufferedInputStream  reader = new BufferedInputStream (socket.getInputStream());
			count=reader.read();
			int ad = reader.available();
			sb.append((char)count);
			while(ad>0){
				count=reader.read();
				sb.append((char)count);
				ad = reader.available();
			}
		} 
	    catch(Exception e){
		   SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		   String current_time_str = time_formatter.format(System.currentTimeMillis());
		   model.addRow(new Object[]{"✘", e, current_time_str});
	    }
		return sb.toString();
	 }
	
	// start listening
	public static void doStart( final JMenuBar menuBar, final JMenuItem start, final JMenuItem stop, final JMenuItem menuClear, final JMenuItem menuBrowseKS, final JMenuItem menuLoad, 
								final JTextField txtLocalHost, final JTextField txtLocalPort, final JTextField txtRemoteHost, final JTextField txtRemotePort,
								final JTextField txtKSPass,	final JButton btnSelectKS, final JTabbedPane tabMain, final JCheckBox chckbxSSL) 
			throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		
		if (thread == null)	{
			thread = new Run();
		}
		if(!thread.isInit) {
			if (thread.initialize()) {
				menuClear.setEnabled(false);
				menuBrowseKS.setEnabled(false);
				menuLoad.setEnabled(false);
				txtLocalPort.setEnabled(false);
				txtLocalHost.setEnabled(false);
				txtRemotePort.setEnabled(false);
				chckbxSSL.setEnabled(false);
				txtRemoteHost.setEnabled(false);
				txtKSPass.setEditable(false);
				btnSelectKS.setEnabled(false);
				stop.setEnabled(true);
				start.setEnabled(false);
				menuBar.setBackground(Color.GREEN);
				String current_time_str = time_formatter.format(System.currentTimeMillis());
				model.addRow(new Object[]{"►", "Started. Listening on: " + ProKSy.lh+":"+ProKSy.lp + "→" + ProKSy.rh+":"+ProKSy.rp , current_time_str});
				thread.isInterupt = false;
				tabMain.setSelectedIndex(2);
				thread.start();
			}
			else{
				tabMain.setSelectedIndex(3);
			}
		}
		else if (thread.getState() == Thread.State.TERMINATED) {
			thread = new Run();
			if ( thread.initialize() ) {
				menuClear.setEnabled(false);
				menuBrowseKS.setEnabled(false);
				menuLoad.setEnabled(false);
				txtLocalPort.setEnabled(false);
				txtLocalHost.setEnabled(false);
				txtRemotePort.setEnabled(false);
				txtRemoteHost.setEnabled(false);
				txtKSPass.setEditable(false);
				btnSelectKS.setEnabled(false);
				stop.setEnabled(true);
				start.setEnabled(false);
				menuBar.setBackground(Color.GREEN);
				String current_time_str = time_formatter.format(System.currentTimeMillis());
				model.addRow(new Object[]{"►", "Started. Listening on: " + ProKSy.lh+":"+ProKSy.lp + "→" + ProKSy.rh+":"+ProKSy.rp , current_time_str});
				thread.isInterupt = false;
				tabMain.setSelectedIndex(2);
				thread.start();
			}
			else{
				tabMain.setSelectedIndex(3);
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		init();
	}
	
	@SuppressWarnings("unused")
	private class SwingAction extends AbstractAction {
		private static final long serialVersionUID = -2734100795685226202L;
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
