package com.stst.head.models;

import java.math.BigDecimal;

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

import com.stst.head.services.Etc;

@Entity
@Table(name="price_mats")
public class PriceMat implements Comparable<PriceMat> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	@Enumerated(EnumType.ORDINAL)
	private CityEnum city; // if it's null, then it's for all cities
	@ManyToOne (optional=false)
	@JoinColumn (name="section_id")
	private Section section;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private MeasurementEnum measurement;
	@Column(nullable = false)
	private BigDecimal price;
	private boolean equipment; // if there is equipment in place of the material
	
	private boolean deleted;

	
	public PriceMat() {}
	
	public PriceMat(CityEnum city, Section section, String name, BigDecimal price, MeasurementEnum measurement, boolean equipment) {
		this.city = city;
		this.section = section;
		this.name = name;
		this.price = price;
		this.measurement = measurement;
		this.equipment = equipment;
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
	public int compareTo(PriceMat p) {
		return p.getSection().getOrderNum() - this.getSection().getOrderNum();
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String city = this.city == null ? "null" : this.city.getName();
		String section = this.section == null ? "null" : this.section.getName();
		String name = this.name == null ? "" : this.name;
		String price = this.price == null ? "" : this.price.toString();
		String equipment = Boolean.toString(this.equipment);
		String measurement = this.measurement == null ? "" : this.measurement.getName();
		return  "PriceMat{"+
				"id="+id+", "+
				"city="+city+", "+
				"section="+section+", "+
				"name="+name+", "+
				"price="+price+", "+
				"equipment="+equipment+", "+
				"measurement="+measurement+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    PriceMat mayBe = (PriceMat) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = city == null ? 1 : city.hashCode();
	    int a3 = name == null ? 1 : name.hashCode();
	    int a5 = measurement == null ? 1 : measurement.hashCode();
	    int a4 = Boolean.hashCode(equipment);
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

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
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

	public boolean isEquipment() {
		return equipment;
	}

	public void setEquipment(boolean equipment) {
		this.equipment = equipment;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	
	// for Repo
	public PriceMat(Long id, CityEnum city, Section section, String name, BigDecimal price, MeasurementEnum measurement, boolean deleted) {
		this.id = id;
		this.city = city;
		this.section = section;
		this.name = name;
		this.price = price;
		this.measurement = measurement;
		this.deleted = deleted;
	}
	public PriceMat(Long id, CityEnum city, String name, MeasurementEnum measurement, boolean deleted) {
		this.id = id;
		this.city = city;
		this.name = name;
		this.measurement = measurement;
		this.deleted = deleted;
	}
	public PriceMat(Long id, String name, MeasurementEnum measurement) {
		this.id = id;
		this.name = name;
		this.measurement = measurement;
	}
}
