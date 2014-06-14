package es.ugr.utilidades;

import java.util.ArrayList;
import java.util.List;

import es.ugr.objetos.Resultado;

public class GrupoDeItems {
	public String string;
	public final List<Resultado> children = new ArrayList<Resultado>();

	public GrupoDeItems(String string) {
		this.string = string;
	}
}
