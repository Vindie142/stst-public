package com.stst.head.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.CityEnum;
import com.stst.head.models.Sobject;

public interface SobjectRepo extends CrudRepository<Sobject, Long> {
	
	// all {pto(id, surname, name, secondName), master(id, surname, name, secondName)}
	@Query(" SELECT DISTINCT o "
			+ "FROM Sobject o LEFT JOIN FETCH o.actions ac "
			+ "WHERE o.id = :id ")
			public Optional<Sobject> findById11(@Param("id") Long id);
	
	// all {pto(id, surname, name, secondName), master(id, surname, name, secondName), sales(id, surname, name, secondName)}
	@Query(" SELECT o "
			+ "FROM Sobject o "
			+ "WHERE o.id = :id")
			public Optional<Sobject> findById2(@Param("id") Long id);
	
	// id, Street, Building
	@Query(" SELECT new com.stst.head.models.Sobject(o.id, o.street, o.building) "
			+ "FROM Sobject o "
			+ "WHERE o.id = :id")
			public Optional<Sobject> findById3(@Param("id") Long id);
	
	// id, Street, Building, pto(id, surname, name, secondName), master(id, surname, name, secondName), statuses, actions
	// && by status or employee or city
	public default List<Sobject> findAll2(Long employeeId, Byte status, CityEnum city) {
		List<Sobject> objects = PRIVATEtoFindAll2();
		
		if (employeeId != null) {
			objects = objects.stream()
					.filter(o -> (o.getPto() != null && o.getPto().getId() == employeeId) || (o.getMaster() != null && o.getMaster().getId() == employeeId) || (o.getSalesman() != null && o.getSalesman().getId() == employeeId) )
					.toList();
		}
		if (status != null) {
			objects = objects.stream().filter(o -> o.getCurrStatus() == status).toList();
		}
		if (city != null) {
			objects = objects.stream().filter(o -> o.getCity() == city).toList();
		}
		return objects;
	}
	@Query(" SELECT DISTINCT o "
			+ "FROM Sobject o LEFT JOIN FETCH o.actions ac ")
			public List<Sobject> PRIVATEtoFindAll2();
	
	// id, Street, Building
	@Query(" SELECT new com.stst.head.models.Sobject(o.id, o.street, o.building) "
			+ "FROM Sobject o ")
			public List<Sobject> findAll3();
	
	// id, Street, Building, pto(id, surname, name, secondName), master(id, surname, name, secondName), statuses, actions
	// && by employee 
	// && without 7,8 status
	public default List<Sobject> findAll4(Long employeeId) {
		List<Sobject> objects = PRIVATEtoFindAll4(employeeId);
		if (employeeId != null) {
			objects = objects.stream()
					.filter(o -> o.getCurrStatus() != 7 && o.getCurrStatus() != 8)
					.toList();
		}
		return objects;
	}
	@Query(" SELECT DISTINCT o "
			+ "FROM Sobject o LEFT JOIN FETCH o.actions ac "
			+ "WHERE o.pto.id = :employeeId OR o.master.id = :employeeId OR o.salesman.id = :employeeId")
			public List<Sobject> PRIVATEtoFindAll4(@Param("employeeId") Long employeeId);
	
