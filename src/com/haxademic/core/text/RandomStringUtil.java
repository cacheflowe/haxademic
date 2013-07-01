package com.haxademic.core.text;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Code from: http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 * @author cacheflowe
 *
 */
public class RandomStringUtil {

	//----------------------------------------------------------------------------
	public static String randomString() {
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}

	//----------------------------------------------------------------------------
	public static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMERIC = "0123456789";
	static Random rnd = new Random();

	public static String randomStringOfLength( int len, String charString )  {
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) sb.append( charString.charAt( rnd.nextInt(charString.length()) ) );
		return sb.toString();
	}

	//----------------------------------------------------------------------------
	public static String randomUUID( int len )  { 
		return UUID.randomUUID().toString().substring( 0, len - 1 );
	}

	//----------------------------------------------------------------------------
	public static SecureRandom random = new SecureRandom();
	public static String newSessionId() {
		return new BigInteger(130, random).toString(32);
	}
}
