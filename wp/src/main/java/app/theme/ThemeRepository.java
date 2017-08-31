package app.theme;

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
import app.forum.Forum;
import app.user.User;

@Repository
public class ThemeRepository {

	private List<Theme> themes;
	private CommonMethods common;

	public ThemeRepository() {
		this.themes = new ArrayList<Theme>();
		common = new CommonMethods();
		initialize();
	}
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbThemes.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						Theme theme = new Theme();
						theme.setId(common.getText(parts[0].trim()));
						theme.setForum(new Forum(common.getText(parts[1].trim()), null,
								null, null, null, null));
						theme.setTitle(common.getText(parts[2].trim()));
						
						if(parts[3].trim().equals("TEXT"))
							theme.setType(ThemeType.TEXT);
						else if(parts[3].trim().equals("PICTURE"))
							theme.setType(ThemeType.PICTURE);
						else if(parts[3].trim().equals("LINK"))
							theme.setType(ThemeType.LINK);
						
						if(!parts[4].trim().equals("null"))
							theme.setAuthor(new  User(common.getText(parts[4].trim()),
									null, null, null, null, null, null, null, null, null, null,null));
						else
							theme.setAuthor(null);
	
						String comments = parts[5].trim();
						List<Comment> commList = new ArrayList<Comment>();
						if(!comments.equals("[]")){
							comments = comments.substring(1, comments.length()-1);
							String[] commParts = comments.split(";");
							for(int i=0;i<commParts.length;i++){
								Comment c = new Comment(commParts[i], null, null, null,
										null, null, null, null, null, null,null,null,null);
								commList.add(c);
							}
						}
						theme.setComments(commList);
						
						if(parts[6].trim()!="null"){
							theme.setContent(common.getText(parts[6].trim()));
						}else
							theme.setContent(null);
						
						theme.setText(common.getText(parts[7].trim()));
						
						theme.setDate(parts[8].trim()); 					
						theme.setLikes(Integer.parseInt(parts[9].trim()));
						theme.setDislikes(Integer.parseInt(parts[10].trim()));
						
						String likeUsers = parts[11].trim();
						List<User> likeUserList = new ArrayList<User>();
						if(!likeUsers.equals("[]")){
							likeUsers = likeUsers.substring(1, likeUsers.length()-1);
							String[] likeUserParts = likeUsers.split(";");
							for(int i=0;i<likeUserParts.length;i++){
								User u = new User(common.getText(likeUserParts[i]),null,null,null,
										null,null,null,null,null,null,null,null);
								likeUserList.add(u);
							}
						}
						theme.setLikeUsers(likeUserList);
						
						String dislikeUsers = parts[12].trim();
						List<User> dislikeUserList = new ArrayList<User>();
						if(!dislikeUsers.equals("[]")){
							dislikeUsers = dislikeUsers.substring(1, dislikeUsers.length()-1);
							String[] dislikeUserParts = dislikeUsers.split(";");
							for(int i=0;i<dislikeUserParts.length;i++){
								User u = new User(common.getText(dislikeUserParts[i]),null,null,
										null,null,null,null,null,null,null,null,null);
								dislikeUserList.add(u);
							}
						}
						theme.setDislikeUsers(dislikeUserList);
						
						
						themes.add(theme);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public List<Theme> findAll(){
		return themes;
	}
	
	public Theme findOne(String id){
		for (Theme theme : themes) {
			if(theme.getId().equals(id))
				return theme;
		}
		return null;
	}
	
	public Theme save(Theme theme){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") 
								+ File.separator + "dbThemes.txt", true)));
			
			writeInFile(out,theme);
			out.close();
			themes.add(theme);
			return theme;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public Theme update(Theme theme){
		for(int i=0;i<themes.size();i++){
			if(themes.get(i).getId().equals(theme.getId())){
				themes.set(i, theme);
				saveThemes();
				return theme;
			}
		}
		return null;
	}
	
	public boolean delete(String id){
		for(int i=0;i<themes.size();i++){
			if(themes.get(i).getId().equals(id)){
				themes.remove(i);
				saveThemes();
				return true;
			}
		}
		return false;
	}
	
	
	private void saveThemes(){
		File file = new File(System.getenv("WEB_PROJECT") 
				+ File.separator + "dbThemes.txt");
     	PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(Theme theme : themes){
				writeInFile(out,theme);
			}
			out.close();		
     	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void writeInFile(PrintWriter out, Theme theme){
		String toWrite = common.setText(theme.getId())+","+common.setText(theme.getForum().getName())
				+","+common.setText(theme.getTitle())+","
				+theme.getType().toString()+","+common.setText(theme.getAuthor().getUsername());
		toWrite+=",[";
		for(int i=0; i<theme.getComments().size(); i++){
			toWrite+=theme.getComments().get(i).getId();
			if(i!=theme.getComments().size()-1)
				toWrite+=";";
		}
		toWrite+="],";
		if(theme.getContent()!=null){
			toWrite+=common.setText(theme.getContent());
		}else
			toWrite+="null";
		toWrite+=",";
		
		toWrite+=common.setText(theme.getText());
		
		toWrite+=","+theme.getDate()+","+theme.getLikes().toString()+","
				+theme.getDislikes().toString()+",[";
		for(int i=0; i<theme.getLikeUsers().size(); i++){
			toWrite+=common.setText(theme.getLikeUsers().get(i).getUsername());
			if(i!=theme.getLikeUsers().size()-1)
				toWrite+=";";
		}
		toWrite+="],[";
		for(int i=0; i<theme.getDislikeUsers().size(); i++){
			toWrite+=common.setText(theme.getDislikeUsers().get(i).getUsername());
			if(i!=theme.getDislikeUsers().size()-1)
				toWrite+=";";
		}
		toWrite+="]";
		out.println(toWrite);
	}
	
}
