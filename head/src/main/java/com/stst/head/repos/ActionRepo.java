package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.Action;

public interface ActionRepo extends CrudRepository<Action, Long> {
	
	// all
	// && by ObjectId
	@Query(" SELECT a "
			+ "FROM Action a "
			+ "WHERE a.object.id = :objectId ")
			public List<Action> findByObjectId1(@Param("objectId") Long objectId);
	
	
	
	
	//// all
	//// && by ObjectId
	//public default List<Action> findByObjectId1(Long objectId) {
	//	Iterable<Action> sourceActions = findAll();
	//	List<Action> actions = new ArrayList<Action>();
	//	sourceActions.forEach(a -> actions.add(a));	
	//	
	//	return actions.stream().filter(a -> a.getObject().getId() == objectId).collect(Collectors.toCollection(ArrayList::new));
	//}
}

