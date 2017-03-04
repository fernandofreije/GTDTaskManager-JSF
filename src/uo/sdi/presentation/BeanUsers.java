package uo.sdi.presentation;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import uo.sdi.business.AdminService;
import uo.sdi.business.Services;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;

@ManagedBean(name = "users")
@RequestScoped
public class BeanUsers {
	
	private List<User> users;

	public BeanUsers() {
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
}