	// id, Street, Building, statuses and by status
	public default List<Sobject> findAll5(Byte status) {
//		List<Sobject> objects = PRIVATEtoFindAll5();
//		objects = objects.stream()
//				.filter(o -> o.getCurrStatus() == status)
//				.toList();
		return PRIVATEtoFindAll5();
	}
	@Query(" SELECT new com.stst.head.models.Sobject(o.id, o.street, o.building) "
			+ "FROM Sobject o LEFT JOIN o.statuses st ")
			public List<Sobject> PRIVATEtoFindAll5();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
//	private List<Sobject> getObjects(){
//		Iterable<Sobject> sourceEmployees = findAll();
//		List<Sobject> objects = new ArrayList<Sobject>();
//		sourceEmployees.forEach(e -> objects.add(e));		
//		return objects;
//	}
//	
//	// all {pto(id, surname, name, secondName), master(id, surname, name, secondName)}
//	@Query(" SELECT o FROM Sobject o LEFT JOIN FETCH o.actions a WHERE o.id = :id ")
//	public Optional<Sobject> findById11(@Param("id") Long id);
//	
//	
//	// all {pto(id, surname, name, secondName), master(id, surname, name, secondName), sales(id, surname, name, secondName)}
//	public default Optional<Sobject> findById2(Long id, ActionRepo actionRepo) {
//		Optional<Sobject> oObject = findById(id);
//		if (oObject.isEmpty()) {
//			return Optional.empty();
//		}
//		Sobject object = oObject.get();
//		object.setActions( actionRepo.findByObjectId1(object.getId()) );
//		return Optional.of(object);
//	}
//	
//	// id, Street, Building
//	public default Optional<Sobject> findById3(Long id) {
//		return findById(id);
//	}
//	
//	// id, Street, Building, pto(id, surname, name, secondName), master(id, surname, name, secondName), statuses, actions with max id
//	public default List<Sobject> findAll1(ActionRepo actionRepo) {
//		List<Sobject> objects = getObjects();
//		for (Sobject o : objects) {
//			o.setActions( actionRepo.findByObjectId1(o.getId()) );
//		}
//		return objects;
//	}
//	
//	// id, Street, Building, pto(id, surname, name, secondName), master(id, surname, name, secondName), statuses, actions
//	// && by status or employee or city
//	public default List<Sobject> findAll2(ActionRepo actionRepo, Long employeeId, Byte status, CityEnum city) {
//		List<Sobject> objects = getObjects();
//		
//		if (employeeId != null) {
//			objects = objects.stream()
//					.filter(o -> (o.getPto() != null && o.getPto().getId() == employeeId) || (o.getMaster() != null && o.getMaster().getId() == employeeId) || (o.getSalesman() != null && o.getSalesman().getId() == employeeId) )
//					.toList();
//		}
//		if (status != null) {
//			objects = objects.stream().filter(o -> o.getCurrStatus() == status).toList();
//		}
//		if (city != null) {
//			objects = objects.stream().filter(o -> o.getCity() == city).toList();
//		}
//		
//		for (Sobject o : objects) {
//			o.setActions( actionRepo.findByObjectId1(o.getId()) );
//		}
//		return objects;
//	}
//	
//	// id, Street, Building
//	public default List<Sobject> findAll3() {
//		List<Sobject> objects = getObjects();
//		return objects;
//	}
//	
//	// id, Street, Building, pto(id, surname, name, secondName), master(id, surname, name, secondName), statuses, actions (by status and employee) 
//	// && without 7,8 status
//	public default List<Sobject> findAll4(ActionRepo actionRepo, Long employeeId) {
//		List<Sobject> objects = getObjects();
//		
//		if (employeeId != null) {
//			objects = objects.stream()
//					.filter(o -> (o.getPto() != null && o.getPto().getId() == employeeId) || (o.getMaster() != null && o.getMaster().getId() == employeeId) || (o.getSalesman() != null && o.getSalesman().getId() == employeeId) )
//					.filter(o -> o.getCurrStatus() != 7 && o.getCurrStatus() != 8)
//					.toList();
//		}
//		
//		for (Sobject o : objects) {
//			o.setActions( actionRepo.findByObjectId1(o.getId()) );
//		}
//		return objects;
//	}
//	
//	// id, Street, Building, statuses and by status
//	public default List<Sobject> findAll5(Byte status) {
//		List<Sobject> objects = getObjects();
//		
//		objects = objects.stream()
//				.filter(o -> o.getCurrStatus() == status)
//				.toList();
//		
//		return objects;
//	}
}
