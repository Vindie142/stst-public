package com.stst.head.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.stst.head.models.CityEnum;
import com.stst.head.models.PriceMat;

public interface PriceMatRepo extends CrudRepository<PriceMat, Long> {
	
	// id, city, section, name, measurement,
	// && by section && by city
	public default List<PriceMat> findAll4(Long sectionId, CityEnum city) {
		return city == null ? PRIVATEtoFindAll4(sectionId) : PRIVATEtoFindAll4(sectionId, city);
	}
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.section, p.name, p.price, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE p.section.id = :sectionId AND p.city = :city")
			public List<PriceMat> PRIVATEtoFindAll4(@Param("sectionId") Long sectionId, @Param("city") CityEnum city);
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.section, p.name, p.price, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE p.section.id = :sectionId AND p.city IS NULL")
			public List<PriceMat> PRIVATEtoFindAll4(@Param("sectionId") Long sectionId);
	
	// id, city, section, name, measurement
	// && and by city(with NULL city)
	// && only isDeleted() == false
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.section, p.name, p.price, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE (p.city = :city OR p.city IS NULL) AND p.deleted = false")
			public List<PriceMat> findAll2(@Param("city") CityEnum city);
	
	// id, city, name, measurement
	// && and by city(with NULL city)
	// && only isDeleted() == false
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.name, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE (p.city = :city OR p.city IS NULL) AND p.deleted = false")
			public List<PriceMat> findAll5(@Param("city") CityEnum city);
	
	// id, city, name, measurement, deleted
	// && and by city
	public default List<PriceMat> findAll6(CityEnum city) {
		return city == null ? PRIVATEtoFindAll6() : PRIVATEtoFindAll6(city);
	}
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.name, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE p.city = :city")
			public List<PriceMat> PRIVATEtoFindAll6(@Param("city") CityEnum city);
	@Query(" SELECT new com.stst.head.models.PriceMat(p.id, p.city, p.name, p.measurement, p.deleted) "
			+ "FROM PriceMat p "
			+ "WHERE p.city IS NULL")
			public List<PriceMat> PRIVATEtoFindAll6();
	
	// All
	// && and by city
	public default List<PriceMat> findAll7(CityEnum city) {
		return city == null ? PRIVATEtoFindAll7() : PRIVATEtoFindAll7(city);
	}
	@Query(" SELECT p "
			+ "FROM PriceMat p "
			+ "WHERE p.city = :city")
			public List<PriceMat> PRIVATEtoFindAll7(@Param("city") CityEnum city);
	@Query(" SELECT p "
			+ "FROM PriceMat p "
			+ "WHERE p.city IS NULL")
			public List<PriceMat> PRIVATEtoFindAll7();

	
	
	
	
	
	
	
	
	
	//private List<PriceMat> getPriceMat(){
	//	Iterable<PriceMat> sourcePriceMats = findAll();
	//	List<PriceMat> priceMats = new ArrayList<PriceMat>();
	//	sourcePriceMats.forEach(e -> priceMats.add(e));		
	//	return priceMats;
	//}
	
	//// id, city, section, name, measurement,
	//// && by section && by city
	//public default List<PriceMat> findAll4(Long sectionId, CityEnum city) {
	//	return getPriceMat().stream()
	//			.filter(p -> p.getSection().getId() == sectionId)
	//			.filter(p -> p.getCity() == city)
	//			.toList();
	//}
	
	//// id, city, name, section, measurement
	//// && and by city(with NULL city)
	//// && only isDeleted() == false
	//public default List<PriceMat> findAll2(CityEnum city) {
	//	return getPriceMat().stream()
	//			.filter(p -> p.getCity() == null || p.getCity() == city)
	//			.filter(p -> p.isDeleted() == false)
	//			.sorted()
	//			.toList();
	//}
	
	//// id, orderNum
	//public default List<PriceMat> findAll3(Long subsectionId) {
	//	return getPriceMat().stream()
	//			.filter(p -> p.getSection().getId() == subsectionId)
	//			.toList();
	//}
	
	//// id, city, name, measurement
	//// && and by city(with NULL city)
	//// && only isDeleted() == false
	//public default List<PriceMat> findAll5(CityEnum city) {
	//	return getPriceMat().stream()
	//			.filter(p -> p.getCity() == null || p.getCity() == city)
	//			.filter(p -> p.isDeleted() == false)
	//			.sorted()
	//			.toList();
	//}
	
	//// id, city, name, measurement
	//// && and by city
	//public default List<PriceMat> findAll6(CityEnum city) {
	//	return getPriceMat().stream()
	//			.filter(p -> p.getCity() == city)
	//			.toList();
	//}
}
