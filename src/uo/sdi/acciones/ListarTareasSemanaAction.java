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

public class ListarTareasSemanaAction implements Accion {

	@Override
	public String execute(HttpServletRequest request,
			HttpServletResponse response) {
		
		String resultado="EXITO";
		HttpSession session=request.getSession();
		User user = (User) session.getAttribute("user");
		
		List<Task> listaTareasSemana;
		
		try {
			TaskService taskService = Services.getTaskService();
			listaTareasSemana=taskService.findWeekTasksByUserId(Long.valueOf(user.getId()));
			FreijeyPabloUtil.groupByDay(listaTareasSemana);
			
			request.setAttribute("listaTareas", listaTareasSemana);
			
			
			session.setAttribute("listaMostrada", "Semana");
			request.setAttribute("sePuedeMostrarTerminadas",false);
			Log.info("Obtenida lista de tareas de la semana conteniendo [%d] tareas", 
					listaTareasSemana.size());
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
