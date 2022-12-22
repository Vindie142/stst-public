package com.stst.head.models;

import java.util.ArrayList;
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

import com.stst.head.repos.SubsectionRepo;

@Entity
@Table(name="price_sections_subsections")
public class Subsection implements Comparable<Subsection> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@ManyToOne (optional=false)
	@JoinColumn (name="section_id")
	private Section section;
	@Column(nullable = false)
	private String name;
	
	private int orderNum;
	
	@OneToMany(mappedBy="subsection", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	private List<PriceWork> priceWorks = new ArrayList<PriceWork>();

	
	public Subsection() {}
	
	public Subsection(Section section, String name) {
		this.section = section;
		this.name = name;
	}
	
	public void setOrderNumByRightWay(int newOrderNum, SubsectionRepo subsectionRepo, boolean isNew) { // changes the ordinal number of a position, taking into account the ordinal numbers of other positions
		List<Subsection> subsections = subsectionRepo.findAll2(section.getId()); // id, orderNum
		
		if (isNew == true) { // when creating a new position
			int maxOrderNum = subsections.size();
			orderNum = maxOrderNum + 1;
			return;
		}
		if (newOrderNum == orderNum) { // when without changes
			return;
		}
		
		Subsection subsectionsOnNewNum = subsections.stream().filter(s -> s.getOrderNum() == newOrderNum).findFirst().orElse(null);
		if (subsectionsOnNewNum == null && newOrderNum < subsections.size()) { // if the position is less than the maximum, but it is free (but it should not be so)
			orderNum = newOrderNum;
		}
		
		if (newOrderNum > orderNum) { // when lower on the list
			for (int i = orderNum; i < newOrderNum; i++) {
				for (Subsection s : subsections) {
					if (s.getOrderNum() == i+1) {
						s.setOrderNum(i);
						break;
					}
				}
			}
		} else { // when higher on the list
			for (int i = orderNum; i > newOrderNum; i--) {
				for (Subsection s : subsections) {
					if (s.getOrderNum() == i-1) {
						s.setOrderNum(i);
						break;
					}
				}
			}
		}
		subsectionRepo.saveAll(subsections);
		orderNum = newOrderNum > subsections.size() ? subsections.size() : newOrderNum;
		subsectionRepo.save(this);
		
	}
	
	@Override
	public int compareTo(Subsection s) {
		return this.orderNum - s.getOrderNum();
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String section = this.section == null ? "null" : this.section.getName();
		String name = this.name == null ? "" : this.name;
		return  "Subsection{"+
				"id="+id+", "+
				"section="+section+", "+
				"name="+name+"}";
	}
	
	@Override
    public boolean equals(Object obj) {
	    if (obj == this) {
	        return true;
	    }
	    if (obj == null || obj.getClass() != this.getClass()) {
	        return false;
	    }
	    Subsection mayBe = (Subsection) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = section == null ? 1 : section.hashCode();
	    int a3 = name == null ? 1 : name.hashCode();
	    return result * (a1 + a2 + a3);
    }
	
	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<PriceWork> getPriceLines() {
		return priceWorks;
	}

	public void setPriceLines(List<PriceWork> priceLines) {
		this.priceWorks = priceLines;
	}

	
	// for Repo
	public Subsection(Long id, int orderNum) {
		this.id = id;
		this.orderNum = orderNum;
	}
}
