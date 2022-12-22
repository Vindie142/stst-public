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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.stst.head.repos.SectionRepo;

@Entity
@Table(name="price_sections")
public class Section implements Comparable<Section> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 128)
	private String name;
	
	private int orderNum;
	
	@OneToMany(mappedBy="section", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	private List<Subsection> subsections = new ArrayList<Subsection>();
	
	@OneToMany(mappedBy="section", fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
	private List<PriceMat> priceMats = new ArrayList<PriceMat>();

	
	public Section() {}
	
	public Section(String name) {
		this.name = name;
	}
	
	public void setOrderNumByRightWay(int newOrderNum, SectionRepo sectionRepo, boolean isNew) { // changes the ordinal number of a position, taking into account the ordinal numbers of other positions
		List<Section> sections = sectionRepo.findAll2(); // id, orderNum
		
		if (isNew == true) { // when creating a new position
			int maxOrderNum = sections.size();
			orderNum = maxOrderNum + 1;
			return;
		}
		if (newOrderNum == orderNum) { // when without changes
			return;
		}
		
		Section sectionsOnNewNum = sections.stream().filter(s -> s.getOrderNum() == newOrderNum).findFirst().orElse(null);
		if (sectionsOnNewNum == null && newOrderNum < sections.size()) { // if the position is less than the maximum, but it is free (but it should not be so)
			orderNum = newOrderNum;
		}
		
		if (newOrderNum > orderNum) { // when lower on the list
			for (int i = orderNum; i < newOrderNum; i++) {
				for (Section s : sections) {
					if (s.getOrderNum() == i+1) {
						s.setOrderNum(i);
						break;
					}
				}
			}
		} else { // when higher on the list
			for (int i = orderNum; i > newOrderNum; i--) {
				for (Section s : sections) {
					if (s.getOrderNum() == i-1) {
						s.setOrderNum(i);
						break;
					}
				}
			}
		}
		sectionRepo.saveAll(sections);
		orderNum = newOrderNum > sections.size() ? sections.size() : newOrderNum;
		sectionRepo.save(this);
		
	}
	
	@Override
	public int compareTo(Section s) {
		return this.orderNum - s.getOrderNum();
	}
	
	@Override
	public String toString() {
		String id = this.id == null ? "" : this.id.toString();
		String name = this.name == null ? "" : this.name;
		return  "Section{"+
				"id="+id+", "+
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
	    Section mayBe = (Section) obj;
	    return mayBe.toString().equals(this.toString());
    }
	
	@Override
    public int hashCode() {
		int result = 31;
	    int a1 = id == null ? 1 : id.hashCode();
	    int a2 = name == null ? 1 : name.hashCode();
	    return result * (a1 + a2);
    }
	
	public List<PriceMat> getPriceMats() {
		return priceMats;
	}

	public void setPriceMats(List<PriceMat> priceMats) {
		this.priceMats = priceMats;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Subsection> getSubsections() {
		return subsections;
	}

	public void setSubsections(List<Subsection> subsections) {
		this.subsections = subsections;
	}
	
	
	
	// for Repo
	public Section(Long id, int orderNum) {
		this.id = id;
		this.orderNum = orderNum;
	}
}
