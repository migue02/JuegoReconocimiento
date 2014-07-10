package es.ugr.bdremota;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 























































import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 

























































import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.utilidades.JSONParser;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class AllEjerciciosActivity extends Activity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> EjerciciosListRem, EjerciciosListLoc;
    List<Ejercicio> listaRem,listaLoc;
    List<Objeto> listaLocObj;
 
    // url to get all products list
    private static String url_all_ejercicios = "http://192.168.1.103/bd_reconocimiento/get_all_ejercicios.php";
    private static String url_create_ejercicio = "http://192.168.1.103/bd_reconocimiento/create_ejercicio.php";
    private static String url_create_objeto = "http://192.168.1.103/bd_reconocimiento/create_objeto.php";
    private static String url_images="http://192.168.1.103/bd_reconocimiento/images/";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EJERCICIOS = "ejercicios";
    private static final String TAG_ID = "_id";
    private static final String TAG_NOMBRE = "nombre";
    private static final String TAG_OBJETOS = "objetos";
    private static final String TAG_DESCRIPCION = "descripcion";
    private static final String TAG_DURACION = "duracion";
    private static final String TAG_OBJETOSRECONOCER = "objetosReconocer";
    private ListView lvRem,lvLoc;
    private Button botonSub,botonDes;
    private ImageView imgdesc;
 
    // products JSONArray
    JSONArray products = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bd_servidor_all_ejercicios);
 
        botonSub=(Button)findViewById(R.id.buttonSub);
        botonSub.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			//new MiUpload().execute("/mnt/sdcard/girl.png", "girl.png");
				//for(int i=0;i<listaLocObj.size();i++)
					new CreateNewObjeto().execute(listaLocObj);
			}
		});
        
        imgdesc=(ImageView)findViewById(R.id.imageDesc);
        botonDes=(Button)findViewById(R.id.buttonDesc);
        
        botonDes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new DownloadTask(AllEjerciciosActivity.this).execute("http://192.168.1.103/bd_reconocimiento/starwars.jpg");
				
			//new DownloadTask(AllEjerciciosActivity.this).execute("http://moonmentum.com/blog/wp-content/uploads/2014/05/7wtvm7m.jpg");
				
			}
		});
        
        // Hashmap for ListView
        EjerciciosListRem = new ArrayList<HashMap<String, String>>();
        EjerciciosListLoc = new ArrayList<HashMap<String, String>>();
        
        // Loading products in Background Thread
        lvRem=(ListView)findViewById(R.id.listRem);
        lvLoc=(ListView)findViewById(R.id.listLoc);
        
        listaLoc=new ArrayList<Ejercicio>();
        listaRem=new ArrayList<Ejercicio>();
        listaLocObj=new ArrayList<Objeto>();
        
        if(isNetworkAvailable()==true){
        
        rellenaLocal();
        
        
        
        new LoadAllEjercicios().execute();
        
        lvLoc.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        			long arg3) {
        		// TODO Auto-generated method stub
        		new CreateNewEjercicio().execute(String.valueOf(arg2));
        	}
		});
 
        }
 
    }

 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllEjercicios extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllEjerciciosActivity.this);
            pDialog.setMessage("Cargando ejercicios, por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_ejercicios, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_EJERCICIOS);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                       // String id = c.getString(TAG_ID);
                        String nombre = c.getString(TAG_NOMBRE);
                       // String objetos = c.getString(TAG_OBJETOS);
                        String descripcion = c.getString(TAG_DESCRIPCION);
                        String duracion = c.getString(TAG_DURACION);
                      //  String objetosReconocer = c.getString(TAG_OBJETOSRECONOCER);
                        
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                     //   map.put(TAG_ID, id);
                        map.put(TAG_NOMBRE, nombre);
                    //    map.put(TAG_OBJETOS, objetos);
                        map.put(TAG_DESCRIPCION, descripcion);
                        map.put(TAG_DURACION, duracion+" Minuto(s)");
                    //    map.put(TAG_OBJETOSRECONOCER, objetosReconocer);
                        
                        // adding HashList to ArrayList
                        EjerciciosListRem.add(map);
                    }
                } 
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllEjerciciosActivity.this, EjerciciosListRem,
                            R.layout.list_item, new String[] {
                                    TAG_NOMBRE, TAG_DESCRIPCION, TAG_DURACION},
                            new int[] { R.id.nombre,R.id.descripcion,R.id.duracion});
                    // updating listview
                    lvRem.setAdapter(adapter);
                }
            });
 
        }
 
    }
    
    
    class CreateNewEjercicio extends AsyncTask<String, String, String> {
    	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllEjerciciosActivity.this);
            pDialog.setMessage("Creando Ejercicios...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
        	Ejercicio ej=listaLoc.get(Integer.valueOf(args[0]));
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombre", ej.getNombre()));
            //params.add(new BasicNameValuePair("objetos", ""));
            params.add(new BasicNameValuePair("objetos", es.ugr.utilidades.Utilidades.ArrayListToJson(ej.getObjetos())));
            //params.add(new BasicNameValuePair("descripcion", ""));
            params.add(new BasicNameValuePair("descripcion", ej.getDescripcion()));
            params.add(new BasicNameValuePair("duracion", String.valueOf(ej.getDuracion())));
            //params.add(new BasicNameValuePair("objetosReconocer",""));
            params.add(new BasicNameValuePair("objetosReconocer",es.ugr.utilidades.Utilidades.ArrayListToJson(ej.getObjetosReconocer())));
            
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jParser.makeHttpRequest(url_create_ejercicio,
                    "POST", params);
 
            // check log cat fro response
            Log.d("Create Response", json.toString());
 
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product 
                    // closing this screen
                	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Creado ejercicios.", Toast.LENGTH_LONG);
                	// toast.show();
                } else {
                    // failed to create product
                 //json.getString("message")
               	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Error al crear:", Toast.LENGTH_LONG);
               //	 toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
    
    
    class CreateNewObjeto extends AsyncTask<List<Objeto>, String, String> {
   	 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllEjerciciosActivity.this);
            pDialog.setMessage("Creando Objetos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Creating product
         * */
        
        @Override
        protected String doInBackground(List<Objeto>... params) {
        	// TODO Auto-generated method stub
            FileOutputStream out;
            String dirFich="";
            Bitmap foto;
        	for(int i=0;i<params[0].size();i++){
        //int i=6;
        	 List<NameValuePair> parametros = new ArrayList<NameValuePair>();
        	 String nombre=params[0].get(i).getNombre();
             parametros.add(new BasicNameValuePair("nombre", params[0].get(i).getNombre()));
             parametros.add(new BasicNameValuePair("keypoints", params[0].get(i).getKeypoints()));
             parametros.add(new BasicNameValuePair("descriptores", params[0].get(i).getDescriptores()));
             parametros.add(new BasicNameValuePair("cols", String.valueOf(params[0].get(i).getCols())));
             parametros.add(new BasicNameValuePair("rows",String.valueOf(params[0].get(i).getRows())));
      
             foto=params[0].get(i).getImagen();
             if(foto!=null){

	             try {
	            	 	dirFich="/mnt/sdcard/"+nombre+".png";
	                    out = new FileOutputStream(dirFich);
	                    foto.compress(Bitmap.CompressFormat.PNG, 90, out);
	                    out.close();
	             } catch (Exception e) {
	                 e.printStackTrace();
	             } finally {
	                    try{
	                        //out.close();
	                    } catch(Throwable ignore) {}
	             }
	             new MiUpload().execute("/mnt/sdcard/"+nombre+".png", nombre+".png");

	             parametros.add(new BasicNameValuePair("imagen",url_images+nombre+".png"));
	        }else
	        	parametros.add(new BasicNameValuePair("imagen",""));
             
             
             // getting JSON Object
             // Note that create product url accepts POST method
             JSONObject json = jParser.makeHttpRequest(url_create_objeto,
                     "POST", parametros);
  
             // check log cat fro response
             Log.d("Create Response", json.toString());
  
             // check for success tag
             try {
                 int success = json.getInt(TAG_SUCCESS);
  
                 if (success == 1) {
                     // successfully created product 
                     // closing this screen
                 	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Creado ejercicios.", Toast.LENGTH_LONG);
                 	// toast.show();
                	 if(foto!=null){
                		// deleteFile(dirFich);
                		 dirFich="";
                	 }
                 } else {
                     // failed to create product
                  //json.getString("message")
                	// Toast toast = Toast.makeText(AllEjerciciosActivity.this, "Error al crear:", Toast.LENGTH_LONG);
                //	 toast.show();
                 }
             } catch (JSONException e) {
                 e.printStackTrace();
             }

        	}
        	
        	return null;
        }
        

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
 
    }
    
    
    private void rellenaLocal(){
    	
    	//Objetos
    	
    	ObjetoDataSource ods=new ObjetoDataSource(this);
    	ods.open();
    	listaLocObj=ods.getAllObjetos();
    	ods.close();
    	
    	//Ejercicios
    	
    	
    	 EjercicioDataSource eds=new EjercicioDataSource(this);
    	 eds.open();
    	 listaLoc=eds.getAllEjercicios();
    	 eds.close();
    	 for(int i=0;i<listaLoc.size();i++){
         // adding each child node to HashMap key => value
       //  map.put(TAG_ID, id);
         HashMap<String, String> mapLoc = new HashMap<String, String>();
         mapLoc.put(TAG_NOMBRE, listaLoc.get(i).getNombre());
       //  map.put(TAG_OBJETOS, objetos);
         mapLoc.put(TAG_DESCRIPCION, listaLoc.get(i).getDescripcion());
         mapLoc.put(TAG_DURACION, listaLoc.get(i).getDuracion()+" Minuto(s)");
        // map.put(TAG_OBJETOSRECONOCER, objetosReconocer);
         
         // adding HashList to ArrayList
         EjerciciosListLoc.add(mapLoc);
    	 }
    	 
    	 ListAdapter adapter = new SimpleAdapter(
                 AllEjerciciosActivity.this, EjerciciosListLoc,
                 R.layout.list_item, new String[] {
                         TAG_NOMBRE, TAG_DESCRIPCION, TAG_DURACION},
                 new int[] { R.id.nombre,R.id.descripcion,R.id.duracion});
         // updating listview
         lvLoc.setAdapter(adapter);
    	 
    	
    }
    

    
    
    /**
     * Upload the specified file to the PHP server.
     *
     * @param filePath The path towards the file.
     * @param fileName The name of the file that will be saved on the server.
     */
    
    public class MiUpload extends AsyncTask<String,Void,String>{
    	//ProgressDialog upDialog;
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
        //    upDialog = new ProgressDialog(AllEjerciciosActivity.this);
          //  upDialog.setMessage("Subiendo imagen...");
           // upDialog.setIndeterminate(false);
           // upDialog.setCancelable(true);
          //  upDialog.show();
    	}

    	@Override
    	protected String doInBackground(String... params) {
    		// TODO Auto-generated method stub
    		String st=new String();
    		InputStream inputStream;
    	      try {
    	    
    	        inputStream = new FileInputStream(new File(params[0]));
    	        byte[] data;
    	        try {
    	          data = IOUtils.toByteArray(inputStream);
    	     
    	          HttpClient httpClient = new DefaultHttpClient();
    	          HttpPost httpPost = new HttpPost("http://192.168.1.103/bd_reconocimiento/up.php");
    	     
    	          InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(data), params[1]);
    	          MultipartEntity multipartEntity = new MultipartEntity();
    	          multipartEntity.addPart("file", inputStreamBody);
    	          httpPost.setEntity(multipartEntity);
    	     
    	          HttpResponse httpResponse = httpClient.execute(httpPost);
    	     
    	          // Handle response back from script.
    	          if(httpResponse != null) {
    	     
    	          } else { // Error, no response.
    	     
    	          }
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
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		// upDialog.dismiss();
    	}
    	
    	}
    	


    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
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
                //output = new FileOutputStream("/mnt/sdcard/chica.png");
                output = new FileOutputStream("/mnt/sdcard/sw.jpg");

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
            return null;
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user 
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                 getClass().getName());
            mWakeLock.acquire();
            pDialog=new ProgressDialog(context);
            pDialog.setMessage("Descargando fichero...");
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            pDialog.dismiss();
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else{
                Toast.makeText(context,"Fichero Descargado", Toast.LENGTH_SHORT).show();
                File imageFile = new File("/mnt/sdcard/sw.jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                imgdesc.setImageBitmap(bitmap);
            }
        }
        
    }
    
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    
}