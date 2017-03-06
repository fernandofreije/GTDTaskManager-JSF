package uo.sdi.presentation;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import alb.util.log.Log;
import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FreijeyPabloUtil;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;

/**
 * ManagedBean to manage the listing of tasks of the user logged in
 * @author Pablo and Fernando
 *
 */
@ManagedBean(name = "tasks")
@RequestScoped
public class BeanTasks {

	private User user;
	private List<Task> listOfTasks;
	private List<Task> listOfFinishedTasks;
	public enum PseudoList {
		Inbox, Hoy, Semana
	}
	
	public BeanTasks() {
	}

	@PostConstruct
	public void init(){
		user = (User) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("LOGGEDIN_USER");
	}

	public List<Task> getListOfTasks(int pseudolist) {
		
		switch (pseudolist) {
			case 1: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasInbox;
				try {
					listaTareasInbox = taskService.findInboxTasksByUserId(user.getId());
					List<Task> listaTareasTerminadasInbox=taskService.
							findFinishedInboxTasksByUserId(user.getId());
					FreijeyPabloUtil.orderAscending(listaTareasInbox);
					FreijeyPabloUtil.orderDescending(listaTareasTerminadasInbox);
					
					setListOfTasks(listaTareasInbox);
					setListOfFinishedTasks(listaTareasTerminadasInbox);
					return listaTareasInbox;
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
			
			case 2: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasHoy;
				try {
					listaTareasHoy = taskService.findTodayTasksByUserId(user.getId());
					FreijeyPabloUtil.groupByCategory(listaTareasHoy);
					
					setListOfTasks(listaTareasHoy);
					return listaTareasHoy;
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
			
			case 3: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasSemana;
				try {
					listaTareasSemana = taskService.findWeekTasksByUserId(user.getId());
					FreijeyPabloUtil.groupByDay(listaTareasSemana);
					
					setListOfTasks(listaTareasSemana);
					return listaTareasSemana;
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
		}
		return null;
	}


	public void setListOfTasks(List<Task> listOfTasks) {
		this.listOfTasks = listOfTasks;
	}

	public List<Task> getListOfFinishedTasks() {
		return listOfFinishedTasks;
	}

	public void setListOfFinishedTasks(List<Task> listOfFinishedTasks) {
		this.listOfFinishedTasks = listOfFinishedTasks;
	}

}
