package uo.sdi.presentation;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import alb.util.log.Log;

/**
 * ManagedBean to manage the actions of the user
 * @author Pablo and Fernando
 *
 */
@ManagedBean(name = "user")
@RequestScoped
public class BeanUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String login;
	private String email;
	private String password;
	private String repeatPassword;
	

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
	
}
