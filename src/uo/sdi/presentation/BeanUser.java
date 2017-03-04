package uo.sdi.presentation;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.UserService;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import alb.util.log.Log;

@ManagedBean(name = "user")
@RequestScoped
public class BeanUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String login;
	private String email;
	private String password;
	private String repeatPassword;
	
	private Boolean isSignedIn;

	


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
	

}
