package app.comment;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentServiceImpl implements CommentService{

	private final CommentRepository commentRepo;
	
	@Autowired
	public CommentServiceImpl(final CommentRepository commentRepo) {
		this.commentRepo = commentRepo;

	}

	@Override
	public List<Comment> findAll() {
		return commentRepo.findAll();
	}

	@Override
	public Comment findOne(String id) {
		return commentRepo.findOne(id);
	}

	@Override
	public Comment save(Comment comment) {
		if(commentRepo.findOne(comment.getId())!=null)	
			return	null;
		return commentRepo.save(comment);
	}

	@Override
	public Comment update(Comment comment) {
		return commentRepo.update(comment);
	}

	@Override
	public String findId(Comment comment) {
		return (commentRepo.findId(comment));
	}

	
	
}
