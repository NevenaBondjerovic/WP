package app.comment;

import java.util.List;

public interface CommentService {

	List<Comment> findAll();
	Comment findOne(String id);
	Comment save(Comment comment);
	Comment update(Comment comment);
	String findId(Comment comment);
	
}
