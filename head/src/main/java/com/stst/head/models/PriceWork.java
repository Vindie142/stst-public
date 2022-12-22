package com.stst.head.models;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.stst.head.repos.PriceWorkRepo;
import com.stst.head.services.Etc;

@Entity
@Table(name="price_works")
public class PriceWork {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	@Enumerated(EnumType.ORDINAL)
	private CityEnum city; // if it's null, then it's for all cities
	@ManyToOne (optional=false)
	@JoinColumn (name="subsection_id")
	private Subsection subsection;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private MeasurementEnum measurement;
	private BigDecimal price;
	
	@ManyToOne (optional=true)
	@JoinColumn (name="price_mat_id")
	private PriceMat priceMat;
	
	private int orderNum;
	private boolean deleted;

	
	public PriceWork() {}
	
	public PriceWork(CityEnum city, Subsection subsection, String name, BigDecimal price, MeasurementEnum measurement, PriceMat priceMat) {
		this.city = city;
		this.subsection = subsection;
		this.name = name;
		this.price = price;
		this.measurement = measurement;
		this.priceMat = priceMat;
	}
	
	public void setOrderNumByRightWay(int newOrderNum, PriceWorkRepo priceWorkRepo, boolean isNew) { // changes the ordinal number of a position, taking into account the ordinal numbers of other positions
		List<PriceWork> priceWorks = priceWorkRepo.findAll3(subsection.getId()); // id, orderNum
		
		if (isNew == true) { // when creating a new position
			int maxOrderNum = priceWorks.size();
			orderNum = maxOrderNum + 1;
			return;
		}
		if (newOrderNum == orderNum) { // when without changes
			return;
		}
		
		PriceWork priceWorkOnNewNum = priceWorks.stream().filter(p -> p.getOrderNum() == newOrderNum).findFirst().orElse(null);
		if (priceWorkOnNewNum == null && newOrderNum < priceWorks.size()) { // if the position is less than the maximum, but it is free (but it should not be so)
			orderNum = newOrderNum;
		}
		
		if (newOrderNum > orderNum) { // when lower on the list
			for (int i = orderNum; i < newOrderNum; i++) {
				for (PriceWork p : priceWorks) {
					if (p.getOrderNum() == i+1) {
						p.setOrderNum(i);
						break;
					}
				}
			}
		} else { // when higher on the list
			for (int i = orderNum; i > newOrderNum; i--) {
				for (PriceWork p : priceWorks) {
					if (p.getOrderNum() == i-1) {
						p.setOrderNum(i);
						break;
					}
				}
			}
		}
		priceWorkRepo.saveAll(priceWorks);
		orderNum = newOrderNum > priceWorks.size() ? priceWorks.size() : newOrderNum;
		priceWorkRepo.save(this);
		
	}
	
	public static class PriceWorkComparatorByWorkAlphabet implements Comparator<PriceWork> {
		   @Override
		   public int compare(PriceWork p1, PriceWork p2) {
			   return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
		   }
	}
	public static class PriceWorkComparatorByMatAlphabet implements Comparator<PriceWork> {
		   @Override
		   public int compare(PriceWork p1, PriceWork p2) {
		       return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
		   }
	}
	public static class PriceWorkComparatorByOrderNum implements Comparator<PriceWork> {
		   @Override
		   public int compare(PriceWork p1, PriceWork p2) {
			   int n1 = p1.getSubsection().getSection().getOrderNum() - p2.getSubsection().getSection().getOrderNum();
			   if (n1 != 0) {
				   return n1;
			   }
			   int n2 = p1.getSubsection().getOrderNum() - p2.getSubsection().getOrderNum();
			   if (n2 != 0) {
				   return n2;
			   }
			   return p1.getOrderNum() - p2.getOrderNum();
		   }
	}
	
	public String getNormName() {
		return "[" + id + "] " + name + " (" + measurement.getName() + ")";
	}
	
	public String getPriceToSnab() {
		return Etc.beautifyingPrice(price);
	}
	public String getPriceToSmet(boolean vat, String cityAbbr) { // vat - Value Added Tax
		return vat ? Etc.beautifyingPrice(Etc.priceWithVatFrom( price, new BigDecimal(CityEnum.getEnumByAbbr(cityAbbr).get().getCashOutRatio()) )) : Etc.beautifyingPrice(price);
	}
	public String getBeautifulPrice() {
		return Etc.beautifyingPrice(price);
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String city = this.city == null ? "null" : this.city.getName();
		String subsection = this.subsection == null ? "null" : this.subsection.getName();
		String name = this.name == null ? "" : this.name;
		String measurement = this.measurement == null ? "" : this.measurement.getName();
		String priceMat = this.priceMat == null ? "" : this.priceMat.getName();
		return  "PriceWork{"+
				"id="+id+", "+
				"city="+city+", "+
				"subsection="+subsection+", "+
				"name="+name+", "+
				"measurement="+measurement+", "+
				"priceMat="+priceMat+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    PriceWork mayBe = (PriceWork) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = city == null ? 1 : city.hashCode();
	    int a3 = name == null ? 1 : name.hashCode();
	    int a4 = measurement == null ? 1 : measurement.hashCode();
	    int a5 = priceMat == null ? 1 : priceMat.hashCode();
	    return result * (a1 + a2 + a3+ a4 + a5);
    }
	
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CityEnum getCity() {
		return city;
	}

	public void setCity(CityEnum city) {
		this.city = city;
	}

	public Subsection getSubsection() {
		return subsection;
	}

	public void setSubsection(Subsection subsection) {
		this.subsection = subsection;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MeasurementEnum getMeasurement() {
		return measurement;
	}

	public void setMeasurement(MeasurementEnum measurement) {
		this.measurement = measurement;
	}

	public PriceMat getPriceMat() {
		return priceMat;
	}

	public void setPriceMat(PriceMat priceMat) {
		this.priceMat = priceMat;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
	// for Repo
	public PriceWork(Long id, int orderNum, CityEnum city, Subsection subsection, String name, BigDecimal price, MeasurementEnum measurement, PriceMat priceMat, boolean deleted) {
		this.id = id;
		this.orderNum = orderNum;
		this.city = city;
		this.subsection = subsection;
		this.name = name;
		this.price = price;
		this.measurement = measurement;
		this.priceMat = priceMat;
		this.deleted = deleted;
	}	
	public PriceWork(Long id, int orderNum, boolean deleted) {
		this.id = id;
		this.orderNum = orderNum;
		this.deleted = deleted;
	}
	public PriceWork(Long id, CityEnum city, String name, MeasurementEnum measurement, boolean deleted) {
		this.id = id;
		this.city = city;
		this.name = name;
		this.measurement = measurement;
		this.deleted = deleted;
	}
}
