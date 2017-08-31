package app.forum;

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
import app.user.User;

@Repository
public class ForumRepository {

	private List<Forum> forums;
	private CommonMethods common;

	public ForumRepository() {
		this.forums = new ArrayList<Forum>();
		common = new CommonMethods();
		initialize();
	}
	
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbForums.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						Forum forum = new Forum();
						forum.setName(common.getText(parts[0].trim()));
						
						forum.setDescription(common.getText(parts[1].trim()));
						
						forum.setIconPath(common.getText(parts[2].trim()));

						forum.setRules(common.getText(parts[3].trim()));
						
						forum.setManager(new User(common.getText(parts[4].trim()), null, null, 
								null, null, null, null, null, null, null, null,null));
						
						String managers = parts[5].trim();
						List<User> managerList = new ArrayList<User>();
						if(!managers.equals("[]")){
							managers = managers.substring(1, managers.length()-1);
							String[] managerParts = managers.split(";");
							for(int i=0;i<managerParts.length;i++){
								User u = new User(common.getText(managerParts[i]), null, null, 
										null, null, null, null, null, null, null, null,null);
								managerList.add(u);
							}
						}
						forum.setManagers(managerList);
						
						
						forums.add(forum);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Forum> findAll(){
		return forums;
	}
	
	public Forum findOne(String name){
		for (Forum forum : forums) {
			if(forum.getName().equals(name))
				return forum;
		}
		return null;
	}
	

	public Forum save(Forum forum){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") + File.separator + "dbForums.txt", true)));
			
			writeInFile(out,forum);
			out.close();
			forums.add(forum);
			return forum;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private void writeInFile(PrintWriter out, Forum forum){
		String toWrite = common.setText(forum.getName())+",";
		
		toWrite+=common.setText(forum.getDescription());
		
		toWrite+=","+common.setText(forum.getIconPath())+",";
		toWrite+=common.setText(forum.getRules());
		
		toWrite+=","+common.setText(forum.getManager().getUsername());
		
		toWrite+=",[";
		for(int i=0; i<forum.getManagers().size(); i++){
			toWrite+=common.setText(forum.getManagers().get(i).getUsername());
			if(i!=forum.getManagers().size()-1)
				toWrite+=";";
		}
		toWrite+="]";
		
		out.println(toWrite);
	}
	
	public Boolean delete(String forumName){
		for(int i=0;i<forums.size();i++){
			if(forums.get(i).getName().equals(forumName)){
				forums.remove(i);
				saveForums();
				return true;
			}
		}
		
		return false;
	}
	
	
	private void saveForums(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbForums.txt");
     	PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(Forum forum : forums){
				writeInFile(out,forum);
			}
			out.close();		
     	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
