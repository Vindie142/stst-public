package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.CityEnum;
import com.stst.head.models.PriceWork;

public interface PriceWorkRepo extends CrudRepository<PriceWork, Long> {
	
	// id, city, subsection(id, name, section[id, name]), name, measurement, priceMat[id, name, measurement]
	// && by subsection and by city
	public default List<PriceWork> findAll4(Long subsectionId, CityEnum city) {
		return city == null ? PRIVATEtoFindAll4(subsectionId) : PRIVATEtoFindAll4(subsectionId, city);

	}
	@Query(" SELECT new com.stst.head.models.PriceWork(p.id, p.orderNum, p.city, p.subsection, p.name, p.price, p.measurement, pm, p.deleted) "
			+ "FROM PriceWork p LEFT JOIN p.priceMat pm "
			+ "WHERE p.subsection.id = :subsectionId AND p.city = :city")
			public List<PriceWork> PRIVATEtoFindAll4(@Param("subsectionId") Long subsectionId, @Param("city") CityEnum city);
	@Query(" SELECT new com.stst.head.models.PriceWork(p.id, p.orderNum, p.city, p.subsection, p.name, p.price, p.measurement, pm, p.deleted) "
			+ "FROM PriceWork p LEFT JOIN p.priceMat pm "
			+ "WHERE p.subsection.id = :subsectionId AND p.city IS NULL")
			public List<PriceWork> PRIVATEtoFindAll4(@Param("subsectionId") Long subsectionId);
	
	// id, orderNum
	@Query(" SELECT p "
			+ "FROM PriceWork p "
			+ "WHERE p.subsection.id = :subsectionId")
			public List<PriceWork> findAll3(@Param("subsectionId") Long subsectionId);
	
	// id, city, name, measurement
	// && and by city
	public default List<PriceWork> findAll5(CityEnum city) {
		return city == null ? PRIVATEtoFindAll5() : PRIVATEtoFindAll5(city);

	}
	@Query(" SELECT new com.stst.head.models.PriceWork(p.id, p.city, p.name, p.measurement, p.deleted) "
			+ "FROM PriceWork p "
			+ "WHERE p.city = :city")
			public List<PriceWork> PRIVATEtoFindAll5(@Param("city") CityEnum city);
	@Query(" SELECT new com.stst.head.models.PriceWork(p.id, p.city, p.name, p.measurement, p.deleted) "
			+ "FROM PriceWork p "
			+ "WHERE p.city IS NULL")
			public List<PriceWork> PRIVATEtoFindAll5();
	
	// All
	// && and by city
	public default List<PriceWork> findAll6(CityEnum city) {
		return city == null ? PRIVATEtoFindAll6() : PRIVATEtoFindAll6(city);
	}
	@Query(" SELECT p "
			+ "FROM PriceWork p "
			+ "WHERE p.city = :city")
			public List<PriceWork> PRIVATEtoFindAll6(@Param("city") CityEnum city);
	@Query(" SELECT p "
			+ "FROM PriceWork p "
			+ "WHERE p.city IS NULL")
			public List<PriceWork> PRIVATEtoFindAll6();
	
	
	
	
	
	
	
	
	//private List<PriceWork> getPriceWork(){
	//	Iterable<PriceWork> sourcePriceWorks = findAll();
	//	List<PriceWork> priceWorks = new ArrayList<PriceWork>();
	//	sourcePriceWorks.forEach(e -> priceWorks.add(e));		
	//	return priceWorks;
	//}
	
	//// id, city, name, measurement, priceMat[id, name, measurement], subsection(id, name, section[id, name])
	//// && by subsection and by city
	//public default List<PriceWork> findAll4(Long subsectionId, CityEnum city) {
	//	return getPriceWork().stream()
	//			.filter(p -> p.getSubsection().getId() == subsectionId)
	//			.filter(p -> p.getCity() == city)
	//			.toList();
	//}
	
	//// id, orderNum
	//public default List<PriceWork> findAll3(Long subsectionId) {
	//	return getPriceWork().stream()
	//			.filter(p -> p.getSubsection().getId() == subsectionId)
	//			.toList();
	//}
	
	//// id, city, name, measurement
	//// && and by city
	//public default List<PriceWork> findAll5(CityEnum city) {
	//	return getPriceWork().stream()
	//			.filter(p -> p.getCity() == city)
	//			.toList();
	//}
	
}
