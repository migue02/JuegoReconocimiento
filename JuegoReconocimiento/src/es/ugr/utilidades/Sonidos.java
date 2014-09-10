package es.ugr.utilidades;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class Sonidos {

	private Context contexto;
	private AssetFileDescriptor afdNavegacion, afdDrop, afdRow, afdRemove,
			afdResultados, afdContador;
	private MediaPlayer playerNavegacion, playerDrop, playerRow, playerRemove,
			playerResultados, playerContador;

	public Sonidos(Context contexto) {
		this.contexto = contexto;

		try {
			// Sonido barra navegacion
			afdNavegacion = contexto.getAssets().openFd("click1.mp3");
			playerNavegacion = new MediaPlayer();
			playerNavegacion.setDataSource(afdNavegacion.getFileDescriptor(),
					afdNavegacion.getStartOffset(), afdNavegacion.getLength());
			playerNavegacion.prepare();

			// Sonido soltar
			afdDrop = contexto.getAssets().openFd("drop1.mp3");
			playerDrop = new MediaPlayer();
			playerDrop.setDataSource(afdDrop.getFileDescriptor(),
					afdDrop.getStartOffset(), afdDrop.getLength());
			playerDrop.prepare();

			// Sonido click en fila
			afdRow = contexto.getAssets().openFd("clickTabla.mp3");
			playerRow = new MediaPlayer();
			playerRow.setDataSource(afdRow.getFileDescriptor(),
					afdRow.getStartOffset(), afdRow.getLength());
			playerRow.prepare();

			// Sonido eliminar fila
			afdRemove = contexto.getAssets().openFd("removeTabla.mp3");
			playerRemove = new MediaPlayer();
			playerRemove.setDataSource(afdRemove.getFileDescriptor(),
					afdRemove.getStartOffset(), afdRemove.getLength());
			playerRemove.prepare();

			afdResultados = contexto.getAssets().openFd("clickResultados.mp3");
			playerResultados = new MediaPlayer();
			playerResultados.setDataSource(afdResultados.getFileDescriptor(),
					afdResultados.getStartOffset(), afdResultados.getLength());
			playerResultados.prepare();

			afdContador = contexto.getAssets().openFd("contador.mp3");
			playerContador = new MediaPlayer();
			playerContador.setDataSource(afdContador.getFileDescriptor(),
					afdContador.getStartOffset(), afdContador.getLength());
			playerContador.prepare();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playNavegacion() {
		try {
			playerNavegacion.start();
		} catch (Exception e) {
		}
	}

	public void playDrop() {
		try {
			playerDrop.start();
		} catch (Exception e) {
		}
	}

	public void playClickRow() {
		try {
			playerRow.start();
		} catch (Exception e) {
		}
	}

	public void playRemove() {
		try {
			playerRemove.start();
		} catch (Exception e) {
		}
	}

	public void playResultados() {
		try {
			playerResultados.start();
		} catch (Exception e) {
		}
	}

	public void playContador() {
		try {
			playerContador.start();
		} catch (Exception e) {
		}
	}

}
