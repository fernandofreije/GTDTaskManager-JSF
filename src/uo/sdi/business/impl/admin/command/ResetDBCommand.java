package uo.sdi.business.impl.admin.command;

import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.command.Command;
import uo.sdi.dto.Category;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
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
			if (!u.getLogin().equals("admin")){
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
			
			Category category;
			for (long j=1; j <=3; j++){
				category = new Category();
				category.setId(j);
				category.setName("categoría"+j);
				category.setUserId(id);
				cDao.save(category);
			}
			
		}

		
		return null;
	}

}
