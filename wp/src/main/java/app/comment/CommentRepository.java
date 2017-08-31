package app.comment;

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
import app.theme.Theme;
import app.user.User;

@Repository
public class CommentRepository {

	private List<Comment> comments;
	private CommonMethods common;

	public CommentRepository() {
		this.comments = new ArrayList<Comment>();
		common = new CommonMethods();
		initialize();
	}
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbComments.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						Comment comment = new Comment();
						comment.setId((parts[0].trim()));
						
						if(!parts[1].trim().equals("null"))
							comment.setTheme(new Theme(common.getText(parts[1].trim()), null, 
									null, null, null, null, null, null, null, null,null,null,null));
						else
							comment.setTheme(null);
						
						if(!parts[2].trim().equals("null"))	
							comment.setAuthor(new User(common.getText(parts[2].trim()), null, 
									null, null, null, null, null, null, null, null, null,null));
						else
							comment.setAuthor(null);
						
						comment.setDate(parts[3].trim()); 					
						
						if(!parts[4].trim().equals("null"))	
							comment.setParentComment(new Comment(parts[4].trim(), null, null,
									null, null, null, null, null, null, null,null,null,null));
						else
							comment.setParentComment(null);
						
						String subComments = parts[5].trim();
						List<Comment> subComList = new ArrayList<Comment>();
						if(!subComments.equals("[]")){
							subComments= subComments.substring(1, subComments.length()-1);
							String[] subComParts = subComments.split(";");
							for(int i=0;i<subComParts.length;i++){
								Comment c = new Comment(subComParts[i], null, null, null, 
										null, null, null, null, null, null,null,null,null);
								subComList.add(c);
							}
						}
						comment.setSubComments(subComList);
						
						comment.setText(common.getText(parts[6].trim()));
						comment.setLikes(Integer.parseInt(parts[7].trim()));
						comment.setDislikes(Integer.parseInt(parts[8].trim()));
						comment.setCommentChanged(Boolean.parseBoolean(parts[9].trim()));
	
						String likeUsers = parts[10].trim();
						List<User> likeUserList = new ArrayList<User>();
						if(!likeUsers.equals("[]")){
							likeUsers = likeUsers.substring(1, likeUsers.length()-1);
							String[] likeUserParts = likeUsers.split(";");
							for(int i=0;i<likeUserParts.length;i++){
								User u = new User(common.getText(likeUserParts[i]),null,null,
										null,null,null,null,null,null,null,null,null);
								likeUserList.add(u);
							}
						}
						comment.setLikeUsers(likeUserList);
						
						String dislikeUsers = parts[11].trim();
						List<User> dislikeUserList = new ArrayList<User>();
						if(!dislikeUsers.equals("[]")){
							dislikeUsers = dislikeUsers.substring(1, dislikeUsers.length()-1);
							String[] dislikeUserParts = dislikeUsers.split(";");
							for(int i=0;i<dislikeUserParts.length;i++){
								User u = new User(common.getText(dislikeUserParts[i]),null,
										null,null,null,null,null,null,null,null,null,null);
								dislikeUserList.add(u);
							}
						}
						comment.setDislikeUsers(dislikeUserList);
						if(parts[12].trim().equals("false"))
							comment.setActive(false);
						else
							comment.setActive(true);
						
						comments.add(comment);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Comment findOneWithInactive(String id){
		for(Comment comment : comments){
			if(comment.getId().equals(id))
				return comment;
		}
		return null;
	}
	
	public List<Comment> findAll(){
		return getActiveComments();
	}
	
	public List<Comment> getActiveComments(){
		List<Comment> returnValue = new ArrayList<Comment>();
		for(Comment com : comments){
			if(com.getActive())
				returnValue.add(com);
		}
		return returnValue;
	}
	
	
	public Comment findOne(String id){
		for(Comment comment : getActiveComments()){
			if(comment.getId().equals(id))
				return comment;
		}
		return null;
	}
	

