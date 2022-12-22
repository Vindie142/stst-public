package com.stst.head.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="objects")
public class Sobject implements Comparable<Sobject> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (length = 128)
	private String custName;
	@Column (length = 128)
	private String custPhone;
	@Column (length = 128)
	private String custEmail;
	
	@Column(nullable = false, length = 128)
	private String street;
	@Column (length = 128)
	private String district;
	@Column(nullable = false, length = 128)
	private String building;
	@Column (length = 512)
	private String note;
	@Column (length = 128)
	private String works;
	@ManyToOne (optional=false)
	@JoinColumn(name="pto_id")
	private Employee pto;
	@ManyToOne (optional=true)
	@JoinColumn(name="master_id")
	private Employee master;
	@ManyToOne (optional=true)
	@JoinColumn(name="salesman_id")
	private Employee salesman;

	private LocalDate birthdate;
	
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private CityEnum city;
	
	@ElementCollection(targetClass=LocalDate.class , fetch=FetchType.EAGER)
	@JoinTable(name="objects_statuses")
	Map<Byte, LocalDate> statuses = new HashMap<Byte, LocalDate>();
	
	@OneToMany(mappedBy="object", fetch=FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	List<Action> actions = new ArrayList<Action>();
	
	
	public Sobject() {}
	
	public Sobject(CityEnum city, String street, String district, String building, String custName, String custPhone, String custEmail, String note,
			String works, Employee pto, Employee master, Employee salesman, Byte status) {
		this.city = city;
		this.street = street;
		this.district = district;
		this.building = building;
		this.custName = custName;
		this.custPhone = custPhone;
		this.custEmail = custEmail;
		this.note = note;
		this.works = works;
		this.pto = pto;
		this.master = master;
		this.salesman = salesman;
		this.statuses.put(status,LocalDate.now());
		this.birthdate = LocalDate.now();
	}
	
	public void addAction(String text) {
		actions.add(new Action(this, text, LocalDateTime.now()));
    }
	
	public Byte getCurrStatus() { // gives the highest status (current)
		for (Byte i = 9; i >= 0; i--) {
			if (statuses.get(i) != null) {
				return i;
			}
		}
		return -1;
    }
	
	public void addStatus(Byte status, LocalDate date) { // logic of setting statuses
		if (status == null) {
			return;
		}
		if ( (status == 0 || status == 1 || status == 2) && date != null ) {
			statuses.put(Byte.valueOf("0"), null);
			statuses.put(Byte.valueOf("1"), null);
			statuses.put(Byte.valueOf("2"), null);
			statuses.put(status, date);
		} else {
			statuses.put(status, date);
		}
		if (date != null && status < 8) {
			for (byte i = 3; i < status; i++) {
				if (statuses.get(i) == null) {
					statuses.put(i, date);
				}
			}
		}
    }
	
	public String getStatus(Byte status) { // transmits a beautiful date value by status
		for (Map.Entry<Byte, LocalDate> entry : statuses.entrySet()) {
			if (entry.getKey() == status) {
				LocalDate date = entry.getValue();
				if (date == null) {return null;}
				return (date.getDayOfMonth() < 10 ? "0"+date.getDayOfMonth() : date.getDayOfMonth()) +"."+ date.getMonthValue() +"."+ String.valueOf( date.getYear() ).substring(2, 4);
			}
		}
		return null; // we return null if there is no status
    }
	
	public String getCurrAction() {
		return Collections.min(actions).getAction();
    }
	
	public List<Action> getSortActions() {
		Collections.sort(actions);
		return actions;
	}
	
	public String getNormName() {
		return "[" + getId() + "] " + getStreet() + ", " + getBuilding();
	}
	
	public static String getNormName(Long id, String street, String building) {
		Sobject object = new Sobject();
		object.setId(id);
		object.setStreet(street);
		object.setBuilding(building);
		String result = object.getNormName();
		object = null;
		return result;
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String city = this.city == null ? "" : this.city.toString();
		String street = this.street == null ? "" : this.street;
		String building = this.building == null ? "" : this.building;
		String note = this.note == null ? "" : this.note;
		String works = this.works == null ? "" : this.works;
		String pto = this.pto == null ? "" : this.pto.getNormName();
		String master = this.master == null ? "" : this.master.getNormName();
		String salesman = this.salesman == null ? "" : this.salesman.getNormName();
		String birthdate = this.birthdate == null ? "" : this.birthdate.toString();
		return  "Sobject{"+
				"id="+id+", "+
				"city="+city+", "+
				"street="+street+", "+
				"building="+building+", "+
				"note="+note+", "+
				"works="+works+", "+
				"pto="+pto+", "+
				"master="+master+", "+
				"birthdate="+birthdate+", "+
				"salesman="+salesman+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    
	    Sobject mayBe = (Sobject) obj;
	    if (id != null && mayBe.getId() !=null) {
	    	return id == mayBe.getId();
		}
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
		int a1 = id == null ? 1 : id.hashCode();
	    int a2 = city == null ? 1 : city.hashCode();
	    int a3 = street == null ? 1 : street.hashCode();
	    int a4 = building == null ? 1 : building.hashCode();
	    int a5 = birthdate == null ? 1 : birthdate.hashCode();
	    int a6 = note == null ? 1 : note.hashCode();
	    return result * (a1 + a2 + a3 + a4 + a5 + a6);
    }

	@Override
	public int compareTo(Sobject a) { // in the output order
		if (a.getCurrStatus() == 0) {return 1;}
		if (this.getCurrStatus() == 0) {return -1;}
		if (a.getCurrStatus() == 1) {return 1;}
		if (this.getCurrStatus() == 1) {return -1;}
		if (a.getCurrStatus() == 4) {return 1;}
		if (this.getCurrStatus() == 4) {return -1;}
		return this.getCurrStatus() - a.getCurrStatus();
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getCustPhone() {
		return custPhone;
	}

	public void setCustPhone(String custPhone) {
		this.custPhone = custPhone;
	}

	public String getCustEmail() {
		return custEmail;
	}

	public void setCustEmail(String custEmail) {
		this.custEmail = custEmail;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getWorks() {
		return works;
	}

	public void setWorks(String works) {
		this.works = works;
	}

	public Employee getPto() {
		return pto;
	}

	public void setPto(Employee pto) {
		this.pto = pto;
	}

	public Employee getMaster() {
		return master;
	}

	public void setMaster(Employee master) {
		this.master = master;
	}

	public Employee getSalesman() {
		return salesman;
	}

	public void setSalesman(Employee salesman) {
		this.salesman = salesman;
	}

	public CityEnum getCity() {
		return city;
	}

	public void setCity(CityEnum city) {
		this.city = city;
	}

	public Map<Byte, LocalDate> getStatuses() {
		return statuses;
	}

	public void setStatuses(Map<Byte, LocalDate> statuses) {
		this.statuses = statuses;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	
	
	
	// for Repo
	public Sobject(Long id, String street, String building) {
		this.id = id;
		this.street = street;
		this.building = building;
	}
	public Sobject(Long id, String street, String building, Map<Byte, LocalDate> statuses) {
		this.id = id;
		this.street = street;
		this.building = building;
		this.statuses = statuses;
	}
}
