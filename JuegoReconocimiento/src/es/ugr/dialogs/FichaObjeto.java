package es.ugr.dialogs;

import java.io.File;
import java.io.IOException;

import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Objeto;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.utilidades.Ficheros;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class FichaObjeto extends Dialog {

	private Objeto oObjeto;
	private Context context;
	private ImageButton btnAyuda, btnAyudaArchivo, btnAyudaGrabar;
	private ImageButton btnNombre, btnNombreArchivo, btnNombreGrabar;
	private ImageButton btnDescripcion, btnDescripcionArchivo, btnDescripcionGrabar;

	private boolean mStartRecording = true;
	private MediaRecorder mRecorder = null;

	private Integer ayuda = 0, nombre = 1, descripcion = 2;

	public FichaObjeto(Context context, Objeto objeto) {
		super(context);
		this.context = context;
		oObjeto = objeto;
		oObjeto.creaFicherosSonido(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ficha_objeto);

		((TextView) findViewById(R.id.edtNombreObjeto)).setText(oObjeto
				.getNombre());
		((TextView) findViewById(R.id.edtDescripcion)).setText(oObjeto
				.getDescripcion());

		if (oObjeto.getImagen() != null) {
			((ImageView) findViewById(R.id.imgObjeto)).setImageBitmap(oObjeto
					.getImagen());
		}

		btnAyuda = (ImageButton) findViewById(R.id.btnAyuda);
		btnDescripcion = (ImageButton) findViewById(R.id.btnDescripcion);
		btnNombre = (ImageButton) findViewById(R.id.btnNombre);

		btnAyuda.setTag(ayuda);
		btnNombre.setTag(nombre);
		btnDescripcion.setTag(descripcion);

		btnAyudaArchivo = (ImageButton) findViewById(R.id.btnAyudaArchivo);
		btnNombreArchivo = (ImageButton) findViewById(R.id.btnNombreArchivo);
		btnDescripcionArchivo = (ImageButton) findViewById(R.id.btnDescripcionArchivo);

		btnAyudaArchivo.setTag(ayuda);
		btnNombreArchivo.setTag(nombre);
		btnDescripcionArchivo.setTag(descripcion);

		btnAyudaGrabar = (ImageButton) findViewById(R.id.btnAyudaGrabar);
		btnNombreGrabar = (ImageButton) findViewById(R.id.btnNombreGrabar);
		btnDescripcionGrabar = (ImageButton) findViewById(R.id.btnDescripcionGrabar);

		btnAyudaGrabar.setTag(ayuda);
		btnNombreGrabar.setTag(nombre);
		btnDescripcionGrabar.setTag(descripcion);

		btnAyudaGrabar.setOnClickListener(onGrabarClick);
		btnNombreGrabar.setOnClickListener(onGrabarClick);
		btnDescripcionGrabar.setOnClickListener(onGrabarClick);

		btnAyudaArchivo.setOnClickListener(onArchivoClick);
		btnNombreArchivo.setOnClickListener(onArchivoClick);
		btnDescripcionArchivo.setOnClickListener(onArchivoClick);

		btnAyuda.setOnClickListener(onEscucharClick);
		btnNombre.setOnClickListener(onEscucharClick);
		btnDescripcion.setOnClickListener(onEscucharClick);

	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);
		setTitle(oObjeto.getNombre());
	}

	private ImageButton getButtonGrabar(Object tag) {
		if (tag.equals(ayuda)) {
			return btnAyudaGrabar;
		} else if (tag.equals(nombre)) {
			return btnNombreGrabar;
		} else if (tag.equals(descripcion)) {
			return btnDescripcionGrabar;
		} else
			return null;
	}

	View.OnClickListener onGrabarClick = new View.OnClickListener() {
		public void onClick(View v) {
			String fileName = "";
			if (v.getTag().equals(ayuda)) {
				fileName = oObjeto.getSonidoAyuda();
			} else if (v.getTag().equals(nombre)) {
				fileName = oObjeto.getSonidoNombre();
			} else if (v.getTag().equals(descripcion)) {
				fileName = oObjeto.getSonidoDescripcion();
			}
			onRecord(mStartRecording, fileName);
			if (mStartRecording) {
				getButtonGrabar(v.getTag()).setImageResource(
						R.drawable.microfono_rojo);
			} else {
				getButtonGrabar(v.getTag()).setImageResource(
						R.drawable.microfono);
			}
			mStartRecording = !mStartRecording;
		}
	};

	View.OnClickListener onArchivoClick = new View.OnClickListener() {
		public void onClick(View v) {
			final Object tag = v.getTag();
			FileChooserDialog dialog = new FileChooserDialog(context);
			dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {

				@Override
				public void onFileSelected(Dialog source, File file) {					
					String filenameArray[] = file.getName().split("\\.");
					String extension = filenameArray[filenameArray.length - 1];
					if (extension.equals("mp3")) {
						source.hide();

						String fileName = "";
						if (tag.equals(ayuda))
							fileName = "ayuda" + oObjeto.getNombre() + ".mp3";
						else if (tag.equals(nombre))
							fileName = "descripcion" + oObjeto.getNombre()
									+ ".mp3";
						else if (tag.equals(descripcion))
							fileName = "nombre" + oObjeto.getNombre() + ".mp3";

						if (Ficheros.copiaFicheros(
								file.getAbsolutePath(),
								context.getString(R.string.pathSounds) + "/"
										+ fileName)) {
							fileName = context.getString(R.string.pathSounds)
									+ "/" + fileName;
							if (tag.equals(ayuda))
								oObjeto.setSonidoAyuda(fileName);
							else if (tag.equals(nombre))
								oObjeto.setSonidoNombre(fileName);
							else if (tag.equals(descripcion))
								oObjeto.setSonidoDescripcion(fileName);
						}
					} else
						Toast.makeText(source.getContext(),
								"Debe seleccionar un fichero MP3",
								Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onFileSelected(Dialog source, File folder,
						String name) {
				}
				
			});

			dialog.show();
			
		}
	};

	View.OnClickListener onEscucharClick = new View.OnClickListener() {
		public void onClick(View v) {
			if (v.getTag().equals(ayuda)) {
				oObjeto.playSonidoAyuda(context);
			} else if (v.getTag().equals(nombre)) {
				oObjeto.playSonidoNombre(context);
			} else if (v.getTag().equals(descripcion)) {
				oObjeto.playSonidoDescripcion(context);
			}				
		}
	};

	private void onRecord(boolean start, String fileName) {
		if (start) {			
			if (Ficheros.ExisteFichero(fileName))
				Ficheros.CreaFichero(fileName);
			startRecording(fileName);
		} else {
			stopRecording();
		}
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	private void startRecording(String fileName) {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(fileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Toast.makeText(context, "prepare() failed", Toast.LENGTH_SHORT)
					.show();
		}

		mRecorder.start();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		oObjeto.stopSonido();
	}

}
