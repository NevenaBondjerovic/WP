package app.comment;

import java.util.List;

import app.theme.Theme;
import app.user.User;

public class Comment {

	private String id;					//ako nema roditeljskih komenatara -> npr 2
										//ako ima onda -> npr 2 4 1
	private Theme theme;				//tema kojoj pripada
	private User author;
	private String date;					//datum komentara
	private Comment parentComment;		//roditeljski komentar
	private List<Comment> subComments;	//podkomentari
	private String text;				//text komentara
	private Integer likes;
	private Integer dislikes;
	private Boolean commentChanged; 	//oznaka da li je komentar izmenjen
	private List<User> likeUsers;		//korisnici koji su dali pozitivan glas komentaru
	private List<User> dislikeUsers;	//korisnici koji su dali negativan glas komentaru	
	private	Boolean	active;				//da li je komentar aktivan, ako je false, onda je obrisan
	
	public Comment() {
		super();
	}
	
	public Comment(String id, Theme theme, User author, String date, Comment parentComment, List<Comment> subComments,
			String text, Integer likes, Integer dislikes, Boolean commentChanged, List<User> likeUsers, 
			List<User> dislikeUsers, Boolean active) {
		super();
		this.id = id;
		this.theme = theme;
		this.author = author;
		this.date = date;
		this.parentComment = parentComment;
		this.subComments = subComments;
		this.text = text;
		this.likes = likes;
		this.dislikes = dislikes;
		this.commentChanged = commentChanged;
		this.likeUsers=likeUsers;
		this.dislikeUsers=dislikeUsers;
		this.active=active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	public List<Comment> getSubComments() {
		return subComments;
	}

	public void setSubComments(List<Comment> subComments) {
		this.subComments = subComments;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public Integer getDislikes() {
		return dislikes;
	}

	public void setDislikes(Integer dislikes) {
		this.dislikes = dislikes;
	}

	public Boolean getCommentChanged() {
		return commentChanged;
	}

	public void setCommentChanged(Boolean commentChanged) {
		this.commentChanged = commentChanged;
	}
	
	public List<User> getLikeUsers() {
		return likeUsers;
	}


	public void setLikeUsers(List<User> likeUsers) {
		this.likeUsers = likeUsers;
	}


	public List<User> getDislikeUsers() {
		return dislikeUsers;
	}


	public void setDislikeUsers(List<User> dislikeUsers) {
		this.dislikeUsers = dislikeUsers;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	
	
	
}
