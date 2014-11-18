package org.sam.christmasdrawing.util;

import java.util.HashSet;
import java.util.Set;

public class Person {
	private String name;
	private Set<String> notAllowed;
	
	public Person() {
		name = "";
		notAllowed = new HashSet<String>();
	}
	
	public Person(String name) {
		this.name = name;
		this.notAllowed = new HashSet<String>();
	}
	
	public Person(String name, Set<String> notAllowed) {
		this.name = name;
		this.notAllowed = new HashSet<String>(notAllowed);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void addNotAllowed(String notAllow) {
		notAllowed.add(notAllow);
	}
	
	public boolean isAllowed(String otherName) {
		return !notAllowed.contains(otherName);
	}
	
}
