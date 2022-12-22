package com.stst.head.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.SnabPoint;

public interface SnabPointRepo extends CrudRepository<SnabPoint, Long> {
	
	// all 
	// && by id
	public default Optional<SnabPoint> findById1(Long id) {
		return findById(id);
	}
	
	// all 
	// && by snabRequest
	@Query(" SELECT s "
			+ "FROM SnabPoint s "
			+ "WHERE s.snabRequest.id = :snabRequestId")
			public List<SnabPoint> findBySnabRequestId1(@Param("snabRequestId") Long snabRequestId);
	
	
	
	
	
	
	
	
	
	//// all 
	//// && by snabRequest
	//public default List<SnabPoint> findBySnabRequestId1(Long snabRequestId) {
	//	Iterable<SnabPoint> sourceSnabPoints = findAll();
	//	List<SnabPoint> snabPoints = new ArrayList<SnabPoint>();
	//	sourceSnabPoints.forEach(s -> snabPoints.add(s));	
	//	
	//	return snabPoints.stream().filter(s -> s.getSnabRequest().getId() == snabRequestId).collect(Collectors.toCollection(ArrayList::new));
	//}
}

