package uo.sdi.acciones;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FieldsCheck;
import uo.sdi.dto.Category;
import uo.sdi.dto.User;
import alb.util.log.Log;

public class AñadirCategoriaAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String resultado = "EXITO";
		
		//Datos de la categoria
		HttpSession session = request.getSession();
		User user = ((User) session.getAttribute("user"));
		String name = request.getParameter("categoryName");
		
		if (FieldsCheck.invalidFieldCheck(name)) {
			request.setAttribute("error", "No puedes crear una categoría sin nombre");
			Log.debug("El usuario no ha introducido un nombre para la categoría");
			return "FRACASO";
		}
		
		Category category = new Category();
		category.setUserId(user.getId());
		category.setName(name);
		
		List<Category> listaCategorias;
		
		try {
			TaskService taskService = Services.getTaskService();
			long categoryId = taskService.createCategory(category);
			
			listaCategorias=taskService.findCategoriesByUserId(user.getId());
			session.setAttribute("listaCategorias", listaCategorias);
			Log.info("Añadida nueva categoria");
			
			request.setAttribute("message", "Se ha añadido la categoría '" + category.getName()  + "'");
			request.getRequestDispatcher("listarTareas?categoryId=" + Long.toString(categoryId)).forward(request, response);
		}
		catch (BusinessException | ServletException | IOException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido creando la categoria: %s",
					b.getMessage());
			resultado="FRACASO";
		}
		
		return resultado;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}
}
