package uo.sdi.presentation;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;

/**
 * ManagedBean to manage the CRUD of tasks of the user logged in
 * @author Pablo and Fernando
 *
 */
@ManagedBean(name = "task")
@RequestScoped
public class BeanTask implements Serializable {

	private static final long serialVersionUID = 1L;
	private User user;
	private Long id;
	private String title, comments;
	private Date created, planned, finished;
	private Long categoryId, userId;

	@ManagedProperty(value="#{tasks}")
    private BeanTasks tasks;

	public BeanTask() {
	}

	@PostConstruct
	public void init(){
		user = (User) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("LOGGEDIN_USER");
		
		}
	
	public void showTask(Task task) {
		setId(task.getId());

		// Info of the task
		setTitle(task.getTitle());
		setComments(task.getComments());
		setCreated(task.getCreated());
		setPlanned(task.getPlanned());
		setFinished(task.getFinished());
		setUserId(task.getUserId());
	}

	public String addTask() {
		// Task is created
		Task task = new Task();
		task.setTitle(getTitle());
		
		//Inyeccion de dependencia???
		task.setUserId(user.getId());

		// Task is registered in db
		TaskService taskService = Services.getTaskService();
		try {
			taskService.createTask(task);
			tasks.forceUpdateList();
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getPlanned() {
		return planned;
	}

	public void setPlanned(Date planned) {
		this.planned = planned;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public BeanTasks getTasks() {
		return tasks;
	}

	public void setTasks(BeanTasks tasks) {
		this.tasks = tasks;
	}


}
