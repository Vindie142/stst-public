package com.stst.head.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.stst.head.services.PasswordHashing;

@Entity
@Table(name="employees")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 128)
	private String surname;
	@Column(nullable = false, length = 128)
	private String name;
	@Column(name="second_name", length = 128)
	private String secondName;
	private String phone;
	@Column(name="telegram_chat_id")
	private Long telegramChatId;
	@Column(nullable = false, unique=true, length = 128)
	private String nickname;
	@Column(nullable = false)
	private String password; // by PBKDF2
	private int secretnum; // the number that is issued at each entrance so that you can log in from only one device
	@Column(nullable = true)
	private Integer twoFactorKey;
	
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private DepartEnum depart;
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private CityEnum city;
	private boolean deleted;
	
	@OneToMany(mappedBy="employee", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	List<Notif> notifs = new ArrayList<Notif>();

	public Employee() {}
	
	public Employee(String name, String surname, String secondName, String phone, Long telegramChatId,
			String nickname, String password, DepartEnum depart, CityEnum city) {
		this.name = name;
		this.surname = surname;
		this.secondName = secondName;
		this.phone = phone;
		this.telegramChatId = telegramChatId;
		this.nickname = nickname;
		this.password = password;
		this.depart = depart;
		this.city = city;
		this.secretnum = 142;
	}
	
	private Employee(String name) { // for creating the first user
		this.name = "1";
		this.surname = "1";
		this.nickname = "1";
		this.password = PasswordHashing.hashPassword("1".toCharArray());
		this.depart = DepartEnum.LEAD;
		this.city = CityEnum.SAMARA;
	}
	public static Employee creatingTheFirstUser () { // for creating the first user
		return new Employee("");
	}
	
	public String getNormName() {
		String normName = surname + " ";
		normName += name.substring(0, 1)+".";
		normName += secondName == null || secondName.length() == 0 ? "" : secondName.substring(0, 1)+".";
		return normName;
	}
	
	public String getNormNameWithId() {
		return id + ": " + getNormName();
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String name = this.name == null ? "" : this.name;
		String surname = this.surname == null ? "" : this.surname;
		String secondName = this.secondName == null ? "" : this.secondName;
		String phone = this.phone == null ? "" : this.phone;
		String telegramChatId = this.telegramChatId == null ? "" : this.telegramChatId.toString();
		String nickname = this.nickname == null ? "" : this.nickname;
		String depart = this.depart == null ? "" : this.depart.getName();
		String city = this.city == null ? "" : this.city.getName();
		return "Employee{"+
				"id="+id+", "+
				"name="+name+", "+
				"surname="+surname+", "+
				"secondName="+secondName+", "+
				"phone="+phone+", "+
				"telegramChatId="+telegramChatId+", "+
				"nickname="+nickname+", "+
				"depart="+depart+", "+
				"city="+city+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    Employee mayBe = (Employee) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int  result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = name == null ? 1 : name.hashCode();
	    int a5 = phone == null ? 1 : phone.hashCode();
	    int a6 = telegramChatId == null ? 1 : telegramChatId.hashCode();
	    int a7 = nickname == null ? 1 : nickname.hashCode();
	    return result * (a1 + a2 + a5 + a6 + a7);
    }
	
	public Integer getTwoFactorKey() {
		return twoFactorKey;
	}

	public void setTwoFactorKey(Integer twoFactorKey) {
		this.twoFactorKey = twoFactorKey;
	}

	public Long getTelegramChatId() {
		return telegramChatId;
	}

	public void setTelegramChatId(Long telegramChatId) {
		this.telegramChatId = telegramChatId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSecretnum() {
		return secretnum;
	}

	public void setSecretnum(int secretnum) {
		this.secretnum = secretnum;
	}

	public DepartEnum getDepart() {
		return depart;
	}

	public void setDepart(DepartEnum depart) {
		this.depart = depart;
	}

	public CityEnum getCity() {
		return city;
	}

	public void setCity(CityEnum city) {
		this.city = city;
	}

	public List<Notif> getNotifs() {
		return notifs;
	}

	public void setNotifs(List<Notif> notifs) {
		this.notifs = notifs;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	// for Repo
	public Employee(Long id, String password, int secretnum) {
		this.id = id;
		this.password = password;
		this.secretnum = secretnum;
	}
	public Employee(Long id, String name, String surname, String secondName) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.secondName = secondName;
	}
	public Employee(Long id, String name, String surname, String secondName, String phone, DepartEnum depart, CityEnum city) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.secondName = secondName;
		this.phone = phone;	
		this.depart = depart;
		this.city = city;
	}
	
}