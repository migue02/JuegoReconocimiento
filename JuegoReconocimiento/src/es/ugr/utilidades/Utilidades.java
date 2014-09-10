package es.ugr.utilidades;

import java.util.ArrayList;
import java.util.List;
import es.ugr.objetos.Ejercicio;
import es.ugr.objetos.Objeto;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilidades {

	public static boolean hasInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}
		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}
		return false;
	}

	public static ArrayList<Objeto> copiaObjetos(ArrayList<Objeto> plObjeto) {
		ArrayList<Objeto> clonedList = new ArrayList<Objeto>(plObjeto.size());
		for (Objeto objeto : plObjeto) {
			clonedList.add(new Objeto(objeto));
		}
		return clonedList;
	}

	public static ArrayList<Ejercicio> copiaEjercicios(
			ArrayList<Ejercicio> plEjercicio) {
		ArrayList<Ejercicio> clonedList = new ArrayList<Ejercicio>(
				plEjercicio.size());
		for (Ejercicio ejercicio : plEjercicio) {
			clonedList.add(new Ejercicio(ejercicio));
		}
		return clonedList;
	}

	public static void LiberaImagenes(List<Objeto> plObjetos) {
		try {
			for (Objeto objeto : plObjetos)
				objeto.liberaImagen();
		} catch (Exception e) {
		}
	}

}
