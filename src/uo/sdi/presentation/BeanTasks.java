package uo.sdi.presentation;

import java.util.List;

import alb.util.log.Log;
import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.FreijeyPabloUtil;
import uo.sdi.dto.Task;

public class BeanTasks {

	private Long userId;
	private List<Task> listOfTasks;
	private List<Task> listOfFinishedTasks;

	public enum PseudoList {
		Inbox, Hoy, Semana
	}

	public BeanTasks() {
	}

	public String getListOfTasks(PseudoList pseudolist) {
		switch (pseudolist) {
			case Inbox: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasInbox;
				try {
					listaTareasInbox = taskService.findInboxTasksByUserId(userId);
					List<Task> listaTareasTerminadasInbox=taskService.
							findFinishedInboxTasksByUserId(userId);
					FreijeyPabloUtil.orderAscending(listaTareasInbox);
					FreijeyPabloUtil.orderDescending(listaTareasTerminadasInbox);
					
					setListOfTasks(listaTareasInbox);
					setListOfFinishedTasks(listaTareasTerminadasInbox);
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
			
			case Hoy: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasHoy;
				try {
					listaTareasHoy = taskService.findTodayTasksByUserId(userId);
					FreijeyPabloUtil.groupByCategory(listaTareasHoy);
					
					setListOfTasks(listaTareasHoy);
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
			
			case Semana: {
				TaskService taskService = Services.getTaskService();
				List<Task> listaTareasSemana;
				try {
					listaTareasSemana = taskService.findWeekTasksByUserId(userId);
					FreijeyPabloUtil.groupByDay(listaTareasSemana);
					
					setListOfTasks(listaTareasSemana);
				} catch (BusinessException e) {
					Log.error(e);
				}
			}
		}
		return null;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Task> getListOfTasks() {
		return listOfTasks;
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
