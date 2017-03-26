package uo.sdi.presentation;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessCheck;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.MessageProvider;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import alb.util.log.Log;

/**
 * ManagedBean to manage the users from the role of the administrator
 * 
 * @author Pablo and Fernando
 * 
 */
@ManagedBean(name = "users")
@ViewScoped
public class BeanUsers {

	private User userSession;
	private List<User> users;
	private List<User> selectedUsers;

	public BeanUsers() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		this.userSession = (User) session.getAttribute("LOGGEDIN_USER");
	}

	@PostConstruct
	public void init() {
		if (users == null)
			setListOfUsers();
	}

	private void setListOfUsers() {
		AdminService adminService = Services.getAdminService();
		try {
			users = adminService.findAllUsers();
			Log.info("List of users refreshed");
		} catch (BusinessException e) {
			Log.error(e);
			BusinessCheck.showBusinessError(e.getMessage());
		}
	}

	public String resetDatabase() {
		AdminService adminService = Services.getAdminService();
		try {
			adminService.resetDB();
			Log.info("Database reseted");
			setListOfUsers();
		} catch (BusinessException e) {
			Log.error(e);
			BusinessCheck.showBusinessError(e.getMessage());
		}
		return null;
	}

	public String toggleActiveUser(User user) {
		AdminService adminService = Services.getAdminService();
		try {
			if (user.getStatus().equals(UserStatus.ENABLED)) {
				adminService.disableUser(user.getId());
				Log.info("User "+user.getLogin()+" succesfully deactivated");
			} else {
				adminService.enableUser(user.getId());
				Log.info("User "+user.getLogin()+" succesfully activated");
			}
			setListOfUsers();
		} catch (BusinessException e1) {
			Log.error(e1);
			BusinessCheck.showBusinessError(e1.getMessage());
		}
		
		return null;

	}

	public String deleteUser(User user) {
		AdminService service = Services.getAdminService();
		try {
			// The user can not delete to himself
			if (user.equals(userSession)) {
				Log.info("An admin cannot delete himself");
				BusinessCheck.showBusinessError(MessageProvider.getValue("adminDeleteHimself"));
			} else {
				service.deepDeleteUser(user.getId());
				setListOfUsers();
			}

		} catch (BusinessException e1) {
			Log.error(e1);
			BusinessCheck.showBusinessError(e1.getMessage());
		}

		Log.info(String.format("User with id %d was deleted sucessfully",
				user.getId()));
		return null;
	}

	public String deleteUsers() {
		AdminService service = Services.getAdminService();
		try {
			for (User u : selectedUsers) {
				// The user can not delete to himself
				if (u.equals(userSession)) {
					Log.info("An admin cannot delete himself");
					BusinessCheck.showBusinessError(MessageProvider.getValue("adminDeleteHimself"));
				} else {
					service.deepDeleteUser(u.getId());
					Log.info(String.format("User with id %d was deleted sucessfully",
							u.getId()));
					setListOfUsers();
				}
			}
		
		} catch (BusinessException e1) {
			Log.error(e1);
			BusinessCheck.showBusinessError(e1.getMessage());
		}
		
		return null;
	}

	public List<User> getUsers() {
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
