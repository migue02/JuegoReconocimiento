package es.ugr.adaptadores;

import java.util.ArrayList;
import java.util.List;

import es.ugr.objetos.Resultado;

public class GrupoDeItemsResultado {
	public String string;
	public final List<Resultado> children = new ArrayList<Resultado>();

	public GrupoDeItemsResultado(String string) {
		this.string = string;
	}
}
