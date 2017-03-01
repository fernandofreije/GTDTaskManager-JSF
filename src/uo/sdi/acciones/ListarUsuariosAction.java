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


public class ListarUsuariosAction implements Accion{


	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
	
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if(user.getIsAdmin()){
			AdminService adminService = Services
					.getAdminService();
			List<User> listOfUsers = null;
			try {
				listOfUsers = adminService.findAllUsers();
				session.setAttribute("listOfUsers", listOfUsers);
				Log.info("Lista de usuarios ejecutada correctamente");
				return "EXITO";
			} catch (BusinessException b) {
				Log.debug(
						"Algo ha ocurrido intentando iniciar sesión como administrador: [%s]: %s",
						user.getLogin(), b.getMessage());
				request.setAttribute("error",
						"Algo ha ocurrido intentando iniciar sesión como administrador: "
								+ b.getMessage());
				return "FRACASO";
			}
			
		}
		Log.debug(
				"El usuario %s no puede acceder a esta seccion porque no es admin"
				,user.getLogin());
		request.setAttribute("error",
				"No se puede acceder a esta url sin ser admin: ");
		return "FRACASO";
		
	}

	@Override
	public String toString() {
		return getClass().getName();
	}
}
