package es.ugr.utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
 

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
 
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.KeyPoint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.util.Base64;
import android.util.Log;
 
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
            List<NameValuePair> params) {
 
        // Making HTTP request
        try {
 
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
 
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
 
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }           
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
 
    }
    
    private static final String TAG = "Reconocimiento::JSONParser";

	public static String matToJson(Mat mat) {
		JsonObject obj = new JsonObject();

		if (mat.isContinuous()) {
			int cols = mat.cols();
			int rows = mat.rows();
			int elemSize = (int) mat.elemSize();

			float[] data = new float[cols * rows * elemSize];

			mat.get(0, 0, data);

			obj.addProperty("rows", mat.rows());
			obj.addProperty("cols", mat.cols());
			obj.addProperty("type", mat.type());

			// We cannot set binary data to a json object, so:
			// Encoding data byte array to Base64.
			// String dataString = new String(Base64.encode(data,
			// Base64.DEFAULT));
			// String dataString = new String(data);

			ByteBuffer buf = ByteBuffer.allocate(data.length * 4);

			for (int i = 0; i < data.length; i++)
				buf.putFloat(data[i]);

			String dataString = new String(Base64.encode(buf.array(),
					Base64.DEFAULT));

			obj.addProperty("data", dataString);

			Gson gson = new Gson();
			String json = gson.toJson(obj);

			return json;
		} else {
			Log.e(TAG, "Mat not continuous.");
		}
		return "{}";
	}

	public static Mat matFromJson(String json) {
		try {
			JsonParser parser = new JsonParser();
			JsonObject JsonObject = parser.parse(json).getAsJsonObject();

			int rows = JsonObject.get("rows").getAsInt();
			int cols = JsonObject.get("cols").getAsInt();
			int type = JsonObject.get("type").getAsInt();

			String dataString = JsonObject.get("data").getAsString();
			byte[] data = Base64.decode(dataString.getBytes(), Base64.DEFAULT);

			final FloatBuffer fb = ByteBuffer.wrap(data).asFloatBuffer();

			final float[] dst = new float[fb.capacity()];

			fb.get(dst);

			Mat mat = new Mat(rows, cols, type);
			mat.put(0, 0, dst);

			return mat;

		} catch (Exception e) {
			return new Mat();
		}
	}

	public static String keypointsToJson(MatOfKeyPoint mat) {
		if (mat != null && !mat.empty()) {
			Gson gson = new Gson();

			JsonArray jsonArr = new JsonArray();

			KeyPoint[] array = mat.toArray();
			for (int i = 0; i < array.length; i++) {
				KeyPoint kp = array[i];

				JsonObject obj = new JsonObject();

				// obj.addProperty("class_id", kp.class_id);
				obj.addProperty("x", kp.pt.x);
				obj.addProperty("y", kp.pt.y);
				obj.addProperty("size", kp.size);
				// obj.addProperty("angle", kp.angle);
				// obj.addProperty("octave", kp.octave);
				// obj.addProperty("response", kp.response);

				jsonArr.add(obj);
			}

			String json = gson.toJson(jsonArr);

			return json;
		}
		return "{}";
	}

	public static MatOfKeyPoint keypointsFromJson(String json) {
		try {
			JsonParser parser = new JsonParser();
			JsonArray jsonArr = parser.parse(json).getAsJsonArray();

			int size = jsonArr.size();

			KeyPoint[] kpArray = new KeyPoint[size];

			for (int i = 0; i < size; i++) {
				KeyPoint kp = new KeyPoint();

				JsonObject obj = (JsonObject) jsonArr.get(i);

				Point point = new Point(obj.get("x").getAsDouble(), obj
						.get("y").getAsDouble());

				kp.pt = point;
				// kp.class_id = obj.get("class_id").getAsInt();
				kp.size = obj.get("size").getAsFloat();
				// kp.angle = obj.get("angle").getAsFloat();
				// kp.octave = obj.get("octave").getAsInt();
				// kp.response = obj.get("response").getAsFloat();

				kpArray[i] = kp;
			}
			MatOfKeyPoint result = new MatOfKeyPoint();
			result.fromArray(kpArray);
			return result;
		} catch (Exception e) {
			return new MatOfKeyPoint();
		}
	}

	public static String ArrayListToJson(ArrayList<String> objetos) {
		String result = "";

		if (!objetos.isEmpty()) {

			JsonArray jsonArr = new JsonArray();

			for (int i = 0; i < objetos.size(); i++) {
				JsonObject obj = new JsonObject();
				obj.addProperty("id", objetos.get(i));
				jsonArr.add(obj);
			}
			Gson gson = new Gson();
			result = gson.toJson(jsonArr);

			return result;
		} else {
			Log.e(TAG, "Mat not continuous.");
		}
		return "{}";

	}

	public static String ArrayListToJsonInt(ArrayList<Integer> objetos) {
		String result = "";

		if (!objetos.isEmpty()) {

			JsonArray jsonArr = new JsonArray();

			for (int i = 0; i < objetos.size(); i++) {
				JsonObject obj = new JsonObject();
				obj.addProperty("id", objetos.get(i));
				jsonArr.add(obj);
			}
			Gson gson = new Gson();
			result = gson.toJson(jsonArr);

			return result;
		} else {
			Log.e(TAG, "Mat not continuous.");
		}
		return "{}";

	}

	public static ArrayList<String> ArrayListFromJson(String idsObjeto) {
		ArrayList<String> result = new ArrayList<String>();

		if (!idsObjeto.isEmpty()) {
			try {
				JsonParser parser = new JsonParser();
				JsonArray jsonArr = parser.parse(idsObjeto).getAsJsonArray();

				for (int i = 0; i < jsonArr.size(); i++)
					result.add(((JsonObject) jsonArr.get(i)).get("id")
							.getAsString());
			} catch (Exception e) {

			}
		}

		return result;
	}

	public static ArrayList<Integer> ArrayListFromJsonInt(String idsObjeto) {
		ArrayList<Integer> result = new ArrayList<Integer>();

		if (!idsObjeto.isEmpty()) {
			try {
				JsonParser parser = new JsonParser();
				JsonArray jsonArr = parser.parse(idsObjeto).getAsJsonArray();

				for (int i = 0; i < jsonArr.size(); i++)
					result.add(((JsonObject) jsonArr.get(i)).get("id")
							.getAsInt());
			} catch (Exception e) {

			}
		}

		return result;
	}

}