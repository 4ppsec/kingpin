import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import javax.net.ServerSocketFactory;
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
	static SSLServerSocket ServerSocketSSL = null;
	static Socket connectionSocket = null;
	static ServerSocket ServerSocket = null;
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
			
			SSLContext context;
			SSLServerSocketFactory factory=null;
			//ServerSocketFactory noSSLFac;
		
			try {
				if(ProKSy.SSL){
				context = SSLContext.getInstance("TLS");
				KeyManagerFactory keyManagerFactory;
				keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
				KeyStore keyStore = KeyStore.getInstance("JKS");
				keyStore.load(new FileInputStream(ProKSy.ks),ProKSy.pw.toCharArray());
			    keyManagerFactory.init(keyStore, ProKSy.pw.toCharArray());
			    context.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);
			    factory = context.getServerSocketFactory();
				}
			    int port = Integer.parseInt(ProKSy.lp);
			    if(ProKSy.SSL)
			    ServerSocketSSL = (SSLServerSocket)factory.createServerSocket(port);
			    else
			    	ServerSocket = (ServerSocket)ServerSocketFactory.getDefault().createServerSocket(port);
			  
			}catch (KeyStoreException e1) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e1, current_time_str});
		    	return false;
			} catch (NoSuchAlgorithmException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			} catch (CertificateException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			} catch (FileNotFoundException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			} catch (IOException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			} catch (UnrecoverableKeyException e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			} catch (KeyManagementException e) {
				
				String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	model.addRow(new Object[]{"✘", e, current_time_str});
		    	return false;
			}
			isInit=true;
			return true;
	 } catch (Exception e) {
		String current_time_str = time_formatter.format(System.currentTimeMillis());
	    model.addRow(new Object[]{"✘", e, current_time_str});
	    return false;
	 }
		
	}
	//noSSL
	//ssl
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
	
	
	//ssl
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
	
	@Override
	public void run() {
		SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DefaultTableModel model = (DefaultTableModel) ProKSy.tblLog.getModel();
		try {
			while (!Thread.interrupted()) {
				if(ProKSy.SSL)
		    	sslconnectionSocket = (SSLSocket) ServerSocketSSL.accept();
				else
					connectionSocket=(Socket)ServerSocket.accept();
		    	String clientSentence="";
		    	try{
		    		if(ProKSy.SSL)
			     clientSentence = readAll(sslconnectionSocket);
		    		else
		    	 clientSentence = readAll(connectionSocket);
		    	}
		    	catch(Exception ea){
		    		String current_time_str = time_formatter.format(System.currentTimeMillis());
		    	    model.addRow(new Object[]{"✘", ea, current_time_str});
		    		if(ProKSy.SSL){
		    	    sslconnectionSocket.close();
		    		sslconnectionSocket=null;
		    		ServerSocketSSL.close();
		    		ServerSocketSSL=null;
		    		}
		    		else{
		    			connectionSocket.close();
			    		connectionSocket=null;
			    		ServerSocket.close();
			    		ServerSocket=null;
		    		}
		    		return;
		    	}
		    	if(isInterupt){
		    		if(ProKSy.SSL){
			    	    sslconnectionSocket.close();
			    		sslconnectionSocket=null;
			    		ServerSocketSSL.close();
			    		ServerSocketSSL=null;
			    		}
			    		else{
			    			connectionSocket.close();
				    		connectionSocket=null;
				    		ServerSocket.close();
				    		ServerSocket=null;
			    		}
		    		String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"■", "ProKSy stopped", current_time_str});
		    		return;
		    	}
			    if (ProKSy.cr != null && ProKSy.cf.compareTo("") != 0 && ProKSy.cf != null && ProKSy.chkreq && clientSentence.toString().contains(ProKSy.cf)) {
			    	clientSentence = clientSentence.replace(ProKSy.cf,ProKSy.cr);
			    	String current_time_str = time_formatter.format(System.currentTimeMillis());
			    	model.addRow(new Object[]{"✔", "Request replacement:  [" + ProKSy.cf + "] → [" + ProKSy.cr + "]", current_time_str});
			    }
			    
				String FromServer = ProKSy.SendMsgServerString(clientSentence, ProKSy.rh,Integer.parseInt(ProKSy.rp));
				
				if (ProKSy.sr != null && ProKSy.sf.compareTo("") != 0 && ProKSy.sf != null && ProKSy.chkres && FromServer.toString().contains(ProKSy.sf)) {
					FromServer = FromServer.replace(ProKSy.sf,ProKSy.sr);
					String current_time_str = time_formatter.format(System.currentTimeMillis());
					model.addRow(new Object[]{"✔", "Response replacement:  [" + ProKSy.sf + "] → [" + ProKSy.sr + "]", current_time_str});
				}
				
			    PrintWriter output=null;
			    if(ProKSy.SSL)
			    output = new PrintWriter(new OutputStreamWriter(sslconnectionSocket.getOutputStream()));
			    else
			        output = new PrintWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
				
				    try {
			      	  //send msg to client
			      	  output.print(FromServer + '\n');
			      	  output.flush(); 
			      	  if(ProKSy.SSL)
			      	  sslconnectionSocket.close();
			      	  else
			      		connectionSocket.close();
			        } 
			        catch (Exception e) {
			        	String current_time_str = time_formatter.format(System.currentTimeMillis());
				    	model.addRow(new Object[]{"✘", e, current_time_str});
			        }
		    }
			
		 } catch (Exception e) {
				String current_time_str = time_formatter.format(System.currentTimeMillis());
 			    model.addRow(new Object[]{"✘", e, current_time_str});
		 }
	}
}
