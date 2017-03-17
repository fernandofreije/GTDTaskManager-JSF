package uo.sdi.business.impl.util;

import java.util.regex.Pattern;
import uo.sdi.business.exception.BusinessCheck;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.User;
import uo.sdi.persistence.Persistence;
import uo.sdi.persistence.UserDao;

public class UserCheck {

	public static void isNotAdmin(User user) throws BusinessException {
		String check = MessageProvider.getValue("isNotAdmin"); 
		BusinessCheck.isFalse( user.getIsAdmin(), check);
	}

	public static void isValidEmailSyntax(User user) throws BusinessException {
		String check = MessageProvider.getValue("isValidEmailSyntax"); 
		BusinessCheck.isTrue( isValidEmail( user.getEmail()), check);
	}

	public static void minLoginLength(User user) throws BusinessException {
		String check = MessageProvider.getValue("minLoginLength"); 
		BusinessCheck.isTrue( user.getLogin().length() >= 3, check);
	}

	public static void minPasswordLength(User user) throws BusinessException {
		String check = MessageProvider.getValue("minPasswordLength"); 
		BusinessCheck.isTrue( user.getPassword().length() >= 6, check);
	}

	public static void notRepeatedLogin(User user) throws BusinessException {
		UserDao uDao = Persistence.getUserDao();
		User u = uDao.findByLogin( user.getLogin() );
		BusinessCheck.isNull(u, MessageProvider.getValue("notRepeatedLogin"));
	}

	private static boolean isValidEmail(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}"
				+ "\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])"
				+ "|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		
        return Pattern.compile(ePattern)
        		.matcher(email)
        		.matches();
    }

}
