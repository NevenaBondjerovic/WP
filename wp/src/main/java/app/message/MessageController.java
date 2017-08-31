package app.message;

import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

	private final MessageService msgService;

	@Autowired
	public MessageController(final MessageService msgService) {
		this.msgService = msgService;
	}
	

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Message> findAll(){
		return msgService.findAll();
	}
	
	@GetMapping(params="id")
	public synchronized ResponseEntity<Object> findOne(@PathParam("id") Integer id){
		Message message = msgService.findOne(id);
		Optional.ofNullable(message).orElseThrow(() -> new ResourceNotFoundException("Message not found."));
		return new ResponseEntity<Object>(message, HttpStatus.OK);
	}

	@GetMapping(params="username")
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Message> findAllByUser(@PathParam("username") String username){
		return msgService.findAllByUser(username);
	}
	
	
	@PostMapping
	public synchronized ResponseEntity<Object> save(@RequestBody Message message){
		if(validate(message)){
			Message m = msgService.save(message);
			if(m==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(m, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping
	public synchronized ResponseEntity<Object> update(@RequestBody Message message){
		if(validate(message)){
			Message m = msgService.update(message);
			if(m==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(m, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	/**
	 * Proverada da li prosledjen {@link Message} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(Message m){
		if(m==null || 
				(m!=null && (m.getSender()==null || m.getSender().getUsername()==null
				|| m.getRecipient()==null || m.getRecipient().getUsername()==null
				|| m.getContent()==null))){
			return false;
		}
		if(m.getSeen()==null)
			m.setSeen(false);;
		return true;
	}

	
	
	
}
