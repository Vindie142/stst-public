package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.Subsection;

public interface SubsectionRepo extends CrudRepository<Subsection, Long> {
	
	// all
	// && by section
	@Query(" SELECT s "
			+ "FROM Subsection s "
			+ "WHERE s.section.id = :sectionId")
			public List<Subsection> findAll1(@Param("sectionId") Long sectionId);
	
	// id, orderNum
	@Query(" SELECT s "
			+ "FROM Subsection s "
			+ "WHERE s.section.id = :sectionId")
			public List<Subsection> findAll2(@Param("sectionId") Long sectionId);
	
	
	
	
	
//	private List<Subsection> getSubsections(){
//		Iterable<Subsection> sourceSubsection = findAll();
//		List<Subsection> subsections = new ArrayList<Subsection>();
//		sourceSubsection.forEach(e -> subsections.add(e));		
//		return subsections;
//	}
//	
//	// all and by section
//	public default List<Subsection> findAll1(Long sectionId){
//		return getSubsections().stream().filter(subs -> subs.getSection().getId() == sectionId).toList();
//	}
//	
//	// id, orderNum
//	public default List<Subsection> findAll2(Long sectionId){
//		return getSubsections().stream().filter(subs -> subs.getSection().getId() == sectionId).toList();
//	}
	
}

