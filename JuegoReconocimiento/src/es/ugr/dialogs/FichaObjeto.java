package es.ugr.dialogs;

import es.ugr.juegoreconocimiento.R;
import es.ugr.objetos.Objeto;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class FichaObjeto extends Dialog {

	private Objeto oObjeto;
	private Context context;

	public FichaObjeto(Context context, Objeto objeto) {
		super(context);
		this.context = context;
		oObjeto = objeto;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ficha_objeto);

		((TextView) findViewById(R.id.edtNombreObjeto)).setText(oObjeto
				.getNombre());
		((TextView) findViewById(R.id.edtDescripcion)).setText(oObjeto
				.getDescripcion());

		if (oObjeto.getImagen() != null) {
			((ImageView) findViewById(R.id.imgObjeto)).setImageBitmap(oObjeto
					.getImagen());
		}
			
	}

	@Override
	public void show() {		
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);
		setTitle(oObjeto.getNombre());
	}

}
