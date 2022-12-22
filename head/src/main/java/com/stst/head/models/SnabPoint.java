package com.stst.head.models;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="snab_requests_points")
public class SnabPoint implements Comparable<SnabPoint> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne (optional=false)
    @JoinColumn (name="snab_requests_id", nullable = false)
	private SnabRequest snabRequest;
	@ManyToOne (optional=false)
	@JoinColumn(name="price_line_id")
	private PriceMat priceMat;
	@Column (length = 128, nullable = false)
	private BigDecimal amount;
	private String note;

	public static final int NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_AMOUNT = 1; // the number of decimal places after rounding the amount
	
	public SnabPoint() {}
	
	public SnabPoint(SnabRequest snabRequest, PriceMat priceMat, BigDecimal amount, String note) {
		this.snabRequest = snabRequest;
		this.priceMat =  priceMat;
		this.amount = amount;
		this.note = note;
	}
	
	@Override
	public int compareTo(SnabPoint a) {
		return this.getPriceMat().compareTo(a.getPriceMat());
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String snabRequest = this.snabRequest == null ? "" : this.snabRequest.getId().toString();
		String priceMat = this.priceMat == null ? "" : this.priceMat.toString();
		String amount = this.amount.toPlainString();
		String note = this.note == null ? "" : this.note;
		return  "SnabPoint{"+
				"id="+id+", "+
				"snabRequest="+snabRequest+", "+
				"priceMat="+priceMat+", "+
				"amount="+amount+", "+
				"note="+note+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    SnabPoint mayBe = (SnabPoint) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = snabRequest == null ? 1 : snabRequest.hashCode();
	    int a3 = priceMat == null ? 1 : priceMat.hashCode();
	    int a4 = amount.hashCode();
	    int a5 = note == null ? 1 : note.hashCode();
	    return result * (a1 + a2 + a3 + a4 + a5);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SnabRequest getSnabRequest() {
		return snabRequest;
	}

	public void setSnabRequest(SnabRequest snabRequest) {
		this.snabRequest = snabRequest;
	}

	public PriceMat getPriceMat() {
		return priceMat;
	}

	public void setPriceMat(PriceMat priceMat) {
		this.priceMat = priceMat;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount.setScale(NUMBER_OF_DECIMAL_AFTER_ROUNDING_THE_AMOUNT);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
