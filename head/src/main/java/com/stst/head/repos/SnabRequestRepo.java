package com.stst.head.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.SnabRequest;

public interface SnabRequestRepo extends CrudRepository<SnabRequest, Long> {
	
	// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done, snabPoints
	// && by id
	@Query(" SELECT s "
			+ "FROM SnabRequest s LEFT JOIN FETCH s.snabPoints sp "
			+ "WHERE s.id = :id")
			public Optional<SnabRequest> findById1(@Param("id") Long id);
	
	// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
	// && and by employee
	@Query(" SELECT s "
			+ "FROM SnabRequest s "
			+ "WHERE s.compiler.id = :employeeId OR s.checker.id = :employeeId OR s.approver.id = :employeeId OR s.snab.id = :employeeId")
			public List<SnabRequest>  findAll1(@Param("employeeId") Long employeeId);
	
	// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
	// && only done=false
	// && and by employee
	@Query(" SELECT s "
			+ "FROM SnabRequest s "
			+ "WHERE s.done = false AND (s.compiler.id = :employeeId OR s.checker.id = :employeeId OR s.approver.id = :employeeId OR s.snab.id = :employeeId)")
			public List<SnabRequest> findAll2(@Param("employeeId") Long employeeId);
	
	// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
	// && and by object
	@Query(" SELECT s "
			+ "FROM SnabRequest s "
			+ "WHERE s.object.id = :objectId")
			public List<SnabRequest> findAll6(@Param("objectId") Long objectId);
	
	// id and by employeeId in snab
	public default boolean findAll3(Long employeeId) {
		return PRIVATEtoFindAll3(employeeId).size() > 0;
	}
	@Query(" SELECT s.id "
			+ "FROM SnabRequest s "
			+ "WHERE s.accepted = false AND s.snab.id = :employeeId")
			public List<SnabRequest> PRIVATEtoFindAll3(@Param("employeeId") Long employeeId);
	
	// id
	// && by employeeId in checker or in approver
	public default boolean findAll4(Long employeeId) {
		return PRIVATEtoFindAll4(employeeId).size() > 0;
	}
	@Query(" SELECT s.id "
			+ "FROM SnabRequest s "
			+ "WHERE (s.checked = false AND s.checker.id = :employeeId) OR (s.approved = false AND s.approver.id = :employeeId)")
			public List<SnabRequest> PRIVATEtoFindAll4(@Param("employeeId") Long employeeId);
	
	// id and by employeeId in approver or in snab
	public default boolean findAll5(Long employeeId) {
		return PRIVATEtoFindAll5(employeeId).size() > 0;
	}
	@Query(" SELECT s.id "
			+ "FROM SnabRequest s "
			+ "WHERE (s.accepted = false AND s.snab.id = :employeeId) OR (s.approved = false AND s.approver.id = :employeeId)")
			public List<SnabRequest> PRIVATEtoFindAll5(@Param("employeeId") Long employeeId);
	
	
	
	
	
	
	
	
	
	
	//private List<SnabRequest> getSnabRequests(){
	//	Iterable<SnabRequest> sourceSnabRequests = findAll();
	//	List<SnabRequest> snabRequests = new ArrayList<SnabRequest>();
	//	sourceSnabRequests.forEach(e -> snabRequests.add(e));		
	//	return snabRequests;
	//}
	
	//// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done, snabPoints
	//public default Optional<SnabRequest> findById1(Long employeeId, SnabPointRepo snabPointRepo) {
	//	SnabRequest snabRequests = findById(employeeId).get();
	//	snabRequests.setSnabPoints(snabPointRepo.findBySnabRequestId1(snabRequests.getId())); // all and by snabRequest
	//	return Optional.of(snabRequests);
	//}
	
	//// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
	//// && and by employee
	//public default List<SnabRequest> findAll1(Long employeeId) {
	//	List<SnabRequest> snabRequests = getSnabRequests().stream()
	//			.filter(s -> (s.getCompiler() != null && s.getCompiler().getId() == employeeId) || (s.getChecker() != null && s.getChecker().getId() == employeeId) || (s.getApprover() != null && s.getApprover().getId() == employeeId) || (s.getSnab() != null && s.getSnab().getId() == employeeId))
	//			.sorted()
	//			.toList();
	//	return snabRequests;
	//}
	
	//// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
	//// && only done=false
	//// && and by employee
	//public default List<SnabRequest> findAll2(Long employeeId) {
	//	List<SnabRequest> snabRequests = getSnabRequests().stream()
	//			.filter(s -> (s.getCompiler() != null && s.getCompiler().getId() == employeeId) || (s.getChecker() != null && s.getChecker().getId() == employeeId) || (s.getApprover() != null && s.getApprover().getId() == employeeId) || (s.getSnab() != null && s.getSnab().getId() == employeeId))
	//			.filter(s -> s.isDone() == false)
	//			.sorted()
	//			.toList();
	//	return snabRequests;
	//}
	
//	// compiler(id, surname, name, secondName), checker(id, surname, name, secondName), approver(id, surname, name, secondName), snab(id, surname, name, secondName), object(id, street, building, pto[id, surname, name, secondName]), dateFrom, dateTo, note, checked, approved, accepted, done
//	// && and by object
//	public default List<SnabRequest> findAll6(Long objectId) {
//		List<SnabRequest> snabRequests = getSnabRequests().stream()
//				.filter(s -> (s.getObject().getId() == objectId) )
//				.sorted()
//				.toList();
//		return snabRequests;
//	}
	
//	// id and by employeeId in snab
//	public default boolean findAll3(Long employeeId) {
//		return getSnabRequests().stream()
//				.anyMatch(s -> s.getSnab() != null && s.getSnab().getId() == employeeId && s.isAccepted() == false);
//	}
//	
//	// id and by employeeId in checker or in approver
//	public default boolean findAll4(Long employeeId) {
//		return getSnabRequests().stream()
//				.anyMatch(s -> (s.getChecker() != null && s.getChecker().getId() == employeeId && s.isChecked() == false) || (s.getApprover() != null && s.getApprover().getId() == employeeId && s.isApproved() == false));
//	}
//	
//	// id and by employeeId in approver or in snab
//	public default boolean findAll5(Long employeeId) {
//		return getSnabRequests().stream()
//				.anyMatch(s -> (s.getSnab() != null && s.getSnab().getId() == employeeId && s.isAccepted() == false) || (s.getApprover() != null && s.getApprover().getId() == employeeId && s.isApproved() == false));
//	}
	
}

