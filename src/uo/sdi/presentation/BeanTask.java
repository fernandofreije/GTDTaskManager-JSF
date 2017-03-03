package uo.sdi.presentation;

import java.io.Serializable;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Task;

public class BeanTask extends Task implements Serializable {

	private static final long serialVersionUID = 1L;

	public BeanTask() {
	}

	public void showTask(Task task) {
		setId(task.getId());

		// Info of the task
		setTitle(task.getTitle());
		setComments(task.getComments());
		setCreated(task.getCreated());
		setPlanned(task.getPlanned());
		setFinished(task.getFinished());
		
		//User inyeccion de dependencia???
		setUserId(task.getUserId());
	}

	public String addTask() {
		// Task is created
		Task task = new Task();
		task.setTitle(getTitle());
		
		//Inyeccion de dependencia???
		task.setUserId(getUserId());

		// Task is registered in db
		TaskService taskService = Services.getTaskService();
		try {
			taskService.createTask(task);
		} catch (BusinessException e) {
			return null;
		}

		return "exito";

	}

	public String editTask() {
		// Find the task we want to edit
		TaskService taskService = Services.getTaskService();
		Task task;
		try {
			task = taskService.findTaskById(getId());
		} catch (BusinessException e1) {
			return null;
		}
		
		//Edit the task
		task.setTitle(getTitle());
		task.setComments(getComments());
		task.setPlanned(getPlanned());

		// Task is updated in db
		try {
			taskService.updateTask(task);
		} catch (BusinessException e) {
			return null;
		}

		return "exito";
	}

	public String finishTask() {
		// Mark the task as finished
		TaskService taskService = Services.getTaskService();
		try {
			taskService.markTaskAsFinished(getId());
		} catch (BusinessException e1) {
			return null;
		}
		return "exito";	
	}


}
