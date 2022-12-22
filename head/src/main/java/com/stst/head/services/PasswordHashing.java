package com.stst.head.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;

public class PasswordHashing {
	
	 public static String hashPassword(char[] passwordChars) { // by PBKDF2

	     String salt = "653498057332984573349857"; // salt value that should append to the password
	     int iterations = 10000; // no. of iterations to be done. This value can be used to adjust the speed of the algorithm
	     int keyLength = 246; // This is the required output length of the hashed function
	     byte[] saltBytes = salt.getBytes();

	     byte[] hashedBytes = hashPassword2(passwordChars, saltBytes, iterations, keyLength);
	     String hashedString = Hex.encodeHexString(hashedBytes);

	     return hashedString;
	 }

	 private static byte[] hashPassword2(final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

	     try {
	         SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
	         PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
	         SecretKey key = skf.generateSecret( spec );
	         byte[] res = key.getEncoded( );
	         return res;
	     } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
	         throw new RuntimeException( e );
	     }
	 }
}
