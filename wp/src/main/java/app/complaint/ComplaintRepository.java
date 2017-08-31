package app.complaint;

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
import app.theme.Theme;
import app.user.User;

@Repository
public class ComplaintRepository {

	private List<Complaint> complaints;
	private CommonMethods common;
	
	public ComplaintRepository(){
		this.complaints = new ArrayList<Complaint>();
		common = new CommonMethods();
		initialize();
	}
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbComplaints.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						Complaint complaint = new Complaint();
						complaint.setId(Integer.parseInt(parts[0].trim()));
						
						complaint.setText(common.getText(parts[1].trim()));
						
						complaint.setDate(parts[2].trim());
						
						if(parts[3].trim().equals("null"))
							complaint.setForum(null);
						else{
							Forum f = new Forum(common.getText(parts[3].trim()),null,null,null,
									null,null);
							complaint.setForum(f);
						}
						
						if(parts[4].trim().equals("null"))
							complaint.setTheme(null);
						else{
							Theme t = new Theme(common.getText(parts[4].trim()),null,null,null,
									null,null,null,null,null,null,null,null,null);
							complaint.setTheme(t);
						}
						
						if(parts[5].trim().equals("null"))
							complaint.setComment(null);
						else{
							Comment c = new Comment(parts[5].trim(),null,null,null,null,
									null,null,null,null,null,null,null,null);
							complaint.setComment(c);
						}
						
						if(parts[6].trim().equals("null"))
							complaint.setUser(null);
						else{
							User u = new User(common.getText(parts[6].trim()),null,null,null,
									null,null,null,null,null,null,null,null);
							complaint.setUser(u);
						}
						
						
						complaints.add(complaint);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	

	public List<Complaint> findAll(){
		return complaints;
	}
	
	
	public Complaint findOne(Integer id){
		for(Complaint complaint : complaints){
			if(complaint.getId().equals(id))
				return complaint;
		}
		return null;
	}
	

	public Complaint save(Complaint complaint){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") 
								+ File.separator + "dbComplaints.txt", true)));
			
			writeInFile(out,complaint);
			out.close();
			complaints.add(complaint);
			return complaint;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public Complaint update(Complaint complaint){
		for(int i=0;i<complaints.size();i++){
			if(complaints.get(i).getId().equals(complaint.getId())){
				complaints.set(i, complaint);
				saveComplaints();
				return complaint;
			}
		}
		return null;
	}
	
	
	private void saveComplaints(){
		File file = new File(System.getenv("WEB_PROJECT") 
				+ File.separator + "dbComplaints.txt");
     	PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(Complaint complaint : complaints){
				writeInFile(out,complaint);
			}
			out.close();		
     	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void writeInFile(PrintWriter out, Complaint complaint){
		String toWrite = complaint.getId().toString()+",";
		
		toWrite+=common.setText(complaint.getText());

		toWrite+=","+complaint.getDate()+",";
		if(complaint.getForum()==null || complaint.getForum().getName()==null){
			toWrite+="null,";
		}else{
			toWrite+=common.setText(complaint.getForum().getName())+",";
		}
		
		if(complaint.getTheme()==null || complaint.getTheme().getId()==null){
			toWrite+="null,";
		}else{
			toWrite+=common.setText(complaint.getTheme().getId())+",";
		}
		
		if(complaint.getComment()==null || complaint.getComment().getId()==null){
			toWrite+="null,";
		}else{
			toWrite+=complaint.getComment().getId()+",";
		}

		if(complaint.getUser()==null || complaint.getUser().getUsername()==null){
			toWrite+="null,";
		}else{
			toWrite+=common.setText(complaint.getUser().getUsername());
		}
		
		out.println(toWrite);
	}
	
	public Integer findId(){
		Integer maxId=1;
		for(Complaint c : complaints){
			if(c.getId()>maxId)
				maxId=c.getId();
		}
		maxId+=1;
		return maxId;
	}
	
	public Boolean delete(Integer id){
		for(int i=0;i<complaints.size();i++){
			if(complaints.get(i).getId().equals(id)){
				complaints.remove(i);
				saveComplaints();
				return true;
			}
		}
		return false;
	}
	
	
	
}
