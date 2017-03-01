package uo.sdi.acciones;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import alb.util.log.Log;

public class CambiarEstadoAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String resultado = "EXITO";
		
		HttpSession session = request.getSession();

		String id = request.getParameter("id");
		Long idUser = Long.parseLong(id);
		
		AdminService adminService = Services.getAdminService();		
		List<User> listOfUsers = null;
		try {
			//Si el usuario está activado, desactivalo
			if (adminService.findUserById(idUser).getStatus().equals(UserStatus.ENABLED)){
				adminService.disableUser(idUser);
				request.setAttribute("message", "El usuario con id " + id + " se ha desactivado correctamente.");
				Log.debug("El usuario con id " + id + " se ha desactivado correctamente.");
			}
			////Si el usuario está desactivado, activalo
			else{
				adminService.enableUser(idUser);
				request.setAttribute("message", "El usuario con id " + id + " se ha activado correctamente.");
				Log.info("El usuario con id " + id + " se ha activado correctamente.");
			}
			
			listOfUsers = adminService.findAllUsers();
		} catch (BusinessException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido intentanto editar el estado del usuario seleccionado");
			resultado = "FRACASO";
		}
		
		session.setAttribute("listOfUsers", listOfUsers);
		return resultado;
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}

}
