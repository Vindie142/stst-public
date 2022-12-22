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
@Table(name="notifs")
public class Notif implements Comparable<Notif> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne (optional=false)
    @JoinColumn (name="employee_id", nullable = false)
	private Employee employee; // owner
	@Column (length = 512)
	private String text; // content
	@Column (length = 128)
	private String link; // link to content
	private LocalDate birthdate;

	
	public Notif() {}
	
	public Notif(Employee employee, String text, String link) {
		this.employee = employee;
		this.text = text;
		this.link = link;
		this.birthdate = LocalDate.now();
	}
	
	@Override
	public int compareTo(Notif n) {
		return n.getId().compareTo(this.id);
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String employee = this.employee == null ? "" : this.employee.getNormName();
		String text = this.text == null ? "" : this.text;
		return  "Notif{"+
				"id="+id+", "+
				"employee="+employee+", "+
				"text="+text+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    Notif mayBe = (Notif) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = employee == null ? 1 : employee.hashCode();
	    int a3 = text == null ? 1 : text.hashCode();
	    return result * (a1 + a2 + a3);
    }
	
	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
