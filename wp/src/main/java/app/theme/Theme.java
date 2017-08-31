package app.theme;

import java.util.List;

import app.comment.Comment;
import app.forum.Forum;
import app.user.User;

/**
 * Tema - entitet koji predstavlja multimedijalni sadrzaj.
 */
public class Theme {

	private String id;				//id je forum.name + theme.title
	private Forum forum;			//podforum kome pripada
	private String title;			//naslov - jedinstven u okviru podforuma
	private ThemeType type;
	private User author;			//korisnik koji je napravio temu
	private List<Comment> comments;	//komentari
	private String content;			//sadrzaj
	private String text;			//tekst
	private String date;			//datum kreiranja
	private Integer likes; 			//broj pozitivnih glasova
	private Integer dislikes; 		//broj negativnih glasova
	private List<User> likeUsers;	//korisnici koji su dali pozitivan glas temi
	private List<User> dislikeUsers;//korisnici koji su dali negativan glas temi	

	public Theme() {
		super();
	}


	public Theme(String id, Forum forum, String title, ThemeType type, User author, List<Comment> comments,
			String content, String text,
			String date, Integer likes, Integer dislikes, List<User> likeUsers, List<User> dislikeUsers) {
		super();
		this.id = id;
		this.forum = forum;
		this.title = title;
		this.type = type;
		this.author = author;
		this.comments = comments;
		this.content = content;
		this.text=text;
		this.date = date;
		this.likes = likes;
		this.dislikes = dislikes;
		this.likeUsers=likeUsers;
		this.dislikeUsers=dislikeUsers;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Forum getForum() {
		return forum;
	}


	public void setForum(Forum forum) {
		this.forum = forum;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public ThemeType getType() {
		return type;
	}


	public void setType(ThemeType type) {
		this.type = type;
	}


	public User getAuthor() {
		return author;
	}


	public void setAuthor(User author) {
		this.author = author;
	}


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
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

	
}
