package es.ugr.bdremota;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chilkatsoft.CkSFtp;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import es.ugr.juegoreconocimiento.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class SubirFicherosSFTP extends AsyncTask<String,Void,String>{
	//ProgressDialog upDialog;
	
	private Context context;

	
	public SubirFicherosSFTP(Context context){
		this.context=context;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		 Session session = null;
		 Channel channel = null;
		    try {
		        JSch ssh = new JSch();
		      //  ssh.setKnownHosts("/path/of/known_hosts/file");
		     
		        session = ssh.getSession("diversidad", "tamen.ugr.es", 22);
		        session.setPassword("PFC_android_14");
		        java.util.Properties config = new java.util.Properties(); 
		        config.put("StrictHostKeyChecking", "no");
		        session.setConfig(config);
		        session.connect();
		        channel = session.openChannel("sftp");
		        channel.connect();
		        ChannelSftp sftp = (ChannelSftp) channel;
		        sftp.put(params[0], "public_html/"+params[2]+"/"+params[1]);
		    } catch (JSchException e) {
		        e.printStackTrace();
		    } catch (SftpException e) {
		        e.printStackTrace();
		    } finally {
		        if (channel != null) {
		            channel.disconnect();
		        }
		        if (session != null) {
		            session.disconnect();
		        }
		    }
		
		
    return "";
	    }


	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		// upDialog.dismiss();
	}
	
	}
	


