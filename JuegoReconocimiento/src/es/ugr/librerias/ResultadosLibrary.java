package es.ugr.librerias;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import es.ugr.juegoreconocimiento.Graficas;
import es.ugr.juegoreconocimiento.R;
import es.ugr.juegoreconocimiento.Tablas;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import es.ugr.basedatos.*;
import es.ugr.objetos.*;
import es.ugr.objetos.TiposPropios.Periodo;
import es.ugr.utilidades.Sonidos;

/**
 * @author Juan Manuel Lucena Morales
 * @mail zaijua@gmail.com
 * @author Miguel Morales Rodríguez
 * @mail miguee02@gmail.com
 * 
 */
public class ResultadosLibrary {

	private Button btnSemana, btnMes, btnAnio;
	private Button btnRanking, btnAlumno;
	private View viewGrafica, viewTabla, viewXLS, viewBorrar;
	private int fecha = Periodo.Semana;
	private TableLayout tlAlumnos;
	private TableLayout tlSeries;
	private File fileExportar;
	private List<Integer> listaIdAlumnos;
	private List<Integer> listaIdSeries;
	private int radioSelec = 0;
	private List<Boolean> alSelec, serSelec;
	private AlumnoDataSource dsAlumno;
	private SerieEjerciciosDataSource dsSerie;

	private Sonidos sonidos;
	private Activity activity;
	private View view;
	private Context context;

	private int leftMargin = 7;
	private int topMargin = 7;
	private int rightMargin = 7;
	private int bottomMargin = 7;

	public void onCreate(Activity activity2) {
		activity = activity2;

		sonidos = new Sonidos(activity);
		alSelec = new ArrayList<Boolean>();
		serSelec = new ArrayList<Boolean>();

		if (view == null) {
			inicializacionBotonesTop(activity
					.findViewById(android.R.id.content));
			inicializacionBotonesBottom(activity
					.findViewById(android.R.id.content));
			inicializaLayouts(activity.findViewById(android.R.id.content));
		} else {
			inicializacionBotonesTop(view);
			inicializacionBotonesBottom(view);
			inicializaLayouts(view);
		}
		
		if (view == null)
			context = activity.findViewById(android.R.id.content).getContext();
		else
			context = view.getContext();

		inicializacionListenerBotonesUp();
		
		dsAlumno = new AlumnoDataSource(context);
		dsSerie = new SerieEjerciciosDataSource(context);
		dsAlumno.open();
		dsSerie.open();

		InicioResultados();

	}
	
	public void onDestroy() {
		if (dsAlumno != null) dsAlumno.close();
		if (dsSerie != null) dsSerie.close();
	}

