package uo.sdi.presentation;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import alb.util.log.Log;
import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;

/**
 * ManagedBean to manage the users from the role of the administrator
 * @author Pablo and Fernando
 *
 */
@ManagedBean(name = "users")
@RequestScoped
public class BeanUsers {
	
	private List<User> users;
	private List<User> selectedUsers;

	public BeanUsers() {
	}
	
	public String resetDatabase(){
		AdminService adminService = Services.getAdminService();
		try {
			adminService.resetDB();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "exito";
	}
	
	public String toggleActiveUser(User user){
		AdminService adminService = Services.getAdminService();
		String action = "activating/deactivating";
		try {
			if(user.getStatus().equals(UserStatus.ENABLED)){
				action = "deactivating";
				adminService.disableUser(user.getId());
			}	
			else{
				action = "activating";
				adminService.enableUser(user.getId());
			}			
		} catch (BusinessException e1) {
			Log.error(String.format("Some error occured %s "
					+ "user with id: %d . Error: %s ",
					action,user.getId(),e1.getMessage()));
			return null;
		}
		
		Log.info(String.format("%s user with id %d was sucessful",
				action,user.getId()));
		return "exito";
		
	}
	
	public String deleteUser(User user){
		AdminService service = Services.getAdminService();
		try {
			service.deepDeleteUser(user.getId());
		} catch (BusinessException e1) {
			Log.error(String.format("Some error occured deleting user with id: %d . Error: %s ",
					user.getId(),e1.getMessage()));
			return null;
		}
		
		Log.info(String.format("User with id %d was deleted sucessfully",
				user.getId()));
		return "exito";
	}
	
	public String deleteUsers(){
		AdminService service = Services.getAdminService();
		try {
			for (User u : selectedUsers)
				service.deepDeleteUser(u.getId());
		} catch (BusinessException e1) {
			Log.error(String.format("Some error occured deleting users"));
			return null;
		}
		return "exito";
	}
	

	public List<User> getUsers(){
		AdminService adminService = Services.getAdminService();
		try {
			users = adminService.findAllUsers();
		} catch (BusinessException e) {
			
		}
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	
	public List<User> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<User> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	
}
