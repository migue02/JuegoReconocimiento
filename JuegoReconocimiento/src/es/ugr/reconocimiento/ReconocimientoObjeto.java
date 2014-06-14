package es.ugr.reconocimiento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.KeyPoint;

import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.juegoreconocimiento.R;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.Resultado;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.utilidades.*;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class ReconocimientoObjeto extends Activity implements CvCameraViewListener2 {
	
	private static final String TAG = "ReconocimientoObjeto::Activity";
	private CameraBridgeViewBase mOpenCvCameraView;
	private boolean buscandoObjeto = false;
	private int nObjeto = -1;
	
	private Mat mGray, mRgba, aux;
	private MatOfKeyPoint keypoints_obj = new MatOfKeyPoint();
	private Mat descriptores_obj = new Mat();
	private KeyPoint[] listaKP_obj;
	private ArrayList<Objeto> objetos;
	private ArrayList<Mat> matsDescriptores = new ArrayList<Mat>(); 
	private ArrayList<MatOfKeyPoint> matsKeyPoints = new ArrayList<MatOfKeyPoint>();
	private int[] colsArray;
	private int[] rowsArray;
	
	private ObjetoDataSource objetosDS;
	private EjercicioDataSource ejerciciosDS;
	
	private String nombre;
	private EditText edtNombre;
	private ImageView image;
	private TextToSpeech ttobj;
	
	private Boolean ciclico;
	private List<Ejercicio> ejercicios;
	private int ejercicioActual = 0;
	private int objetoActual = 0;
	private ArrayList<Integer> listaCoincidencias = new ArrayList<Integer>();
	private int numMatches = 10;
	
	private Alumno alumno;
	private SerieEjercicios serie;
	private Resultado resultadoActual = new Resultado();
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				// Load native library after(!) OpenCV initialization
				System.loadLibrary("opencv_java");
				System.loadLibrary("nonfree");
				System.loadLibrary("juegoReconocimientoLib");

				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_reconocimiento_objeto);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		Bundle extras;
		if (savedInstanceState == null) {
		    extras = getIntent().getExtras();
		    if(extras == null) {
		        alumno = new Alumno();
		        serie = new SerieEjercicios();
		        ciclico = false;
		    } else {
		        alumno= (Alumno) extras.getSerializable("Alumno");
		        serie= (SerieEjercicios) extras.getSerializable("Serie");
		        ciclico = extras.getBoolean("Ciclico");
		    }
		} else {
			alumno= (Alumno) savedInstanceState.getSerializable("Alumno");
	        serie= (SerieEjercicios) savedInstanceState.getSerializable("Serie");
	        ciclico = savedInstanceState.getBoolean("Ciclico");
		}
		
		
		objetosDS = new ObjetoDataSource(this);
		objetosDS.open();
		if (serie == null)
			objetos = objetosDS.getAllObjetos();
		else{
			ejerciciosDS = new EjercicioDataSource(this);
			ejerciciosDS.open();
			ejercicios = ejerciciosDS.getAllEjercicios(serie);
			objetos = objetosDS.getAllObjetos(ejercicios.get(ejercicioActual));

			resultadoActual.setFechaRealizacion(new GregorianCalendar().getTime());
			resultadoActual.setIdAlumno(alumno.getIdAlumno());
			resultadoActual.setIdEjercicio(serie.getIdSerie());
		}
		rellenar(false);
		
		ttobj=new TextToSpeech(getApplicationContext(), 
	      new TextToSpeech.OnInitListener() {
	      @Override
	      public void onInit(int status) {
	         if(status != TextToSpeech.ERROR){
	             ttobj.setLanguage(Locale.UK);
	            }				
	         }
	      });
		
	}
	
	private void rellenar(boolean aniadir){
		
		rowsArray = null;
		colsArray = null;
		matsDescriptores.clear();
		matsKeyPoints.clear();
		
		colsArray = new int[objetos.size()];
		rowsArray = new int[objetos.size()];
		
		for(int i=0;i<objetos.size(); i++){
			colsArray[i] = objetos.get(i).getCols();
			rowsArray[i] = objetos.get(i).getRows();
			
			Mat tempMat=Utilidades.matFromJson(objetos.get(i).getDescriptores());
            matsDescriptores.add(tempMat);
			
            MatOfKeyPoint tempMatKeyPoint=Utilidades.keypointsFromJson(objetos.get(i).getKeypoints());
    		matsKeyPoints.add(tempMatKeyPoint);
		}
		
        long[] tempAddrDesc = new long[matsDescriptores.size()]; 
        long[] tempAddrKeyP = new long[matsKeyPoints.size()]; 
        int[] tempCols = new int[colsArray.length];
        int[] tempRows = new int[rowsArray.length];
        
        for (int i=0;i<matsDescriptores.size();i++)
        {
            tempAddrDesc[i]= matsDescriptores.get(i).getNativeObjAddr();
            tempAddrKeyP[i]= matsKeyPoints.get(i).getNativeObjAddr();            
            tempCols[i] = objetos.get(i).getCols();
            tempRows[i] = objetos.get(i).getRows();
        }

        RellenarObjetos(tempAddrDesc, tempAddrKeyP, tempCols, tempRows);
        
        
        tempAddrDesc = null;
        tempAddrKeyP = null;
        tempCols = null;
        tempRows = null;
        
	}
	
	public void onReconocerClick(View v){
		buscandoObjeto=true;
		nObjeto=-1;
		if (objetoActual == 0)
			ttobj.speak("Ejercicio " + ejercicios.get(ejercicioActual).getNombre(), TextToSpeech.QUEUE_FLUSH, null);
		ttobj.speak("Busca el objeto " + objetos.get(objetoActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
		((Chronometer) findViewById(R.id.cronometro)).start();
	}
	
	public void onCancelarClick(View v){
		if (!buscandoObjeto){
			Intent myIntent = new Intent(ReconocimientoObjeto.this,
					MainActivity.class);
			finish();
			startActivity(myIntent);
		}else{
			buscandoObjeto=false;
			nObjeto=-1;
		}
	}
	
	public void onCronoEmpieza(View v){
		((Chronometer) findViewById(R.id.cronometro)).setBase(SystemClock.elapsedRealtime());
		((Chronometer) findViewById(R.id.cronometro)).start();
	}
	
	public void onCronoPara(View v){
		((Chronometer) findViewById(R.id.cronometro)).stop();
		long elapsedMillis = (SystemClock.elapsedRealtime() - ((Chronometer) findViewById(R.id.cronometro)).getBase())/1000;
		int horas = (int)elapsedMillis/3600;
		int remainder = (int) elapsedMillis - horas * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    double secs = remainder; 
	    double tiempo = (horas*60) + mins + (secs/10);
		Toast.makeText(ReconocimientoObjeto.this, "Tiempo: " + tiempo, 
	            Toast.LENGTH_SHORT).show();
	}
	
	public void onCapturarClick(View v){
		aux = mRgba.clone();
		FindFeatures(mGray.getNativeObjAddr(),
				aux.getNativeObjAddr(), descriptores_obj.getNativeObjAddr(), keypoints_obj.getNativeObjAddr());
		if (!keypoints_obj.empty() || !descriptores_obj.empty()) {
			final Dialog dialog = new Dialog(ReconocimientoObjeto.this);
			dialog.setContentView(R.layout.activity_dialog_objeto);
			dialog.setTitle("¿Desea guardar este objeto?");
	
			// set the custom dialog components - image and button
			// convert to bitmap:
			Bitmap bm = Bitmap.createBitmap(aux.cols(), aux.rows(),Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(aux, bm);
			image = (ImageView) dialog.findViewById(R.id.imageObjeto);
			image.setImageBitmap(bm);			
			
			edtNombre = (EditText) dialog.findViewById(R.id.edtNombre);
			Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
			// if button is clicked, close the custom dialog
			btnAceptar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String keyString, desString;				
					//keypoints_obj = new MatOfKeyPoint(listaKP_obj);
					keyString = Utilidades.keypointsToJson(keypoints_obj);
					desString = Utilidades.matToJson(descriptores_obj);
					Objeto obj=objetosDS.createObjeto(edtNombre.getText().toString(), keyString, desString, aux.cols(), aux.rows());
					objetos.add(obj);
					rellenar(true);
					//id = obj.getId();
					descriptores_obj.release();
					keypoints_obj.release();
					dialog.dismiss();
					aux.release();
				}
			});
			
			Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
			// if button is clicked, close the custom dialog
			btnCancelar.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					descriptores_obj.release();
					keypoints_obj.release();
					objetos.clear();
					aux.release();
				}
			});		
			dialog.show();
		}else
			Toast.makeText(this, "Es necesario capturar de nuevo el objeto", Toast.LENGTH_SHORT).show();
	}
	
	public class MyRunnable implements Runnable {
		public int nObjetoActual;
		public MyRunnable(int nObjetoActual){
			this.nObjetoActual=nObjetoActual;
		}
		@Override
		  public void run() {
			nObjeto=FindObjects(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(),nObjetoActual);
		  }
		} 
	
	public class HebraJuego implements Runnable {
		int coincidencias;
		public HebraJuego(){}
		@Override
		public void run() {
			coincidencias=ObtieneCoincidencias(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(),objetoActual);
			listaCoincidencias.add(coincidencias);
		}
	} 	
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (!buscandoObjeto || (buscandoObjeto && nObjeto == -1)){
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if (buscandoObjeto) {
				if (InicializaEscenario(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr())){
					ExecutorService executor = Executors.newFixedThreadPool(objetos.size());
			    	for (int i = 0; i < objetos.size() || nObjeto != -1; i++) {
		    	      Runnable findObjectThread = new MyRunnable(i);
		    	      executor.execute(findObjectThread);
		    	    }
			    	// This will make the executor accept no new threads
			        // and finish all existing threads in the queue
			        executor.shutdown();
			        // Wait until all threads are finish
			        while (!executor.isTerminated()){}
			        LiberaEscenario();
			        nObjeto = obtieneObjeto();
					if (nObjeto!=-1){
						resultadoActual.setAciertos(resultadoActual.getAciertos()+1);
						nombre=objetos.get(nObjeto).getNombre();
						ReconocimientoObjeto.this.runOnUiThread(new Runnable() {
						    public void run() {
						    	ttobj.speak("Encontrado el objeto "+nombre, TextToSpeech.QUEUE_FLUSH, null);
						    	Toast.makeText(getApplicationContext(), "Encontrado el objeto "+nombre, Toast.LENGTH_SHORT).show();
						    	actualizaEscenarioJuego();
						    }
						});
						nObjeto=-1;
					}
				}
			}
		}
		return mRgba;
	}
	
	private int obtieneObjeto(){
		int maximo,objeto=-1;
		if ((maximo = Collections.max(listaCoincidencias)) >= numMatches)
			for (int i=0; i<listaCoincidencias.size(); i++)
				if (listaCoincidencias.get(i) == maximo){
					objeto=i;
					if (objeto == objetoActual)
						return objeto;
				}
		return objeto;
	}
	
	private void actualizaEscenarioJuego(){
		boolean juegoTerminado=false;
		objetoActual = (objetoActual+1)%objetos.size();
		if (objetoActual == 0){
			ejercicioActual = (ejercicioActual+1)%ejercicios.size();
			if (ciclico || ejercicioActual != 0){
				LiberaObjetos();
				objetos.clear();
				objetos = objetosDS.getAllObjetos(ejercicios.get(ejercicioActual));
				rellenar(false);
			}else{
				LiberaObjetos();
				objetos.clear();
				juegoTerminado=true;
			}
		}
		if (!juegoTerminado){
			if (objetoActual==0) ttobj.speak("Ejercicio " + ejercicios.get(ejercicioActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
			ttobj.speak("Busca el objeto " + objetos.get(objetoActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
		}else{
			ttobj.speak("El juego ha terminado", TextToSpeech.QUEUE_ADD, null);
			((Chronometer) findViewById(R.id.cronometro)).stop();
		}
	}
	
	@Override
	public void onPause() {
		objetosDS.close();
		if(ttobj !=null){
			ttobj.stop();
			ttobj.shutdown();
		}
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
		objetosDS.open();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		colsArray = null;
		rowsArray = null;
		matsDescriptores = null;
		matsKeyPoints = null;
		edtNombre = null;
		image=null;
		LiberaObjetos();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		aux = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
	}
	
	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mGray.release();
		mRgba.release();
		aux.release();
		descriptores_obj.release();
		keypoints_obj.release();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (!buscandoObjeto){
				Intent myIntent = new Intent(ReconocimientoObjeto.this,
						EmpezarJuego.class);
				finish();
				startActivity(myIntent);
				return true;
			}else{
				buscandoObjeto=false;
				nObjeto=-1;
				return false;
			}
		}

		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.reconocimiento_objeto, menu);
		return true;
	}
	
	public native void FindFeatures(long matAddrGr, long matAddrRgba,
			long matAddrDescriptores, long matAddrKeyPoints);
	
	public native int FindObjects(long matAddrGray, long matAddrRgba, int i);
	
	public native int ObtieneCoincidencias(long matAddrGray, long matAddrRgba, int objeto);
	
	public native boolean InicializaEscenario(long matAddrGray, long matAddrRgba);
	
	public native int LiberaEscenario();
	
	public native int RellenarObjetos(long[] descriptors, long[] keyPoints, int[] cols, int[] rows);
	
	public native int LiberaObjetos();

}
