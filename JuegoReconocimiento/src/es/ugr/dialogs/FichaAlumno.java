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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
	private ImageButton imgChico, imgChica;
	private ImageView imgPincipal;
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
		setContentView(R.layout.dialogo_alumnos);

		if (!insertar)
			setTitle("Modificar Alumno");
		else
			setTitle("Crear Alumno");

		edtNombre = (EditText) findViewById(R.id.daNombre);
		edtApellidos = (EditText) findViewById(R.id.daApellidos);
		tvFecha = (TextView) findViewById(R.id.MuestraFecha);
		edtObservaciones = (EditText) findViewById(R.id.daObserva);
		imgChico = (ImageButton) findViewById(R.id.BotonHombre);
		imgChica = (ImageButton) findViewById(R.id.BotonMujer);
		GuardarDia = (Button) findViewById(R.id.gAlumnos);
		CancelarDialog = (Button) findViewById(R.id.cAlumnos);
		imgPincipal = (ImageView) findViewById(R.id.AlumPrin);

		// Asignacion de los valores del tipo alumno
		edtNombre.setText(alumno.getNombre());
		edtApellidos.setText(alumno.getApellidos());
		tvFecha.setText(alumno.getFecha_nac_AsStrign());
		edtObservaciones.setText(alumno.getObservaciones());

		
		imgChico.setSelected(alumno.getSexo()==Sexo.Hombre);
		imgChica.setSelected(alumno.getSexo()==Sexo.Mujer);
		if (alumno.getSexo() == Sexo.Hombre){
			imgPincipal.setImageResource(R.drawable.boy_amp);
			sexo = Sexo.Hombre;
		}else{
			imgPincipal.setImageResource(R.drawable.girl_amp);
			sexo = Sexo.Mujer;
		}
			
		
		
		// Controlador Fecha
		btnFecha = (Button) findViewById(R.id.CambiarFecha);
		dtpFecha = null;

		GuardarDia.setOnClickListener(onGuardarClick);
		CancelarDialog.setOnClickListener(onCancelarClick);
		imgChica.setOnClickListener(onChicaClick);
		imgChico.setOnClickListener(onChicoClick);
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

	View.OnClickListener onChicoClick = new View.OnClickListener() {
		public void onClick(View v) {
			imgChico.setSelected(true);
			imgChica.setSelected(false);
			imgPincipal.setImageResource(R.drawable.boy_amp);
			sexo = Sexo.Hombre;
		}
	};

	View.OnClickListener onChicaClick = new View.OnClickListener() {
		public void onClick(View v) {
			imgChica.setSelected(true);
			imgChico.setSelected(false);
			imgPincipal.setImageResource(R.drawable.girl_amp);
			sexo = Sexo.Mujer;
		}
	};

	View.OnClickListener onCancelarClick = new View.OnClickListener() {
		public void onClick(View v) {
			dismiss();
		}
	};

	View.OnClickListener onGuardarClick = new View.OnClickListener() {
		public void onClick(View v) {
			alumno.setNombre(edtNombre.getText().toString());
			alumno.setApellidos(edtApellidos.getText().toString());
			alumno.setObservaciones(edtObservaciones.getText().toString());
			alumno.setSexo(sexo);
			SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
					"dd/MM/yyyy");
			Date fecha = null;
			try {

				fecha = formatoDelTexto.parse(tvFecha.getText().toString());

			} catch (ParseException ex) {

				ex.printStackTrace();

			}
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
					Toast.makeText(context, "Alumno creado", Toast.LENGTH_SHORT)
							.show();
			}
			dismiss();
			function.run();
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

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
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
}
