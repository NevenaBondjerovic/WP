package app.message;

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
public class MessageRepository {

	private List<Message> messages;
	private CommonMethods common;
	
	public MessageRepository(){
		this.messages = new ArrayList<Message>();
		common = new CommonMethods();
		initialize();
	}
	
	public void initialize(){
		File file = new File(System.getenv("WEB_PROJECT") + File.separator + "dbMessages.txt");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = "";
			try {
				while((line = in.readLine())!=null){
					line = line.trim();
					String[] parts = line.split(",");
					if(parts.length>1){
						Message message = new Message();
						message.setId(Integer.parseInt(parts[0].trim()));
						User sender = new User(common.getText(parts[1].trim()),null,null,null,
								null,null,null,null,null,null,null,null);
						message.setSender(sender);
						User recipient = new User(common.getText(parts[2].trim()),null,null,
								null,null,null,null,null,null,null,null,null);
						message.setRecipient(recipient);
						
						message.setContent(common.getText(parts[3].trim()));
						
						if(parts[4].trim().equals("false")){
							message.setSeen(false);
						}else{
							message.setSeen(true);
						}
						
						messages.add(message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Message> findAll(){
		return messages;
	}
	
	public Message findOne(Integer id){
		for(Message m : messages){
			if(m.getId().equals(id)){
				return m;
			}
		}
		return null;
	}
	
	public List<Message> findAllByUser(String username){
		List<Message> returnValue = new ArrayList<Message>();
		for(Message m : messages){
			if(m.getSender().getUsername().equals(username) 
					|| m.getRecipient().getUsername().equals(username)){
				returnValue.add(m);
			}
		}
		return returnValue;
	}
	
	public Message save(Message message){
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(
						new FileWriter(System.getenv("WEB_PROJECT") 
								+ File.separator + "dbMessages.txt", true)));
			
			writeInFile(out,message);
			out.close();
			messages.add(message);
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public Message update(Message message){
		for(int i=0;i<messages.size();i++){
			if(messages.get(i).getId().equals(message.getId())){
				messages.set(i, message);
				saveMessages();
				return message;
			}
		}
		return null;
	}
	
	
	private void saveMessages(){
		File file = new File(System.getenv("WEB_PROJECT") 
				+ File.separator + "dbMessages.txt");
     	PrintWriter out = null;
		try {
			out = new PrintWriter(file);
			for(Message message : messages){
				writeInFile(out,message);
			}
			out.close();		
     	} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void writeInFile(PrintWriter out, Message message){
		String toWrite = message.getId()+","+common.setText(message.getSender().getUsername())+","
				+common.setText(message.getRecipient().getUsername())+",";
		
		toWrite+=common.setText(message.getContent());
		
		toWrite+=",";
		if(message.getSeen())
			toWrite+="true";
		else
			toWrite+="false";
		
		out.println(toWrite);
	}
	
	public Integer findId(){
		Integer maxId=1;
		for(Message m : messages){
			if(m.getId()>maxId)
				maxId=m.getId();
		}
		maxId+=1;
		return maxId;
	}
	
	
	
	
	
	
	
	
	
}
