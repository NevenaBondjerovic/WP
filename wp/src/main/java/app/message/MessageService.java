package app.message;

import java.util.List;

public interface MessageService {

	List<Message> findAll();
	Message findOne(Integer id);
	List<Message> findAllByUser(String username);
	Message save(Message message);
	Message update(Message message);
	
}
