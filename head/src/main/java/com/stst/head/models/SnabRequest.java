package com.stst.head.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="snab_requests")
public class SnabRequest implements Comparable<SnabRequest> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne (optional=false)
	@JoinColumn(name="compiler_id")
	private Employee compiler; // mast, pto, lead
	@ManyToOne
	@JoinColumn(name="checker_id")
	private Employee checker; // pto, lead
	@ManyToOne
	@JoinColumn(name="approver_id")
	private Employee approver; // pto, lead
	@ManyToOne
	@JoinColumn(name="snab_id")
	private Employee snab; // snab, lead
	@ManyToOne (optional=false)
	@JoinColumn(name="object_id")
	private Sobject object;
	
	@Column(name="date_from")
	private LocalDateTime dateFrom; // when it goes to snab
	@Column(name="date_to")
	private LocalDate dateTo;
	@Column (length = 512)
	private String note;
	@OneToMany(mappedBy="snabRequest", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	List<SnabPoint> snabPoints = new ArrayList<SnabPoint>();
	
	private boolean checked; // by pto, lead
	private boolean approved; // by pto, lead
	private boolean accepted; // by snab, lead
	private boolean done; // by snab, lead
		
	public SnabRequest() {}
	
	public SnabRequest(Employee compiler, Sobject object, LocalDate dateTo, String note) {
		this.compiler = compiler;
		this.object = object;
		this.dateTo = dateTo;
		this.note = note;
	}
	
	public void setDateFromNow() {
		this.dateFrom = LocalDateTime.now();
	}
	
	public void addSnabPoint(PriceMat priceMat, BigDecimal amount, String note) {
		snabPoints.add(new SnabPoint(this, priceMat, amount, note));
    }
	
	public String getNormalDateFrom() {
		return dateFrom.getDayOfMonth() +"."+ dateFrom.getMonthValue() +"."+ String.valueOf( dateFrom.getYear() ).substring(2, 4);
	}
	
	public String getNormalDateTo() {
		return dateTo.getDayOfMonth() +"."+ dateTo.getMonthValue() +"."+ String.valueOf( dateTo.getYear() ).substring(2, 4);
	}
	
	@Override
	public int compareTo(SnabRequest a) {
		return a.getId().compareTo(this.id);
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String compiler = this.compiler == null ? "" : this.compiler.getNormName();
		String checker = this.checker == null ? "" : this.checker.getNormName();
		String approver = this.approver == null ? "" : this.approver.getNormName();
		String snab = this.snab == null ? "" : this.snab.getNormName();
		String object = this.object == null ? "" : this.object.getNormName();
		String dateFrom = this.dateFrom == null ? "" : this.dateFrom.toString();
		return  "SnabRequest{"+
				"id="+id+", "+
				"compiler="+compiler+", "+
				"checker="+checker+", "+
				"approver="+approver+", "+
				"snab="+snab+", "+
				"object="+object+", "+
				"dateFrom="+dateFrom+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    SnabRequest mayBe = (SnabRequest) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = compiler == null ? 1 : compiler.hashCode();
	    int a3 = checker == null ? 1 : checker.hashCode();
	    int a4 = approver == null ? 1 : approver.hashCode();
	    int a5 = snab == null ? 1 : snab.hashCode();
	    int a6 = object == null ? 1 : object.hashCode();
	    int a7 = dateFrom == null ? 1 : dateFrom.hashCode();
	    return result * (a1 + a2 + a3 + a4 + a5 + a6 + a7);
    }
	
	
	public List<SnabPoint> getSnabPoints() {
		return snabPoints;
	}

	public void setSnabPoints(List<SnabPoint> snabPoints) {
		Collections.sort(snabPoints);
		Collections.reverse(snabPoints);
		this.snabPoints = snabPoints;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getCompiler() {
		return compiler;
	}

	public void setCompiler(Employee compiler) {
		this.compiler = compiler;
	}

	public Employee getChecker() {
		return checker;
	}

	public void setChecker(Employee checker) {
		this.checker = checker;
	}

	public Employee getApprover() {
		return approver;
	}

	public void setApprover(Employee approver) {
		this.approver = approver;
	}

	public Employee getSnab() {
		return snab;
	}

	public void setSnab(Employee snab) {
		this.snab = snab;
	}

	public Sobject getObject() {
		return object;
	}

	public void setObject(Sobject object) {
		this.object = object;
	}

	public LocalDateTime getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(LocalDateTime dateFrom) {
		this.dateFrom = dateFrom;
	}

	public LocalDate getDateTo() {
		return dateTo;
	}

	public void setDateTo(LocalDate dateTo) {
		this.dateTo = dateTo;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
}
