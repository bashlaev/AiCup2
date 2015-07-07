package com.devoler.aicup2.persistent;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class DAOTest {

	@Test
	public void test() {
		DAO.saveTestResult(1, 100500);
		DAO.saveTestResult(1, 100400);
		DAO.saveTestResult(1, 100600);
		DAO.saveTestResult(1, 100000);
		DAO.saveTestResult(2, 100);
		List<TestResults> results1 = DAO.getTestResults(1, 10);
		assertEquals(4, results1.size());
		assertEquals(100000, results1.get(0).getTestResult());
		assertEquals(100400, results1.get(1).getTestResult());
		assertEquals(100500, results1.get(2).getTestResult());
		assertEquals(100600, results1.get(3).getTestResult());
		List<TestResults> results11 = DAO.getTestResults(1, 2);
		assertEquals(2, results11.size());
		assertEquals(100000, results11.get(0).getTestResult());
		assertEquals(100400, results11.get(1).getTestResult());
		List<TestResults> results2 = DAO.getTestResults(2, 10);
		assertEquals(1, results2.size());
		assertEquals(100, results2.get(0).getTestResult());
	}

}
