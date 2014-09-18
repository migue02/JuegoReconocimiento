package es.ugr.activities;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import es.ugr.basedatos.ResultadoDataSource;
import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Resultado;
import es.ugr.utilidades.Sonidos;
import android.media.ExifInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ResultadoSerie extends Activity {

	private List<Resultado> lResultados;
	private Resultado oResultado;
	private ViewFlipper vf;
	private Sonidos sonidos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resultado_serie);

		sonidos = new Sonidos(this);
		Intent intent = getIntent();
		int[] lIdResultados = intent.getIntArrayExtra("Resultados");
		vf = (ViewFlipper) findViewById(R.id.viewFlipper);



		if (lIdResultados.length > 0) {

			if(lIdResultados.length>1)
				vf.setOnTouchListener(new ListenerTouchViewFlipper());
			lResultados = new ArrayList<Resultado>();

			ResultadoDataSource dsResultado = new ResultadoDataSource(this);
			dsResultado.open();

			for (int id : lIdResultados) {
				lResultados.add(dsResultado.getResultado(id));
			}

			dsResultado.close();

			((TextView) findViewById(R.id.lblAlumno)).setText(intent
					.getStringExtra("Alumno"));

			((TextView) findViewById(R.id.lblSerie)).setText(String
					.valueOf(intent.getStringExtra("Serie")));

			for (int i = 0; i < lResultados.size(); i++)
				creaGrafica(i,lResultados.size());

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.resultado_serie, menu);
		return true;
	}

	public void creaGrafica(int i,int size) {
		final GraphicalView mChart;

		View child = getLayoutInflater().inflate(
				R.layout.layout_resultado_serie, null);
		oResultado = lResultados.get(i);
		if (oResultado != null) {
			/*if(size==1){
				((ImageView) child.findViewById(R.id.resizq)).setVisibility(View.GONE);
				((ImageView) child.findViewById(R.id.resder)).setVisibility(View.GONE);
			}*/
			((TextView) child.findViewById(R.id.tPosicion))
					.setText("Resultado(" + String.valueOf(i + 1) + "/"
							+ lResultados.size() + ")");

			TextView puntuacion = ((TextView) child
					.findViewById(R.id.tPuntuacion));
			puntuacion.setText("PUNTUACION: "
					+ String.valueOf(oResultado.getPuntuacion()));

			TextView totales = ((TextView) child.findViewById(R.id.tTotales));
			totales.setTextColor(Color.BLUE);
			totales.setText("Total: "
					+ String.valueOf(oResultado.getAciertos()
							+ oResultado.getFallos()));

			TextView aciertos = ((TextView) child.findViewById(R.id.tAciertos));
			aciertos.setTextColor(Color.GREEN);
			aciertos.setText("Aciertos: "
					+ String.valueOf(oResultado.getAciertos()));

			TextView fallos = ((TextView) child.findViewById(R.id.tFallos));
			fallos.setTextColor(Color.RED);
			fallos.setText("Fallos: " + String.valueOf(oResultado.getFallos()));

			TextView duracion = ((TextView) child.findViewById(R.id.tDuracion));
			duracion.setText("Duración: "
					+ String.valueOf(oResultado.getDuracion()) + " minuto(s)");

			double[] values = new double[] { lResultados.get(i).getAciertos(),
					lResultados.get(i).getFallos() };
			int[] colors = new int[] { Color.GREEN, Color.RED };
			DefaultRenderer renderer = buildCategoryRenderer(colors);
			renderer.setChartTitleTextSize(20);
			renderer.setDisplayValues(true);
			renderer.setShowLabels(true);
			renderer.setLabelsColor(Color.GRAY);
			renderer.setPanEnabled(false);
			renderer.setZoomEnabled(false);
			SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
			r.setHighlighted(true);

			mChart = (GraphicalView) ChartFactory.getPieChartView(this,
					buildCategoryDataset("Project budget", values), renderer);

			FrameLayout fl = (FrameLayout) child
					.findViewById(R.id.layoutGrafica);

			fl.addView(mChart);
			vf.addView(child);

		}

	}

	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 5, 5, 5, 5 });
		renderer.setDisplayValues(true);
		renderer.setTextTypeface(Typeface.MONOSPACE);
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected CategorySeries buildCategoryDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		series.add("Aciertos", values[0]);
		series.add("Fallos", values[1]);
		return series;
	}

	public void onAceptarClick(View v) {
		finish();
	}

	private class ListenerTouchViewFlipper implements View.OnTouchListener {
		private float init_x;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // Cuando el usuario toca la pantalla
											// por primera vez
				init_x = event.getX();
				return true;
			case MotionEvent.ACTION_UP: // Cuando el usuario deja de presionar
				float distance = init_x - event.getX();

				if (distance > 0) {
					vf.setInAnimation(inFromRightAnimation());
					vf.setOutAnimation(outToLeftAnimation());
					vf.showPrevious();
					sonidos.playDrop();
				}

				if (distance < 0) {
					vf.setInAnimation(inFromLeftAnimation());
					vf.setOutAnimation(outToRightAnimation());
					vf.showNext();
					sonidos.playDrop();
				}

			default:
				break;
			}

			return false;
		}

	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());

		return inFromRight;

	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

}
