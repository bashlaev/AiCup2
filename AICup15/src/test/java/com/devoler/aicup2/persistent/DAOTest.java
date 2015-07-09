package com.devoler.aicup2.persistent;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.devoler.aicup2.model.RaceResult.Status;

public class DAOTest {

	@Test
	public void testResults() {
		DAO.saveTestResult(1, Status.SUCCESS, 100500, "s1");
		DAO.saveTestResult(1, Status.SUCCESS, 100400, "s2");
		DAO.saveTestResult(1, Status.SUCCESS, 100600, "s3");
		DAO.saveTestResult(1, Status.DID_NOT_FINISH, 100000, "s4");
		DAO.saveTestResult(2, Status.SUCCESS, 100, String.format("%010000d", 1));
		List<TestResults> results1 = DAO.getTestResults(1, 10);
		assertEquals(4, results1.size());
		assertEquals(100400, results1.get(0).getTestResult());
		assertEquals(100500, results1.get(1).getTestResult());
		assertEquals(100600, results1.get(2).getTestResult());
		assertEquals(Status.DID_NOT_FINISH.ordinal(), results1.get(3).getStatus());
		assertEquals(100000, results1.get(3).getTestResult());
		List<TestResults> results11 = DAO.getTestResults(1, 2);
		assertEquals(2, results11.size());
		assertEquals(100400, results11.get(0).getTestResult());
		assertEquals(100500, results11.get(1).getTestResult());
		List<TestResults> results2 = DAO.getTestResults(2, 10);
		assertEquals(1, results2.size());
		assertEquals(100, results2.get(0).getTestResult());
	}
	
	@Test
	public void submitResults() {
		long time = System.currentTimeMillis();
		DAO.submitResult("p1", "s11", "s12", "s13", time);
		DAO.submitResult("p2", "s21", "s22", "s23", time + 1000);
		DAO.submitResult("p3", "s31", "s32", "s33", time + 2000);
		List<SubmitResults> results = DAO.getSubmitResults();
		assertEquals(3, results.size());
		assertEquals("p1", results.get(0).getName());
		assertEquals("p2", results.get(1).getName());
		assertEquals("p3", results.get(2).getName());
		assertEquals("s11", results.get(0).getSolution1());
		assertEquals("s21", results.get(1).getSolution1());
		assertEquals("s31", results.get(2).getSolution1());
		DAO.submitResult("p2", "s21+", "s22+", "s23+", time + 3000);
		results = DAO.getSubmitResults();
		assertEquals(3, results.size());
		assertEquals("p1", results.get(0).getName());
		assertEquals("p3", results.get(1).getName());
		assertEquals("p2", results.get(2).getName());
		assertEquals("s11", results.get(0).getSolution1());
		assertEquals("s31", results.get(1).getSolution1());
		assertEquals("s21+", results.get(2).getSolution1());
	}

}
