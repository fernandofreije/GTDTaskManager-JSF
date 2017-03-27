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
 * ManagedBean to manage the users from the role of the administrator ViewScoped
 * because all operations are ajaxified and we don´t change the view while the
 * user is logged in.
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

	/**
	 * Method to update the list of users whenever an operation is performed in
	 * the system
	 */
	private void setListOfUsers() {
		AdminService adminService = Services.getAdminService();
		try {
			users = adminService.findAllUsers();
			Log.debug("List of users refreshed");
		} catch (BusinessException e) {
			Log.debug(e);
			BusinessCheck.showBusinessError(e.getMessage());
		}
	}

	/**
	 * Method to reset the database
	 * 
	 * @return null because the page is ajaxified
	 */
	public String resetDatabase() {
		AdminService adminService = Services.getAdminService();
		try {
			// Database is reseted
			adminService.resetDB();
			Log.debug("Database reseted");
			setListOfUsers();
		} catch (BusinessException e) {
			Log.debug(e);
			BusinessCheck.showBusinessError(e.getMessage());
		}
		return null;
	}

	/**
	 * Method to change the status of a given user
	 * 
	 * @param user
	 *            to change his status
	 * @return null because the page is ajaxified
	 */
	public String toggleActiveUser(User user) {
		AdminService adminService = Services.getAdminService();
		try {
			// If user is enabled, we disable it
			if (user.getStatus().equals(UserStatus.ENABLED)) {
				adminService.disableUser(user.getId());
				Log.debug("User " + user.getLogin() + " succesfully deactivated");
			}
			// Otherwise, we enable it
			else {
				adminService.enableUser(user.getId());
				Log.debug("User " + user.getLogin() + " succesfully activated");
			}
			setListOfUsers();
		} catch (BusinessException e1) {
			Log.debug(e1);
			BusinessCheck.showBusinessError(e1.getMessage());
		}
		return null;
	}

	/**
	 * Method to delete a single user from the system
	 * 
	 * @param user
	 *            to delete
	 * @return null because the page is ajaxified
	 */
	public String deleteUser(User user) {
		AdminService service = Services.getAdminService();
		try {
			// The user can not delete to himself
			if (user.equals(userSession)) {
				Log.debug("An admin cannot delete himself");
				BusinessCheck.showBusinessError(MessageProvider
						.getValue("adminDeleteHimself"));
			} else {
				service.deepDeleteUser(user.getId());
				setListOfUsers();
				Log.debug(String.format(
						"User with id %d was deleted sucessfully", user.getId()));
			}
		} catch (BusinessException e1) {
			Log.debug(e1);
			BusinessCheck.showBusinessError(e1.getMessage());
		}
		return null;
	}

	/**
	 * Method to delete a list of selected users from the system
	 * 
	 * @param user
	 *            to delete
	 * @return null because the page is ajaxified
	 */
	public String deleteUsers() {
		AdminService service = Services.getAdminService();
		try {
			for (User u : selectedUsers) {
				// The user can not delete to himself
				if (u.equals(userSession)) {
					Log.debug("An admin cannot delete himself");
					BusinessCheck.showBusinessError(MessageProvider
							.getValue("adminDeleteHimself"));
				} else {
					service.deepDeleteUser(u.getId());
					Log.debug(String.format(
							"User with id %d was deleted sucessfully",
							u.getId()));
					setListOfUsers();
				}
			}
		} catch (BusinessException e1) {
			Log.debug(e1);
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
