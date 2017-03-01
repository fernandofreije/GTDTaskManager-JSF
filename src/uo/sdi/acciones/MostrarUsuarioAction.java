package uo.sdi.acciones;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import alb.util.log.Log;

public class MostrarUsuarioAction implements Accion{

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String login = request.getParameter("login");
		
		UserService userService = Services.getUserService();
		AdminService adminService = Services.getAdminService();
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		User userToEdit;
		try {
			//Si el usuario es admin, podrá encontrar incluso un usuario desactivado
			if (user.getIsAdmin()){
				String id = request.getParameter("id");
				Long userId = Long.parseLong(id);
				
				userToEdit = adminService.findUserById(userId);
				if (userToEdit != null)
					session.setAttribute("userToEdit", userToEdit);
				else{
					request.setAttribute("error", "El usuario no existe.");
					return "FRACASO";
				}
			}
			else{
				userToEdit = userService.findLoggableUser(login);
				if (userToEdit != null)
					session.setAttribute("userToEdit", userToEdit);
				else {
					request.setAttribute("error", "El usuario no existe.");
					return "FRACASO";
				}
			}
			Log.info("Se está mostrando información del usuario " + userToEdit.getLogin());
			
			
		} catch (BusinessException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido intentando editar el usuario: %s", b.getMessage());
		
			return "FRACASO";
		}
		return "EXITO";
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}

}
