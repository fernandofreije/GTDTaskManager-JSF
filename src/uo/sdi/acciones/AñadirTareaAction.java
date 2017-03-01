package uo.sdi.acciones;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FieldsCheck;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import alb.util.date.DateUtil;
import alb.util.log.Log;

public class AñadirTareaAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {

		String resultado = "EXITO";

		// Datos de la categoria
		HttpSession session = request.getSession();
		User user = ((User) session.getAttribute("user"));
		String taskName = request.getParameter("taskName");
		String categoryId = request.getParameter("categoryId");

		if (FieldsCheck.invalidFieldCheck(taskName)) {
			request.setAttribute("error",
					"No puedes crear una tarea sin nombre");
			Log.debug("El usuario no ha introducido un nombre para la tarea");
			return "FRACASO";
		}
		Task task = new Task();
		task.setTitle(taskName);
		if (categoryId != null)
			task.setCategoryId(Long.parseLong(categoryId));
		task.setUserId(user.getId());

		// Si la lista que se esta mostrando es la pseudolista Hoy se le pone
		// de planeada un minuto antes de que acabe el día de hoy
		String listaMostrada = request.getParameter("listaMostrada");
		if (listaMostrada != null && listaMostrada.equalsIgnoreCase("hoy"))
			task.setPlanned(DateUtil.today());

		try {
			TaskService taskService = Services.getTaskService();
			taskService.createTask(task);
			request.setAttribute("message", "Se ha añadido la tarea '" + task.getTitle() + "'");
			if (categoryId != null) {
				request.getRequestDispatcher(
						"listarTareas?id=" + task.getCategoryId()).forward(
						request, response);
			} else {
				request.getRequestDispatcher("listarTareasInbox").forward(
						request, response);
			}
			Log.info("Se ha añadido la tarea '" + task.getTitle() + "'");

		} catch (BusinessException | ServletException | IOException b) {
			request.setAttribute("error", b.getMessage());
			Log.debug("Algo ha ocurrido creando la tarea: %s", b.getMessage());
			resultado = "FRACASO";
		}
		
		return resultado;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
