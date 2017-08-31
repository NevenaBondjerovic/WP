package app.forum;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.comment.Comment;
import app.complaint.Complaint;
import app.complaint.ComplaintService;
import app.theme.Theme;
import app.theme.ThemeService;
import app.user.User;
import app.user.UserService;

@RestController
@RequestMapping("/forums")
public class ForumController {

	private final ForumService forumService;
	private final ThemeService themeService;
	private final ComplaintService complaintService;
	private final UserService userService;

	@Autowired
	public ForumController(final ForumService forumService,final ThemeService themeService,
			final ComplaintService complaintService, final UserService userService) {
		this.forumService = forumService;
		this.themeService = themeService;
		this.complaintService = complaintService;
		this.userService = userService;
	}
	
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Forum> findAll(){
		return forumService.findAll();
	}
	
	@GetMapping(params="name")
	public synchronized ResponseEntity<Object> findOne(@PathParam("name") String name){
		Forum forum = forumService.findOne(name);
		Optional.ofNullable(forum)
			.orElseThrow(() -> new ResourceNotFoundException("Forum not found!"));
		return new ResponseEntity<Object>(forum, HttpStatus.OK);
	}
	
	@PostMapping
	public synchronized ResponseEntity<Object> save(@RequestBody Forum forum){
		if(validate(forum)){
			Forum f = forumService.save(forum);
			if(f==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(f, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value="/upload")
	public synchronized void setImage(@RequestBody Image image){
		try {
		    // retrieve image
		    BufferedImage bi = ImageIO.read(ImageIO.createImageInputStream(image));
		    File outputfile = new File("src/main/resources/db/image.jpg");
		    ImageIO.write(bi, "jpg", outputfile);
		} catch (IOException e) {

		}
	}
	
	@DeleteMapping(params="forumName")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public synchronized void delete(@PathParam("forumName") String forumName){
		if(forumService.delete(forumName)==false){
			throw new ResourceNotFoundException("Forum not found.");
		}else{
			List<String> themeIdToDelete = new ArrayList<String>();
			for(Theme theme:themeService.findAll()){
				if(theme.getForum().getName().trim().equals(forumName.trim())){
					themeIdToDelete.add(theme.getId());
				}
			}
			for(String id : themeIdToDelete)
				themeService.delete(id);
			
			List<Complaint> complToDelete = new ArrayList<Complaint>();
			for(Complaint compl : complaintService.findAll()){
				if(compl.getForum()!=null && compl.getForum().getName().equals(forumName)){
					complToDelete.add(compl);
				}
			}
			for(Complaint cDelete : complToDelete)
				complaintService.delete(cDelete.getId());
			
			for(User u:userService.findAll()){
				for(int i=0;i<u.getForums().size();i++){
					if(u.getForums().get(i).getName().equals(forumName)){
						u.getForums().remove(i);
						userService.update(u);
						break;
					}
				}
			}
		}
	}
	
	
	
	/**
	 * Proverada da li prosledjen {@link Comment} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(Forum f){
		if(f==null || 
				(f!=null && (f.getName()==null || f.getManager()==null 
				|| f.getManager()==null || f.getManager().getUsername()==null 
				|| f.getManagers()==null || f.getManagers().size()==0 || f.getDescription()==null))){
			return false;
		}
		if(f.getIconPath()==null)
			f.setIconPath("");
		if(f.getRules()==null)
			f.setRules("");
		return true;
	}

	
	
	
	
}
