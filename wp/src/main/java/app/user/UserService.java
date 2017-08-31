package app.user;

import java.util.List;

public interface UserService {

	List<User> findAll();
	User findOne(String username);
	User login(String username, String password);
	User save(User user);
	User update(User user);
	
}
