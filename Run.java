import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.table.DefaultTableModel;


public class Run extends Thread {
	
	volatile boolean stop = false;
	static SSLSocket sslconnectionSocket = null;
	static SSLServerSocket HacmeServerSocketSSL = null;
	public  Boolean isInit = false;
	public  Boolean isInterupt = false;
	public boolean initialize() {
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { 
			    new X509TrustManager() {     
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
			            return new X509Certificate[0];
			        } 
			        public void checkClientTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			            } 
			        public void checkServerTrusted( 
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
			
			//SSLServerSocket HacmeServerSocketSSL=null;
			SSLContext context;
			SSLServerSocketFactory factory;
			try {
				context = SSLContext.getInstance("TLS");
				KeyManagerFactory keyManagerFactory;
				keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(new FileInputStream(ProKSy.ks),ProKSy.pw.toCharArray());
			    keyManagerFactory.init(keyStore, ProKSy.pw.toCharArray());
			    context.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);
			    factory = context.getServerSocketFactory();
		
			    int port = Integer.parseInt(ProKSy.lp);
			    HacmeServerSocketSSL = (SSLServerSocket)factory.createServerSocket(port);
			   // SSLSocket sslconnectionSocket=null;
			  
			}catch (KeyStoreException e1) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e1, current_time_str});
		    	return false;
			} catch (NoSuchAlgorithmException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			} catch (CertificateException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			} catch (FileNotFoundException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			} catch (IOException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			} catch (UnrecoverableKeyException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			} catch (KeyManagementException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"?", e, current_time_str});
		    	return false;
			}
			isInit=true;
			return true;
	 } catch (Exception e) {
		String current_time_str = time_formatter.format(System.currentTimeMillis());
	    model.addRow(new Object[]{"?", e, current_time_str});
	    return false;
	 }
		
	}
	public static String readAll(SSLSocket socket) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		StringBuilder sb = new StringBuilder();
	   try{
		sb.append( reader.readLine());
	   }
	   catch(Exception e){
		   SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		   String current_time_str = time_formatter.format(System.currentTimeMillis());
		   model.addRow(new Object[]{"?", e, current_time_str});
	   }
		return sb.toString();
	 }
	@Override
	public void run() {
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		try {
			while (!Thread.interrupted()) {
		    	sslconnectionSocket = (SSLSocket) HacmeServerSocketSSL.accept();
		    	String clientSentence="";
		    	try{
			     clientSentence = readAll(sslconnectionSocket);
		    	}
		    	catch(Exception ea){
		    		String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	    model.addRow(new Object[]{"?", ea, current_time_str});
		    		sslconnectionSocket.close();
		    		sslconnectionSocket=null;
		    		HacmeServerSocketSSL.close();
		    		HacmeServerSocketSSL=null;
		    		 return;
		    	}
		    	if(isInterupt){
		    		sslconnectionSocket.close();
		    		sslconnectionSocket=null;
		    		HacmeServerSocketSSL.close();
		    		HacmeServerSocketSSL=null;
		    		String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"!", "ProKSy stopped", current_time_str});
		    		return;
		    	}
			    if (ProKSy.cr != null && ProKSy.cf.compareTo("") != 0 && ProKSy.cf != null && ProKSy.cr.compareTo("") != 0 && ProKSy.chkreq) {
			    	clientSentence = clientSentence.replace(ProKSy.cf,ProKSy.cr);
			    	String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"<-", "Request replacement:  [" + ProKSy.cf + "] --> [" + ProKSy.cr + "]", current_time_str});
			    }
			    else if (ProKSy.cf.compareTo("") != 0 && ProKSy.cf != null && (ProKSy.cr.compareTo("") == 0 || ProKSy.cr != null) && ProKSy.chkreq && clientSentence.toLowerCase().contains(ProKSy.cf.toLowerCase())) {
			    	String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"!", "Request match! [" + ProKSy.cf + "]", current_time_str});
			    }
			    
				String FromServer = ProKSy.SendMsgServerString(clientSentence,ProKSy.rh,Integer.parseInt(ProKSy.rp));
				
				if (ProKSy.sr != null && ProKSy.sf.compareTo("") != 0 && ProKSy.sf != null && ProKSy.sr.compareTo("") != 0 && ProKSy.chkres) {
					FromServer = FromServer.replace(ProKSy.sf,ProKSy.sr);
					String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"->", "Response replacement:  [" + ProKSy.sf + "] --> [" + ProKSy.sr + "]", current_time_str});
				}
				else if (ProKSy.sf != null && ProKSy.sf.compareTo("") != 0 && (ProKSy.sr == null || ProKSy.sr.compareTo("") == 0) && ProKSy.chkres && FromServer.toLowerCase().contains(ProKSy.sf.toLowerCase())) {
					String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"!", "Response match! [" + ProKSy.sf + "]", current_time_str});
				}
			    PrintWriter output=null;
			    output = new PrintWriter(new OutputStreamWriter(sslconnectionSocket.getOutputStream()));
				    try {
			      	  //send msg to client
			      	  output.print(FromServer + '\n');
			      	  output.flush();  
			        } 
			        catch (Exception e) {
			        	String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"?", e, current_time_str});
			            //System.out.println(e);
			        }
		    }
			
		 } catch (Exception e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
			    model.addRow(new Object[]{"?", e, current_time_str});
		 }
	}
}
