package uo.sdi.presentation;

import java.util.ArrayList;
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
	private TaskList listOfTasks;
	private List<Task> listOfFinishedTasks;
	private List<Task> selectedTasks;
	
	private String taskName;
	
	private String currentList;

	public BeanTasks() {
	}

	@PostConstruct
	public void init(){
		user = (User) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("LOGGEDIN_USER");
		if (listOfTasks==null)
			setTasksInbox();
	}

	public void setTasksInbox() {
		TaskService taskService = Services.getTaskService();
		
		try {
			List<Task> listaTareas = new ArrayList<Task>();
			currentList = "inbox";
			
			//Obtenemos de la base de datos las listas
			List<Task> listaTareasNoTerminadasInbox = taskService.findInboxTasksByUserId(user.getId());
			List<Task> listaTareasTerminadasInbox=taskService.
					findFinishedInboxTasksByUserId(user.getId());
			
			//Ordenamos las listas
			FreijeyPabloUtil.orderAscending(listaTareasNoTerminadasInbox);
			FreijeyPabloUtil.orderDescending(listaTareasTerminadasInbox);
			
			//Metemos en la lista de tareas ambas listas
			listaTareas.addAll(listaTareasNoTerminadasInbox);
			listaTareas.addAll(listaTareasTerminadasInbox);
			
			setListOfTasks(new TaskList(listaTareas));
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
			
			setListOfTasks(new TaskList(listaTareas));
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
			
			setListOfTasks(new TaskList(listaTareas));
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
	
	
	public String addTask() {
		// Task is created
		Task task = new Task();
		task.setTitle(getTaskName());
		
		//Inyeccion de dependencia???
		task.setUserId(user.getId());

		// Task is registered in db
		TaskService taskService = Services.getTaskService();
		try {
			taskService.createTask(task);
			forceUpdateList();
		} catch (BusinessException e) {
			return null;
		}

		return "exito";

	}
	
	public String edit(Task task) {
		// Find the task we want to edit
		TaskService taskService = Services.getTaskService();
		// Task is updated in db
		try {
			taskService.updateTask(task);
		} catch (BusinessException e) {
			return null;
		}

		return "exito";
	}
	
	public void forceUpdateList(){
		switch (currentList){
		case "inbox" : setTasksInbox();break;
		case "today" : setTasksToday();break;
		case "week" : setTasksWeek();break;
		}
	}
	

	public TaskList getListOfTasks() {
		return this.listOfTasks;
	}

	public void setListOfTasks(TaskList taskList) {
		this.listOfTasks = taskList;
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
	
	public void setTaskName(String name){
		this.taskName=name;
	}
	
	public String getTaskName(){
		return this.taskName;
	}

}
