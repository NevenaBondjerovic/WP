package app.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import app.CommonMethods;
import app.comment.Comment;
import app.complaint.Complaint;
import app.forum.Forum;
import app.theme.Theme;

@Repository
public class UserRepository {

	private List<User> users;
	private CommonMethods common;
	
	public UserRepository(){
		this.users = new ArrayList<User>();
		common = new CommonMethods();
		initialize();
	}
	
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbUsers.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						User user = new User();
						user.setUsername(common.getText(parts[0].trim()));
						user.setPassword(common.getText(parts[1].trim()));
						user.setName(parts[2].trim());
						user.setSurname(parts[3].trim());
						if(parts[4].trim().equals("USER")){
							user.setRole(UserRole.USER);
						}else if(parts[4].trim().equals("ADMINISTRATOR")){
							user.setRole(UserRole.ADMINISTRATOR);
						}else if(parts[4].trim().equals("MANAGER")){
							user.setRole(UserRole.MANAGER);
						}
						user.setPhone(parts[5].trim());
						user.setEmail(parts[6].trim());
						user.setRegistrationDate(parts[7]); 	
						
						String forums = parts[8].trim();
						List<Forum> forumList = new ArrayList<Forum>();
						if(!forums.equals("[]")){
							forums = forums.substring(1, forums.length()-1);
							String[] forumParts = forums.split(";");
							for(int i=0;i<forumParts.length;i++){
								Forum forum = new Forum(forumParts[i], null, null, null, null, null);
								forumList.add(forum);
							}
						}
						user.setForums(forumList);
						
						String themes = parts[9].trim();
						List<Theme> themeList = new ArrayList<Theme>();
						if(!themes.equals("[]")){
							themes = themes.substring(1, themes.length()-1);
							String[] themeParts = themes.split(";");
							if(themeParts.length>0){
								for(int i=0;i<themeParts.length;i++){
									Theme theme = new Theme(themeParts[i], null, null, null, null, null, null, null, null, null,null,null,null);
									themeList.add(theme);
								}
							}
						}
						user.setThemes(themeList);
						
						String comments = parts[10].trim();
						List<Comment> commentList = new ArrayList<Comment>();
						if(!comments.equals("[]")){
							comments = comments.substring(1, comments.length()-1);
							String[] commentParts = comments.split(";");
							if(commentParts.length>0){
								for(int i=0;i<commentParts.length;i++){
									Comment comment = new Comment(commentParts[i], null, null, null, null, null, null, null, null, null,null,null,null);
									commentList.add(comment);
								}
							}
						}
						user.setComments(commentList);
						
						String complaints = parts[11].trim();
						List<Complaint> complaintList = new ArrayList<Complaint>();
						if(!complaints.equals("[]")){
							complaints = complaints.substring(1, complaints.length()-1);
							String[] complaintParts = complaints.split(";");
							if(complaintParts.length>0){
								for(int i=0;i<complaintParts.length;i++){
									Complaint c = new Complaint(Integer.parseInt(complaintParts[i]), null, null, null, null, null,null);
									complaintList.add(c);
								}
							}
						}
						user.setComplaints(complaintList);
						
						users.add(user);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<User> findAll(){
		return users;
	}
	
	public User findOne(String username){
		for (User user : users) {
			if(user.getUsername().equals(username))
				return user;
		}
		return null;
	}
	
	public User save(User user){
		if(findOne(user.getUsername())!=null){
				return null;
		}
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") + File.separator + "dbUsers.txt", true)));
			writeInFile(out,user);
			
			out.close();
			users.add(user);
			return user;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void writeInFile(PrintWriter out, User user){
		String toWrite=common.setText(user.getUsername())+","+common.setText(user.getPassword())
					+","+user.getName()+","
					+user.getSurname()+","+user.getRole().toString()+","+user.getPhone()+","
					+user.getEmail()+","+user.getRegistrationDate();
		toWrite+=",[";
		for(int i=0; i<user.getForums().size(); i++){
			toWrite+=user.getForums().get(i).getName();
			if(i!=user.getForums().size()-1)
				toWrite+=";";
		}
		toWrite+="],[";
		for(int i=0; i<user.getThemes().size(); i++){
			toWrite+=user.getThemes().get(i).getId();
			if(i!=user.getThemes().size()-1)
				toWrite+=";";
		}
		toWrite+="],[";
		for(int i=0; i<user.getComments().size(); i++){
			toWrite+=user.getComments().get(i).getId();
			if(i!=user.getComments().size()-1)
				toWrite+=";";
		}
		toWrite+="],[";
		for(int i=0; i<user.getComplaints().size(); i++){
			toWrite+=user.getComplaints().get(i).getId();
			if(i!=user.getComplaints().size()-1)
				toWrite+=";";
		}
		toWrite+="]";
		out.println(toWrite);
	}
	
	public User update(User user){
		if(findOne(user.getUsername())!=null){
			for(int i=0;i<users.size();i++){
				if(users.get(i).getUsername().equals(user.getUsername())){
					users.set(i, user);
					saveUsers();
					return user;
				}
			}
		}
		return null;
	}
	
	private void saveUsers(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbUsers.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(User user : users){
				writeInFile(out,user);
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
