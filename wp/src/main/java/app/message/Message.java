package app.message;

import app.user.User;

/**
 * Entitet direktne komunikacije izmedju dva korisnika.
 */
public class Message {

	private Integer id;
	private User sender;
	private User recipient;
	private String content;
	private Boolean seen;

	public Message() {
		super();
	}

	public Message(Integer id, User sender, User recipient, String content, Boolean seen) {
		super();
		this.id = id;
		this.sender = sender;
		this.recipient = recipient;
		this.content = content;
		this.seen = seen;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getSeen() {
		return seen;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}
	
	
	
}
