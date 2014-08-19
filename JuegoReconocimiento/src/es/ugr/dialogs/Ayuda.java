package es.ugr.dialogs;

import es.ugr.juegoreconocimiento.R;
import es.ugr.utilidades.Globals;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Ayuda extends Dialog{

	private int n;
	
	public Ayuda(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public Ayuda(Context context, int nFragment){
		super(context);
		n=nFragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogo_ayuda);
		
		ImageView imagen=(ImageView)findViewById(R.id.imagenAyuda);
		TextView tit=(TextView)findViewById(R.id.tituloAyuda);
		TextView tv=(TextView)findViewById(R.id.textoAyuda);
		String titulo,st;
		setTitle("Ayuda");
		
		
		switch (n) {
		case 1:
			titulo="Gestión Alumnos";
			tit.setTextColor(getContext().getResources().getColor(R.color.verde_ic));
			tit.setText(titulo);
			st="En esta ventana se muestran la lista de alumnos. \n\nHaga click sobre alguno de ellos para mostrar más información o modificarlos.\n\nTambién puede ordenarlos deslizando el botón.";
			tv.setText(st);
			imagen.setImageResource(R.drawable.alumnos_64);
			break;
			
		case 2:
			titulo="Resultados";
			tit.setTextColor(getContext().getResources().getColor(R.color.amarillo_ic));
			tit.setText(titulo);
			st="En esta ventana se muestran los resultados. \n\n"
					+ "Seleccione el filtrado por tiempo que desee: Esta semana, este mes o últimos 6 meses.\n"
					+ "Seleccione que tipos de datos desea observar, Ranking por ejercicio, o resultados de un alumno.\n"
					+ "En la tabla de alumnos, debe seleccionar los alumnos a mostrar información, al igual que con las series de ejercicios.\n"
					+ "Cuando haya finalizado el filtrado, seleccione que desea realizar con estos datos: Borrarlos, exportarlos a .XLS, representar gráficas o ver los resultados en forma de tabla.";
			tv.setText(st);
			imagen.setImageResource(R.drawable.resultados_64);
			break;
			
		case 3:
			titulo="Ejercicios";
			tit.setTextColor(getContext().getResources().getColor(R.color.rojo_ic));
			tit.setText(titulo);
			st="En esta ventana se muestran los ejercicios. \n\n"
					+ "En la barra superior tiene los siguientes botones:\n\n"
					+ "\tSincronizar ejercicios: Pulse para sincronizar los ejercicios con el servidor remoto.\n"
					+ "\tImportar ejercicios: Pulse para importar los ejercicios desde un fichero local o página WEB.\n\n"
					+ "Pulse sobre cualquier ejercicio para obtener más información sobre cada ejercicio, o modificar la duración estimada del ejercicio.\n"
					+ "Arraste el botón mover para ordenar los ejercicios a su gusto.";
			tv.setText(st);
			imagen.setImageResource(R.drawable.ejercicios_64);
			break;
			
			
		case 4:
			titulo="Serie Ejercicios";
			tit.setTextColor(getContext().getResources().getColor(R.color.azul_ic));
			tit.setText(titulo);
			st="En esta ventana se muestran los Serie de ejercicios. \n\n"
					+ "En la barra superior tiene el botón: Añadir Serie, para crear una nueva serie de ejercicios de cero.\n"
					+ "Pulse sobre cualquier serie de ejercicios para obtener más información sobre cada serie de ejercicios, o modificar sus atributos.\n"
					+ "Arraste el botón mover para ordenar las series a su gusto.";
			tv.setText(st);
			imagen.setImageResource(R.drawable.series_64);
			break;
			
			
		case 5:
			titulo="Objetos";
			tit.setTextColor(getContext().getResources().getColor(R.color.naranja_ic));
			tit.setText(titulo);
			st="En esta ventana se muestran los Objetos. \n\n"
					+ "En la barra superior tiene el botón: Añadir Sincronizar ejercicios, para crear traer los objetos guardados en el servidor remoto, y subir los objetos que tiene en este dispositivo que no se encuentren en el servidor remoto.\n"
					+ "Pulse sobre cualquier objeto para obtener más información sobre el ejercicio y sus atributos.\n";
			
			tv.setText(st);
			imagen.setImageResource(R.drawable.objetos_64);
			break;
			
		default:
			break;
		}


		
		
		
	}
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
}
