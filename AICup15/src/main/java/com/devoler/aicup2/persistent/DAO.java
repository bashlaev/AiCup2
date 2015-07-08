package com.devoler.aicup2.persistent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.devoler.aicup2.model.RaceResult;

public final class DAO {
	private static final Configuration configuration;
	private static final SessionFactory sessionFactory;
	private static Session session;
	private static Transaction tx;

	static {
		configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		sessionFactory = configuration.buildSessionFactory();
	}

	static Session getSession() {
		if (session == null) {
			session = sessionFactory.openSession();
		}
		return session;
	}

	static void closeSession() {
		if ((session != null) && session.isOpen()) {
			session.close();
			session = null;
		}
	}

	static void beginTransaction() {
		try {
			if (tx == null) {
				tx = getSession().beginTransaction();
			}
		} catch (final HibernateException ex) {
			ex.printStackTrace();
		}
	}

	static void commitTransaction() {
		try {
			if ((tx != null) && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.commit();
				tx = null;
			}
		} catch (final HibernateException ex) {
			rollbackTransaction();
			ex.printStackTrace();
		} finally {
			try {
				closeSession();
			} catch (final HibernateException ex) {
				ex.printStackTrace();
			}
		}
	}

	static void rollbackTransaction() {
		try {
			if ((tx != null) && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.rollback();
			}
		} catch (final HibernateException ex) {
			ex.printStackTrace();
		} finally {
			closeSession();
		}
	}

	public static synchronized void saveTestResult(long testNo,
			RaceResult.Status status, long testResult, String solution) {
		try {
			Session session = getSession();
			beginTransaction();
			try {
				TestResults results = new TestResults();
				results.setTestNo(testNo);
				results.setStatus(status.ordinal());
				results.setTestResult(testResult);
				results.setTestTimestamp(new Date());
				results.setSolution(solution);
				session.save(results);
				commitTransaction();
			} catch (RuntimeException e) {
				rollbackTransaction();
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized List<TestResults> getTestResults(long testNo,
			int maxResults) {
		try {
			Session session = getSession();
			beginTransaction();
			try {
				List<TestResults> results = (List<TestResults>) session
						.createCriteria(TestResults.class)
						.add(Restrictions.eq("testNo", testNo))
						.addOrder(Order.asc("status"))
						.addOrder(Order.asc("testResult"))
						.addOrder(Order.desc("testTimestamp"))
						.setMaxResults(maxResults).list();
				commitTransaction();
				return results;
			} catch (RuntimeException e) {
				rollbackTransaction();
				throw e;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
