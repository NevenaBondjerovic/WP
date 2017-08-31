package app.user;

import java.util.List;

import javax.persistence.Entity;

import app.comment.Comment;
import app.complaint.Complaint;
import app.forum.Forum;
import app.theme.Theme;

@Entity
public class User {

	private String username;			//jedinstven
	private String password;
	private String name;
	private String surname;
	private UserRole role;
	private String phone;
	private String email;
	private String registrationDate;	//dd.mm.yyyy.
	private List<Forum> forums;			//spisak pracenih podforuma
	private List<Theme> themes;			//spisak snimljenih tema
	private List<Comment> comments;		//spisak snimljenih komentara
	private List<Complaint> complaints;	//spisak zalbi,za administatora i moderatora
	
	
	public User(String username, String password, String name, String surname, UserRole role, String phone,
			String email, String registrationDate, List<Forum> forums, List<Theme> themes, 
			List<Comment> comments, List<Complaint> complaints) {
		super();
		this.username = username;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.role = role;
		this.phone = phone;
		this.email = email;
		this.registrationDate = registrationDate;
		this.forums = forums;
		this.themes = themes;
		this.comments = comments;
		this.complaints=complaints;
	}

	public User() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}

	public List<Forum> getForums() {
		return forums;
	}

	public void setForums(List<Forum> forums) {
		this.forums = forums;
	}

	public List<Theme> getThemes() {
		return themes;
	}

	public void setThemes(List<Theme> themes) {
		this.themes = themes;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Complaint> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	
}
