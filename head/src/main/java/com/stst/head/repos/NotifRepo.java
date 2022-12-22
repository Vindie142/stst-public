package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.Notif;

public interface NotifRepo extends CrudRepository<Notif, Long> {
	
	// all
	// && by EmployeeId
	@Query(" SELECT n "
			+ "FROM Notif n "
			+ "WHERE n.employee.id = :employeeId")
			public List<Notif> findByEmployeeId1(@Param("employeeId") Long employeeId);

	
	
	
	
	
	
	
	
	//// all
	//// && by EmployeeId
	//public default List<Notif> findByEmployeeId1(Long employeeId) {
	//	
	//	Iterable<Notif> sourceNotifs = findAll();
	//	List<Notif> notifs = new ArrayList<Notif>();
	//	sourceNotifs.forEach(n -> notifs.add(n));	
	//	
	//	return notifs.stream().filter(n -> n.getEmployee().getId() == employeeId).sorted().toList();
	//}
}

