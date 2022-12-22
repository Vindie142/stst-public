package com.stst.head.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum CityEnum { // all departments of the company are stored here
	
	// !!!!! new entries have to be entered only at the end !!!!!
	SAMARA ("samara", "Самара", "0.15", false),
	TOLYATTI ("tolyatti", "Тольятти", "0.15", false);

	private String abbr; // abbreviation
	private String name;
	private String cashOutRatio; // коэффициент обналички
	private boolean deleted;
	
	CityEnum(String abbr, String name, String cashOutRatio, boolean deleted) {
		this.abbr = abbr;
		this.name = name;
		this.cashOutRatio = cashOutRatio;
		this.deleted = deleted;
	}

	public static Optional<CityEnum> getEnumByAbbr(String abbr) { // returns an instance by abbreviation
		try {
			CityEnum cityEnum = CityEnum.valueOf(abbr.toUpperCase().trim());
			return Optional.of(cityEnum);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	public static Map<String, String> getMap() { // returns map of the enum
		Map<String, String> map = new HashMap<String, String>();
		for (CityEnum cityEnum : CityEnum.values()) {
			if (!cityEnum.isDeleted()) {
				map.put(cityEnum.abbr, cityEnum.name);
			}
		}
		return map;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public String getAbbr() {
	    return abbr;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCashOutRatio() {
		return cashOutRatio;
	}
	
}
