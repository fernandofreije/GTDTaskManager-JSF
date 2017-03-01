package uo.sdi.acciones;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Category;
import uo.sdi.dto.User;
import alb.util.log.Log;

public class ListarCategoriasAction implements Accion {
	
	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String resultado="EXITO";
		
		List<Category> listaCategorias;
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("user");
		
		try {
			TaskService taskService = Services.getTaskService();
			listaCategorias=taskService.findCategoriesByUserId(user.getId());
			session.setAttribute("listaCategorias", listaCategorias);
			Log.info("Obtenida lista de categorías conteniendo [%d] categorías", 
					listaCategorias.size());
		}
		catch (BusinessException b) {
			Log.debug("Algo ha ocurrido obteniendo lista de categorías: %s",
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
