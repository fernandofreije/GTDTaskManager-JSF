package uo.sdi.acciones;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FreijeyPabloUtil;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import alb.util.log.Log;

public class ListarTareasInboxAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		String resultado="EXITO";
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("user");
		
		//All not finished tasks in the user's inbox (without category)
		List<Task> listaTareasInbox;
		List<Task> listaTareasTerminadasInbox;
		
		
		try {
			TaskService taskService = Services.getTaskService();
			listaTareasInbox=taskService.findInboxTasksByUserId(Long.valueOf(user.getId()));
			listaTareasTerminadasInbox=taskService.
					findFinishedInboxTasksByUserId(Long.valueOf(user.getId()));
			
			FreijeyPabloUtil.orderAscending(listaTareasInbox);
		
			
			FreijeyPabloUtil.orderDescending(listaTareasTerminadasInbox);
			
			request.setAttribute("listaTareas", listaTareasInbox);
			request.setAttribute("listaTareasTerminadas", listaTareasTerminadasInbox);
			
			request.setAttribute("listaMostrada", "Inbox");
			request.setAttribute("sePuedeMostrarTerminadas",true);
			
			Log.info("Obtenida lista de tareas del día conteniendo [%d] tareas", 
					listaTareasInbox.size());
		}
		catch (BusinessException b) {
			Log.debug("Algo ha ocurrido obteniendo lista de tareas: %s",
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
