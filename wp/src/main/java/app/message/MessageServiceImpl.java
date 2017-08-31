package app.message;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MessageServiceImpl implements MessageService{

	private final MessageRepository	 msgRepo;
	
	@Autowired
	public MessageServiceImpl(final MessageRepository msgRepo) {
		this.msgRepo=msgRepo;
	}

	@Override
	public List<Message> findAll() {
		return msgRepo.findAll();
	}

	@Override
	public Message findOne(Integer id) {
		return msgRepo.findOne(id);
	}

	@Override
	public List<Message> findAllByUser(String username) {
		return msgRepo.findAllByUser(username);
	}

	@Override
	public Message save(Message message) {
		if(message.getId()==null){
			message.setId(msgRepo.findId());
			return msgRepo.save(message);
		}
		return null;
	}

	@Override
	public Message update(Message message) {
		if(message.getId()!=null && msgRepo.findOne(message.getId())!=null){
			return msgRepo.update(message);
		}
		return null;
	}

}
