package uo.sdi.presentation;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import alb.util.log.Log;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;

@ManagedBean(name = "user")
@SessionScoped
public class BeanUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String login;
	private String email;
	private String password;
	private String repeatPassword;
	private Boolean isAdmin;
	private UserStatus status;

	private FacesContext context = FacesContext.getCurrentInstance();
	private ResourceBundle msgs = context.getApplication().getResourceBundle(
			context, "msgs");

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public String getRepeatPassword() {
		return repeatPassword;
	}

	public void setRepeatPassword(String repeatPassword) {
		this.repeatPassword = repeatPassword;
	}
	
	/**
	 * Through this method the user is registered in the system.
	 * @return String containing the next view to show
	 */
	public String signUp() {
		
		//If passwords are not equal.
		if (!password.equals(repeatPassword)){
			Log.info("The passwords must be equals");
			return "error";
		}
		UserService userService = Services.getUserService();
		User user = null;
		try {
			user = userService.findLoggableUser(login);
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
		cloneUser.setEmail(email);
		cloneUser.setLogin(login);
		cloneUser.setPassword(password);
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
			user = userService.findLoggableUser(login);
		} catch (BusinessException b) {
			Log.info("Something ocurred when trying to sign in: "
					+ b.getMessage());
		}
		//If the user exists
		if (user != null) {
			//If the password is correct
			if (user.getPassword().equals(password)) {
				this.id = user.getId();
				this.login = user.getLogin();
				this.email = user.getEmail();
				this.status = user.getStatus();
				this.isAdmin = user.getIsAdmin();

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
		
		return "exito";
	}

}
