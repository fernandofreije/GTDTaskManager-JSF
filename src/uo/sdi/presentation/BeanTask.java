package uo.sdi.presentation;

import java.util.Date;
import java.util.List;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.TaskService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Category;
import uo.sdi.dto.Task;
import alb.util.date.DateUtil;

public class BeanTask {

	private Long id;

	private String title;
	private String comments;
	private Date created = DateUtil.today();
	private Date planned;
	private Date finished;

	private Long categoryId;
	private Long userId;

	private List<Category> categories;

	public BeanTask() {
	}

	public void showTask(Task task) {
		this.id = task.getId();

		// Info of the task
		this.title = task.getTitle();
		this.comments = task.getComments();
		this.created = task.getCreated();
		this.planned = task.getPlanned();
		this.finished = task.getFinished();
		this.categoryId = task.getCategoryId();
		this.userId = task.getUserId();

		// Categories of the user
		TaskService taskService = Services.getTaskService();
		try {
			categories = taskService.findCategoriesByUserId(userId);
		} catch (BusinessException e) {

		}
	}

	public String addTask() {
		// Task is created
		Task task = new Task();
		task.setTitle(title);
		if (categoryId != null)
			task.setCategoryId(categoryId);
		task.setUserId(userId);

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
			task = taskService.findTaskById(id);
		} catch (BusinessException e1) {
			return null;
		}
		
		//Edit the task
		task.setTitle(title);
		if (categoryId != null)
			task.setCategoryId(categoryId);
		task.setComments(comments);
		task.setPlanned(planned);

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
			taskService.markTaskAsFinished(id);
		} catch (BusinessException e1) {
			return null;
		}
		return "exito";	
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

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}
