package com.stst.head.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="objects_actions")
public class Action implements Comparable<Action> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne (optional=false)
    @JoinColumn (name="object_id")
	private Sobject object;
	@Column (length = 512)
	private String action;
	private LocalDateTime date;
	
	public Action() {}
	
	public Action(Sobject object, String action, LocalDateTime date) {
		this.object = object;
		this.action = action;
		this.date = date;
	}
	
	public String getNormalDate() {
		return (date.getDayOfMonth() < 10 ? "0"+date.getDayOfMonth() : date.getDayOfMonth()) +"."+ date.getMonthValue() +"."+ String.valueOf( date.getYear() ).substring(2, 4);
	}
	
	@Override
	public int compareTo(Action a) {
		return a.getDate().compareTo(this.date);
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String object = this.object == null ? "" : this.object.getId().toString();
		String action = this.action == null ? "" : this.action;
		String date = this.date == null ? "" : this.date.toString();
		return  "Action{"+
				"id="+id+", "+
				"object="+object+", "+
				"action="+action+", "+
				"date="+date+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    Action mayBe = (Action) obj;
	    if (id != null && mayBe.getId() !=null) {
	    	return id == mayBe.getId();
		}
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a2 = object == null ? 1 : object.getId().hashCode();
	    int a3 = action == null ? 1 : action.hashCode();
	    int a4 = date == null ? 1 : date.hashCode();
	    return result * (a2 + a3 + a4);
    }
	
	
	public Sobject getObject() {
		return object;
	}
	public void setObject(Sobject object) {
		this.object = object;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
}
