package app.forum;

import java.util.List;

public interface ForumService {

	List<Forum> findAll();
	Forum findOne(String name);
	Forum save(Forum forum);
	Boolean delete(String forumName);
	
}
