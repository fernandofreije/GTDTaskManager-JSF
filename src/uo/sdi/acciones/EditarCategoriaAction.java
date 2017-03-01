package uo.sdi.acciones;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FieldsCheck;
import uo.sdi.dto.Category;
import uo.sdi.dto.User;
import uo.sdi.dto.util.Cloner;
import alb.util.log.Log;

public class EditarCategoriaAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		User user = ((User) session.getAttribute("user"));
		
		//Datos de la category
		String categoryId = request.getParameter("categoryId");
		String newName = request.getParameter("newName");
		
		if (FieldsCheck.invalidFieldCheck(newName)) {
			request.setAttribute("error", "No puedes dejar el nombre de la categoría vacío");
			Log.debug("El usuario no ha introducido un nombre para la categoría");
			return "FRACASO";
		}
		
		//Find category
		TaskService taskService = Services.getTaskService();
		Category category;
		List<Category> listaCategorias;
		
		try {
			category = taskService.findCategoryById(Long.parseLong(categoryId));
			
		} catch (BusinessException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido editando la categoria: %s", b.getMessage());
			return "FRACASO";
		}
		
		//Clone Category
		Category cloneCategory = Cloner.clone(category);
		
		//Set new fields
		cloneCategory.setName(newName);
		cloneCategory.setId(category.getId());
		
		//Update category
		try {		
			taskService.updateCategory(cloneCategory);
			request.setAttribute("message", "Se ha editado la categoría correctamente.");
			request.setAttribute("category", cloneCategory);
			listaCategorias=taskService.findCategoriesByUserId(user.getId());
			session.setAttribute("listaCategorias", listaCategorias);
			Log.info("Se ha editado la categoría correctamente.");
		} catch (BusinessException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido editando la categoria: %s",
					b.getMessage());
			return "FRACASO";
		}
		request.setAttribute("message", "Los datos han sido actualizados correctamente.");
		return "EXITO";
	}
	
	@Override
	public String toString() {
		return getClass().getName();
	}

}
