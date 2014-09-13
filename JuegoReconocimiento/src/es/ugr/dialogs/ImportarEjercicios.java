package es.ugr.dialogs;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.parseadorXML.EjercicioParser;
import es.ugr.parseadorXML.EjerciciosMarker;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class ImportarEjercicios extends Dialog {

	private Context context;
	private EjercicioDataSource dsEjercicio;

	private EditText edtFichero, edtURL;
	private Runnable function;
	
	private Button btnAceptar, btnCancelar, btnSelFich;

	public ImportarEjercicios(Context context, EjercicioDataSource dsEjercicio,
			Runnable function) {
		super(context);
		this.context = context;
		this.dsEjercicio = dsEjercicio;
		this.function = function;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_importar_ej);
		setTitle("Importar Ejercicio desde...");

		edtURL = ((EditText) findViewById(R.id.editTextURL));
		edtFichero = ((EditText) findViewById(R.id.editTextFich));
		edtURL.setText("http://tamen.ugr.es/~diversidad/XML/segun.xml");
		
		btnAceptar = (Button) findViewById(R.id.aImportar);
		btnCancelar = (Button) findViewById(R.id.cImportar);
		btnSelFich = (Button) findViewById(R.id.BotonSelFich);
		
		btnAceptar.setOnClickListener(onImportarClick);
		btnCancelar.setOnClickListener(onCancelarClick);
		btnSelFich.setOnClickListener(onSeleccionaClick);
	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);
	}
	
	View.OnClickListener onSeleccionaClick = new View.OnClickListener() {
		public void onClick(View v) {
			FileChooserDialog dialog = new FileChooserDialog(context);
			dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {

				@Override
				public void onFileSelected(Dialog source, File folder, String name) {
					source.hide();
					Toast toast = Toast.makeText(source.getContext(),
							"File created: " + folder.getName() + "/" + name,
							Toast.LENGTH_LONG);
					toast.show();
				}

				@Override
				public void onFileSelected(Dialog source, File file) {
					source.hide();
					Toast toast = Toast.makeText(source.getContext(),
							"Fichero Seleccionado: " + file.getPath(),
							Toast.LENGTH_LONG);
					toast.show();
					edtFichero.setText(file.getPath());
				}
			});

			dialog.show();
		}
	};

	View.OnClickListener onImportarClick = new View.OnClickListener() {
		public void onClick(View v) {
			RetrieveFeed task = new RetrieveFeed();
			if (((RadioButton) findViewById(R.id.radioButtonExp1)).isChecked())
				task.execute(edtFichero.getText().toString(), "Fichero");
			else
				task.execute(edtURL.getText().toString(), "URL");
			dismiss();
		}
	};

	View.OnClickListener onCancelarClick = new View.OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};
	

	private class RetrieveFeed extends AsyncTask<String, Integer, Boolean> {

		public List<EjerciciosMarker> ListaEj;

		protected Boolean doInBackground(String... params) {

			EjercicioParser ejercicioparser = new EjercicioParser(params[0],
					params[1]);
			ListaEj = ejercicioparser.parse();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			for (int i = 0; i < ListaEj.size(); i++) {
				dsEjercicio.createEjercicio(ListaEj.get(i).getNombre(),
						new Date(), ListaEj.get(i).getEscenario(),
						ListaEj.get(i).getDescripcion(), ListaEj.get(i)
								.getDuracion(), ListaEj.get(i).getReconocer(),
						ListaEj.get(i).getSonidoDescripcion());
			}
			function.run();
		}

	}

}
