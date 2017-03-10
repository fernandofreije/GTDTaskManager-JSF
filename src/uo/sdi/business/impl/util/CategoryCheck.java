package uo.sdi.business.impl.util;

import uo.sdi.business.exception.BusinessCheck;
import uo.sdi.business.exception.BusinessException;
import uo.sdi.dto.Category;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import uo.sdi.persistence.Persistence;

public class CategoryCheck {

	public static void nameIsNotNull(Category category) throws BusinessException {
		BusinessCheck.isNotNull( category.getName(), 
				MessageProvider.getValue("nameIsNotNull"));
	}

	public static void nameIsNotEmpty(Category category) throws BusinessException {
		BusinessCheck.isFalse( category.getName().length() == 0 , 
				MessageProvider.getValue("nameIsNotEmpty"));
	}

	public static void userIsNotNull(Category category) throws BusinessException {
		BusinessCheck.isNotNull( category.getUserId(),
				MessageProvider.getValue("userIsNotNull"));
	}

	public static void isValidUser(Category c) throws BusinessException {
		User u = Persistence.getUserDao().findById( c.getUserId() );
		
		BusinessCheck.isNotNull( u,
				MessageProvider.getValue("isValidUser"));
		
		BusinessCheck.isTrue( u.getStatus().equals( UserStatus.ENABLED ),
				MessageProvider.getValue("isEnabledUser"));
	}

	public static void isUniqueName(Category category) throws BusinessException {
		Category other = Persistence.getCategoryDao()
				.findByUserIdAndName( category.getUserId(), category.getName() );
	
		BusinessCheck.isNull( other, 
				MessageProvider.getValue("isUniqueName"));
	}

	public static void isNotForAdminUser(Category category) throws BusinessException {
		User u = Persistence.getUserDao().findById( category.getUserId() );
		BusinessCheck.isFalse( u.getIsAdmin(), MessageProvider.getValue("isNotForAdminUser"));
	}

}
