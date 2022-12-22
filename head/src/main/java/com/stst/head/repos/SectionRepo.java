package com.stst.head.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.Section;

public interface SectionRepo extends CrudRepository<Section, Long> {
	
	// all
	@Query(" SELECT s "
			+ "FROM Section s ")
			public List<Section> findAll1();
	
	// id, orderNum
	@Query(" SELECT s "
			+ "FROM Section s ")
			public List<Section> findAll2();
	
	// all
	// && by order num
	@Query(" SELECT s "
			+ "FROM Section s "
			+ "WHERE s.orderNum = :orderNum")
			public Optional<Section> findByOrderNum(@Param("orderNum") int orderNum);
	
	
	
	
	
	
	
	
	
	
	
	
//	private List<Section> getSections(){
//		Iterable<Section> sourceSections = findAll();
//		List<Section> sections = new ArrayList<Section>();
//		sourceSections.forEach(e -> sections.add(e));		
//		return sections;
//	}
//	
//	// all
//	public default List<Section> findAll1(){
//		return getSections();
//	}
//	
//	// id, orderNum
//	public default List<Section> findAll2(){
//		return getSections();
//	}
//	
//	// all
//	public default Optional<Section> findByOrderNum(int OrderNum){
//		return getSections().stream()
//				.filter(s -> s.getOrderNum() == OrderNum)
//				.findFirst();
//	}
//	
//	//public default int getMaxOrderNum(){ // max order number
//	//	return getSections().stream().map(s -> s.getOrderNum()).mapToInt(v -> v)
//	//            .max()
//	//            .orElse(0);
//	//}
}

