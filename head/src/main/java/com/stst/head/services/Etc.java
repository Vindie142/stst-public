package com.stst.head.services;

import java.math.BigDecimal;

public class Etc {
	
	public static String standardizePhone(String phone) { // we get the format " 89276 047743sdvs" and we give "8 (927) 604-77-43"
		char[] charPhone = phone.replaceFirst("\\+7", "8").replaceAll("[^\\d]","").toCharArray();
		if (charPhone.length == 11) {
			return charPhone[0] + " (" + charPhone[1] + charPhone[2] + charPhone[3] + ") " + charPhone[4] + charPhone[5] + charPhone[6] +
					"-" + charPhone[7] + charPhone[8] + "-" + charPhone[9] + charPhone[10];
		}
		return phone;
	}
	
	public static Long idFromDatalist(String str) { // we get the format "542: Александров А.А." or "[542] Александров А.А." and we give "542"
		if (str == null || str == "") {return null;}
		
		int iend = str.indexOf(":");
		if (iend != -1) {
			try {
				return Long.valueOf( str.substring(0 , iend) );
			} catch (NumberFormatException e) {return null;}
		}
		
		int iend2 = str.indexOf("]");
		if (iend2 != -1) {
			try {
				return Long.valueOf( str.substring(1 , iend2) );
			} catch (NumberFormatException e) {return null;}
		}
		return null;
	}
	
	public static String valueFormHttpResponseBody(String body, String neededKey) { 
		int pastKeyIndex = body.indexOf(neededKey) + neededKey.length();
		int separationIndex = body.indexOf(':', pastKeyIndex +1) ;
		int firstValueIndex = body.indexOf('"', separationIndex +1);
		int pastValueIndex = body.indexOf('"', firstValueIndex +1);
		return body.substring(firstValueIndex+1, pastValueIndex);
	}
	
	public static BigDecimal priceWithVatFrom(BigDecimal price, BigDecimal ratio) { // ценаСНДС = ценаБезНДС * (коэфф / (1 - коэфф))
		return price.multiply( ratio.divide( new BigDecimal("1").subtract(ratio)  ) );
	}
	
	public static String beautifyingPrice(BigDecimal price) { // from 12345678.23542 to 12 345 678.23542
		String priceS = price.toPlainString();
		int index = priceS.indexOf('.');
	    int decimal = index < 0 ? 0 : priceS.length() - 1 - index;
	    
	    String answer = "";
	    byte counter = 0;
	    for (int i = priceS.length() - decimal - 2; i >= 0; i--) {
	    	answer = priceS.charAt(i) + answer;
	    	counter++;
	    	if (counter == 3 && i != 0) {
	    		answer = " " + answer;
	    		counter = 0;
	    	}
		}
	    return answer + priceS.substring(index);
	}
}
