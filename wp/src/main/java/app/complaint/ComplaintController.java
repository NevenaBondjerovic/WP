package app.complaint;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.comment.CommentService;
import app.forum.Forum;
import app.forum.ForumService;
import app.theme.Theme;
import app.theme.ThemeService;
import app.user.User;
import app.user.UserRole;
import app.user.UserService;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {

	private final ComplaintService complaintService;
	private final UserService userService;
	private final ForumService forumService;
	private final ThemeService themeService;
	private final CommentService commentService;

	@Autowired
	public ComplaintController(final ComplaintService complaintService,
			final UserService userService, final ForumService forumService,
			final ThemeService themeService, final CommentService commentService) {
		super();
		this.complaintService = complaintService;
		this.userService = userService;
		this.forumService = forumService;
		this.themeService = themeService;
		this.commentService = commentService;
	}

	@PostMapping
	public synchronized ResponseEntity<Object> save(@RequestBody Complaint complaint){
		if(complaint.getId()!=null){
			complaint.setId(null);
		}else{
			complaint.setId(complaintService.findId());
		}
		if(validate(complaint)){
			Optional.ofNullable(userService.findOne(complaint.getUser().getUsername()))
				.orElseThrow(() -> new ResourceNotFoundException("User not found!"));
			if(complaint.getForum()!=null)
				Optional.ofNullable(forumService.findOne(complaint.getForum().getName()))
					.orElseThrow(() -> new ResourceNotFoundException("Forum not found!"));
			if(complaint.getTheme()!=null)
				Optional.ofNullable(themeService.findOne(complaint.getTheme().getId()))
					.orElseThrow(() -> new ResourceNotFoundException("Theme not found!"));
			if(complaint.getComment()!=null)
				Optional.ofNullable(commentService.findOne(complaint.getComment().getId()))
					.orElseThrow(() -> new ResourceNotFoundException("Comment not found!"));
			Complaint c = complaintService.save(complaint);
	
			for(User u:userService.findAll()){
				if(u.getRole().equals(UserRole.ADMINISTRATOR)){
					u.getComplaints().add(c);
					userService.update(u);
				}
			}

			if(complaint.getTheme()!=null){
				Forum f = forumService.findOne(complaint.getTheme().getForum().getName());
				if(f!=null){
					User u = userService.findOne(f.getManager().getUsername());
					if(!u.getRole().equals(UserRole.ADMINISTRATOR)){
						u.getComplaints().add(c);
						userService.update(u);
					}
				}
			}
			if(complaint.getComment()!=null){
				Theme t=themeService.findOne(complaint.getComment().getTheme().getId());
				if(t!=null){
					Forum f=forumService.findOne(t.getForum().getName());
					if(f!=null){
						User u=userService.findOne(f.getManager().getUsername());
						if(!u.getRole().equals(UserRole.ADMINISTRATOR)){
							u.getComplaints().add(c);
							userService.update(u);
						}
					}
				}
			}
			if(c==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			return new ResponseEntity<Object>(c, HttpStatus.CREATED);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Complaint> findAll(){
		return complaintService.findAll();
	}
	
	@GetMapping(params="id")
	public synchronized ResponseEntity<Object> findOne(@PathParam("id") Integer id){
		Complaint c = complaintService.findOne(id);
		Optional.ofNullable(c).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
		return new ResponseEntity<Object>(c, HttpStatus.OK);
	}
	
	@PutMapping
	public synchronized ResponseEntity<Object> update(@RequestBody Complaint complaint){
		if(validate(complaint)){
			Complaint c = complaintService.update(complaint);
			if(c==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(c, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(params="id")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public synchronized void delete(@PathParam("id") Integer id){
		if(complaintService.delete(id)==false){
			throw new ResourceNotFoundException("Complaint not found.");
		}
	}
	
	
	/**
	 * Proverada da li je prosledjeni {@link Complaint} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(Complaint c){
		if(c==null || 
				(c!=null && (c.getId()==null || c.getText()==null || c.getUser()==null 
				|| c.getUser().getUsername()==null 
				|| (c.getForum()!=null && c.getForum().getName()==null)
				|| (c.getTheme()!=null && c.getTheme().getId()==null)
				|| (c.getComment()!=null && c.getComment().getId()==null)))){
			return false;
		}

		if(c.getDate()==null){
			Date today = new Date();
			String[] parts=today.toString().split(" ");
			String date=parts[2]+"."+stringToNumberMonth(parts[1])+"."+parts[5]+".";
			c.setDate(date);
		}

		if(c.getForum()==null){
			if(c.getTheme()==null){
				if(c.getComment()==null)
					return false;
			}else{
				if(c.getComment()!=null)
					return false;
			}	
		}else
			if(c.getTheme()!=null || c.getComment()!=null)
				return false;
		
		
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
