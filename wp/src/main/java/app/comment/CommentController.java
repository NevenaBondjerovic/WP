package app.comment;

import java.util.ArrayList;
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

import app.complaint.Complaint;
import app.complaint.ComplaintService;
import app.theme.Theme;
import app.theme.ThemeService;
import app.user.User;
import app.user.UserService;

@RestController
@RequestMapping("/comments")
public class CommentController {

	private final CommentService commentService;
	private final ThemeService themeService;
	private final UserService userService;
	private final ComplaintService complaintService;
	
	@Autowired
	public CommentController(final CommentService commentService, final ThemeService themeService,
			final UserService userService, final ComplaintService complaintService) {
		this.commentService = commentService;
		this.themeService = themeService;
		this.userService = userService;
		this.complaintService = complaintService;
	}

	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Comment> findAll(){
		return commentService.findAll();
	}
	
	@GetMapping(params="id")
	public synchronized ResponseEntity<Object> findOne(@PathParam("id") String id){
		Comment comment = commentService.findOne(id);
		Optional.ofNullable(comment).orElseThrow(() -> new ResourceNotFoundException());
		return new ResponseEntity<Object>(comment, HttpStatus.OK);
	}
	
	@GetMapping(params="themeId")
	@ResponseStatus(HttpStatus.OK)
	public synchronized Theme findCommentsForTheme(@PathParam("themeId") String themeId ){
		Theme theme = themeService.findOne(themeId);
		Optional.ofNullable(theme).orElseThrow(()-> new ResourceNotFoundException("Theme not found!"));
		List<Comment> newList = new ArrayList<Comment>();
		for (Comment comment : theme.getComments()) {
			Comment c = Optional.ofNullable(commentService.findOne(comment.getId())).get();
			newList.add(c);
		}
		theme.setComments(newList);
		return theme;
	}
	
	@GetMapping(params="commentId")
	@ResponseStatus(HttpStatus.OK)
	public synchronized List<Comment> findSubcomments(@PathParam("commentId") String commentId){
		Comment parentComm = commentService.findOne(commentId);
		Optional.ofNullable(parentComm).orElseThrow(()-> new ResourceNotFoundException("Comment not found!"));
		List<Comment> subcomments = new ArrayList<Comment>();
		for (Comment comment : parentComm.getSubComments()) {
			Comment c = Optional.ofNullable(commentService.findOne(comment.getId())).get();
			subcomments.add(c);
		}
		return subcomments;
	}
	

	@PostMapping
	public synchronized ResponseEntity<Object> save(@RequestBody Comment comment){
		if(validate(comment)){
			Comment c = commentService.save(comment);
			if(c==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(c, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PutMapping
	public synchronized ResponseEntity<Object> update(@RequestBody Comment comment){
		if(validate(comment)){
			Comment c = commentService.update(comment);
			if(c==null)
				return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			else
				return new ResponseEntity<Object>(c, HttpStatus.OK);
		}else{
			return new ResponseEntity<Object>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(params="commentId")
	public synchronized ResponseEntity<Object> delete(@PathParam("commentId") String commentId){
		Comment c=commentService.findOne(commentId);
		if(c==null)
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		else{
			c.setActive(false);
			if(commentService.update(c)==null)
				return new ResponseEntity<Object>(HttpStatus.CONFLICT);
			else{
				if(c.getParentComment()!=null){
					Comment parent=commentService.findOne(c.getParentComment().getId());
					for(int i=0;i<parent.getSubComments().size();i++){
						if(parent.getSubComments().get(i).getId().equals(c.getId())){
							parent.getSubComments().remove(i);
							break;
						}
					}
					commentService.update(parent);
				}
				for(Comment subCom : c.getSubComments()){
					Comment subC =commentService.findOne(subCom.getId());
					if(subC!=null){
						subC.setActive(false);
						commentService.update(subC);
						deleteForeignKeys(subC.getId());
					}
				}
				Theme t=themeService.findOne(c.getTheme().getId());
				if(t!=null){
					for(int i=0;i<t.getComments().size();i++){
						if(t.getComments().get(i).getId().equals(c.getId())){
							t.getComments().remove(i);
							themeService.update(t);
							break;
						}
					}
				}
				deleteForeignKeys(commentId);
			}
				
		}
		return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
		
	}
	
	private void deleteForeignKeys(String commentId){

		for(User user : userService.findAll()){
			for(int i=0;i<user.getComments().size();i++){
				if(user.getComments().get(i).getId().equals(commentId)){
					user.getComments().remove(i);
					userService.update(user);
					break;
				}
			}
		}
		List<Complaint> complToDelete = new ArrayList<Complaint>();
		for(Complaint compl : complaintService.findAll()){
			if(compl.getComment()!=null && compl.getComment().getId().equals(commentId)){
				complToDelete.add(compl);
			}
		}
		for(Complaint complDelete : complToDelete){
			complaintService.delete(complDelete.getId());
		}
	}
	
	
	/**
	 * Proverada da li prosledjen {@link Comment} ima sve elemente koje treba da ima.
	 */
	private Boolean validate(Comment c){
		if(c==null || 
				(c!=null && (c.getTheme()==null ||c.getTheme().getId()==null
						|| c.getAuthor()==null || c.getAuthor().getUsername()==null
						|| c.getText()==null))){
			return false;
		}
		if(c.getDate()==null){
			Date today = new Date();
			String[] parts=today.toString().split(" ");
			String date=parts[2]+"."+stringToNumberMonth(parts[1])+"."+parts[5]+".";
			c.setDate(date);
		}
		if(c.getSubComments()==null){
			c.setSubComments(new ArrayList<Comment>());
		}
		if(c.getCommentChanged()==null)
			c.setCommentChanged(false);
		if(c.getLikes()==null)
			c.setLikes(0);
		if(c.getDislikes()==null)
			c.setDislikes(0);
		if(c.getLikeUsers()==null)	
			c.setLikeUsers(new ArrayList<User>());
		if(c.getDislikeUsers()==null)	
			c.setDislikeUsers(new ArrayList<User>());

		if(c.getId()==null){
			c.setId(commentService.findId(c));
		}
		if(c.getActive()==null)
			c.setActive(true);
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
