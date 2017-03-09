package uo.sdi.presentation;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FreijeyPabloUtil;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import alb.util.log.Log;

/**
 * ManagedBean to manage the listing of tasks of the user logged in
 * @author Pablo and Fernando
 *
 */
@ManagedBean(name = "tasks")
@SessionScoped
public class BeanTasks {

	private User user;
	private List<Task> listOfTasks;
	private List<Task> listOfFinishedTasks;
	private List<Task> selectedTasks;
	private String currentList;

	public BeanTasks() {
	}

	@PostConstruct
	public void init(){
		user = (User) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("LOGGEDIN_USER");
		setTasksInbox();
	}

	public void setTasksInbox() {
		TaskService taskService = Services.getTaskService();
		List<Task> listaTareas;
		try {
			currentList = "inbox";
			listaTareas = taskService.findInboxTasksByUserId(user.getId());
			List<Task> listaTareasTerminadasInbox=taskService.
					findFinishedInboxTasksByUserId(user.getId());
			FreijeyPabloUtil.orderAscending(listaTareas);
			FreijeyPabloUtil.orderDescending(listaTareasTerminadasInbox);
			
			setListOfTasks(listaTareas);
			setListOfFinishedTasks(listaTareasTerminadasInbox);
		} catch (BusinessException e) {
			Log.error(e);
		}
	}
	
	public void setTasksToday() {
		currentList = "today";
		TaskService taskService = Services.getTaskService();
		List<Task> listaTareas;
		try {
			listaTareas = taskService.findTodayTasksByUserId(user.getId());
			FreijeyPabloUtil.groupByCategory(listaTareas);
			
			setListOfTasks(listaTareas);
		} catch (BusinessException e) {
			Log.error(e);
		}
	}
	
	public void setTasksWeek() {
		currentList = "week";
		TaskService taskService = Services.getTaskService();
		List<Task> listaTareas;
		try {
			listaTareas = taskService.findWeekTasksByUserId(user.getId());
			FreijeyPabloUtil.groupByDay(listaTareas);
			
			setListOfTasks(listaTareas);
		} catch (BusinessException e) {
			Log.error(e);
		}
	}
	
	public void finishTasks(){
		TaskService taskService = Services.getTaskService();
		try {	
			for (Task t:selectedTasks){
				taskService.markTaskAsFinished(t.getId());
			}
			forceUpdateList();
		} catch (BusinessException e) {
			Log.error(e);
		}
	}
	
	public void forceUpdateList(){
		switch (currentList){
		case "inbox" : setTasksInbox();break;
		case "today" : setTasksToday();break;
		case "week" : setTasksWeek();break;
		}
	}
	

	public List<Task> getListOfTasks() {
		return this.listOfTasks;
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
	
	public List<Task> getSelectedTasks() {
		return selectedTasks;
	}

	public void setSelectedTasks(List<Task> selectedTasks) {
		this.selectedTasks = selectedTasks;
	}

}
