package app.user;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	private final UserRepository userRepo;
	
	@Autowired
	public UserServiceImpl(final UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	@Override
	public List<User> findAll() {
		return userRepo.findAll();
	}

	@Override
	public User findOne(String username) {
		return userRepo.findOne(username);
	}

	@Override
	public User login(String username, String password) {
		User user = userRepo.findOne(username);
		if(user!=null && user.getPassword().equals(password)){
			return user;
		}
		return null;
	}

	@Override
	public User save(User user) {
		return userRepo.save(user);
	}

	@Override
	public User update(User user) {
		return userRepo.update(user);
	}
	
}
