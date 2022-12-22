package com.stst.head.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum MeasurementEnum { // all departments of the company are stored here
	
	// !!!!! new entries have to be entered only at the end !!!!!
	P0 ("p0", " ", false),
	P1 ("p1", "шт", false),
	P2 ("p2", "ряд", false),
	P3 ("p3", "отв.", false),
	P4 ("p4", "фланец", false),
	P5 ("p5", "стык", false),
	P6 ("p6", "мп", false),
	P7 ("p7", "мм", false),
	P8 ("p8", "см", false),
	P9 ("p9", "м", false),
	P10 ("p10", "м²", false),
	P11 ("p11", "м³", false),
	P12 ("p12", "г", false),
	P13 ("p13", "кг", false),
	P14 ("p14", "т", false),
	P15 ("p15", "час", false),
	P16 ("p16", "сут", false),
	P17 ("p17", "чел/час", false),
	P18 ("p18", "чел/дн", false),
	P19 ("p19", "смена", false),
	P20 ("p20", "рейс", false),
	P21 ("p21", "л", false);

	private String abbr; // abbreviation
	private String name;
	private boolean deleted;
	
	MeasurementEnum(String abbr, String name, boolean deleted) {
		this.abbr = abbr;
		this.name = name;
		this.deleted = deleted;
	}

	public static Optional<MeasurementEnum> getEnumByAbbr(String abbr) { // returns an instance by abbreviation
		if (abbr == null || abbr.equals("")) {
			return Optional.of(P0);
		}
		
		try {
			MeasurementEnum measurementEnum = MeasurementEnum.valueOf(abbr.toUpperCase().trim());
			return Optional.of(measurementEnum);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
	
	public static Map<String, String> getMap() { // returns map of the enum
		Map<String, String> map = new HashMap<String, String>();
		for (MeasurementEnum measurementEnum : MeasurementEnum.values()) {
			if (!measurementEnum.isDeleted()) {
				map.put(measurementEnum.abbr, measurementEnum.name);
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
	
}
