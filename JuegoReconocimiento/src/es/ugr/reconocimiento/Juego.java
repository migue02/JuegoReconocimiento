package es.ugr.reconocimiento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import es.ugr.juegoreconocimiento.R;
import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.basedatos.SerieEjerciciosDataSource;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import es.ugr.objetos.Resultado;
import es.ugr.objetos.SerieEjercicios;
import es.ugr.utilidades.Utilidades;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Juego extends Activity implements CvCameraViewListener2 {
	
	private static final String TAG = "Juego::Activity";
	
	private CameraBridgeViewBase mOpenCvCameraView;
	private int nObjeto = -1;
	private Mat mGray, mRgba;
	private Mat aux, auxGray;
	private MatOfKeyPoint keypoints_obj = new MatOfKeyPoint();
	private Mat descriptores_obj = new Mat();
	private KeyPoint[] listaKP_obj;
	private ArrayList<Mat> matsDescriptores = new ArrayList<Mat>(); 
	private ArrayList<MatOfKeyPoint> matsKeyPoints = new ArrayList<MatOfKeyPoint>();
	private int[] colsArray, rowsArray;
	private TextToSpeech ttobj;
	
	private EditText edthessianThreshold, edtnOctaves, edtnOctaveLayers;
	private CheckBox chkExtended, chkUpright;
	
	private double hessianThreshold=1300;
	private int nOctaves=4, nOctaveLayers=2;
	private boolean extended=false, upright=false;
	
	private Alumno oAlumno;
	private SerieEjercicios oSerie;
	private boolean bCiclico = false;
	
	private List<Ejercicio> lEjercicios;
	private List<Objeto> lObjetosEscenario;
	private List<Objeto> lObjetos;
	
	private EjercicioDataSource dsEjercicios;
	private Ejercicio oEjercicioActual;
	private Objeto oObjetoActual;
	private Objeto oObjetoReconocido;
	
	private ObjetoDataSource dsObjetos;
	
	private List<Resultado> lResultados;
	private Resultado oResultadoActual;
	private ResultadoDataSource dsResultado;
	
	private boolean bEsperandoRespuesta = false;
	private boolean bJuegoIniciado = false;
	
	private static String acierto = "Acierto ";
	private static String error = "Error ";
	
	private AssetFileDescriptor afd;
	private MediaPlayer player;
	
	
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
				
				//InicializaSurf(1800.0, 4, 2, true, true);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_juego);
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView2);
		mOpenCvCameraView.setCvCameraViewListener(this);
		
		chkExtended = (CheckBox) findViewById(R.id.edtExtended);
		edthessianThreshold = (EditText) findViewById(R.id.edtHessian);
		edtnOctaveLayers = (EditText) findViewById(R.id.edtnOctaveLayers);
		edtnOctaves = (EditText) findViewById(R.id.edtnOctaves);
		chkUpright = (CheckBox) findViewById(R.id.edtUpRight);
		
		edthessianThreshold.setText("1500");
		edtnOctaveLayers.setText("2");
		edtnOctaves.setText("4");
		
		Bundle extras;
		if (savedInstanceState == null) {
		    extras = getIntent().getExtras();
		    if(extras == null) {
		    	AlumnoDataSource dsAlumnos = new AlumnoDataSource(Juego.this);
		    	dsAlumnos.open();
		    	SerieEjerciciosDataSource dsSeries = new SerieEjerciciosDataSource(Juego.this);
		    	dsSeries.open();
		    	try{
			        oAlumno = dsAlumnos.getAllAlumnos().get(0);
			        oSerie = dsSeries.getAllSeriesEjercicios().get(0);
		    	} catch (Exception e){
			        oAlumno = new Alumno();
			        oSerie = new SerieEjercicios();
		    	}
		        bCiclico = false;
		        dsAlumnos.close();
		        dsSeries.close();
		    } else {
		    	try{
			        oAlumno= (Alumno) extras.getSerializable("Alumno");
			        oSerie= (SerieEjercicios) extras.getSerializable("Serie");
			        bCiclico = extras.getBoolean("Ciclico");
		    	}catch (Exception e){
			        oAlumno = new Alumno();
			        oSerie = new SerieEjercicios();
			        bCiclico = false;
		    	}
		    }
		} else {
			try{
				oAlumno= (Alumno) savedInstanceState.getSerializable("Alumno");
		        oSerie= (SerieEjercicios) savedInstanceState.getSerializable("Serie");
		        bCiclico = savedInstanceState.getBoolean("Ciclico");
			}catch (Exception e){
		        oAlumno = new Alumno();
		        oSerie = new SerieEjercicios();
		        bCiclico = false;
	    	}
		}
		
		setTitle(oAlumno.getNombre() +" - "+oSerie.getNombre());
		
		
		ttobj=new TextToSpeech(getApplicationContext(), 
	      new TextToSpeech.OnInitListener() {
	      @Override
	      public void onInit(int status) {
	         if(status != TextToSpeech.ERROR){
	        	 Locale locale = new Locale("spa","ESP");
	             ttobj.setLanguage(locale);
	            }				
	         }
	      });
		
		dsObjetos = new ObjetoDataSource(this);
		dsObjetos.open();
		dsResultado = new ResultadoDataSource(this);
		dsResultado.open();
		dsEjercicios = new EjercicioDataSource(this);
		dsEjercicios.open();
		
		try{
			lEjercicios = dsEjercicios.getAllEjercicios(oSerie);
			oEjercicioActual = lEjercicios.get(0);
		} catch (Exception e){
			lEjercicios = dsEjercicios.getAllEjercicios();
			if (lEjercicios.size() > 0)
				oEjercicioActual = lEjercicios.get(0);
			else
				oEjercicioActual = new Ejercicio();
		}
		
		
		
	}
	
	private void iniciarJuego(){
		
		bEsperandoRespuesta = false;
		nObjeto=-1;
		bJuegoIniciado = true;
		
		lObjetosEscenario = dsObjetos.getAllObjetosEscenario(oEjercicioActual);
		lObjetos = dsObjetos.getAllObjetosReconocer(oEjercicioActual);	
		if (lObjetos.size() > 0){
			oObjetoActual = lObjetos.get(0);
			rellenar(false);
		}else{
			oObjetoActual = new Objeto();
		}
		
		
		oResultadoActual = new Resultado();
		oResultadoActual.setFechaRealizacion(new GregorianCalendar().getTime());
		oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
		oResultadoActual.setIdEjercicio(oSerie.getIdSerie());
		
	}

	private int getEjercicioActual(){
		int lnEjercicioActual=-1;
		for (int i=0; i<lEjercicios.size();i++)
			if (lEjercicios.get(i).getIdEjercicio() == oEjercicioActual.getIdEjercicio()){
				lnEjercicioActual = i;
				break;
			}
		return lnEjercicioActual;
	}
	
	private int getObjetoActual(){
		int lnObjetoActual=-1;
		for (int i=0; i<lObjetos.size();i++)
			if (lObjetos.get(i).getId() == oObjetoActual.getId()){
				lnObjetoActual = i;
				break;
			}
		return lnObjetoActual;
	}
	
	private boolean actualizaJuego(){
		
		boolean lbJuegoTerminado=true;
		
		int lnObjetoActual=getObjetoActual();
		
		if (lnObjetoActual >= 0){
			
			lnObjetoActual = (lnObjetoActual+1) % lObjetos.size();
		
			if (lnObjetoActual < lObjetos.size()){
				
				oObjetoActual = lObjetos.get(lnObjetoActual);
				lbJuegoTerminado = false;
			
			}else{ //Era último objeto del ejercicio lnObjetoActual == lObjetos.size()
			
				int lnEjercicioActual=getEjercicioActual();
				
				if ((lnEjercicioActual >= 0) && (lnEjercicioActual < lEjercicios.size()) || bCiclico){
					
					lnEjercicioActual = (lnEjercicioActual+1) % lEjercicios.size();
					
					if (lnEjercicioActual == 0){
						oResultadoActual.setDuracion(getDuracionActual()); //en minutos
						oResultadoActual.calculaPuntuacion();
						lResultados.add(oResultadoActual);
						
						oResultadoActual = new Resultado();
						oResultadoActual.setFechaRealizacion(new GregorianCalendar().getTime());
						oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
						oResultadoActual.setIdEjercicio(oSerie.getIdSerie());
						iniciarCrono();
					}
					
					oEjercicioActual = lEjercicios.get(lnEjercicioActual);
					
					lnObjetoActual=0;
					
					lObjetosEscenario = dsObjetos.getAllObjetosEscenario(oEjercicioActual);
					lObjetos = dsObjetos.getAllObjetosReconocer(oEjercicioActual);		
					oObjetoActual = lObjetos.get(lnObjetoActual);
					
					lbJuegoTerminado = false;
				}
			}
		}
		
		if (lbJuegoTerminado){
			oResultadoActual.setDuracion(getDuracionActual()); //en minutos
			lResultados.add(oResultadoActual);
			//Mostrar todos los resultados en una actividad nueva o dialog
		}else
			Toast.makeText(Juego.this, "Buscando "+oObjetoActual.getNombre(), Toast.LENGTH_SHORT).show();
		return lbJuegoTerminado;
		
	}
	
	private void rellenar(boolean aniadir){
		
		rowsArray = null;
		colsArray = null;
		matsDescriptores.clear();
		matsKeyPoints.clear();
		
		colsArray = new int[lObjetosEscenario.size()];
		rowsArray = new int[lObjetosEscenario.size()];
		
		for(int i=0;i<lObjetosEscenario.size(); i++){
			colsArray[i] = lObjetosEscenario.get(i).getCols();
			rowsArray[i] = lObjetosEscenario.get(i).getRows();
			
			Mat tempMat=Utilidades.matFromJson(lObjetosEscenario.get(i).getDescriptores());
            matsDescriptores.add(tempMat);
			
            MatOfKeyPoint tempMatKeyPoint=Utilidades.keypointsFromJson(lObjetosEscenario.get(i).getKeypoints());
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
            tempCols[i] = lObjetosEscenario.get(i).getCols();
            tempRows[i] = lObjetosEscenario.get(i).getRows();
        }

        RellenarObjetos(tempAddrDesc, tempAddrKeyP, tempCols, tempRows);
        
        tempAddrDesc = null;
        tempAddrKeyP = null;
        tempCols = null;
        tempRows = null;
        
	}
	
	private int getDuracionActual(){
		((Chronometer) findViewById(R.id.cronometro)).stop();
		long elapsedMillis = (SystemClock.elapsedRealtime() - ((Chronometer) findViewById(R.id.cronometro)).getBase())/1000;
		int horas = (int)elapsedMillis/3600;
		int remainder = (int) elapsedMillis - horas * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    double secs = remainder; 
	    double tiempo = (horas*60) + mins + (secs/10);
		Toast.makeText(Juego.this, "Tiempo: " + tiempo, 
	            Toast.LENGTH_SHORT).show();
		return mins;
	}
	
	private void iniciarCrono(){
		((Chronometer) findViewById(R.id.cronometro)).setBase(SystemClock.elapsedRealtime());
		((Chronometer) findViewById(R.id.cronometro)).start();
	}
	
	public void onReconocerClick(View v){
		iniciarJuego();
		iniciarCrono();
	}

	public void onInicializaSurf(View v){
		InicializaSurf(Double.parseDouble(edthessianThreshold.getText().toString()), Integer.parseInt(edtnOctaves.getText().toString()), 
				Integer.parseInt(edtnOctaveLayers.getText().toString()), chkExtended.isChecked(), chkUpright.isChecked());
	}
	
	public void onVerObjetosReconocer(View v){
			
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Objetos reconocer");

		ListView modeList = new ListView(this);
		final ArrayList<Objeto> llObjetos = new ArrayList<Objeto>(dsObjetos.getAllObjetosReconocer(oEjercicioActual));
		ArrayAdapter<Objeto> modeAdapter = new ArrayAdapter<Objeto>(this, android.R.layout.simple_list_item_1, android.R.id.text1, llObjetos);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();
		
		modeList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
					if (!bJuegoIniciado){
						int id=(int) llObjetos.get(location).getId();
						oEjercicioActual.eliminaObjetoReconocer(id);
						dsObjetos.eliminaObjeto(id);
						dsEjercicios.modificaEjercicio(oEjercicioActual);
					}
					dialog.dismiss();
				return false;
			}});
		
		dialog.show();
	}
	
	public void onVerObjetosEscenario(View v){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(Juego.this);
		builder.setTitle("Objetos escenario");

		ListView modeList = new ListView(Juego.this);
		final ArrayList<Objeto> llObjetosEscenario = new ArrayList<Objeto>(dsObjetos.getAllObjetosEscenario(oEjercicioActual));
		ArrayAdapter<Objeto> modeAdapter = new ArrayAdapter<Objeto>(Juego.this, android.R.layout.simple_list_item_1, android.R.id.text1, llObjetosEscenario);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
					if (!bJuegoIniciado){
						int id=(int) llObjetosEscenario.get(location).getId();
						oEjercicioActual.eliminaObjetoEscenario(id);
						dsObjetos.eliminaObjeto(id);
						dsEjercicios.modificaEjercicio(oEjercicioActual);
					}
					dialog.dismiss();
				return false;
			}});
		
		dialog.show();
	}
	
	public void onAciertoClick(View v){
		final ImageView btn = (ImageView) findViewById(R.id.btnMatch);
		btn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
		    	 	btn.setImageResource(R.drawable.acierto_pulsado);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                	btn.setImageResource(R.drawable.acierto);
                	if(bEsperandoRespuesta){
                		oResultadoActual.incrementaAcierto();
            			if (actualizaJuego()){
            				Toast.makeText(Juego.this, "El juego ha acabado con un resultado de "+lResultados.get(0).getPuntuacion(), 
            			            Toast.LENGTH_LONG).show();
            				bJuegoIniciado = false;
            			}
            			bEsperandoRespuesta = false;
                	}
                }
                return true;
			}
		});
	}
	
	public void onErrorClick(View v){
		
		final ImageView btn = (ImageView) findViewById(R.id.btnError);
		btn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					btn.setImageResource(R.drawable.error_pulsado);
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					btn.setImageResource(R.drawable.error);
					if (bEsperandoRespuesta){		
						oResultadoActual.incrementaFallo();
						bEsperandoRespuesta = false;
						/*if (actualizaJuego())
							Toast.makeText(Juego.this, "El juego ha acabado con un resultado de "+lResultados.get(0).getPuntuacion(), 
						            Toast.LENGTH_LONG).show();*/
					}
				}
				return true;
			}
		});
			
	}
	
	public void onCapturarClick(View v){
		
		if (!bJuegoIniciado){
		
			auxGray = mGray.clone();
			aux = mRgba.clone();
			
			
			Imgproc.GaussianBlur(auxGray, auxGray, new Size(3,3), 2);
			Imgproc.Canny(auxGray, auxGray, 40, 120);
	
			Imgproc.GaussianBlur(aux, aux, new Size(3,3), 2);
			Imgproc.Canny(aux, aux, 40, 120);
			
			Imgproc.resize(auxGray, auxGray, new Size(320,240));
			Imgproc.resize(aux, aux, new Size(320,240));
			//long startTime = System.currentTimeMillis();
			
			float elapsedTime=FindFeatures(auxGray.getNativeObjAddr(),
					aux.getNativeObjAddr(), descriptores_obj.getNativeObjAddr(), keypoints_obj.getNativeObjAddr());
	
			//long stopTime = System.currentTimeMillis();
			//long elapsedTime = stopTime - startTime;		
			Toast.makeText(this, "Tiempo = "+elapsedTime+"\n"+"KeyPoints = "+keypoints_obj.size(), Toast.LENGTH_SHORT).show();
			if (!keypoints_obj.empty() || !descriptores_obj.empty()) {
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.activity_dialog_objeto);
				dialog.setTitle("¿Desea guardar este objeto?");
		
				// set the custom dialog components - image and button
				// convert to bitmap:
				Bitmap bmModificado = Bitmap.createBitmap(aux.cols(), aux.rows(),Bitmap.Config.ARGB_8888);
		        org.opencv.android.Utils.matToBitmap(aux, bmModificado);
				((ImageView) dialog.findViewById(R.id.imageObjeto)).setImageBitmap(bmModificado);
				
				final Bitmap bmOrigen = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(),Bitmap.Config.ARGB_8888);
		        org.opencv.android.Utils.matToBitmap(mRgba, bmOrigen);
				((ImageView) dialog.findViewById(R.id.imageObjetoOriginal)).setImageBitmap(bmOrigen);
				
				Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
				// if button is clicked, close the custom dialog
				btnAceptar.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String keyString, desString;				
						keyString = Utilidades.keypointsToJson(keypoints_obj);
						desString = Utilidades.matToJson(descriptores_obj);
						Objeto objeto = new Objeto(-1,((EditText) dialog.findViewById(R.id.edtNombre)).getText().toString(), 
								keyString, desString, aux.cols(), aux.rows(), bmOrigen);
						dsObjetos.createObjeto(objeto);
						descriptores_obj.release();
						keypoints_obj.release();
						dialog.dismiss();
						aux.release();
						auxGray.release();
						if (objeto.getId() != -1)
							insertaEnSerie(objeto);
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
						aux.release();
						auxGray.release();
					}
				});		
				dialog.show();
			}else
				Toast.makeText(this, "Es necesario capturar de nuevo el objeto", Toast.LENGTH_SHORT).show();
		
		}
		
	}
	
	private void insertaEnSerie(final Objeto objeto) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Series + "+objeto.getNombre());
		SerieEjerciciosDataSource dsSerie =  new SerieEjerciciosDataSource(this);
		dsSerie.open();
		final List<SerieEjercicios> listaSeries = dsSerie.getAllSeriesEjercicios();
		dsSerie.close();
		ListView modeList = new ListView(this);
		ArrayAdapter<SerieEjercicios> modeAdapter = 
				new ArrayAdapter<SerieEjercicios>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listaSeries);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemClickListener(new OnItemClickListener() {		

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int location, long id) {
				insertaEnEjercicio(listaSeries.get(location), objeto);
				dialog.dismiss();
			}});
		
		dialog.show();		
	}

	private void insertaEnEjercicio(SerieEjercicios serieEjercicios, final Objeto objeto) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ejercicios - "+serieEjercicios.getNombre() +" - "+objeto.getNombre());		
		final List<Ejercicio> listaEjercicios = dsEjercicios.getAllEjercicios(serieEjercicios);		
		final ListView modeList = new ListView(this);
		ArrayAdapter<Ejercicio> modeAdapter = 
				new ArrayAdapter<Ejercicio>(this, android.R.layout.simple_list_item_1, android.R.id.text1, listaEjercicios);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemClickListener(new OnItemClickListener() {		

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int location, long id) {
				listaEjercicios.get(location).getObjetosReconocer().add((int) objeto.getId());
				listaEjercicios.get(location).getObjetos().add((int) objeto.getId());
				dsEjercicios.modificaEjercicio(listaEjercicios.get(location));
				dialog.dismiss();
				Toast.makeText(Juego.this, "Añadido a reconocer", Toast.LENGTH_SHORT).show();
			}});
		
		modeList.setOnItemLongClickListener(new OnItemLongClickListener() {		

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,int location, long id) {
				listaEjercicios.get(location).getObjetos().add((int) objeto.getId());
				dsEjercicios.modificaEjercicio(listaEjercicios.get(location));
				dialog.dismiss();
				Toast.makeText(Juego.this, "Añadido a escenario", Toast.LENGTH_SHORT).show();
				return true;		
			}});
		
		
		
		dialog.show();	
	}
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if ((!bJuegoIniciado || (bJuegoIniciado && nObjeto == -1)) && !bEsperandoRespuesta){
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if ((bJuegoIniciado && lObjetos.size()>0)) {
				auxGray = mGray.clone();
				
				Imgproc.GaussianBlur(auxGray, auxGray, new Size(3,3), 2);
				Imgproc.Canny(auxGray, auxGray, 40, 120);				
				Imgproc.resize(auxGray, auxGray, new Size(320,240));
				
				nObjeto=FindObjects(auxGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
				
				mGray = auxGray.clone();
				auxGray.release();
				
				if (nObjeto!=-1){
					int id = (int)lObjetosEscenario.get(nObjeto).getId();
					bEsperandoRespuesta = true;
					
					oObjetoReconocido = dsObjetos.getObjetoSimple(id); //Solo id, nombre e imagen
					String respuestaAux = "";
					
					if (oObjetoReconocido.getId() == oObjetoActual.getId()){
						respuestaAux = acierto;
						try{
							afd = getAssets().openFd("acierto.mp3");
						} catch (IOException e){
							e.printStackTrace();
						}
					}else{
						respuestaAux = error;
						try{
							afd = getAssets().openFd("fallo.mp3");
						} catch (IOException e){
							e.printStackTrace();
						}
					}
					
					final String respuesta = respuestaAux;
					this.runOnUiThread(new Runnable() {
					    public void run() {
					    	try {
								player = new MediaPlayer();
								player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
							    player.prepare();
							    player.start();
							} catch (IOException e) {
								e.printStackTrace();
							}	
					    	ttobj.speak(respuesta, TextToSpeech.QUEUE_FLUSH, null);					    	
					    	Toast.makeText(getApplicationContext(), respuesta + " - encontrado el objeto " + oObjetoReconocido.getNombre(), Toast.LENGTH_SHORT).show();
					    }
					});
					nObjeto=-1;
				}
			}
		}
		return mRgba;
	}
	
	@Override
	public void onPause() {
		dsObjetos.close();
		dsEjercicios.close();
		dsResultado.close();
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
		if(dsObjetos!=null) dsObjetos.open();
		if(dsEjercicios!=null) dsEjercicios.open();
		if(dsResultado!=null) dsResultado.open();
		mOpenCvCameraView.enableView();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		colsArray = null;
		rowsArray = null;
		matsDescriptores = null;
		matsKeyPoints = null;
		lObjetos = null;
		lObjetosEscenario = null;
		LiberaObjetos();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		aux = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);
		auxGray = new Mat(height, width, CvType.CV_8UC1);
	}
	
	@Override
	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
		aux.release();
		auxGray.release();
		descriptores_obj.release();
		keypoints_obj.release();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (bJuegoIniciado){
				bJuegoIniciado=false;
				nObjeto=-1;
				return false;
			}
			else{
				finish();
				return super.onKeyDown(keyCode, event);
			}
				
		}
		return super.onKeyDown(keyCode, event);
	}

	public native float FindFeatures(long matAddrGr, long matAddrRgba,
			long matAddrDescriptores, long matAddrKeyPoints);
	
	public native int FindObjects(long matAddrGray, long matAddrRgba);
	
	public native int RellenarObjetos(long[] descriptors, long[] keyPoints, int[] cols, int[] rows);
	
	public native int LiberaObjetos();
	
	public native void InicializaSurf(double phessian, int pnOctaves, int pnOctaveLayers, boolean pExtended, boolean pUpright);

}
