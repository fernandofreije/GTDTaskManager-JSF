package uo.sdi.acciones;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import alb.util.log.Log;

public class EliminarUsuarioAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		HttpSession session = request.getSession();
		
		String id = request.getParameter("id");
		Long idUser = Long.parseLong(id);
		
		AdminService adminService = Services.getAdminService();		
		List<User> listOfUsers = null;
		try {
			adminService.deepDeleteUser(idUser);
			listOfUsers = adminService.findAllUsers();
			Log.info("Usuario eliminado correctamente");
		} catch (BusinessException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido al eliminar al usuario");
			return "FRACASO";
		}
		request.setAttribute("message", "El usuario se ha eliminado correctamente.");
		session.setAttribute("listOfUsers", listOfUsers);
		
		return "EXITO";
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}

}
