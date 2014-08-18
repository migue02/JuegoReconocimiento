package es.ugr.dialogs;

import es.ugr.basedatos.EjercicioDataSource;
import es.ugr.basedatos.ObjetoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FichaEjercicio extends Dialog {

	private Context context;
	private EjercicioDataSource dsEjercicio;
	private Ejercicio oEjercicio;

	private EditText duracion;
	private TextView descripcion, escenario;
	private Runnable function;

	public FichaEjercicio(Context context, EjercicioDataSource dsEjercicio,
			Ejercicio ejercicio, Runnable function) {
		super(context);
		this.context = context;
		this.dsEjercicio = dsEjercicio;
		this.oEjercicio = ejercicio;
		this.function = function;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_ejercicios);
		setTitle(oEjercicio.getNombre());

		duracion = (EditText) findViewById(R.id.DuracionEj);
		duracion.setText(String.valueOf(oEjercicio.getDuracion()));
		descripcion = (TextView) findViewById(R.id.textDesc);
		descripcion.setText(oEjercicio.getDescripcion());
		escenario = (TextView) findViewById(R.id.textEscenario);
		escenario.setTextSize(20);

		// Table Layout dentro dialogo
		TableLayout tablaObjetos = (TableLayout) findViewById(R.id.tablaDiaEjercicios);
		tablaObjetos.removeAllViews();

		TableRow filaObj;
		TextView te1, te2;

		ObjetoDataSource ods = new ObjetoDataSource(context);
		ods.open();

		Objeto obj = new Objeto();

		String textEscenario = new String("");
		for (int i = 0; i < oEjercicio.getObjetos().size(); i++) {
			obj = ods.getObjeto(oEjercicio.getObjetos().get(i));
			textEscenario = textEscenario + obj.getNombre();
			if (i != oEjercicio.getObjetos().size() - 1)
				textEscenario = textEscenario + " ,";
		}
		escenario.setText(textEscenario);

		// Para cada ejercicio
		for (int j = 0; j < oEjercicio.getObjetosReconocer().size(); j++) {
			filaObj = new TableRow(context);

			te1 = new TextView(context);
			te1.setText(String.valueOf(j + 1));
			te1.setPadding(2, 0, 5, 0);
			te1.setTextSize(20);

			te2 = new TextView(context);
			obj = ods.getObjeto(oEjercicio.getObjetosReconocer().get(j));
			te2.setText(obj.getNombre());
			te2.setPadding(2, 0, 5, 0);
			te2.setTextSize(20);

			filaObj.addView(te1);
			filaObj.addView(te2);

			tablaObjetos.addView(filaObj);
		}

		ods.close();

		ImageButton guardarSerie = (ImageButton) findViewById(R.id.guardarDiaEj);
		guardarSerie.setBackgroundResource(R.drawable.sel_icono);
		guardarSerie.setOnClickListener(onGuardarClick);

	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);
	}

	View.OnClickListener onGuardarClick = new View.OnClickListener() {
		public void onClick(View v) {
			oEjercicio.setDuracion(Integer.parseInt(duracion.getText()
					.toString()));
			dsEjercicio.modificaEjercicio(oEjercicio);
			function.run();
		}
	};

}
