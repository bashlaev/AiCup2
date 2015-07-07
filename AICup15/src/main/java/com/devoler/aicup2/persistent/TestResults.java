package com.devoler.aicup2.persistent;

import java.util.Date;

public final class TestResults {
	private long id;
	private long testNo;
	private Date testTimestamp;
	private long testResult;
	private String solution;
	
	public long getId() {
		return id;
	}
	
	public long getTestNo() {
		return testNo;
	}
	
	public long getTestResult() {
		return testResult;
	}
	
	public Date getTestTimestamp() {
		return testTimestamp;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setTestNo(long testNo) {
		this.testNo = testNo;
	}
	
	public void setTestResult(long testResult) {
		this.testResult = testResult;
	}
	
	public void setTestTimestamp(Date testTimestamp) {
		this.testTimestamp = testTimestamp;
	}
	
	public String getSolution() {
		return solution;
	}
	
	public void setSolution(String solution) {
		this.solution = solution;
	}
	
	@Override
	public String toString() {
		return testResult + " ms, " + testTimestamp;
	}
}
