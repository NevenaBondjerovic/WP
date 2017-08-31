package app.user;

import java.util.ArrayList;
import java.util.Date;
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

import app.comment.Comment;
import app.complaint.Complaint;
import app.forum.Forum;
import app.theme.Theme;

@RestController
public class UserController {

	private final UserService userService;
	
	@Autowired
	public UserController(final UserService userService){
		this.userService = userService;
	}
	
	@PostMapping
	@RequestMapping("/login")
	public synchronized ResponseEntity<Object> login(@RequestBody LoginDTO userDTO){
		User user = userService.login(userDTO.getUsername(), userDTO.getPassword());
		Optional.ofNullable(user)
			.orElseThrow(() -> new ResourceNotFoundException("User not found!"));
		return new ResponseEntity<Object>(user, HttpStatus.OK);
	}
	
	@PostMapping
	@RequestMapping("/registration")
	public synchronized ResponseEntity<Object> registration(@RequestBody User user){
		User u = null;
		if(validate(user)){
			u = userService.save(user);
			if(u==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			return new ResponseEntity<Object>(u, HttpStatus.CREATED);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/users")
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<User> findAll(){
		return userService.findAll();
	}
	
	@GetMapping(path="/users", params="username")
	public synchronized ResponseEntity<Object> findOne(@PathParam("username") String username){
		User user = userService.findOne(username);
		Optional.ofNullable(user).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
		return new ResponseEntity<Object>(user, HttpStatus.OK);
	}
	
	@PutMapping(path="/users")
	public synchronized ResponseEntity<Object> update(@RequestBody User user){
		if(validate(user)){
			User u = userService.update(user);
			if(u==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(u, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	/**
	 * Proverada da li je prosledjeni {@link User} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(User u){
		//sredi da ne bude i getUsername==null
		if(u==null || 
				(u!=null && (u.getEmail()==null || u.getName()==null || u.getPassword()==null 
					|| u.getPhone()==null || u.getSurname()==null 
					|| u.getUsername()==null))){
			return false;
		}
		if(u.getRole()==null)
			u.setRole(UserRole.USER);
		if(u.getRegistrationDate()==null){
			Date today = new Date();
			String[] parts=today.toString().split(" ");
			String date=parts[2]+"."+stringToNumberMonth(parts[1])+"."+parts[5]+".";
			u.setRegistrationDate(date);
		}
		if(u.getComments()==null)
			u.setComments(new ArrayList<Comment>());
		if(u.getForums()==null)
			u.setForums(new ArrayList<Forum>());
		if(u.getThemes()==null)
			u.setThemes(new ArrayList<Theme>());
		if(u.getComplaints()==null || u.getRole().equals(UserRole.USER)){
			u.setComplaints(new ArrayList<Complaint>());
		}
		return true;
	}
	
	private String stringToNumberMonth(String month){
		if(month.equals("Jan"))
			return "01";
		else if(month.equals("Feb"))
			return "02";
		else if(month.equals("Mar"))
			return "03";
		else if(month.equals("Apr"))
			return "04";
		else if(month.equals("May"))
			return "05";
		else if(month.equals("Jun"))
			return "06";
		else if(month.equals("Jul"))
			return "07";
		else if(month.equals("Aug"))
			return "08";
		else if(month.equals("Sep"))
			return "09";
		else if(month.equals("Oct"))
			return "10";
		else if(month.equals("Nov"))
			return "11";
		else if(month.equals("Dec"))
			return "12";
		return null;
	}
	
}




