package uo.sdi.acciones;

import javax.servlet.http.HttpServletRequest;
import alb.util.log.Log;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CerrarSesionAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String resultado = "EXITO";

		HttpSession sesion = request.getSession();

		sesion.invalidate();

		Log.info("Se ha cerrado la sesion");
		request.setAttribute("message", "Gracias por su visita. Esperemos que vuelva pronto.");
		return resultado;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
