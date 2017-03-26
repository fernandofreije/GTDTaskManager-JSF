package uo.sdi.business.impl.admin.command;

import alb.util.date.DateUtil;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.command.Command;
import uo.sdi.dto.Category;
import uo.sdi.dto.Task;
import uo.sdi.dto.User;
import uo.sdi.persistence.CategoryDao;
import uo.sdi.persistence.Persistence;
import uo.sdi.persistence.TaskDao;
import uo.sdi.persistence.UserDao;

public class ResetDBCommand implements Command<Void> {

	@Override
	public Void execute() throws BusinessException {
		TaskDao tDao = Persistence.getTaskDao();
		CategoryDao cDao = Persistence.getCategoryDao();
		UserDao uDao = Persistence.getUserDao();
		
		for (User u : uDao.findAll()){
			if (!u.getLogin().equals("admin1")){
				tDao.deleteAllFromUserId( u.getId() );
				cDao.deleteAllFromUserId( u.getId() );
				uDao.delete( u.getId() );
			}
		}
		
		User user;
		for (long i = 1; i <= 3; i++){
			user = new User();
			user.setLogin("user"+i);
			user.setPassword("user"+i);
			user.setEmail("user"+i+"@mail.com");
			Long id = uDao.save(user);
			
			Task task;
			int daysToAdd = 1;
			int totalTasks = 1;
			
			//10 tareas previstas a los 6 días siguientes.
			for (long k=1; k<=10; k++){
				task = new Task();
				task.setTitle("Tarea " + k + " del usuario " + i);
				if (k%2 == 0)
					daysToAdd++;
				task.setPlanned(DateUtil.addDays(DateUtil.today(), daysToAdd));
				task.setComments("");
				task.setUserId(id);
				tDao.save(task);
				totalTasks++;
				
			}
			//10 tareas previstas para el día que se ejecuta la tarea.
			for (long k=11; k<=20; k++){
				task = new Task();
				task.setTitle("Tarea " + k + " del usuario " + i);
				task.setPlanned(DateUtil.today());
				task.setComments("");
				task.setUserId(id);
				tDao.save(task);
				totalTasks++;
			}
			
			//3 categorías por usuario 
			Category category;
			for (long j=1; j <=3; j++){
				category = new Category();
				category.setId(j);
				category.setName("categoría"+j);
				category.setUserId(id);
				Long categoryId = cDao.save(category);
				
				int tasksPerCategory = 3;
				if (j == 3) 
					tasksPerCategory = 4;
				
				//10 tareas retrasadas y pertenecientes a la categoría 1 (3), 
				//categoría 2 (3) y categoría 3 (4).
				for (long k=1; k<=tasksPerCategory; k++){
					task = new Task();
					task.setTitle("Tarea " + totalTasks + " del usuario " + i + " de la categoría " + j);
					task.setPlanned(DateUtil.yesterday());
					task.setComments("");
					task.setCategoryId(categoryId);
					task.setUserId(id);
					tDao.save(task);
					totalTasks++;
				}
			}
			
		}	
		return null;
	}

}
