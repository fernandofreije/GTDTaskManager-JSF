package uo.sdi.presentation.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;

/**
 * Servlet Filter implementation class LoginFilterAdmin
 */
@WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, urlPatterns = { "/restricted/*" }, initParams = { @WebInitParam(name = "LoginParam", value = "/login.xhtml"), @WebInitParam(name = "ListParam", value = "/restrictedAdmin/panelAdmin.xhtml") })
public class LoginFilterUser implements Filter {

	FilterConfig config = null;

	/**
	 * Default constructor.
	 */
	public LoginFilterUser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// Si no es petición HTTP nada que hacer
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}
		
		// En el resto de casos se verifica que se haya hecho login previamente
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		// Si no hay login, redirección al formulario de login
		if (session.getAttribute("LOGGEDIN_USER") == null) {
			String loginForm = config.getInitParameter("LoginParam");
			res.sendRedirect(req.getContextPath() + loginForm);
			return;
		}
		//Si hay login
		else{
			User user = (User) session.getAttribute("LOGGEDIN_USER");
			//Si es admin, redireccion a la página de admin
			if (user.getIsAdmin()){
				String loginForm = config.getInitParameter("ListParam");
				res.sendRedirect(req.getContextPath() + loginForm);
				return;
			}
			//Si el usuario está desactivado, redirección al formulario de login
			else if (user.getStatus().equals(UserStatus.DISABLED)){
				String loginForm = config.getInitParameter("LoginParam");
				res.sendRedirect(req.getContextPath() + loginForm);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		config = fConfig;
	}

}
