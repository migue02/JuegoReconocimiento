package es.ugr.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import es.ugr.basedatos.AlumnoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Alumno;
import es.ugr.objetos.TiposPropios.Sexo;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FichaAlumno extends Dialog {

	private Context context;
	private Alumno alumno;
	private AlumnoDataSource dsAlumno;
	private Runnable function;
	private boolean insertar;
	private Sexo sexo;

	// Captacion de los componentes
	private Button GuardarDia, CancelarDialog;
	private EditText edtNombre;
	private EditText edtApellidos;
	private EditText edtObservaciones;
	private Button Chico, Chica;
	private ImageView imgSexo;
	private TextView tvFecha;
	private DatePickerDialog dtpFecha;
	private Button btnFecha;

	public FichaAlumno(Context context, AlumnoDataSource dsAlumno,
			Alumno alumno, boolean esNuevo, Runnable function) {
		super(context);
		this.context = context;
		this.dsAlumno = dsAlumno;
		this.alumno = alumno;
		this.function = function;
		this.insertar = esNuevo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.getWindow().setBackgroundDrawableResource(R.drawable.background);
		setContentView(R.layout.dialogo_alumnos);

		if (!insertar)
			setTitle("Modificar Alumno");
		else
			setTitle("Crear Alumno");

		edtNombre = (EditText) findViewById(R.id.daNombre);
		edtApellidos = (EditText) findViewById(R.id.daApellidos);
		tvFecha = (TextView) findViewById(R.id.MuestraFecha);
		edtObservaciones = (EditText) findViewById(R.id.daObserva);
		Chico = (Button) findViewById(R.id.pulsaChico);
		Chica = (Button) findViewById(R.id.pulsaChica);

		GuardarDia = (Button) findViewById(R.id.gAlumnos);
		CancelarDialog = (Button) findViewById(R.id.cAlumnos);
		imgSexo = (ImageView) findViewById(R.id.AlumPrin);

		// Asignacion de los valores del tipo alumno
		edtNombre.setText(alumno.getNombre());
		edtApellidos.setText(alumno.getApellidos());
		tvFecha.setText(alumno.getFecha_nac_AsStrign());
		edtObservaciones.setText(alumno.getObservaciones());

		Chico.setSelected(alumno.getSexo() == Sexo.Hombre);
		Chica.setSelected(alumno.getSexo() == Sexo.Mujer);
		if (alumno.getSexo() == Sexo.Hombre) {
			imgSexo.setImageDrawable(getContext().getResources().getDrawable(
					R.drawable.boy_amp));
			sexo = Sexo.Hombre;
		} else {
			imgSexo.setImageDrawable(getContext().getResources().getDrawable(
					R.drawable.girl_amp));
			sexo = Sexo.Mujer;
		}

		// Controlador Fecha
		btnFecha = (Button) findViewById(R.id.CambiarFecha);
		dtpFecha = null;

		GuardarDia.setOnClickListener(onGuardarClick);
		CancelarDialog.setOnClickListener(onCancelarClick);
		Chica.setOnTouchListener(onChicaTouch);
		Chico.setOnTouchListener(onChicoTouch);

		btnFecha.setOnClickListener(onFechaClick);

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

	View.OnTouchListener onChicoTouch = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Chico.setSelected(true);
			Chica.setSelected(false);
			((BitmapDrawable) imgSexo.getDrawable()).getBitmap().recycle(); 
			imgSexo = (ImageView) findViewById(R.id.AlumPrin);
			imgSexo.setImageDrawable(getContext().getResources().getDrawable(
					R.drawable.boy_amp));
			sexo = Sexo.Hombre;
			return false;
		}
	};

	View.OnTouchListener onChicaTouch = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Chica.setSelected(true);
			Chico.setSelected(false);
			((BitmapDrawable) imgSexo.getDrawable()).getBitmap().recycle();
			imgSexo = (ImageView) findViewById(R.id.AlumPrin);
			imgSexo.setImageDrawable(getContext().getResources().getDrawable(
					R.drawable.girl_amp));
			sexo = Sexo.Mujer;
			return false;
		}
	};

	View.OnClickListener onCancelarClick = new View.OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};

	View.OnClickListener onGuardarClick = new View.OnClickListener() {
		public void onClick(View v) {

			SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
					"dd/MM/yyyy");
			Date fecha = null;
			try {

				fecha = formatoDelTexto.parse(tvFecha.getText().toString());

			} catch (ParseException ex) {

				ex.printStackTrace();

			}

			if (edtNombre.getText().toString().equals(""))
				DialogoCamposNecesarios(1);
			else if (edtApellidos.getText().toString().equals(""))
				DialogoCamposNecesarios(2);
			else if (fecha.compareTo(new Date()) > 0)
				DialogoCamposNecesarios(3);
			else {

				alumno.setNombre(edtNombre.getText().toString());
				alumno.setApellidos(edtApellidos.getText().toString());
				alumno.setObservaciones(edtObservaciones.getText().toString());
				alumno.setSexo(sexo);
				alumno.setFecha_nac(fecha);

				// Si es modificar
				boolean correcto = false;
				if (insertar == false) {
					correcto = dsAlumno.modificaAlumno(alumno);
					if (correcto == true)
						Toast.makeText(context, "Alumno modificado",
								Toast.LENGTH_SHORT).show();
				} else {
					// Si es insertar
					correcto = dsAlumno.createAlumno(alumno) != null;
					if (correcto == true)
						Toast.makeText(context, "Alumno creado",
								Toast.LENGTH_SHORT).show();
				}
				dismiss();
				function.run();

			}
		}
	};

	private class PickDate implements DatePickerDialog.OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			view.updateDate(year, monthOfYear, dayOfMonth);
			int mes = monthOfYear + 1;
			tvFecha.setText(dayOfMonth + "/" + mes + "/" + year);
			dtpFecha.hide();
		}

	}

	View.OnClickListener onFechaClick = new View.OnClickListener() {
		public void onClick(View v) {
			Calendar dtTxt = null;
			String preExistingDate = (String) tvFecha.getText().toString();
			if (preExistingDate != null && !preExistingDate.equals("")) {
				StringTokenizer st = new StringTokenizer(preExistingDate, "/");
				String initialDate = st.nextToken();
				String initialMonth = st.nextToken();
				String initialYear = st.nextToken();
				if (dtpFecha == null)
					dtpFecha = new DatePickerDialog(getContext(),
							new PickDate(), Integer.parseInt(initialYear),
							Integer.parseInt(initialMonth) - 1,
							Integer.parseInt(initialDate));
				dtpFecha.updateDate(Integer.parseInt(initialYear),
						Integer.parseInt(initialMonth) - 1,
						Integer.parseInt(initialDate));
			} else {
				dtTxt = Calendar.getInstance();
				if (dtpFecha == null)
					dtpFecha = new DatePickerDialog(getContext(),
							new PickDate(), dtTxt.get(Calendar.YEAR),
							dtTxt.get(Calendar.MONTH),
							dtTxt.get(Calendar.DAY_OF_MONTH));
				dtpFecha.updateDate(dtTxt.get(Calendar.YEAR),
						dtTxt.get(Calendar.MONTH),
						dtTxt.get(Calendar.DAY_OF_MONTH));
			}
			dtpFecha.show();
		}
	};

	private void DialogoCamposNecesarios(int coderr) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Atención");
		if (coderr == 1)
			builder.setMessage("Debe rellenar el campo Nombre");
		if (coderr == 2)
			builder.setMessage("Debe rellenar el campo Apellidos");
		if (coderr == 3)
			builder.setMessage("Debe introducir una Fecha de Nacimiento correcta");
		builder.setPositiveButton("Aceptar", null);
		builder.create();
		builder.show();

	}
}
