package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.Task;

public interface TaskRepo extends CrudRepository<Task, Long> {
	
	// id, text, sender(id, name, surname, secondName), object(id, street, building), message
	// && by owner
	@Query(" SELECT t "
			+ "FROM Task t "
			+ "WHERE t.owner.id = :ownerId")
			public List<Task> findAll1(@Param("ownerId") Long ownerId);
	
	// id, text, sender(id, name, surname, secondName), object(id, street, building), message
	// && by owner and by object
	@Query(" SELECT t "
			+ "FROM Task t "
			+ "WHERE t.owner.id = :ownerId AND t.object.id = :objectId")
			public List<Task> findAll2(@Param("objectId") Long objectId, @Param("ownerId") Long ownerId);
	
	
	

	
	
	
	
	
	
	
//	private List<Task> getTasks(){
//		Iterable<Task> sourceTasks = findAll();
//		List<Task> tasks = new ArrayList<Task>();
//		sourceTasks.forEach(e -> tasks.add(e));		
//		return tasks;
//	}
//	
//	// id, text, owner(id), sender(id, surname, name, secondName), object(id, street, building)
//	// && by owner
//	public default List<Task> findAll1(Long ownerId) {
//		return getTasks().stream()
//				.filter(t -> t.getOwner().getId() == ownerId)
//				.sorted()
//				.toList();
//	}
//	
//	// id, text, owner(id), sender(id, surname, name, secondName), object(id, street, building) and by object
//	// && by owner
//	public default List<Task> findAll2(Long objectId, Long ownerId) {
//		return getTasks().stream()
//				.filter(t -> t.getObject() != null).filter(t -> t.getObject().getId() == objectId)
//				.filter(t -> t.getOwner().getId() == ownerId)
//				.sorted()
//				.toList();
//	}
	
}
