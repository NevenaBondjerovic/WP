package app.forum;

import java.util.List;

import app.user.User;

/**
 * Podforum - entitet koji poseduje teme.
 */
public class Forum {

	private String name;					//jedinstven
	private String description;
	private String iconPath;				//ikonica
	private String rules;					//spisak pravila
	private User manager; 					//odgovorni moderator
	private List<User> managers; 			//moderatori 

	public Forum() {
		super();
	}

	public Forum(String name, String description, String iconPath, String rules, User manager, List<User> managers) {
		super();
		this.name = name;
		this.description = description;
		this.iconPath = iconPath;
		this.rules = rules;
		this.manager = manager;
		this.managers = managers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public User getManager() {
		return manager;
	}

	public void setManager(User manager) {
		this.manager = manager;
	}

	public List<User> getManagers() {
		return managers;
	}

	public void setManagers(List<User> managers) {
		this.managers = managers;
	}
	
	
}
