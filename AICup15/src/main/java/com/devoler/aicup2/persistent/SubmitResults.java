package com.devoler.aicup2.persistent;

import java.util.Date;

public final class SubmitResults {
	private long id;
	private String name;
	private Date submitTime;
	private String solution1;
	private String solution2;
	private String solution3;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSolution1() {
		return solution1;
	}
	
	public void setSolution1(String solution1) {
		this.solution1 = solution1;
	}
	
	public String getSolution2() {
		return solution2;
	}
	
	public void setSolution2(String solution2) {
		this.solution2 = solution2;
	}
	
	public String getSolution3() {
		return solution3;
	}
	
	public void setSolution3(String solution3) {
		this.solution3 = solution3;
	}
	
	public Date getSubmitTime() {
		return submitTime;
	}
	
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
}
