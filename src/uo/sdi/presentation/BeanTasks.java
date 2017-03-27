package uo.sdi.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessCheck;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Category;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import alb.util.date.DateUtil;
import alb.util.log.Log;

/**
 * ManagedBean to manage the lists of tasks of the user logged in
 * 
 * @author Pablo and Fernando
 * 
 */
@ManagedBean(name = "tasks")
@SessionScoped
public class BeanTasks implements Serializable {

	private static final long serialVersionUID = 1L;
	private User user;
	private Task task;

	private TaskList listOfTasks;
	private List<Task> listOfFinishedTasks;
	private List<Task> selectedTasks;
	private String currentList;

	private List<Category> listOfCategories;

	public BeanTasks() {
	}

	@PostConstruct
	public void init() {
		user = (User) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get("LOGGEDIN_USER");
		if (listOfTasks == null)
			setTasksInbox();
		task = new Task();
		task.setPlanned(DateUtil.today());
	}

	/**
	 * Method to update the list of tasks of inbox whenever an operation is
	 * performed in the system
	 */
	public String setTasksInbox() {
		TaskService taskService = Services.getTaskService();
		try {
			List<Task> listaTareas = new ArrayList<Task>();
			currentList = "inbox";

			// Obtenemos de la base de datos las listas
			List<Task> listaTareasNoTerminadasInbox = taskService
					.findInboxTasksByUserId(user.getId());
			List<Task> listaTareasTerminadasInbox = taskService
					.findFinishedInboxTasksByUserId(user.getId());

			// Metemos en la lista de tareas ambas listas
			listaTareas.addAll(listaTareasNoTerminadasInbox);
			listaTareas.addAll(listaTareasTerminadasInbox);
			setListOfTasks(new TaskList(listaTareas));
			
			Log.debug("Inbox list of tasks refreshed");
		} catch (BusinessException e) {
			Log.debug(e);
		}
		return "exito";
	}
	
	/**
	 * Method to update the list of tasks of today whenever an operation is
	 * performed in the system
	 */
	public String setTasksToday() {
		currentList = "today";
		TaskService taskService = Services.getTaskService();
		List<Task> listaTareas;
		try {
			// Metemos en la lista de tareas las listas de hoy
			listaTareas = taskService.findTodayTasksByUserId(user.getId());
			setListOfTasks(new TaskList(listaTareas));
			Log.debug("Today list of tasks refreshed");
		} catch (BusinessException e) {
			Log.debug(e);
		}
		return "exito";
	}
	
	/**
	 * Method to update the list of tasks of week whenever an operation is
	 * performed in the system
	 */
	public String setTasksWeek() {
		currentList = "week";
		TaskService taskService = Services.getTaskService();
		List<Task> listaTareas;
		try {
			// Metemos en la lista de tareas las listas de week
			listaTareas = taskService.findWeekTasksByUserId(user.getId());
			setListOfTasks(new TaskList(listaTareas));
			Log.debug("Week list of tasks refreshed");
		} catch (BusinessException e) {
			Log.debug(e);
		}
		return "exito";
	}
	
	/**
	 * Mark a list of selected tasks as finished
	 * @return null because the page is ajaxified
	 */
	public String finishTasks() {
		TaskService taskService = Services.getTaskService();
		try {
			for (Task t : selectedTasks) {
				taskService.markTaskAsFinished(t.getId());
				Log.debug("Finished task: " + task.getTitle());
			}
			forceUpdateList();
		} catch (BusinessException e) {
			Log.debug(e);
			BusinessCheck.showBusinessError(e.getMessage());
		}
		return null;
	}
	/**
	 * Add a Task to the list of tasks.
	 * @return String containing the next view to show.
	 * It could be:
	 * 	- Inbox if the tasks has no category
	 * 	- Today if the task has category and it is planned for today
	 *  - Week if the task has category and it is planned for the future
	 */
	public String addTask() {
		String resultado = "";

		// Si tiene categoria
		if (task.getCategoryEditable() != null) {
			// Redireccionamos a hoy si está planeada para hoy
			if (task.getPlannedEditable().equals(DateUtil.today()))
				resultado = "today";
			// Redireccionamos a semana si está planeada con fecha posterior a
			// hoy
			else if (task.getPlannedEditable().after(DateUtil.today()))
				resultado = "week";
			else
				resultado = "inbox";
		}
		// Si no tiene categoria redireccionamos a inbox
		else
			resultado = "inbox";

		task.setUserId(user.getId());
		// Task is registered in db
		TaskService taskService = Services.getTaskService();
		try {
			taskService.createTask(task);
			Log.debug("Added task: " + task.getTitle());
			this.currentList = resultado;
			forceUpdateList(); // Actualizamos las listas de db
			clearFields(); // Limpiamos los campos del formulario
		} catch (BusinessException e) {
			Log.debug(e);
			BusinessCheck.showBusinessError(e.getMessage());
			return null;
		}
		return resultado;
	}

	/**
	 * Method to clear the fields of the form after the task is added.
	 */
	private void clearFields() {
		this.task.setTitle("");
		this.task.setComments("");
		this.task.setCategoryId(null);
		this.task.setPlanned(DateUtil.today());
	}
	
	/**
	 * Edit a given task on the fly
	 * @return null because the page is ajaxified
	 */
	public String edit(Task task) {
		// Find the task we want to edit
		TaskService taskService = Services.getTaskService();
		// Task is updated in db
		try {
			taskService.updateTask(task);
			Log.debug("Edited task: " + task.getTitle());
			forceUpdateList();
		} catch (BusinessException e) {
			Log.debug(e);
		}
		return null;
	}

	/**
	 * Update the list depending on the current list you are viewing 
	 */
	public void forceUpdateList() {
		switch (currentList) {
		case "inbox":
			setTasksInbox();
			break;
		case "today":
			setTasksToday();
			break;
		case "week":
			setTasksWeek();
			break;
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

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return this.task;
	}

	public List<Category> getListOfCategories() {
		TaskService taskService = Services.getTaskService();
		try {
			listOfCategories = taskService.findCategoriesByUserId(user.getId());
		} catch (BusinessException e) {
			Log.debug(e);
		}
		return listOfCategories;
	}

	public void setListOfCategories(List<Category> listOfCategories) {
		this.listOfCategories = listOfCategories;
	}

	public String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		return DateUtil.toString(date);
	}
}
