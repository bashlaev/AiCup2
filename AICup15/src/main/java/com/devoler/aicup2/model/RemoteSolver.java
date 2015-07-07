package com.devoler.aicup2.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public final class RemoteSolver implements Solver {
	private class SolverCallable implements Callable<String> {
		private final String raceTrackString;
		
		public SolverCallable(final String raceTrackString) {
			this.raceTrackString = raceTrackString;
		}
		
		@Override
		public String call() throws Exception {
			byte[] raceTrackBytes = raceTrackString.getBytes();
			URLConnection urlConnection = url.openConnection();
			if (!(urlConnection instanceof HttpURLConnection)) {
				throw new RuntimeException("Not an HTTP url: " + url);
			}
			HttpURLConnection connection = (HttpURLConnection) urlConnection;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "text/plain");
			connection.setRequestProperty("Content-Type", "text/plain");
			connection.setRequestProperty("Content-Length", String.valueOf(raceTrackBytes.length));
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(60000);
			OutputStream os = connection.getOutputStream();
			os.write(raceTrackBytes);
			os.flush();
			os.close();
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				throw new RuntimeException("Response code: " + responseCode);
			}
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try (InputStream is = connection.getInputStream()) {
				int nRead;
				byte[] data = new byte[256];
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
			}
			return new String(buffer.toByteArray());
		}
	}
	
	private final URL url;
	private final ExecutorService executor = Executors
			.newSingleThreadExecutor(new BasicThreadFactory.Builder().daemon(
					true).build());

	public RemoteSolver(final URL url) {
		this.url = url;
	}

	@Override
	public Future<String> solve(String raceTrackString) {
		return executor.submit(new SolverCallable(raceTrackString));
	}

}