	public Comment save(Comment comment){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") 
								+ File.separator + "dbComments.txt", true)));
			
			writeInFile(out,comment);
			out.close();
			comments.add(comment);
			if(comment.getParentComment()!=null){
				for(int i=0;i<comments.size();i++){
					if(comments.get(i).getId().equals(comment.getParentComment().getId())){
						Comment c=comments.get(i);
						c.getSubComments().add(comment);
						comments.set(i, c);
					}
				}
			}
			return comment;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public Comment update(Comment comment){
		for(int i=0;i<comments.size();i++){
			if(comments.get(i).getId().equals(comment.getId())){
				comments.set(i, comment);
				saveComments();
				return comment;
			}
		}
		return null;
	}
	
	
	private void saveComments(){
		File file = new File(System.getenv("WEB_PROJECT") 
				+ File.separator + "dbComments.txt");
     	PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(Comment comment : comments){
				writeInFile(out,comment);
			}
			out.close();		
     	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String findId(Comment comment){
		Integer maxId=1;
		String returnValue="";
		if(comment.getParentComment()==null){
			for(Comment c : comments){
				if(!c.getId().trim().contains(" ")){
					if(Integer.parseInt(c.getId().trim())>maxId)
						maxId=Integer.parseInt(c.getId().trim());
				}
			}
		}else{
			for(Comment c : comments){
				String id=c.getId().trim();
				if(id.contains(" ")){
					if(id.substring(0, id.indexOf(" "))
							.equals(comment.getParentComment().getId().trim())){
						maxId=Integer.parseInt(id.trim().substring(id.indexOf(" ")+1));
					}
				}
			}
			returnValue+=comment.getParentComment().getId()+" "; 
		}
		maxId+=1;
		returnValue+=maxId.toString();
		return returnValue;
	}
	
	public Boolean delete(String id){
		Comment c = findOneWithInactive(id);
		if(c!=null){
			if(remove(c.getId())){
				for(Comment subCom : c.getSubComments()){
					remove(subCom.getId());
				}
				List<String> inactiveToDelete = new ArrayList<String>();
				for(Comment com : comments){
					if(!com.getActive() && com.getParentComment()!=null 
							&& com.getParentComment().getId().equals(c.getId()))
						inactiveToDelete.add(com.getId());
				}
				for(String inactiveId : inactiveToDelete)
					remove(inactiveId);
				if(c.getParentComment()!=null && c.getParentComment().getId()!=null){
					Comment parentCom = findOne(c.getParentComment().getId());
					if(parentCom!=null){
						parentCom.getSubComments().remove(c);
						update(parentCom);
					}
				}
				saveComments();
			}
		}
		return false;
	}
	
	private Boolean remove(String id){
		for(int i=0;i<comments.size();i++){
			if(comments.get(i).getId().equals(id)){
				comments.remove(i);
				return true;
			}
		}
		return false;
	}

	public List<Comment> getComments() {
		return comments;
	}
	

	private void writeInFile(PrintWriter out, Comment comment){
		String toWrite = comment.getId()+","+common.setText(comment.getTheme().getId())+","
				+common.setText(comment.getAuthor().getUsername())+","+comment.getDate()+",";
		if(comment.getParentComment()==null)
			toWrite+="null";
		else
			toWrite+=comment.getParentComment().getId();
		toWrite+=",[";
		for(int i=0; i<comment.getSubComments().size(); i++){
			toWrite+=comment.getSubComments().get(i).getId();
			if(i!=comment.getSubComments().size()-1)
				toWrite+=";";
		}
		toWrite+="],";
		toWrite+=common.setText(comment.getText());
		toWrite+=","+comment.getLikes().toString()+","+comment.getDislikes().toString()
				+","+comment.getCommentChanged().toString()+",[";
		for(int i=0; i<comment.getLikeUsers().size(); i++){
			toWrite+=common.setText(comment.getLikeUsers().get(i).getUsername());
			if(i!=comment.getLikeUsers().size()-1)
				toWrite+=";";
		}
		toWrite+="],[";
		for(int i=0; i<comment.getDislikeUsers().size(); i++){
			toWrite+=common.setText(comment.getDislikeUsers().get(i).getUsername());
			if(i!=comment.getDislikeUsers().size()-1)
				toWrite+=";";
		}
		toWrite+="],";
		if(!comment.getActive())
			toWrite+="false";
		else
			toWrite+="true";
		out.println(toWrite);
	}
	
	
}
