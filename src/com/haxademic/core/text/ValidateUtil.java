package com.haxademic.core.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {
	 
 
	protected static final String EMAIL_PATTERN =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	protected static final String EMAIL_CHARACTER =  "[A-Za-z0-9\\-\\+\\.@_]";
	protected static final String ALPHANUMERIC =  "[A-Za-z0-9]";
	protected static final String ALPHANUMERIC_AND_CHARCTERS =  "[a-zA-Z0-9\\-#\\.\\(\\)\\/%&\\s]";

	protected static Pattern patternEmail = Pattern.compile(EMAIL_PATTERN);
	protected static Matcher emailMatcher;
 
	protected static Pattern patternEmailChar = Pattern.compile(EMAIL_CHARACTER);
	protected static Matcher emailCharMatcher;
 
	protected static Pattern patternAlphanumericChar = Pattern.compile(ALPHANUMERIC);
	protected static Matcher alphanumericCharMatcher;
 
	protected static Pattern patternAlphanumericCharAndSpecialChars = Pattern.compile(ALPHANUMERIC_AND_CHARCTERS);
	protected static Matcher patternAlphanumericCharAndSpecialCharsMatcher;
	
	/**
	 * Validate email input with regular expression
	 * Originally from: http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
	 * 
	 * @param input String for validation
	 * @return true valid email, false invalid email
	 */
	public static boolean validateEmail( String input ) {
		emailMatcher = patternEmail.matcher( input );
		return emailMatcher.matches();
	}
	
	/**
	 * Validate a single email character input with regular expression
	 * 
	 * @param input String for validation
	 * @return true valid email character, false invalid email character
	 */
	public static boolean validateEmailCharacter( String input ) {
		emailCharMatcher = patternEmailChar.matcher( input );
		return emailCharMatcher.matches();
	}
	
	/**
	 * Validate a single alphanumeric character input with regular expression
	 * 
	 * @param input String for validation
	 * @return true valid alphanumeric character, false invalid alphanumeric character
	 */
	public static boolean validateAlphanumericCharacter( String input ) {
		alphanumericCharMatcher = patternAlphanumericChar.matcher( input );
		return alphanumericCharMatcher.matches();
	}

	public static boolean alphanumericCharactersWithSpecialCharacters( String input ) {
		patternAlphanumericCharAndSpecialCharsMatcher = patternAlphanumericCharAndSpecialChars.matcher( input );
		return patternAlphanumericCharAndSpecialCharsMatcher.matches();
	}
}
