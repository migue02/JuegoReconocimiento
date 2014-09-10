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

import es.ugr.juegoreconocimiento.R;
import android.content.Context;
import android.os.AsyncTask;

public class SubirFicheros extends AsyncTask<String, Void, String> {
	// ProgressDialog upDialog;

	private Context context;

	public SubirFicheros(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		String st = new String();
		InputStream inputStream;
		try {

			inputStream = new FileInputStream(new File(params[0]));
			byte[] data;
			try {
				data = IOUtils.toByteArray(inputStream);

				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(
						context.getString(R.string.servidor_remoto) + "up.php");

				InputStreamBody inputStreamBody = new InputStreamBody(
						new ByteArrayInputStream(data), params[1]);

				MultipartEntityBuilder builder = MultipartEntityBuilder
						.create();
				builder.addPart("file", inputStreamBody);
				builder.addTextBody("tipo",
						context.getString(R.string.servidor_remoto) + "/"
								+ params[2] + "/");
				httpPost.setEntity(builder.build());

				HttpResponse httpResponse = httpClient.execute(httpPost);

				// Handle response back from script.
				// if(httpResponse != null) {
				//
				// } else { // Error, no response.
				//
				// }
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return st;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}

}
