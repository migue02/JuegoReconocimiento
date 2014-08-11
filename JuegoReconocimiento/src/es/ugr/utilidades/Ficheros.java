package es.ugr.utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import es.ugr.juegoreconocimiento.R;

public class Ficheros {

	public static boolean copiaFicheros(String pathOrigen, String pathDestino) {
		InputStream in = null;
		OutputStream out = null;
		try {
			File inFile = new File(pathOrigen);
			in = new FileInputStream(inFile);
			File outFile = new File(pathDestino);
			out = new FileOutputStream(outFile);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch (IOException e) {
			Log.e("tag", "Failed to copy file: " + pathOrigen, e);
			return false;
		}
	}

	public static void copyAssets(Context context) {
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			if (filename.contains("descripcion") || filename.contains("ayuda")
					|| filename.contains("nombre")) {
				InputStream in = null;
				OutputStream out = null;
				try {
					in = assetManager.open(filename);
					File outFile = new File(
							context.getString(R.string.pathSounds), "/"
									+ filename);
					out = new FileOutputStream(outFile);
					copyFile(in, out);
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} catch (IOException e) {
					Log.e("tag", "Failed to copy asset file: " + filename, e);
				}
			}
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void creaCarpetas(Context context) {
		if (!existeDir(context.getString(R.string.pathJuego)))
			createDir(context.getString(R.string.pathJuego));
		if (!existeDir(context.getString(R.string.pathImages)))
			createDir(context.getString(R.string.pathImages));
		if (!existeDir(context.getString(R.string.pathSounds)))
			createDir(context.getString(R.string.pathSounds));
	}

	private static boolean existeDir(String path) {
		File folder = new File(path);
		return (folder.exists());
	}

	public static void eliminaImagenes(Context context) {
		DeleteRecursive(new File(context.getString(R.string.pathImages)));
	}

	public static void eliminaSonidos(Context context) {
		DeleteRecursive(new File(context.getString(R.string.pathSounds)));
	}

	public static boolean createDir(String path) {
		File folder = new File(path);
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		return success;
	}

	private static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);

		fileOrDirectory.delete();
	}

	public static boolean ExisteFichero(String path) {
		File f = new File(path);
		return (f.exists() && !f.isDirectory());
	}

	public static boolean CreaFichero(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

}