	private void inicializacionListenerBotonesUp() {
		btnRanking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRanking.setSelected(true);
				btnAlumno.setSelected(false);
				radioSelec = 0;
			}
		});

		btnAlumno.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRanking.setSelected(false);
				btnAlumno.setSelected(true);
				radioSelec = 1;
			}
		});

		btnSemana.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
				fecha = Periodo.Semana;
				btnSemana.setSelected(true);
				btnMes.setSelected(false);
				btnAnio.setSelected(false);

			}
		});

		btnMes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fecha = Periodo.Mes;
				btnSemana.setSelected(false);
				btnMes.setSelected(true);
				btnAnio.setSelected(false);
			}
		});

		btnAnio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fecha = Periodo.SeisMeses;
				btnSemana.setSelected(false);
				btnMes.setSelected(false);
				btnAnio.setSelected(true);
			}
		});
	}

	private void inicializaLayouts(View view) {
		((LinearLayout) view.findViewById(R.id.layoutResultados))
				.setBackgroundResource(R.drawable.tabla);
		
		((LinearLayout) view.findViewById(R.id.resultadosButtonG1))
				.setBackgroundResource(R.drawable.layoutredondeado);

		((LinearLayout) view.findViewById(R.id.resultadosButtonG2))
				.setBackgroundResource(R.drawable.layoutredondeado);

		tlAlumnos = (TableLayout) view.findViewById(R.id.ResulTablaAl);
		tlSeries = (TableLayout) view.findViewById(R.id.ResulTablaSer);
	}

	private void inicializacionBotonesTop(View view) {
		btnSemana = (Button) view.findViewById(R.id.btnSemana);
		btnSemana.setSelected(true);

		btnMes = (Button) view.findViewById(R.id.btnMes);

		btnAnio = (Button) view.findViewById(R.id.btnAnio);

		btnRanking = (Button) view.findViewById(R.id.btnRanking);
		btnRanking.setSelected(true);

		btnAlumno = (Button) view.findViewById(R.id.btnAlumno);

	}

	private void inicializacionBotonesBottom(View view) {
		viewGrafica = view.findViewById(R.id.ResulGrafica);
		viewTabla = view.findViewById(R.id.MuestraTabla);
		viewXLS = view.findViewById(R.id.ResulXLS);
		viewBorrar = view.findViewById(R.id.ResulBorrar);
	}

	private void InicioResultados() {

		// Declaracion tableRowParams
		TableRow rowAl = new TableRow(context);
		TableRow rowSer = new TableRow(context);

		rowAl.setBackgroundResource(R.drawable.tablarestit);
		rowSer.setBackgroundResource(R.drawable.tablarestit);

		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);

		tableRowParams.setMargins(leftMargin, topMargin, rightMargin,
				bottomMargin);
		tableRowParams.gravity = Gravity.CENTER_VERTICAL;

		TableRow.LayoutParams imageParams = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);

		imageParams
				.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
		imageParams.gravity = Gravity.CENTER_VERTICAL;
		imageParams.height = 40;
		imageParams.width = 40;
		// Fin declaración TableRowParams

		TextView tit1, tit3;

		ImageView imgtit1, imgtit2;

		imgtit1 = new ImageView(context);
		imgtit2 = new ImageView(context);

		tit1 = new TextView(context);
		tit3 = new TextView(context);

		imgtit1.setImageResource(R.drawable.alumnos);
		imgtit2.setImageResource(R.drawable.series);

		imgtit1.setLayoutParams(imageParams);
		imgtit2.setLayoutParams(imageParams);

		tit1.setText("Alumno");
		tit1.setLayoutParams(tableRowParams);
		tit1.setTextAppearance(activity, R.style.TituloTabla);

		tit3.setText("Serie");
		tit3.setLayoutParams(tableRowParams);
		tit3.setTextAppearance(activity, R.style.TituloTabla);

		rowAl.addView(imgtit1);
		rowAl.addView(tit1);
		rowSer.addView(imgtit2);
		rowSer.addView(tit3);

		tlAlumnos.addView(rowAl);
		tlSeries.addView(rowSer);

		final List<Alumno> la = dsAlumno.getAllAlumnos();
		final List<SerieEjercicios> lse = dsSerie.getAllSeriesEjercicios();
		

		// Añadir campo todos
		rowAl = new TableRow(context);
		rowSer = new TableRow(context);

		// rowAl.setBackgroundResource(R.color.tabla_resaltado);
		// rowSer.setBackgroundResource(R.color.tabla_resaltado);
		rowAl.setSelected(false);
		rowAl.setBackgroundResource(R.drawable.seliconoresultados);
		rowSer.setBackgroundResource(R.drawable.seliconoresultados);

		final CheckBox cbtAl;
		final CheckBox cbtSer;
		cbtAl = new CheckBox(context);
		cbtSer = new CheckBox(context);

		TextView tvTodosAlumnos = new TextView(context);
		TextView tvTodasSeries = new TextView(context);

		tvTodosAlumnos.setLayoutParams(tableRowParams);
		tvTodasSeries.setLayoutParams(tableRowParams);

		rowAl.addView(cbtAl);
		tvTodosAlumnos.setText("Todos los alumnos");
		tvTodosAlumnos.setLayoutParams(tableRowParams);
		tvTodosAlumnos.setTextAppearance(context, R.style.TituloTabla);
		rowAl.addView(tvTodosAlumnos);

		rowSer.addView(cbtSer);
		tvTodasSeries.setText("Todas las Series");
		tvTodasSeries.setLayoutParams(tableRowParams);
		tvTodasSeries.setTextAppearance(context, R.style.TituloTabla);
		rowSer.addView(tvTodasSeries);

		rowAl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sonidos.playResultados();
				TableRow tr = (TableRow) v;
				boolean nv;
				if (tr.isSelected())
					nv = false;
				else
					nv = true;
				cbtAl.setChecked(nv);
				for (int i = 0; i < la.size(); i++) {
					TableRow traux = ((TableRow) tlAlumnos.getChildAt(i + 2));
					traux.setSelected(nv);
					CheckBox cb = (CheckBox) ((TableRow) tlAlumnos
							.getChildAt(i + 2)).getChildAt(0);
					cb.setChecked(nv);
					cb.setEnabled(tr.isSelected());
					if (nv == true)
						traux.setBackgroundResource(R.color.bloqueado);
					else
						traux.setBackgroundResource(R.drawable.seliconoresultados);
					traux.setEnabled(tr.isSelected());
					alSelec.set(i, nv);
				}
				tr.setSelected(nv);
			}
		});

		cbtAl.setClickable(false);
		cbtSer.setClickable(false);

		rowSer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sonidos.playResultados();
				TableRow tr = (TableRow) v;
				boolean nv;
				if (tr.isSelected())
					nv = false;
				else
					nv = true;
				cbtSer.setChecked(nv);
				for (int i = 0; i < lse.size(); i++) {
					TableRow traux = ((TableRow) tlSeries.getChildAt(i + 2));
					traux.setSelected(nv);
					CheckBox cb = (CheckBox) ((TableRow) tlSeries
							.getChildAt(i + 2)).getChildAt(0);
					cb.setChecked(nv);
					cb.setEnabled(tr.isSelected());
					if (nv == true)
						traux.setBackgroundResource(R.color.bloqueado);
					else
						traux.setBackgroundResource(R.drawable.seliconoresultados);
					traux.setEnabled(tr.isSelected());
					serSelec.set(i, nv);
				}
				tr.setSelected(nv);
			}
		});

		tlAlumnos.addView(rowAl);
		tlSeries.addView(rowSer);

		for (int i = 0; i < la.size(); i++) {

			rowAl = new TableRow(context);
			rowAl.setTag(i);
			rowAl.setSelected(false);
			rowAl.setBackgroundResource(R.drawable.seliconoresultados);

			rowAl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView alum = new TextView(context);
			final CheckBox cb = new CheckBox(context);
			cb.setClickable(false);
			alum.setText(la.get(i).getApellidos() + ", "
					+ la.get(i).getNombre());
			alum.setLayoutParams(tableRowParams);
			alum.setTextAppearance(context, R.style.TextoTablaResultados);
			rowAl.addView(cb);
			rowAl.addView(alum);
			alSelec.add(false);
			final int pos = i;

			rowAl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sonidos.playResultados();
					TableRow tr = (TableRow) v;
					if (tr.isSelected())
						tr.setSelected(false);
					else
						tr.setSelected(true);

					cb.setChecked(tr.isSelected());
					alSelec.set(pos, tr.isSelected());
				}
			});
			tlAlumnos.addView(rowAl);
		}

		// Series

		for (int i = 0; i < lse.size(); i++) {

			rowSer = new TableRow(context);
			rowSer.setTag(i);
			rowSer.setBackgroundResource(R.drawable.seliconoresultados);

			rowSer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			final CheckBox cb = new CheckBox(context);
			TextView serie = new TextView(context);
			serie.setText(lse.get(i).getNombre());
			serie.setLayoutParams(tableRowParams);
			serie.setTextAppearance(context, R.style.TextoTablaResultados);
			cb.setClickable(false);
			rowSer.addView(cb);
			rowSer.addView(serie);
			serSelec.add(false);
			final int pos2 = i;

			rowSer.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sonidos.playResultados();
					TableRow tr = (TableRow) v;
					if (tr.isSelected())
						tr.setSelected(false);
					else
						tr.setSelected(true);

					cb.setChecked(tr.isSelected());
					serSelec.set(pos2, tr.isSelected());
				}
			});

			tlSeries.addView(rowSer);
		}

		// Fin series

		viewGrafica.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent graficaIntent = new Intent(context, Graficas.class);
				graficaIntent.putExtra("tipoFecha", fecha);
				// Lista de alumnos seleccionados
				listaIdAlumnos = new ArrayList<Integer>();
				for (int i = 0; i < alSelec.size(); i++) {
					if (alSelec.get(i) == true)
						listaIdAlumnos.add(la.get(i).getIdAlumno());
				}

				listaIdSeries = new ArrayList<Integer>();
				for (int i = 0; i < serSelec.size(); i++) {
					if (serSelec.get(i) == true)
						listaIdSeries.add(lse.get(i).getIdSerie());
				}

				if (listaIdAlumnos.size() == 0 || listaIdSeries.size() == 0) {
					Toast.makeText(v.getContext(),
							"Seleccionar al menos un Alumno-Serie",
							Toast.LENGTH_LONG).show();
				} else {
					graficaIntent.putExtra("tipoGrafica", radioSelec);
					graficaIntent.putIntegerArrayListExtra("listaAlumnos",
							(ArrayList<Integer>) listaIdAlumnos);
					graficaIntent.putIntegerArrayListExtra("listaSeries",
							(ArrayList<Integer>) listaIdSeries);
					context.startActivity(graficaIntent);
				}
			}
		});

		viewTabla.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent tablaIntent = new Intent(context, Tablas.class);

				tablaIntent.putExtra("tipoFecha", fecha);
				// Lista de alumnos seleccionados
				listaIdAlumnos = new ArrayList<Integer>();
				for (int i = 0; i < alSelec.size(); i++) {
					if (alSelec.get(i) == true)
						listaIdAlumnos.add(la.get(i).getIdAlumno());
				}

				listaIdSeries = new ArrayList<Integer>();
				for (int i = 0; i < serSelec.size(); i++) {
					if (serSelec.get(i) == true)
						listaIdSeries.add(lse.get(i).getIdSerie());
				}

				if (listaIdAlumnos.size() == 0 || listaIdSeries.size() == 0) {
					Toast.makeText(v.getContext(),
							"Seleccionar al menos un Alumno-Serie",
							Toast.LENGTH_LONG).show();
				} else {
					tablaIntent.putExtra("tipoGrafica", radioSelec);
					tablaIntent.putIntegerArrayListExtra("listaAlumnos",
							(ArrayList<Integer>) listaIdAlumnos);
					tablaIntent.putIntegerArrayListExtra("listaSeries",
							(ArrayList<Integer>) listaIdSeries);
					context.startActivity(tablaIntent);
				}

			}
		});

		viewXLS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.dialogo_exportar);
				dialog.setTitle("Exportar...");

				Button expor, cancelar;
				final RadioButton rb1;
				final RadioButton rb2;
				rb1 = (RadioButton) dialog.findViewById(R.id.radioButtonExp1);
				rb2 = (RadioButton) dialog.findViewById(R.id.radioButtonExp2);

				expor = (Button) dialog.findViewById(R.id.aExportar);
				cancelar = (Button) dialog.findViewById(R.id.cExportar);
				expor.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						List<Alumno> listaAlumnos = new ArrayList<Alumno>();
						List<SerieEjercicios> listaSeries = new ArrayList<SerieEjercicios>();
						int tipoExport = 0;
						if (rb1.isChecked())
							tipoExport = 1;
						else {
							tipoExport = 2;

							// listaIdAlumnos=new ArrayList<Integer>();
							for (int i = 0; i < alSelec.size(); i++) {
								if (alSelec.get(i) == true)
									listaAlumnos.add(la.get(i));
							}

							// listaIdSeries=new ArrayList<Integer>();
							for (int i = 0; i < serSelec.size(); i++) {
								if (serSelec.get(i) == true)
									listaSeries.add(lse.get(i));
							}

						}
						ExportarXLS(tipoExport, listaAlumnos, listaSeries);
						dialog.dismiss();
					}
				});
				dialog.show();

				cancelar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
			}

		});

		viewBorrar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.dialogo_borrar_res);
				dialog.setTitle("Borrar resultados...");

				Button borrar, cancelar;
				final RadioButton rb1;
				final RadioButton rb2;
				rb1 = (RadioButton) dialog.findViewById(R.id.radioButtonExp1);
				rb2 = (RadioButton) dialog.findViewById(R.id.radioButtonExp2);

				borrar = (Button) dialog.findViewById(R.id.aBorrar);
				cancelar = (Button) dialog.findViewById(R.id.cBorrar);
				borrar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ResultadoDataSource rds = new ResultadoDataSource(
								context);
						rds.open();
						int borrados = 0;

						if (rb2.isChecked()) {
							for (int i = 0; i < alSelec.size(); i++) {
								if (alSelec.get(i) == true)
									for (int j = 0; j < serSelec.size(); j++) {
										if (serSelec.get(j) == true) {
											borrados = borrados
													+ rds.borrarResultadosAlumno(
															la.get(i),
															lse.get(j), fecha);
										}
									}
							}
						} else {
							borrados = rds.borraTodosResultados();
						}
						Toast.makeText(
								context,
								"Borrado(s) " + String.valueOf(borrados)
										+ " resultados.", Toast.LENGTH_LONG)
								.show();

						rds.close();

					}
				});
				cancelar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

				dialog.show();

			}
		});
	}

	// Funciones privadas para el uso de excel

	private WritableWorkbook createWorkbook(String fileName) {
		// exports must use a temp file while writing to avoid memory hogging
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setUseTemporaryFileDuringWrite(true);

		// get the sdcard's directory
		File sdCard = Environment.getExternalStorageDirectory();
		// add on the your app's path
		File dir = new File(sdCard.getAbsolutePath() + "/XLS");
		// make them in case they're not there
		boolean crea = dir.mkdirs();
		// create a standard java.io.File object for the Workbook to use
		fileExportar = new File(dir, fileName);

		WritableWorkbook wb = null;

		try {
			// create a new WritableWorkbook using the java.io.File and
			// WorkbookSettings from above
			// wb = Workbook.createWorkbook(wbfile,wbSettings);
			wb = Workbook.createWorkbook(fileExportar, wbSettings);
		} catch (IOException ex) {

		}

		return wb;
	}

	private WritableSheet createSheet(WritableWorkbook wb, String sheetName,
			int sheetIndex) {
		// create a new WritableSheet and return it
		return wb.createSheet(sheetName, sheetIndex);
	}

	/**
	 * 
	 * @param columnPosition
	 *            - column to place new cell in
	 * @param rowPosition
	 *            - row to place new cell in
	 * @param contents
	 *            - string value to place in cell
	 * @param headerCell
	 *            - whether to give this cell special formatting
	 * @param sheet
	 *            - WritableSheet to place cell in
	 * @throws RowsExceededException
	 *             - thrown if adding cell exceeds .xls row limit
	 * @throws WriteException
	 *             - Idunno, might be thrown
	 */
	private void writeCell(int columnPosition, int rowPosition,
			String contents, boolean headerCell, WritableSheet sheet)
			throws RowsExceededException, WriteException {
		// create a new cell with contents at position
		Label newCell = new Label(columnPosition, rowPosition, contents);

		if (headerCell) {
			// give header cells size 10 Arial bolded
			WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD);
			WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
			// center align the cells' contents
			headerFormat.setAlignment(Alignment.CENTRE);
			newCell.setCellFormat(headerFormat);
		}

		sheet.addCell(newCell);
	}

	void ExportarXLS(int tipoExpor, List<Alumno> la, List<SerieEjercicios> ls) {

		List<Resultado> lr = new ArrayList<Resultado>();
		String textoDesc = new String();

		ResultadoDataSource rds = new ResultadoDataSource(context);

		rds.open();

		textoDesc = new String("");

		if (tipoExpor == 1) {
			lr = rds.getAllResultados();
			textoDesc = new String("todos");

		}

		if (tipoExpor == 2) {

			switch (fecha) {
			case Periodo.Semana:
				textoDesc = "UltimaSemana";
				break;
			case Periodo.Mes:
				textoDesc = "UltimoMes";
				break;
			case Periodo.SeisMeses:
				textoDesc = "Ultimos6Meses";
				break;
			default:
				break;
			}

			if (radioSelec == 0) {

				for (int i = 0; i < ls.size(); i++)
					for (int j = 0; j < la.size(); j++) {
						List<Resultado> listaSerie = rds.getResultadosAlumno(
								la.get(j), ls.get(i), fecha);
						for (int k = 0; k < listaSerie.size(); k++)
							lr.add(listaSerie.get(k));

					}

			} else {
				for (int j = 0; j < la.size(); j++)
					for (int i = 0; i < ls.size(); i++) {
						List<Resultado> listaSerie = rds.getResultadosAlumno(
								la.get(j), ls.get(i), fecha);
						for (int k = 0; k < listaSerie.size(); k++)
							lr.add(listaSerie.get(k));

					}

			}

		}

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");

		WritableWorkbook ww = createWorkbook(textoDesc + "Resultados" + "-"
				+ sdf.format(now) + ".xls");
		WritableSheet ws = createSheet(ww, "Resultados", 0);
		try {

			writeCell(0, 0, "Nombre Alumno", true, ws);
			writeCell(1, 0, "Apellidos Alumno", true, ws);
			writeCell(2, 0, "Serie Ejercicios", true, ws);
			writeCell(3, 0, "Fecha", true, ws);
			writeCell(4, 0, "Duración", true, ws);
			writeCell(5, 0, "Nº Objetos", true, ws);
			writeCell(6, 0, "Aciertos", true, ws);
			writeCell(7, 0, "Fallos", true, ws);
			writeCell(8, 0, "Puntuación", true, ws);

			for (int i = 0; i < lr.size(); i++) {
				writeCell(0, i + 1, dsAlumno.getAlumnos(lr.get(i).getIdAlumno())
						.getNombre(), false, ws);
				writeCell(1, i + 1, dsAlumno.getAlumnos(lr.get(i).getIdAlumno())
						.getApellidos(), false, ws);
				writeCell(2, i + 1,
						dsSerie.getSerieEjercicios(lr.get(i).getIdEjercicio())
								.getNombre(), false, ws);
				writeCell(3, i + 1, lr.get(i).getFechaRealizacion_AsStrign(),
						false, ws);
				writeCell(4, i + 1, String.valueOf(lr.get(i).getDuracion()),
						false, ws);
				writeCell(5, i + 1,
						String.valueOf(lr.get(i).getNumeroObjetosReconocer()),
						false, ws);
				writeCell(6, i + 1, String.valueOf(lr.get(i).getAciertos()),
						false, ws);
				writeCell(7, i + 1, String.valueOf(lr.get(i).getFallos()),
						false, ws);
				writeCell(8, i + 1, String.valueOf(lr.get(i).getPuntuacion()),
						false, ws);

			}

		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}

		try {
			ww.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ww.close();
			Toast.makeText(context, "Creado " + fileExportar.toString(),
					Toast.LENGTH_LONG).show();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		rds.close();

	}
	
	public void setView(View rootView) {
		view = rootView;
	}


}
