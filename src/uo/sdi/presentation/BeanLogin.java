package uo.sdi.presentation;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import alb.util.log.Log;
import alb.util.log.LogLevel;
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
			Log.setLogLevel(LogLevel.DEBUG);
			user = userService.findLoggableUser(getLogin());
			Log.debug("User "+ login +" found in database");
		} catch (BusinessException b) {
			BusinessCheck.showBusinessError(b.getMessage());
			Log.debug(b);
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
					Log.debug("Admin user "+ login +" succesfully logged in");
					return "exitoAdmin";
				}
				//If the user is not admin
				else{
					Log.debug("User "+login+" succesfully logged in");
					return "exito";
				}		
			}
			//If the password is incorrect
			else{
				Log.debug("Incorrect password: "+password);
				BusinessCheck.showBusinessError(MessageProvider.getValue("incorrectPassword"));
				setIsSignedIn(false);
				return null;
			}
		}
		//Otherwise
		else {
			Log.debug("Incorrect login: "+login);
			BusinessCheck.showBusinessError(MessageProvider.getValue("incorrectLogin"));
			setIsSignedIn(false);
			return null;
		}
	}
	

	/**
	 * Through this method the user is logged out of the system.
	 * 
	 * @return String containing the next view to show
	 */
	public String logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		session.invalidate();
		Log.debug("User " + login + " logged out");
		return "exito";
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
