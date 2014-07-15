package es.ugr.reconocimiento;

import java.util.ArrayList;
import java.util.Collections;
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
	private boolean bJugando = false;
	private int nObjetoReconocido = -1;
	
	private Mat mGray, mRgba, mAux;
	private MatOfKeyPoint mKeyPointsObjeto = new MatOfKeyPoint();
	private Mat mDescriptoresObjeto = new Mat();
	private KeyPoint[] lKeyPointsObjetos;
	private ArrayList<Mat> lmDescriptoresObjetos = new ArrayList<Mat>(); 
	private ArrayList<MatOfKeyPoint> lmKeyPointsObjetos = new ArrayList<MatOfKeyPoint>();
	private int[] lColsObjetos;
	private int[] lRowsObjetos;
	
	private ObjetoDataSource dsObjeto;
	private EjercicioDataSource dsEjercicio;
	
	private String sNombre;
	private EditText edtNombre;
	private ImageView ivImagen;
	private TextToSpeech ttsTextToSpeech;
	
	private Boolean bCiclico;
	private List<Ejercicio> lEjercicios;
	private int nEjercicioActual = 0;
	private int nObjetoActual = 0;
	private ArrayList<Objeto> lObjetos;
	private ArrayList<Objeto> lObjetosEscenario;
	private ArrayList<Integer> lCoincidencias = new ArrayList<Integer>();
	private int nMatches = 20;
	
	private Alumno oAlumno;
	private SerieEjercicios oSerie;
	private Resultado oResultado = new Resultado();
	private String solucion;
	
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
		        oAlumno = new Alumno();
		        oSerie = new SerieEjercicios();
		        bCiclico = false;
		    } else {
		        oAlumno= (Alumno) extras.getSerializable("Alumno");
		        oSerie= (SerieEjercicios) extras.getSerializable("Serie");
		        bCiclico = extras.getBoolean("Ciclico");
		    }
		} else {
			oAlumno= (Alumno) savedInstanceState.getSerializable("Alumno");
	        oSerie= (SerieEjercicios) savedInstanceState.getSerializable("Serie");
	        bCiclico = savedInstanceState.getBoolean("Ciclico");
		}
		
		
		dsObjeto = new ObjetoDataSource(this);
		dsObjeto.open();
		if (oSerie == null)
			lObjetos = dsObjeto.getAllObjetos();
		else{
			dsEjercicio = new EjercicioDataSource(this);
			dsEjercicio.open();
			lEjercicios = dsEjercicio.getAllEjercicios(oSerie);
			lObjetosEscenario = dsObjeto.getAllObjetosEscenario(lEjercicios.get(nEjercicioActual));
			lObjetos = dsObjeto.getAllObjetosReconocer(lEjercicios.get(nEjercicioActual));
			
			oResultado.setFechaRealizacion(new GregorianCalendar().getTime());
			oResultado.setIdAlumno(oAlumno.getIdAlumno());
			oResultado.setIdEjercicio(oSerie.getIdSerie());
		}
		rellenar(false);
		
		ttsTextToSpeech=new TextToSpeech(getApplicationContext(), 
	      new TextToSpeech.OnInitListener() {
	      @Override
	      public void onInit(int status) {
	         if(status != TextToSpeech.ERROR){
	        	 Locale loc = new Locale ("spa", "ESP");
	             ttsTextToSpeech.setLanguage(loc);
	            }				
	         }
	      });

	}
	
	private void rellenar(boolean aniadir){
		
		lRowsObjetos = null;
		lColsObjetos = null;
		lmDescriptoresObjetos.clear();
		lmKeyPointsObjetos.clear();
		
		lColsObjetos = new int[lObjetosEscenario.size()];
		lRowsObjetos = new int[lObjetosEscenario.size()];
		
		for(int i=0;i<lObjetosEscenario.size(); i++){
			lColsObjetos[i] = lObjetosEscenario.get(i).getCols();
			lRowsObjetos[i] = lObjetosEscenario.get(i).getRows();
			
			Mat tempMat=Utilidades.matFromJson(lObjetosEscenario.get(i).getDescriptores());
            lmDescriptoresObjetos.add(tempMat);
			
            MatOfKeyPoint tempMatKeyPoint=Utilidades.keypointsFromJson(lObjetosEscenario.get(i).getKeypoints());
    		lmKeyPointsObjetos.add(tempMatKeyPoint);
		}
		
        long[] tempAddrDesc = new long[lmDescriptoresObjetos.size()]; 
        long[] tempAddrKeyP = new long[lmKeyPointsObjetos.size()]; 
        int[] tempCols = new int[lColsObjetos.length];
        int[] tempRows = new int[lRowsObjetos.length];
        
        for (int i=0;i<lmDescriptoresObjetos.size();i++)
        {
            tempAddrDesc[i]= lmDescriptoresObjetos.get(i).getNativeObjAddr();
            tempAddrKeyP[i]= lmKeyPointsObjetos.get(i).getNativeObjAddr();            
            tempCols[i] = lObjetosEscenario.get(i).getCols();
            tempRows[i] = lObjetosEscenario.get(i).getRows();
        }

        RellenarObjetos(tempAddrDesc, tempAddrKeyP, tempCols, tempRows);
        
        
        tempAddrDesc = null;
        tempAddrKeyP = null;
        tempCols = null;
        tempRows = null;
        
	}
	
	public void onReconocerClick(View v){
		bJugando=true;
		nObjetoReconocido=-1;
		if (nObjetoActual==0 && nEjercicioActual ==0){
			((Chronometer) findViewById(R.id.cronometro)).setBase(SystemClock.elapsedRealtime());
			((Chronometer) findViewById(R.id.cronometro)).start();
		}
		if (nObjetoActual == 0){
			ttsTextToSpeech.speak("Ejercicio " + lEjercicios.get(nEjercicioActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
			Toast.makeText(getApplicationContext(), "Ejercicio " + lEjercicios.get(nEjercicioActual).getNombre(), Toast.LENGTH_SHORT).show();
		}
		ttsTextToSpeech.speak("Busca el objeto " + lObjetos.get(nObjetoActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
		Toast.makeText(getApplicationContext(),"Busca el objeto " + lObjetos.get(nObjetoActual).getNombre(), Toast.LENGTH_SHORT).show();
	}
	
	public void onCancelarClick(View v){
		if (!bJugando){
			Intent myIntent = new Intent(ReconocimientoObjeto.this,
					MainActivity.class);
			finish();
			startActivity(myIntent);
		}else{
			bJugando=false;
			nObjetoReconocido=-1;
		}
	}
	
	public void onReiniciar(View v){
		bJugando=false;
		nObjetoReconocido=-1;
		nObjetoActual=0;
		nEjercicioActual=0;
		if (oSerie == null)
			lObjetos = dsObjeto.getAllObjetos();
		else{
			dsEjercicio = new EjercicioDataSource(this);
			dsEjercicio.open();
			lEjercicios = dsEjercicio.getAllEjercicios(oSerie);
			lObjetos = dsObjeto.getAllObjetosReconocer(lEjercicios.get(nEjercicioActual));
			lObjetosEscenario = dsObjeto.getAllObjetosEscenario(lEjercicios.get(nEjercicioActual));
			
			oResultado.setFechaRealizacion(new GregorianCalendar().getTime());
			oResultado.setIdAlumno(oAlumno.getIdAlumno());
			oResultado.setIdEjercicio(oSerie.getIdSerie());
		}
		rellenar(false);
	}
	
	public void onSalir(View v){
		Intent myIntent = new Intent(ReconocimientoObjeto.this,
				EmpezarJuego.class);
		finish();
		startActivity(myIntent);
	}
	
	public void onAcierto(View v){
    	if (nObjetoReconocido == nObjetoActual){
    		oResultado.setAciertos(oResultado.getAciertos()+1);
    		actualizaEscenarioJuego();
    		nObjetoReconocido=-1;
    	}
	}
	
	public void onFallo(View v){
    	oResultado.setFallos(oResultado.getFallos()+1);
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
		mAux = mRgba.clone();
		FindFeatures(mGray.getNativeObjAddr(),
				mAux.getNativeObjAddr(), mDescriptoresObjeto.getNativeObjAddr(), mKeyPointsObjeto.getNativeObjAddr());
		if (!mKeyPointsObjeto.empty() || !mDescriptoresObjeto.empty()) {
			final Dialog dialog = new Dialog(ReconocimientoObjeto.this);
			dialog.setContentView(R.layout.activity_dialog_objeto);
			dialog.setTitle("¿Desea guardar este objeto?");
	
			// set the custom dialog components - image and button
			// convert to bitmap:
			Bitmap bm = Bitmap.createBitmap(mAux.cols(), mAux.rows(),Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(mAux, bm);
			ivImagen = (ImageView) dialog.findViewById(R.id.imageObjeto);
			ivImagen.setImageBitmap(bm);			
			
			edtNombre = (EditText) dialog.findViewById(R.id.edtNombre);
			Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
			// if button is clicked, close the custom dialog
			btnAceptar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String keyString, desString;				
					//keypoints_obj = new MatOfKeyPoint(listaKP_obj);
					keyString = Utilidades.keypointsToJson(mKeyPointsObjeto);
					desString = Utilidades.matToJson(mDescriptoresObjeto);
					/*Objeto obj=*///dsObjeto.createObjeto(edtNombre.getText().toString(), keyString, desString, mAux.cols(), mAux.rows());
					//lObjetos.add(obj);
					//rellenar(true);
					//id = obj.getId();
					mDescriptoresObjeto.release();
					mKeyPointsObjeto.release();
					dialog.dismiss();
					mAux.release();
				}
			});
			
			Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
			// if button is clicked, close the custom dialog
			btnCancelar.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					mDescriptoresObjeto.release();
					mKeyPointsObjeto.release();
					lObjetos.clear();
					lObjetosEscenario.clear();
					mAux.release();
				}
			});		
			dialog.show();
		}else
			Toast.makeText(this, "Es necesario capturar de nuevo el objeto", Toast.LENGTH_SHORT).show();
	}
	
	public class MyRunnable implements Runnable {
		public int nObjActual;
		public MyRunnable(int nObjActual){
			this.nObjActual=nObjActual;
		}
		@Override
		  public void run() {
			nObjetoReconocido=FindObjects(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(),nObjActual);
		  }
		} 
	
	public class HebraJuego implements Runnable {
		int coincidencias;
		public int nObjActual;
		public HebraJuego(int nObjActual){
			this.nObjActual=nObjActual;
		}
		@Override
		public void run() {
			coincidencias=ObtieneCoincidencias(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(),nObjActual);
			lCoincidencias.add(coincidencias);
		}
	} 	
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (!bJugando || (bJugando && nObjetoReconocido == -1)){
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if (bJugando) {
				if (InicializaEscenario(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr())){
					ExecutorService executor = Executors.newFixedThreadPool(lObjetos.size());
			    	for (int i = 0; i < lObjetosEscenario.size() || nObjetoReconocido != -1; i++) {
		    	      //Runnable findObjectThread = new MyRunnable(i);
			    		Runnable findObjectThread = new HebraJuego(i);
			    		executor.execute(findObjectThread);
		    	    }
			    	// This will make the executor accept no new threads
			        // and finish all existing threads in the queue
			        executor.shutdown();
			        // Wait until all threads are finish
			        while (!executor.isTerminated()){}
			        LiberaEscenario();
			        nObjetoReconocido = obtieneObjeto();
			        /*ReconocimientoObjeto.this.runOnUiThread(new Runnable() {
					    public void run() {						    	
					    	Toast.makeText(getApplicationContext(), nObjetoReconocido+"-"+nObjetoActual, Toast.LENGTH_SHORT).show();
					    }
					});*/
					if (nObjetoReconocido >= 0 && nObjetoReconocido < lObjetosEscenario.size()){
						sNombre=lObjetosEscenario.get(nObjetoReconocido).getNombre();							
						
						solucion="Acierto, se ha encontrado el objeto ";
				    	if (lObjetosEscenario.get(nObjetoReconocido).getId() != 
				    			lObjetos.get(nObjetoActual).getId()){
				    		solucion="Fallo, se ha encontrado el objeto ";
				    		oResultado.setFallos(oResultado.getFallos()+1);
				    	}
				    	
				    	//ttsTextToSpeech.speak(solucion+sNombre, TextToSpeech.QUEUE_FLUSH, null);
				    	
						ReconocimientoObjeto.this.runOnUiThread(new Runnable() {
						    public void run() {						    	
						    	Toast.makeText(getApplicationContext(), nObjetoReconocido+"-"+nObjetoActual +"->"+ solucion+sNombre, Toast.LENGTH_SHORT).show();
						    	/*if (lObjetosEscenario.get(nObjetoReconocido).getId() == 
						    			lObjetos.get(nObjetoActual).getId()){
						    		oResultado.setAciertos(oResultado.getAciertos()+1);
						    		actualizaEscenarioJuego();
						    	}*/
						    }
						});
						nObjetoReconocido=-1;
					}
				}
			}
		}
		return mRgba;
	}
	
	private int obtieneObjeto(){ // se igualara a nObjetoReconocer
		int maximo=0,objeto=-1;
		if (lCoincidencias != null && lCoincidencias.size() > 0){
			maximo = Collections.max(lCoincidencias);
			if (maximo >= nMatches){
				for (int i=0; i<lCoincidencias.size(); i++){
					if (lCoincidencias.get(i) == maximo){
						objeto=i;
						if (lObjetosEscenario.get(i).getId() == lObjetos.get(nObjetoActual).getId())//en la lista de objetos a reconocer
							return objeto;
					}
				}
			}
		}
		lCoincidencias.clear();
		return objeto;
	}
	
	private void actualizaEscenarioJuego(){
		bJugando=false;
		boolean juegoTerminado=false;
		nObjetoActual = (nObjetoActual+1)%lObjetos.size();
		if (nObjetoActual == 0){
			nEjercicioActual = (nEjercicioActual+1)%lEjercicios.size();
			if (bCiclico || nEjercicioActual != 0){
				LiberaObjetos();
				lObjetos.clear();
				lObjetosEscenario.clear();
				lObjetos = dsObjeto.getAllObjetosReconocer(lEjercicios.get(nEjercicioActual));
				lObjetosEscenario = dsObjeto.getAllObjetosEscenario(lEjercicios.get(nEjercicioActual));
				rellenar(false);
			}else{
				LiberaObjetos();
				lObjetos.clear();
				lObjetosEscenario.clear();
				juegoTerminado=true;
			}
		}
		if (!juegoTerminado){
			if (nObjetoActual==0){
				ttsTextToSpeech.speak("Ejercicio " + lEjercicios.get(nEjercicioActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
				Toast.makeText(getApplicationContext(), "Ejercicio " + lEjercicios.get(nEjercicioActual).getNombre(), Toast.LENGTH_SHORT).show();
			}
			ttsTextToSpeech.speak("Busca el objeto " + lObjetos.get(nObjetoActual).getNombre(), TextToSpeech.QUEUE_ADD, null);
			Toast.makeText(getApplicationContext(), "Busca el objeto " + lObjetos.get(nObjetoActual).getNombre(), Toast.LENGTH_SHORT).show();
		}else{
			ttsTextToSpeech.speak("El juego ha terminado", TextToSpeech.QUEUE_ADD, null);
			Toast.makeText(getApplicationContext(), "El juego ha terminado", Toast.LENGTH_SHORT).show();
			((Chronometer) findViewById(R.id.cronometro)).stop();
		}
	}
	
	@Override
	public void onPause() {
		dsObjeto.close();
		if(ttsTextToSpeech !=null){
			ttsTextToSpeech.stop();
			ttsTextToSpeech.shutdown();
		}
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
		dsObjeto.open();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lColsObjetos = null;
		lRowsObjetos = null;
		lmDescriptoresObjetos = null;
		lmKeyPointsObjetos = null;
		edtNombre = null;
		ivImagen=null;
		lObjetos.clear();
		lObjetosEscenario.clear();
		lCoincidencias.clear();
		lEjercicios.clear();
		lKeyPointsObjetos = null;
		LiberaObjetos();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mAux = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
	}
	
	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mGray.release();
		mRgba.release();
		mAux.release();
		mDescriptoresObjeto.release();
		mKeyPointsObjeto.release();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (bJugando){
				bJugando=false;
				nObjetoReconocido=-1;
				return false;
			}
			else
				return super.onKeyDown(keyCode, event);
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
