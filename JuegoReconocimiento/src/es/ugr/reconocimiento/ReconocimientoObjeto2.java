package es.ugr.reconocimiento;

import java.util.ArrayList;
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
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.MainActivity;
import es.ugr.objetos.Objeto;
import es.ugr.utilidades.Utilidades;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class ReconocimientoObjeto2 extends Activity implements CvCameraViewListener2 {
	
	private static final String TAG = "ReconocimientoObjeto::Activity";
	
	private CameraBridgeViewBase mOpenCvCameraView;
	private boolean buscandoObjeto = false;
	private int nObjeto = -1;
	private Mat mGray;
	private Mat mRgba;
	private Mat aux, auxGray;
	private ObjetoDataSource datasource;
	private MatOfKeyPoint keypoints_obj = new MatOfKeyPoint();
	private Mat descriptores_obj = new Mat();
	private KeyPoint[] listaKP_obj;
	private ArrayList<Objeto> objetos;
	private ArrayList<Mat> matsDescriptores = new ArrayList<Mat>(); 
	private ArrayList<MatOfKeyPoint> matsKeyPoints = new ArrayList<MatOfKeyPoint>();
	private int[] colsArray;
	private int[] rowsArray;
	private String nombre;
	private EditText edtNombre;
	private ImageView image;
	private TextToSpeech ttobj;
	
	private EditText edthessianThreshold;
	private EditText edtnOctaves;
	private EditText edtnOctaveLayers;
	private CheckBox chkExtended;
	private CheckBox chkUpright;

	
	double hessianThreshold=1300;
	int nOctaves=4;
	int nOctaveLayers=2;
	boolean extended=false;
	boolean upright=false;

	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_reconocimiento_objeto2);
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
		
		datasource = new ObjetoDataSource(this);
		datasource.open();
		objetos = datasource.getAllObjetos(7);
		rellenar(false);
		
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
	}
	
	public void onSettingsClick(View v){
		datasource.eliminaTodosObjetos();
		LiberaObjetos();
		objetos.clear();
		objetos = datasource.getAllObjetos(7);
		rellenar(false);
	}
	
	public void onCancelarClick(View v){
		if (!buscandoObjeto){
			Intent myIntent = new Intent(ReconocimientoObjeto2.this,
					MainActivity.class);
			finish();
			startActivity(myIntent);
		}else{
			buscandoObjeto=false;
			nObjeto=-1;
		}
	}
	
	public void onInicializaSurf(View v){
		InicializaSurf(Double.parseDouble(edthessianThreshold.getText().toString()), Integer.parseInt(edtnOctaves.getText().toString()), 
				Integer.parseInt(edtnOctaveLayers.getText().toString()), chkExtended.isChecked(), chkUpright.isChecked());
		//InicializaSurf(1800.0, 4, 2, true, false);
	}
	
	public void onVerObjetos(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Objetos");

		ListView modeList = new ListView(this);
		ArrayAdapter<Objeto> modeAdapter = new ArrayAdapter<Objeto>(this, android.R.layout.simple_list_item_1, android.R.id.text1, objetos);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		modeList.setOnItemClickListener(new OnItemClickListener() {		

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int location, long id) {
				((Button) findViewById(R.id.btnObjetos)).setText(objetos.get(location).getNombre());
				dialog.dismiss();
			}});
		
		dialog.show();
	}
	
	public void onCapturarClick(View v){
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
			final Dialog dialog = new Dialog(ReconocimientoObjeto2.this);
			dialog.setContentView(R.layout.activity_dialog_objeto);
			dialog.setTitle("�Desea guardar este objeto?");
	
			// set the custom dialog components - image and button
			// convert to bitmap:
			Bitmap bm = Bitmap.createBitmap(aux.cols(), aux.rows(),Bitmap.Config.ARGB_8888);
	        org.opencv.android.Utils.matToBitmap(aux, bm);
			image = (ImageView) dialog.findViewById(R.id.imageObjeto);
			image.setImageBitmap(bm);			
			
			edtNombre = (EditText) dialog.findViewById(R.id.edtNombre);
			Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
			// if button is clicked, close the custom dialog
			btnAceptar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String keyString, desString;				
					keyString = Utilidades.keypointsToJson(keypoints_obj);
					desString = Utilidades.matToJson(descriptores_obj);
					datasource.createObjeto(edtNombre.getText().toString(), keyString, desString, aux.cols(), aux.rows());
					objetos.clear();
					objetos=datasource.getAllObjetos(7);
					LiberaObjetos();
					rellenar(false);
					//id = obj.getId();
					descriptores_obj.release();
					keypoints_obj.release();
					dialog.dismiss();
					aux.release();
					auxGray.release();

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
					auxGray.release();
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
			//nObjeto=FindObjects(mGray.getNativeObjAddr(), mRgba.getNativeObjAddr(),nObjetoActual);
		  }
		} 
	
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (!buscandoObjeto || (buscandoObjeto && nObjeto == -1)){
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if (buscandoObjeto && objetos.size()>0) {
				auxGray = mGray.clone();
				aux = mRgba.clone();
				
				Imgproc.GaussianBlur(auxGray, auxGray, new Size(3,3), 2);
				Imgproc.Canny(auxGray, auxGray, 40, 120);				
				Imgproc.resize(auxGray, auxGray, new Size(320,240));
				
				nObjeto=FindObjects(auxGray.getNativeObjAddr(), mRgba.getNativeObjAddr());
				
				mGray = auxGray.clone();
				auxGray.release();
				
				if (nObjeto!=-1){
					nombre=objetos.get(nObjeto).getNombre();
					ReconocimientoObjeto2.this.runOnUiThread(new Runnable() {
					    public void run() {
					    	ttobj.speak(nombre, TextToSpeech.QUEUE_FLUSH, null);
					    	Toast.makeText(getApplicationContext(), "Encontrado el objeto "+nombre, Toast.LENGTH_SHORT).show();
					    }
					});
					nObjeto=-1;
				}
			}
		}
		return mRgba;
	}
	
/*	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
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
					if (nObjeto!=-1){
						nombre=objetos.get(nObjeto).getNombre();
						ReconocimientoObjeto2.this.runOnUiThread(new Runnable() {
						    public void run() {
						    	ttobj.speak(nombre, TextToSpeech.QUEUE_FLUSH, null);
						    	Toast.makeText(getApplicationContext(), "Encontrado el objeto "+nombre, Toast.LENGTH_SHORT).show();
						    }
						});
						nObjeto=-1;
					}
				}
			}
		}
		return mRgba;
	}*/
	
	@Override
	public void onPause() {
		datasource.close();
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
		datasource.open();
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
		auxGray = new Mat(height, width, CvType.CV_8UC1);
	}
	
	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
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
			if (buscandoObjeto){
				buscandoObjeto=false;
				nObjeto=-1;
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

		return true;
	}
	
	public native float FindFeatures(long matAddrGr, long matAddrRgba,
			long matAddrDescriptores, long matAddrKeyPoints);
	
	public native int FindObjects(long matAddrGray, long matAddrRgba);
	
	public native int RellenarObjetos(long[] descriptors, long[] keyPoints, int[] cols, int[] rows);
	
	public native int LiberaObjetos();
	
	public native void InicializaSurf(double phessian, int pnOctaves, int pnOctaveLayers, boolean pExtended, boolean pUpright);

}
