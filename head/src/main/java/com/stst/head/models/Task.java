package com.stst.head.models;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="tasks")
public class Task implements Comparable<Task> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (length = 512)
	private String text;
	@ManyToOne (optional=false)
	@JoinColumn(name="owner_id")
	private Employee owner;
	@ManyToOne
	@JoinColumn(name="sender_id")
	private Employee sender;
	@ManyToOne
	@JoinColumn(name="object_id")
	private Sobject object;
	@Column(nullable = false)
	private LocalDate birthdate;
	private boolean message; // if it's a question or answer on the problem. Deleted without notification after responding to this task
		
	public Task() {}
	
	public Task(Employee owner, Employee sender, Sobject object, String text) {
		this.owner = owner;
		this.sender = sender;
		this.object = object;
		this.text = text;
		this.birthdate = LocalDate.now();
	}
	public Task(Employee owner, Employee sender, Sobject object, String text, boolean message) {
		this.owner = owner;
		this.sender = sender;
		this.object = object;
		this.text = text;
		this.birthdate = LocalDate.now();
		this.message = message;
	}
	
	@Override
	public int compareTo(Task a) {
		return a.getId().compareTo(this.id);
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String text = this.text == null ? "" : this.text;
		String owner = this.owner == null ? "" : this.owner.getId().toString();
		String sender = this.sender == null ? "" : this.sender.getId().toString();
		String object = this.object == null ? "" : this.object.getId().toString();
		return  "Task{"+
				"id="+id+", "+
				"text="+text+", "+
				"owner="+owner+", "+
				"sender="+sender+", "+
				"object="+object+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    Task mayBe = (Task) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = text == null ? 1 : text.hashCode();
	    int a5 = birthdate == null ? 1 : birthdate.hashCode();
	    return result * (a1 + a2 + a5);
    }
	
	
	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public boolean isMessage() {
		return message;
	}

	public void setMessage(boolean message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Employee getOwner() {
		return owner;
	}

	public void setOwner(Employee owner) {
		this.owner = owner;
	}

	public Employee getSender() {
		return sender;
	}

	public void setSender(Employee sender) {
		this.sender = sender;
	}

	public Sobject getObject() {
		return object;
	}

	public void setObject(Sobject object) {
		this.object = object;
	}
	
	// id, sender(id, name, surname, secondName), object(id, street, building), text, message
	// for Repo
	public Task(Long id, String text, boolean message, Long employeeId, String employeeName,
				String employeeSurname, String employeeSecondName, Long objectId, String objectStreet, String objectBuilding) {
		this.id = id;
		this.sender = new Employee(employeeId, employeeName, employeeSurname, employeeSecondName);
		this.object = new Sobject(objectId, objectStreet, objectBuilding);
		this.text = text;
		this.message = message;
	}
}
