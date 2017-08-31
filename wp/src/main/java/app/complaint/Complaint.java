package app.complaint;

import javax.persistence.Entity;

import app.comment.Comment;
import app.forum.Forum;
import app.theme.Theme;
import app.user.User;

@Entity
public class Complaint {

	private Integer id;
	private String text;				//text zalbe
	private String date;				//datum zalbe
	private Forum forum;
	private Theme theme;
	private Comment comment;			//forum,theme,comment -> 
										//-> jedno mora da ne bude null,ostala dva moraju null
	private User user;					//korisnik koji je ulozio zalbu

	
	public Complaint() {
	}


	public Complaint(Integer id, String text, String date, Forum forum, Theme theme, Comment comment, User user) {
		super();
		this.id = id;
		this.text = text;
		this.date = date;
		this.forum = forum;
		this.theme = theme;
		this.comment = comment;
		this.user = user;
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public Forum getForum() {
		return forum;
	}


	public void setForum(Forum forum) {
		this.forum = forum;
	}


	public Theme getTheme() {
		return theme;
	}


	public void setTheme(Theme theme) {
		this.theme = theme;
	}


	public Comment getComment() {
		return comment;
	}


	public void setComment(Comment comment) {
		this.comment = comment;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}
	
	
	
}
