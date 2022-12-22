package com.stst.head.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.CityEnum;
import com.stst.head.models.DepartEnum;
import com.stst.head.models.Employee;

public interface EmployeeRepo extends CrudRepository<Employee, Long> {
	
	Optional<Employee> findByNickname(String nickname);
	
	// id, password, secretnum
	// && only isDeleted() == false
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.password, e.secretnum) "
			+ "FROM Employee e "
			+ "WHERE e.id = :id AND e.deleted = false")
			public Optional<Employee> findById1(@Param("id") Long id);
	
	// id, name, surname, secondName
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName) "
			+ "FROM Employee e "
			+ "WHERE e.id = :id")
			public Optional<Employee> findById2(@Param("id") Long id);
	
	// id, name, surname, secondName, phone, depart, city
	// && only isDeleted() == false
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName, e.phone, e.depart, e.city) "
			+ "FROM Employee e "
			+ "WHERE e.deleted = false")
			public List<Employee> findAll1();
	
	// id, name, surname, secondName
	// && only depart == 'lead, mast, pto, sales'
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName) "
			+ "FROM Employee e "
			+ "WHERE e.depart IN :departEnums")
			public List<Employee> findAll2(@Param("departEnums") List<DepartEnum> departEnums);
	
	// id, name, surname, secondName
	// && only isDeleted() == false
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName) "
			+ "FROM Employee e "
			+ "WHERE e.deleted = false")
			public List<Employee> findAll3();
	
	// id, name, surname, secondName
	// && by city
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName) "
			+ "FROM Employee e "
			+ "WHERE e.city = :city")
			public List<Employee> findAll4(@Param("city") CityEnum city);
	
	// id, name, surname, secondName
	// && only isDeleted() == false
	// && by depart + leads
	public default List<Employee> getByDepart1(DepartEnum depart) {
		List<Employee> list1 = PRIVATEtoGetByDepart1(depart);
		list1.addAll( PRIVATEtoGetByDepart1(DepartEnum.getEnumByAbbr("lead").get()) );
			return list1;
		}
	@Query(" SELECT new com.stst.head.models.Employee(e.id, e.name, e.surname, e.secondName) "
			+ "FROM Employee e "
			+ "WHERE e.depart = :depart AND e.deleted = false")
			public List<Employee> PRIVATEtoGetByDepart1(@Param("depart") DepartEnum depart);

	

	
	
	
	//private List<Employee> getEmployees(){
	//	Iterable<Employee> sourceEmployees = findAll();
	//	List<Employee> employees = new ArrayList<Employee>();
	//	sourceEmployees.forEach(e -> employees.add(e));		
	//	return employees;
	//}
	
	//// id, Password, Secretnum
	//// && only isDeleted() == false
	//public default Optional<Employee> findById1(Long id) {
	//	Optional<Employee> оptEmployee = findById(id);
	//	if (оptEmployee.isEmpty()) {
	//		return Optional.empty();
	//	}
	//	Employee employee = оptEmployee.get();
	//	if (employee.isDeleted() == true) {
	//		return Optional.empty();
	//	}
	//	return оptEmployee;
	//}
	
	//// id, surname, name, secondName
	//public default Optional<Employee> findById2(Long id) {
	//	Optional<Employee> оptEmployee = findById(id);
	//	return оptEmployee;
	//}
	
	//// id, surname, name, secondName
	//public default List<Employee> findAll2() {
	//	return getEmployees();
	//}
		
	//// id, surname, name, secondName
	//// && only isDeleted() == false
	//public default List<Employee> findAll3() {
	//	return getEmployees().stream()
	//			.filter(e -> e.isDeleted() == false)
	//			.toList();
	//}
	
	//// id, surname, name, secondName && only isDeleted() == false
	//// && only depart
	//public default List<Employee> getByDepart1(String depart){
	//	List<Employee> employees = getEmployees().stream()
	//			.filter(e -> e.isDeleted() == false)
	//			.toList();
	//	return employees.stream()
	//			.filter( e -> e.getDepart().getAbbr().equals(depart) || e.getDepart().getAbbr().equals("lead") )
	//			.toList();
	//}
}
