package uo.sdi.presentation;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import alb.util.log.Log;

@ManagedBean(name = "user")
@SessionScoped
public class BeanUser extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	private String repeatPassword;
	private Boolean isSignedIn;

	private FacesContext context = FacesContext.getCurrentInstance();
	private ResourceBundle msgs = context.getApplication().getResourceBundle(
			context, "msgs");
	

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
		}
		
		return "exito";
	
	}

	/**
	 * Through this method the user is logged into the system.
	 * @return String containing the next view to show
	 */
	public String signIn() {

		UserService userService = Services.getUserService();
		User user = null;
		try {
			user = userService.findLoggableUser(getLogin());
		} catch (BusinessException b) {
			Log.info("Something ocurred when trying to sign in: "
					+ b.getMessage());
		}
		//If the user exists
		if (user != null) {
			//If the password is correct
			if (user.getPassword().equals(getPassword())) {
				setId(user.getId());
				setLogin(user.getLogin());
				setEmail(user.getEmail());
				setStatus(user.getStatus());
				setIsAdmin(user.getIsAdmin());
				setIsSignedIn(true);
				BeanSettings settings = (BeanSettings) FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap().get(new String("settings"));
				settings.setUser(this);

				return "exito";
			}
			//Otherwise
			else{
				return "error";
			}
		} else {

			return "error";
		}
	}

	/**
	 * Through this method the user is logged out of the system.
	 * @return String containing the next view to show
	 */
	public String signOut() {
		BeanSettings settings = (BeanSettings) FacesContext
				.getCurrentInstance().getExternalContext().getSessionMap()
				.get(new String("settings"));
		//Invalidate session
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		settings.setUser(null);
		setIsSignedIn(false);
		return "exito";
	}
	
	public String toggleActiveUser(long id){
		AdminService adminService = Services.getAdminService();
		User user;
		String action = "activating/deactivating";
		try {
			user = adminService.findUserById(id);
			if(user.getStatus().equals(UserStatus.ENABLED)){
				action = "deactivating";
				adminService.disableUser(id);
			}	
			else{
				action = "activating";
				adminService.enableUser(id);
			}			
		} catch (BusinessException e1) {
			Log.error(String.format("Some error occured %s "
					+ "user with id: %d . Error: %s ",
					action,id,e1.getMessage()));
			return null;
		}
		
		Log.info(String.format("%s user with id %d was sucessful",
				action,id));
		return "exito";
		
	}
	
	public String deleteUser(long id){
		AdminService service = Services.getAdminService();
		
		try {
			service.deepDeleteUser(id);
		} catch (BusinessException e1) {
			Log.error(String.format("Some error occured deleting user with id: %d . Error: %s ",
					id,e1.getMessage()));
			return null;
		}
		
		Log.info(String.format("User with id %d was deleted sucessfully",
				id));
		return "exito";
		
	}

}
