package uo.sdi.presentation;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import alb.util.log.Log;

/**
 * ManagedBean to manage the settings of the application.
 * 
 * @author Pablo and Fernando
 * 
 */
@ManagedBean(name = "settings")
@SessionScoped
public class BeanSettings implements Serializable {
	private static final long serialVersionUID = 2L;
	private static final Locale ENGLISH = new Locale("en");
	private static final Locale SPANISH = new Locale("es");
	private Locale locale = new Locale("en");

	public Locale getLocale() {
		return (locale);
	}

	/**
	 * Set the locale to Spanish
	 */
	public void setSpanish(ActionEvent event) {
		locale = SPANISH;
		try {
			FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
			Log.debug("Language changed to Spanish");
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * Set the locale to English
	 */
	public void setEnglish(ActionEvent event) {
		locale = ENGLISH;
		try {
			FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
			Log.debug("Language changed to English");
		} catch (Exception e) {
			Log.debug(e);
		}
	}

}