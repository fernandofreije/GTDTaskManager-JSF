package uo.sdi.acciones;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import uo.sdi.dto.util.Cloner;
import alb.util.log.Log;

public class EditarTareaAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String resultado = "EXITO";
		HttpSession session = request.getSession();
		User user = ((User) session.getAttribute("user"));
		
		
		// Datos del task
		String taskId = request.getParameter("taskId");
		
		String newTitle = request.getParameter("newTitle");
		
		Date date;
		
		String newComment = request.getParameter("newComment");
		long newCategoryId = Long.parseLong(request.getParameter("newCategoryId"));
		
		
		//Find task and categories
		TaskService taskService = Services.getTaskService();
		Task task = null;
		List<Category> categories = null;
		try {
			categories = taskService.findCategoriesByUserId(user.getId());
			task = taskService.findTaskById(Long.parseLong(taskId));
		} catch (BusinessException b) {
			request.setAttribute("error", "Algo ha ocurrido editando la tarea: " +  b.getMessage());
			Log.debug("Algo ha ocurrido editando la tarea: %s", b.getMessage());
			return "FRACASO";
		}
		
		// If new fields are empty
		if (FieldsCheck.invalidFieldCheck(newTitle)) {
			request.setAttribute("error", "Existen campos vacios, por favor, rellenalos todos.");
			Log.debug("El usuario no ha rellado los campos al actualizar datos");
			return "FRACASO";
		}
		
		//Clone task
		Task cloneTask = Cloner.clone(task);

		//Set new fields
		cloneTask.setTitle(newTitle);
		cloneTask.setComments(newComment);
		if (newCategoryId != -1){
			cloneTask.setCategoryId(newCategoryId);
		}
		else{
			cloneTask.setCategoryId(null);
		}
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String plannedDateString=request.getParameter("newPlannedDate");
		date = null;
		if (!plannedDateString.equals("")){
			try {
				date = formatter.parse(plannedDateString);
				
			} catch (ParseException e) {
				Log.debug("Ha ocurrido un problema con la fecha");
				return "FRACASO";
			}
		}
		cloneTask.setPlanned(date);
		
		
		 // Hay que mirar como hacemos con las fechas
		
		//Update task
		try {
			taskService.updateTask(cloneTask);
			//Añadimos la tarea y la categoria para mostrarlas de nuevo a la sesion
			session.setAttribute("task", cloneTask);
			session.setAttribute("selectedCategory", cloneTask.getCategoryId());
			
			//Añadimos categorias disponibles del usuario a la sesion
			session.setAttribute("categories", categories);
			
			session.setAttribute("message", "Se ha editado la tarea correctamente.");
			request.getRequestDispatcher("mostrarTarea?taskId="+taskId).forward(request,response);
			Log.info("Se ha editado la tarea correctamente.");
		} catch (BusinessException b) {
			request.setAttribute("error", "Algo ha ocurrido editando la tarea: " +  b.getMessage());
			Log.debug("Algo ha ocurrido editando la tarea: %s", b.getMessage());
			return "FRACASO";
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("message", "Los datos han sido actualizados correctamente.");
		
		return resultado;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
