package com.stst.head.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public enum DepartEnum { // all departments of the company are stored here

	// !!!!! new entries have to be entered only at the end !!!!!
	PTO ("pto", "ПТО"),
	SNAB ("snab", "СНАБЖЕНИЕ"),
	SALES ("sales", "ПРОДАЖИ"),
	MAST ("mast", "МАСТЕР"),
	BUH ("buh", "БУХГАЛТЕРИЯ"),
	ADM ("adm", "АДМИНИСТРАЦИЯ"),
	LEAD ("lead", "РУКОВОДСТВО"),
	ADM2 ("adm2", "АДМИН+");

	private String abbr; // abbreviation
	private String name;

	DepartEnum(String abbr, String name) {
	    this.abbr = abbr;
	    this.name = name;
	}

	public static Optional<DepartEnum> getEnumByAbbr(String abbr) { // returns an instance by abbreviation
		try {
			DepartEnum departEnum = DepartEnum.valueOf(abbr.toUpperCase().trim());
			return Optional.of(departEnum);
		} catch (Exception e) {
			return Optional.empty();
		}
		
	}
	public static List<DepartEnum> getEnumsByAbbrs(String... abbr) { // returns an instance by abbreviation
		List<DepartEnum> departEnums = new ArrayList<DepartEnum>();
		for (int i = 0; i < abbr.length; i++) {
			try {
				DepartEnum departEnum = DepartEnum.valueOf(abbr[i].toUpperCase().trim());
				departEnums.add(departEnum);
			} catch (Exception e) {
				continue;
			}
		}
		return departEnums;
	}
	
	public static Map<String, String> getMap() { // returns map of the enum
		Map<String, String> map = new HashMap<String, String>();
		for (DepartEnum departEnum : DepartEnum.values()) { 
		    map.put(departEnum.abbr, departEnum.name);
		}
		return map;
	}
	
	public String getAbbr() {
	    return abbr;
	}
	
	public String getName() {
	    return name;
	}
}