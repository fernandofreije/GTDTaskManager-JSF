package uo.sdi.acciones;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FieldsCheck;
import uo.sdi.dto.User;
import uo.sdi.dto.util.Cloner;
import alb.util.log.Log;

public class EditarUsuarioAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String resultado = "EXITO";

		String newLogin = request.getParameter("newLogin")
			,login = request.getParameter("login")
			,newEmail = request.getParameter("newEmail")
			,password = request.getParameter("password")
			,newPassword = request.getParameter("newPassword")
			,newPassword2 = request.getParameter("newPassword2");
		
		HttpSession session = request.getSession();
		User user = ((User) session.getAttribute("user"));
		User userToEdit = null;
		
		//Si el usuario es admin, editará otro user que no es el de la sesión
		if (user.getIsAdmin()){
			UserService userService = Services.getUserService();
			try {
				userToEdit = userService.findLoggableUser(login);
				session.setAttribute("userToEdit", userToEdit);
				
			} catch (BusinessException b) {
				request.setAttribute("error", "Algo ha ocurrido actualizando los datos del usuario: "
						+ b.getMessage());
				Log.debug(
						"Algo ha ocurrido actualizando los datos del usuario [%s]",
						user.getLogin());
				return "FRACASO";
			}
		}
		//Si no es admin, se editará a si mismo
		else{
			userToEdit = user;
			session.setAttribute("userToEdit", userToEdit);
		}
		
		User userClone = Cloner.clone(userToEdit);
		
		// If new fields are empty
		if (FieldsCheck.invalidFieldCheck(newEmail, newPassword, newPassword2)) {
			request.setAttribute("error", "Existen campos vacios, por favor, rellenalos todos.");
			Log.debug(
					"El usuario [%s] no ha rellado todos los campos al actualizar datos",
					user.getLogin());
			return "FRACASO";
		} else {
			// Logica relacionada con la Vista
			if (!userToEdit.getEmail().equals(newEmail)) {
				userClone.setEmail(newEmail);
				Log.info("Modificado el email de [%s] con el valor [%s]",
						userClone.getLogin(), newEmail);
			}
			//Si las viejas contraseñas y la repetición de la nueva coinciden, se actualiza
			if (userToEdit.getPassword().equals(password) && newPassword.equals(newPassword2)) {
				userClone.setPassword(newPassword);
				Log.info("Modificada la password de [%s]",
						userClone.getLogin());
			}
			//Las viejas password no coinciden
			if (!userToEdit.getPassword().equals(password)){
				request.setAttribute("error", "La contraseña vieja no coincide con la almacenada en la base de datos, inténtelo de nuevo");
				Log.debug("La contraseña vieja no coincide con la almacenada en la base de datos, inténtelo de nuevo");
				return "FRACASO";
			}
			//La repetición de la password no coincide
			if (!newPassword.equals(newPassword2)) {
				request.setAttribute("error", "Las nuevas passwords no coinciden, inténtelo de nuevo");
				Log.debug("Las nuevas passwords no coinciden");
				return "FRACASO";
			}
			//La nueva password es igual a la anterior
			if (userToEdit.getPassword().equals(newPassword)) {
				request.setAttribute("error", "La nueva password debe de ser distinta a la anterior, inténtelo de nuevo");
				Log.debug("La password introducida coincide con el anterior");
				return "FRACASO";
			}
			if (!userToEdit.getLogin().equals(newLogin)){
				userClone.setLogin(newLogin);
				Log.info("Modificado el login de [%s] con el valor [%s]", login, newLogin);
			}
			try {
				UserService userService = Services.getUserService();
				userService.updateUserDetails(userClone);
				
				//If everything was ok and the operation was not performed from admin Panel
				if (!user.getIsAdmin()){
					session.setAttribute("user", userClone);
					session.setAttribute("userToEdit", userClone);
				}
				//If everything was ok and the operation was  performed from admin Panel
				else{
					request.setAttribute("userToEdit", userClone);
				}	
				Log.info("Se ha editado correctamente el usuario.");
				
			} catch (BusinessException b) {
				request.setAttribute("error", "Algo ha ocurrido actualizando los datos del usuario: "
						+ b.getMessage());
				Log.debug(
						"Algo ha ocurrido actualizando los datos del usuario [%s]",
						user.getLogin());
				return "FRACASO";
			}
		}
		
		request.setAttribute("message", "Los datos han sido actualizados correctamente.");
		return resultado;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
