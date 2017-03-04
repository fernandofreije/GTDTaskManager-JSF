package uo.sdi.presentation;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import alb.util.log.Log;
import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;

@ManagedBean(name = "users")
@RequestScoped
public class BeanUsers {
	
	private String login;
	private String email;
	private String password;
	private String repeatPassword;
	
	private Boolean isSignedIn;
	
	private List<User> users;
	private User user = new User();

	public BeanUsers() {
	}
	
	/**
	 * Through this method the user is registered in the system.
	 * @return String containing the next view to show
	 */
	public String signUp() {
		
		//If passwords are not equal.
		if (!getPassword().equals(getRepeatPassword())){
			Log.info("The passwords must be equals");
			return "error";
		}
		UserService userService = Services.getUserService();
		User user = null;
		try {
			user = userService.findLoggableUser(getLogin());
		} catch (BusinessException b) {
			Log.info("Something ocurred when trying to sign up: "
					+ b.getMessage());
			return "error";
		}
		//If user is already registered.
		if (user != null){
			Log.info("There exist a user registered with that login");
			return "error";
		}
		//Otherwise, save the user in the db.
		User cloneUser = new User();
		cloneUser.setEmail(getEmail());
		cloneUser.setLogin(getLogin());
		cloneUser.setPassword(getPassword());
		try {
			userService.registerUser(cloneUser);
		} catch (BusinessException b) {
			Log.info("Something ocurred when trying to sign up: "
					+ b.getMessage());
			return "error";
		}
		
		return "exito";
	
	}

	/**
	 * Through this method the user is logged out of the system.
	 * @return String containing the next view to show
	 */
	public String logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		session.invalidate();
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

	public List<User> getUsers(){
		AdminService adminService = Services.getAdminService();
		List<User> listOfUsers = null;
		try {
			listOfUsers = adminService.findAllUsers();
		} catch (BusinessException e) {
			
		}
		return listOfUsers;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
	
	public Boolean getIsSignedIn() {
		return isSignedIn;
	}

	public void setIsSignedIn(Boolean isSignedIn) {
		this.isSignedIn = isSignedIn;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
