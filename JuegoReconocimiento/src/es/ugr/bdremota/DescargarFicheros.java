package es.ugr.bdremota;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class DescargarFicheros extends AsyncTask<String, Integer, String> {

	public DescargarFicheros() {

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// take CPU lock to prevent CPU from going off if the user
		// presses the power button during download
		// PowerManager pm = (PowerManager)
		// context.getSystemService(Context.POWER_SERVICE);
		// mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		// getClass().getName());
		// mWakeLock.acquire();

	}

	@Override
	protected String doInBackground(String... sUrl) {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(sUrl[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "Server returned HTTP " + connection.getResponseCode()
						+ " " + connection.getResponseMessage();
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();
			// Comprobar si existe, sino crearlo;
			File fichsalida = new File(sUrl[1]);
			if (!fichsalida.exists()) {
				fichsalida.createNewFile();
			}
			output = new FileOutputStream(fichsalida);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				if (isCancelled()) {
					input.close();
					return null;
				}
				total += count;
				// publishing the progress....
				if (fileLength > 0) // only if total length is known
					publishProgress((int) (total * 100 / fileLength));
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			return e.toString();
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
		return sUrl[1];
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);

	}

	@Override
	protected void onPostExecute(String result) {
		// mWakeLock.release();
	}

}
