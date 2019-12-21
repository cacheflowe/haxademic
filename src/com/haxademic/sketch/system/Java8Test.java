package com.haxademic.sketch.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;

import processing.core.PImage;

public class Java8Test
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	public PImage img;

	protected void config() {
		// Config.setProperty( AppSettings.FPS, "60" );
	}


	public void firstFrame() {

		sortArray();
		interfaceTest();
		methodReference();
		interfaceDefaultMethods();
	}
	
	// LAMBDA OPERATIONS ============================================


	protected void sortArray() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		// sort a collection
		Collections.sort(names, (String a, String b) -> {
			return b.compareTo(a);
		});
		// or more brief
		Collections.sort(names, (String a, String b) -> a.compareTo(b));
		P.println(names);
	}
	
	// INLINE INTERFACE CREATION =======================================


	protected void interfaceTest() {
		//with type declaration
		MathOperation addition = (int a, int b) -> a + b;

		//with out type declaration
		MathOperation subtraction = (a, b) -> a - b;

		//with return statement along with curly braces
		MathOperation multiplication = (int a, int b) -> { return a * b; };

		//without return statement and without curly braces
		MathOperation division = (int a, int b) -> a / b;

		System.out.println("10 + 5 = " + operate(10, 5, addition));
		System.out.println("10 - 5 = " + operate(10, 5, subtraction));
		System.out.println("10 x 5 = " + operate(10, 5, multiplication));
		System.out.println("10 / 5 = " + operate(10, 5, division));

		//with parenthesis
		GreetingService greetService1 = message ->
		System.out.println("Hello " + message);

		//without parenthesis
		GreetingService greetService2 = (message) ->
		System.out.println("Hello " + message);

		greetService1.sayMessage("Mahesh");
		greetService2.sayMessage("Suresh");
	}

	interface MathOperation {
		int operation(int a, int b);
	}

	interface GreetingService {
		void sayMessage(String message);
	}

	private int operate(int a, int b, MathOperation mathOperation){
		return mathOperation.operation(a, b);
	}

	// METHOD REFERENCES =======================================
	
	protected void methodReference() {
		List<String> names = new ArrayList<String>();

		names.add("Mahesh");
		names.add("Suresh");
		names.add("Ramesh");
		names.add("Naresh");
		names.add("Kalpesh");

		names.forEach(System.out::println);
	}
	
	// INTERFACE DEFAULT METHODS ===============================
	
	protected void interfaceDefaultMethods() {
		Vehicle vehicle = new Car();
		vehicle.print();
	}
	
	interface Vehicle {
		default void print(){
			System.out.println("I am a vehicle!");
		}

		static void blowHorn(){
			System.out.println("Blowing horn!!!");
		}
	}

	interface FourWheeler {
		default void print(){
			System.out.println("I am a four wheeler!");
		}
	}

	class Car implements Vehicle, FourWheeler {
		public void print(){
			Vehicle.super.print();
			FourWheeler.super.print();
			Vehicle.blowHorn();
			System.out.println("I am a car!");
		}
	}

	public void drawApp() {
	}
}
