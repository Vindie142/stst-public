package com.stst.head.repos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.CityEnum;
import com.stst.head.models.TimeTracking;

public interface TimeTrackingRepo extends CrudRepository<TimeTracking, Long> {
	
	// All
	// && by owner
	@Query(" SELECT t "
			+ "FROM TimeTracking t "
			+ "WHERE t.employee.id = :employeeId")
			public List<TimeTracking> findAll1(@Param("employeeId") Long employeeId);
	
	// All
	// && by owner AND by dayDate
	@Query(" SELECT t "
			+ "FROM TimeTracking t "
			+ "WHERE t.employee.id = :employeeId AND t.dayDate = :dayDate")
			public List<TimeTracking> findAll2(@Param("employeeId") Long employeeId, @Param("dayDate") LocalDate dayDate);
	
	// All
	// && by city AND by monthDate
	public default List<TimeTracking> findAll3(CityEnum city, int monthDate) {
		return PRIVATEtoFindAll3(city).stream().filter(t -> t.getDayDate().getMonth().getValue() == monthDate).toList();
	}
	@Query(" SELECT t "
			+ "FROM TimeTracking t "
			+ "WHERE t.employee.city = :city")
			public List<TimeTracking> PRIVATEtoFindAll3(@Param("city") CityEnum city);

	// past with max id
	// && by owner && get max id
	@Query(" SELECT t "
			+ "FROM TimeTracking t "
			+ "WHERE t.id = (SELECT max(t.id) "
			+ "				FROM TimeTracking t "
			+ "        		WHERE t.employee.id = :employeeId)")
			public Optional<TimeTracking> find2(@Param("employeeId") Long employeeId);
	
}
