package app.forum;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ForumServiceImpl implements ForumService{

	private final ForumRepository forumRepo;

	@Autowired
	public ForumServiceImpl(final ForumRepository forumRepo) {
		this.forumRepo = forumRepo;
	}

	@Override
	public List<Forum> findAll() {
		return forumRepo.findAll();
	}

	@Override
	public Forum findOne(String name) {
		return forumRepo.findOne(name);
	}

	@Override
	public Forum save(Forum forum) {
		return forumRepo.save(forum);
	}

	@Override
	public Boolean delete(String forumName) {
		return forumRepo.delete(forumName);
	}
	
}
