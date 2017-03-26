package uo.sdi.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import alb.util.log.Log;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessCheck;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.util.MessageProvider;
import uo.sdi.dto.User;


@ManagedBean(name = "login")
@SessionScoped
public class BeanLogin implements Serializable {
	
	private static final long serialVersionUID = 5L;
	private String login;
	private String password; 
	private Boolean isSignedIn;
	

	public String login(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		UserService userService = Services.getUserService();
		User user = null;
		try {
			user = userService.findLoggableUser(getLogin());
			Log.info("User "+ login +" found in database");
		} catch (BusinessException b) {
			BusinessCheck.showBusinessError(b.getMessage());
			Log.error(b);
			return null;
		}
		//If the user exists, session does not contain and user and is new
		if (user != null && session.getAttribute("user")==null){ //&& session.isNew()) {
			//If the password is correct
			if (user.getPassword().equals(getPassword())) {
				
				//Move the user to session
				session.setAttribute("LOGGEDIN_USER", user);
				setIsSignedIn(true);
				
				BusinessCheck.showBusinessInfo(MessageProvider.getValue("loginOk"));
				//If the user is admin
				if (user.getIsAdmin()){
					Log.info("Admin user "+ login +" succesfully logged in");
					return "exitoAdmin";
				}
					
				//If the user is not admin
				else{
					Log.info("User "+login+" succesfully logged in");
					return "exito";
				}
					
				
			}
			//If the password is incorrect
			else{
				Log.info("Incorrect password: "+password);
				BusinessCheck.showBusinessError(MessageProvider.getValue("incorrectPassword"));
				setIsSignedIn(false);
				return null;
			}
		}
		//Otherwise
		else {
			Log.info("Incorrect login: "+login);
			BusinessCheck.showBusinessError(MessageProvider.getValue("incorrectLogin"));
			setIsSignedIn(false);
			return null;
		}
	}


	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Boolean getIsSignedIn() {
		return isSignedIn;
	}


	public void setIsSignedIn(Boolean isSignedIn) {
		this.isSignedIn = isSignedIn;
	}
	

}
