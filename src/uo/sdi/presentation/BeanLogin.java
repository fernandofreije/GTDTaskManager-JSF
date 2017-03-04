package uo.sdi.presentation;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import alb.util.log.Log;


@ManagedBean(name = "login")
@SessionScoped
public class BeanLogin extends User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Boolean isSignedIn;
	
	public Boolean getIsSignedIn() {
		return isSignedIn;
	}

	public void setIsSignedIn(Boolean isSignedIn) {
		this.isSignedIn = isSignedIn;
	}

	public String login(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		UserService userService = Services.getUserService();
		User user = null;
		try {
			user = userService.findLoggableUser(getLogin());
		} catch (BusinessException b) {
			Log.info("Something ocurred when trying to sign in: "
					+ b.getMessage());
		}
		//If the user exists, session does not contain and user and is new
		if (user != null && session.getAttribute("user")==null && session.isNew()) {
			//If the password is correct
			if (user.getPassword().equals(getPassword())) {
				
				//Initialize user
				setId(user.getId());
				setLogin(user.getLogin());
				setEmail(user.getEmail());
				setPassword(user.getPassword());
				setIsAdmin(user.getIsAdmin());
				setStatus(user.getStatus());
				
				//Move the user to session
				session.setAttribute("user", user);
				setIsSignedIn(true);
				
				//If the user is admin
				if (user.getIsAdmin())
					return "adminPanel";
				//If the user is not admin
				else
					return "exito";
			}
			//Otherwise
			else{
				setIsSignedIn(false);
				return null;
			}
		} else {
			setIsSignedIn(false);
			return null;
		}
	}
	

}
