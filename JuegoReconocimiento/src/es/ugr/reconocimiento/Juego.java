package es.ugr.reconocimiento;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

import com.squareup.picasso.Picasso;

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
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import es.ugr.reconocimiento.JuegoLibreria;

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

	private double hessianThreshold = 1300;
	private int nOctaves = 4, nOctaveLayers = 2;
	private boolean extended = false, upright = false;

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

	private boolean bVistaCapturar;

	// //////////////
	// Iniciar cámara
	// //////////////
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
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

	// /////////////////
	// Iniciar actividad
	// /////////////////
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_juego);

		ttobj = new TextToSpeech(getApplicationContext(),
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status != TextToSpeech.ERROR) {
							Locale locale = new Locale("spa", "ESP");
							ttobj.setLanguage(locale);
						}
					}
				});

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
			if (extras == null) {
				AlumnoDataSource dsAlumnos = new AlumnoDataSource(Juego.this);
				dsAlumnos.open();
				SerieEjerciciosDataSource dsSeries = new SerieEjerciciosDataSource(
						Juego.this);
				dsSeries.open();
				try {
					oAlumno = dsAlumnos.getAllAlumnos().get(0);
					oSerie = dsSeries.getAllSeriesEjercicios().get(0);
				} catch (Exception e) {
					oAlumno = new Alumno();
					oSerie = new SerieEjercicios();
				}
				bCiclico = false;
				dsAlumnos.close();
				dsSeries.close();
			} else {
				try {
					oAlumno = (Alumno) extras.getSerializable("Alumno");
					oSerie = (SerieEjercicios) extras.getSerializable("Serie");
					bCiclico = extras.getBoolean("Ciclico");
				} catch (Exception e) {
					oAlumno = new Alumno();
					oSerie = new SerieEjercicios();
					bCiclico = false;
				}
				try {
					bVistaCapturar = extras.getBoolean("VistaCapturar");
				} catch (Exception e) {
					bVistaCapturar = false;
				}
			}
		} else {
			try {
				oAlumno = (Alumno) savedInstanceState.getSerializable("Alumno");
				oSerie = (SerieEjercicios) savedInstanceState
						.getSerializable("Serie");
				bCiclico = savedInstanceState.getBoolean("Ciclico");
			} catch (Exception e) {
				oAlumno = new Alumno();
				oSerie = new SerieEjercicios();
				bCiclico = false;
			}
			try {
				bVistaCapturar = savedInstanceState.getBoolean("VistaCapturar");
			} catch (Exception e) {
				bVistaCapturar = false;
			}
		}

		dsObjetos = new ObjetoDataSource(this);
		dsObjetos.open();

		if (!bVistaCapturar) { // Modo Juego

			setTitle(oAlumno.getNombre() + " - " + oSerie.getNombre());

			dsResultado = new ResultadoDataSource(this);
			dsResultado.open();
			dsEjercicios = new EjercicioDataSource(this);
			dsEjercicios.open();

			try {
				lEjercicios = dsEjercicios.getAllEjercicios(oSerie);
				oEjercicioActual = lEjercicios.get(0);
			} catch (Exception e) {
				lEjercicios = dsEjercicios.getAllEjercicios();
				if (lEjercicios.size() > 0)
					oEjercicioActual = lEjercicios.get(0);
				else
					oEjercicioActual = new Ejercicio();
			}
			((ImageView) findViewById(R.id.btnCapturar))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutEdits))
					.setVisibility(View.GONE);
		} else { // Modo añadir objeto

			setTitle("Añadir objeto");
			((LinearLayout) findViewById(R.id.layoutBotones))
					.setVisibility(View.GONE);
			((ImageView) findViewById(R.id.btnReconocer))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.layoutCrono))
					.setVisibility(View.GONE);
		}
	}

	// ///////////////////
	// Empezar a reconocer
	// ///////////////////
	private void iniciarJuego() {

		bEsperandoRespuesta = false;
		nObjeto = -1;
		bJuegoIniciado = true;

		lObjetosEscenario = dsObjetos.getAllObjetosEscenario(oEjercicioActual);
		lObjetos = dsObjetos.getAllObjetosReconocer(oEjercicioActual);
		if (lObjetos.size() > 0) {
			oObjetoActual = lObjetos.get(0);
			rellenar(false);
		} else {
			oObjetoActual = new Objeto();
		}

		oResultadoActual = new Resultado();
		oResultadoActual.setFechaRealizacion(new GregorianCalendar().getTime());
		oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
		oResultadoActual.setIdEjercicio(oSerie.getIdSerie());

		if (oObjetoActual != null)
			Toast.makeText(Juego.this, "Buscando " + oObjetoActual.getNombre(),
					Toast.LENGTH_SHORT).show();

	}

	// ///////////////////////////////////////////////////////
	// Decidir siguiente objeto a reconocer y de que ejercicio
	// ///////////////////////////////////////////////////////
	private boolean actualizaJuego() {

		boolean lbJuegoTerminado = true;

		int lnObjetoActual = JuegoLibreria.getObjetoActual(lObjetos,
				oObjetoActual);

		if (lnObjetoActual >= 0) {

			lnObjetoActual = (lnObjetoActual + 1) % lObjetos.size();

			if (lnObjetoActual < lObjetos.size()) {

				oObjetoActual = lObjetos.get(lnObjetoActual);
				lbJuegoTerminado = false;

			} else { // Era último objeto del ejercicio lnObjetoActual ==
						// lObjetos.size()

				int lnEjercicioActual = JuegoLibreria.getEjercicioActual(
						lEjercicios, oEjercicioActual);

				if ((lnEjercicioActual >= 0)
						&& (lnEjercicioActual < lEjercicios.size()) || bCiclico) {

					lnEjercicioActual = (lnEjercicioActual + 1)
							% lEjercicios.size();

					if (lnEjercicioActual == 0) {
						oResultadoActual.setDuracion(JuegoLibreria
								.getDuracionActual(this)); // en
						// minutos
						oResultadoActual.calculaPuntuacion();
						lResultados.add(oResultadoActual);

						oResultadoActual = new Resultado();
						oResultadoActual
								.setFechaRealizacion(new GregorianCalendar()
										.getTime());
						oResultadoActual.setIdAlumno(oAlumno.getIdAlumno());
						oResultadoActual.setIdEjercicio(oSerie.getIdSerie());
						JuegoLibreria.iniciarCrono(this);
					}

					oEjercicioActual = lEjercicios.get(lnEjercicioActual);

					lnObjetoActual = 0;

					lObjetosEscenario = dsObjetos
							.getAllObjetosEscenario(oEjercicioActual);
					lObjetos = dsObjetos
							.getAllObjetosReconocer(oEjercicioActual);
					oObjetoActual = lObjetos.get(lnObjetoActual);

					lbJuegoTerminado = false;
				}
			}
		}

		if (lbJuegoTerminado) {
			oResultadoActual.setDuracion(JuegoLibreria.getDuracionActual(this)); // en
																					// minutos
			lResultados.add(oResultadoActual);
			// Mostrar todos los resultados en una actividad nueva o dialog
		} else
			Toast.makeText(Juego.this, "Buscando " + oObjetoActual.getNombre(),
					Toast.LENGTH_SHORT).show();
		return lbJuegoTerminado;

	}

	// ////////////////////////////////////////////////////////////////////////
	// Rellenar matrices de KeyPoints y Descriptors en la actividad y en nativo
	// ////////////////////////////////////////////////////////////////////////
	private void rellenar(boolean aniadir) {

		rowsArray = null;
		colsArray = null;
		matsDescriptores.clear();
		matsKeyPoints.clear();

		colsArray = new int[lObjetosEscenario.size()];
		rowsArray = new int[lObjetosEscenario.size()];

		for (int i = 0; i < lObjetosEscenario.size(); i++) {
			colsArray[i] = lObjetosEscenario.get(i).getCols();
			rowsArray[i] = lObjetosEscenario.get(i).getRows();

			Mat tempMat = Utilidades.matFromJson(lObjetosEscenario.get(i)
					.getDescriptores());
			matsDescriptores.add(tempMat);

			MatOfKeyPoint tempMatKeyPoint = Utilidades
					.keypointsFromJson(lObjetosEscenario.get(i).getKeypoints());
			matsKeyPoints.add(tempMatKeyPoint);
		}

		long[] tempAddrDesc = new long[matsDescriptores.size()];
		long[] tempAddrKeyP = new long[matsKeyPoints.size()];
		int[] tempCols = new int[colsArray.length];
		int[] tempRows = new int[rowsArray.length];

		for (int i = 0; i < matsDescriptores.size(); i++) {
			tempAddrDesc[i] = matsDescriptores.get(i).getNativeObjAddr();
			tempAddrKeyP[i] = matsKeyPoints.get(i).getNativeObjAddr();
			tempCols[i] = lObjetosEscenario.get(i).getCols();
			tempRows[i] = lObjetosEscenario.get(i).getRows();
		}

		RellenarObjetos(tempAddrDesc, tempAddrKeyP, tempCols, tempRows);

		tempAddrDesc = null;
		tempAddrKeyP = null;
		tempCols = null;
		tempRows = null;

	}

	// //////////////////////////////////////////////////////////////////
	// Se ha hecho click en terminar juego y se muestra actividad resumen
	// //////////////////////////////////////////////////////////////////
	public void onClickSalir(View v) {
		Intent intent = new Intent(this, ResultadoSerie.class);
		if (lResultados == null)
			lResultados = new ArrayList<Resultado>();

		if (lResultados.isEmpty()) {
			Resultado a = new Resultado();
			a.setAciertos(15);
			a.setDuracion(20);
			a.setFallos(6);
			a.setFechaRealizacion(new Date());
			a.setIdAlumno(oAlumno.getIdAlumno());
			a.setIdEjercicio(oSerie.getIdSerie());
			a.calculaPuntuacion();
			dsResultado.createResultado(a);
			lResultados.add(a);
		}
		int[] ids = new int[lResultados.size()];

		for (int i = 0; i < lResultados.size(); i++)
			ids[i] = lResultados.get(i).getIdEjercicio();

		intent.putExtra("Resultados", ids);
		intent.putExtra("Alumno", oAlumno.getNombre());
		intent.putExtra("Serie", oSerie.getNombre());
		startActivity(intent);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ayuda y se escucha la ayuda asociada al objeto a
	// reconocer actualmente
	// /////////////////////////////////////////////////////////////////////
	public void onAyudaClick(View v) {
		if (oObjetoActual != null)
			oObjetoActual.playSonidoAyuda(this);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en descripción y se escucha la descripción asociada
	// al objeto a reconocer actualmente
	// /////////////////////////////////////////////////////////////////////
	public void onDescripcionClick(View v) {
		if (oObjetoActual != null)
			oObjetoActual.playSonidoDescripcion(this);
	}

	// ////////////////////////////////////////////////////////////////////
	// Se ha hecho click en SURF y se inicializan los valores del algoritmo
	// ////////////////////////////////////////////////////////////////////
	public void onInicializaSurf(View v) {
		InicializaSurf(
				Double.parseDouble(edthessianThreshold.getText().toString()),
				Integer.parseInt(edtnOctaves.getText().toString()),
				Integer.parseInt(edtnOctaveLayers.getText().toString()),
				chkExtended.isChecked(), chkUpright.isChecked());
	}

	// //////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ver objetos a reconocer y se muestran los objetos
	// a reconocer en este ejercicio
	// //////////////////////////////////////////////////////////////////////
	public void onVerObjetosReconocer(View v) {
		JuegoLibreria.onVerObjetos(this, dsObjetos, oEjercicioActual,
				dsEjercicios, bJuegoIniciado, true);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Se ha hecho click en ver objetos con los que se jugarán en el ejercicio
	// actual y se muestran todos los objetos de este ejercicio
	// ///////////////////////////////////////////////////////////////////////
	public void onVerObjetosEscenario(View v) {
		JuegoLibreria.onVerObjetos(this, dsObjetos, oEjercicioActual,
				dsEjercicios, bJuegoIniciado, false);
	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en acierto y se suma un acierto al resultado actual
	// /////////////////////////////////////////////////////////////////////
	public void onAciertoClick(View v) {
		final ImageView btn = (ImageView) findViewById(R.id.btnMatch);
		btn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Picasso.with(v.getContext())
							.load(R.drawable.acierto_pulsado).into(btn);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Picasso.with(v.getContext()).load(R.drawable.acierto)
							.into(btn);
					if (bEsperandoRespuesta) {
						oResultadoActual.incrementaAcierto();
						if (actualizaJuego()) {
							Toast.makeText(
									Juego.this,
									"El juego ha acabado con un resultado de "
											+ lResultados.get(
													lResultados.size() - 1)
													.getPuntuacion(),
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

	// /////////////////////////////////////////////////////////////////
	// Se ha hecho click en fallo y se suma un fallo al resultado actual
	// /////////////////////////////////////////////////////////////////
	public void onErrorClick(View v) {

		final ImageView btn = (ImageView) findViewById(R.id.btnError);
		btn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Picasso.with(v.getContext()).load(R.drawable.error_pulsado)
							.into(btn);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Picasso.with(v.getContext()).load(R.drawable.error)
							.into(btn);
					if (bEsperandoRespuesta) {
						oResultadoActual.incrementaFallo();
						bEsperandoRespuesta = false;
					}
				}
				return true;
			}
		});

	}

	// ////////////////////////////////////////////////////////////////////
	// Se ha hecho click en iniciar reconocimiento y se empieza a reconocer
	// objetos
	// ////////////////////////////////////////////////////////////////////
	public void onReconocerClick(View v) {
		if (!bJuegoIniciado) {
			iniciarJuego();
			JuegoLibreria.iniciarCrono(this);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	// Se ha hecho click en capturar objeto y se muestra un diálogo para crear
	// un objeto. Este formulario contiene la imagen original y la imagen
	// procesada, con el que se podrá guardar en la base de datos o descartarlo
	// ////////////////////////////////////////////////////////////////////////
	public void onCapturarClick(View v) {

		if (!bJuegoIniciado) {

			auxGray = mGray.clone();
			aux = mRgba.clone();

			Imgproc.GaussianBlur(auxGray, auxGray, new Size(3, 3), 2);
			Imgproc.Canny(auxGray, auxGray, 40, 120);

			Imgproc.GaussianBlur(aux, aux, new Size(3, 3), 2);
			Imgproc.Canny(aux, aux, 40, 120);

			Imgproc.resize(auxGray, auxGray, new Size(320, 240));
			Imgproc.resize(aux, aux, new Size(320, 240));

			float elapsedTime = FindFeatures(auxGray.getNativeObjAddr(),
					aux.getNativeObjAddr(),
					descriptores_obj.getNativeObjAddr(),
					keypoints_obj.getNativeObjAddr());

			Toast.makeText(
					this,
					"Tiempo = " + elapsedTime + "\n" + "KeyPoints = "
							+ keypoints_obj.size(), Toast.LENGTH_SHORT).show();
			if (!keypoints_obj.empty() || !descriptores_obj.empty()) {

				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.activity_dialog_objeto);
				dialog.setTitle("¿Desea guardar este objeto?");

				// set the custom dialog components - image and button
				// convert to bitmap:
				Bitmap bmModificado = Bitmap.createBitmap(aux.cols(),
						aux.rows(), Bitmap.Config.ARGB_8888);
				org.opencv.android.Utils.matToBitmap(aux, bmModificado);
				((ImageView) dialog.findViewById(R.id.imageObjeto))
						.setImageBitmap(bmModificado);

				final Bitmap bmOrigen = Bitmap.createBitmap(mRgba.cols(),
						mRgba.rows(), Bitmap.Config.ARGB_8888);
				org.opencv.android.Utils.matToBitmap(mRgba, bmOrigen);
				((ImageView) dialog.findViewById(R.id.imageObjetoOriginal))
						.setImageBitmap(bmOrigen);

				Button btnAceptar = (Button) dialog
						.findViewById(R.id.btnAceptar);
				// if button is clicked, close the custom dialog
				btnAceptar.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						String keyString, desString;
						keyString = Utilidades.keypointsToJson(keypoints_obj);
						desString = Utilidades.matToJson(descriptores_obj);
						String nombreObjeto = ((EditText) dialog
								.findViewById(R.id.edtNombre)).getText()
								.toString();
						Objeto objeto = new Objeto(-1, nombreObjeto,
								"descripcion", new Date(), keyString,
								desString, aux.cols(), aux.rows(), Juego.this
										.getString(R.string.pathImages)
										+ "/"
										+ nombreObjeto + ".png", "", "", "");
						objeto.setImagen(bmOrigen);
						dsObjetos.createObjeto(objeto);
						descriptores_obj.release();
						keypoints_obj.release();
						dialog.dismiss();
						aux.release();
						auxGray.release();
						if (objeto.getId() != -1)
							JuegoLibreria.insertaEnSerie(Juego.this, objeto,
									oEjercicioActual);
					}
				});

				Button btnCancelar = (Button) dialog
						.findViewById(R.id.btnCancelar);
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
			} else
				Toast.makeText(this,
						"Es necesario capturar de nuevo el objeto",
						Toast.LENGTH_SHORT).show();
		}

	}

	// /////////////////////////////////////////////////////////////////////
	// Se ha hecho click en el frame de la cámara y se empezará a jugar o se
	// capturará un objeto depediendo de como se ha iniciado la actividad
	// /////////////////////////////////////////////////////////////////////
	public void onFrameClick(View v) {
		if (!bVistaCapturar) {
			if (!bJuegoIniciado)
				onReconocerClick(v);
		} else
			onCapturarClick(v);
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if ((!bJuegoIniciado || (bJuegoIniciado && nObjeto == -1))
				&& !bEsperandoRespuesta) {
			mRgba = inputFrame.rgba();
			mGray = inputFrame.gray();

			if ((bJuegoIniciado && lObjetos.size() > 0)) {
				auxGray = mGray.clone();

				Imgproc.GaussianBlur(auxGray, auxGray, new Size(3, 3), 2);
				Imgproc.Canny(auxGray, auxGray, 40, 120);
				Imgproc.resize(auxGray, auxGray, new Size(320, 240));

				nObjeto = FindObjects(auxGray.getNativeObjAddr(),
						mRgba.getNativeObjAddr());

				mGray = auxGray.clone();
				auxGray.release();

				if (nObjeto != -1) {
					int id = (int) lObjetosEscenario.get(nObjeto).getId();
					bEsperandoRespuesta = true;

					oObjetoReconocido = dsObjetos.getObjetoSimple(id); // Solo
																		// id,
																		// nombre
																		// e
																		// imagen
					String respuestaAux = "";

					if (oObjetoReconocido.getId() == oObjetoActual.getId()) {
						respuestaAux = acierto;
						try {
							afd = getAssets().openFd("acierto.mp3");
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						respuestaAux = error;
						try {
							afd = getAssets().openFd("fallo.mp3");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					final String respuesta = respuestaAux;
					this.runOnUiThread(new Runnable() {
						public void run() {
							try {
								player = new MediaPlayer();
								player.setDataSource(afd.getFileDescriptor(),
										afd.getStartOffset(), afd.getLength());
								player.prepare();
								player.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
							ttobj.speak(respuesta, TextToSpeech.QUEUE_FLUSH,
									null);
							Toast.makeText(
									getApplicationContext(),
									respuesta + " - encontrado el objeto "
											+ oObjetoReconocido.getNombre(),
									Toast.LENGTH_SHORT).show();
						}
					});
					nObjeto = -1;
				}
			}
		}
		return mRgba;
	}

	@Override
	public void onPause() {
		if (dsObjetos != null)
			dsObjetos.close();
		if (dsEjercicios != null)
			dsEjercicios.close();
		if (dsResultado != null)
			dsResultado.close();
		if (ttobj != null) {
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
		if (dsObjetos != null)
			dsObjetos.open();
		if (dsEjercicios != null)
			dsEjercicios.open();
		if (dsResultado != null)
			dsResultado.open();
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
			if (bJuegoIniciado) {
				bJuegoIniciado = false;
				nObjeto = -1;
				return false;
			} else {
				finish();
				return super.onKeyDown(keyCode, event);
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	// Procesa una imagen aplicacion el algoritmo SURF
	public native float FindFeatures(long matAddrGr, long matAddrRgba,
			long matAddrDescriptores, long matAddrKeyPoints);

	// Devuelve el id del objeto encontrado por el algoritmo SURF
	public native int FindObjects(long matAddrGray, long matAddrRgba);

	// Crear todas las matrices de KeyPoints y Descriptors que se usarán para
	// encontrar objetos
	public native int RellenarObjetos(long[] descriptors, long[] keyPoints,
			int[] cols, int[] rows);

	// Libera recursos creados en la parte nativa
	public native int LiberaObjetos();

	// Inicializa los valores que usa el algoritmo SURF
	public native void InicializaSurf(double phessian, int pnOctaves,
			int pnOctaveLayers, boolean pExtended, boolean pUpright);

}
