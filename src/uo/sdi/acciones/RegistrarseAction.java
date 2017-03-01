package uo.sdi.acciones;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FieldsCheck;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import alb.util.log.Log;

public class RegistrarseAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String login = request.getParameter("loginUsuario"), email = request
				.getParameter("emailUsuario"), password = request
				.getParameter("passwordUsuario"), confirmContraseña = request
				.getParameter("confirmPasswordUsuario");

		if (FieldsCheck.invalidFieldCheck(login, email, password,
				confirmContraseña)) {
			request.setAttribute("error", "Existen campos vacios, por favor, rellenalos todos.");
			Log.debug("El usuario no ha rellenado los 4 campos al registrarse");
			return "FRACASO";
		} else {
			if (!password.equals(confirmContraseña)) {
				request.setAttribute("error", "Las contraseñas deben de ser iguales.");
				Log.debug("El usuario ha introducido contraseñas diferentes");
				return "FRACASO";
			} else {
				try {
					User user = new User();
					user.setLogin(login);
					user.setEmail(email);
					user.setPassword(password);
					user.setStatus(UserStatus.ENABLED);

					UserService userService = Services.getUserService();
					long idUser = userService.registerUser(user);
					request.setAttribute("message", "Te has registrado correctamente con el nombre de usuario '" + user.getLogin() + "'"
							+ " e id: '" + idUser +"'");
					Log.info("Se ha registrado el usuario con login " + user.getLogin());

				} catch (BusinessException b) {
					request.setAttribute("error", "Ha ocurrido algo durante el registro y no se ha completado: " + b.getMessage());
					Log.debug("Ha ocurrido algo durante el registro y no se ha completado ");
					return "FRACASO";
				}
			}
		}
		
		return "EXITO";
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
