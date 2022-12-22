package com.stst.head.models;

public class User {

	private String id;
	private String password;
	private String secretnum;
	private String depart;
	
	public User() {}
	
	public User(String id, String password, String secretnum, String depart) {
		this.id = id;
		this.password = password;
		this.secretnum = secretnum;
		this.depart = depart;
	}
	
	@Override
	public String toString() {
		String nickname = this.id == null ? "" : this.id;
		String password = this.password == null ? "" : this.password;
		String secretnum = this.secretnum == null ? "" : this.secretnum;
		String depart = this.depart == null ? "" : this.depart;
		return  "User{"+
				"nickname="+nickname+", "+
				"password="+password+", "+
				"secretnum="+secretnum+", "+
				"depart="+depart+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    User mayBe = (User) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int  result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = password == null ? 1 : password.hashCode();
	    int a3 = secretnum == null ? 1 : secretnum.hashCode();
	    int a4 = depart == null ? 1 : depart.hashCode();
	    return result * (a1 + a2 + a3 + a4);
    }
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSecretnum() {
		return secretnum;
	}
	public void setSecretnum(String secretnum) {
		this.secretnum = secretnum;
	}
	public String getDepart() {
		return depart;
	}
	public void setDepart(String depart) {
		this.depart = depart;
	}
	
	
}
