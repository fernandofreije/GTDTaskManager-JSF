package uo.sdi.business.impl.user.command;

import uo.sdi.business.exception.BusinessException;
import uo.sdi.business.impl.command.Command;
import uo.sdi.dto.User;
import uo.sdi.dto.types.UserStatus;
import uo.sdi.persistence.Persistence;

public class FindByLoginCommand<T> implements Command<User> {

	private String login;

	public FindByLoginCommand(String login) {
		this.login = login;
	}

	@Override
	public User execute() throws BusinessException {
		User user = Persistence.getUserDao().findByLogin(login);

		return (user != null && user.getStatus().equals(UserStatus.ENABLED)) ? user
				: null;
	}

}
