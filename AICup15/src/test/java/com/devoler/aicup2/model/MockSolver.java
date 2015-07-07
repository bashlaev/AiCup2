package com.devoler.aicup2.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class MockSolver {
	public MockSolver() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/", new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				try (InputStream is = exchange.getRequestBody()) {
					int nRead;
					byte[] data = new byte[256];
					while ((nRead = is.read(data, 0, data.length)) != -1) {
						buffer.write(data, 0, nRead);
					}
					buffer.flush();
				}
				String in = new String(buffer.toByteArray());
				System.out.println("Solver: received task " + in);
				RaceTrack track = RaceTrackParser.parse(in);
				System.out.println("Solver: parsed track " + track);
				StringTokenizer st = new StringTokenizer(in, " ");
				// ignore width
				st.nextToken();
				String directions = st.nextToken();
				StringBuilder solution = new StringBuilder();
				for(char c: directions.toCharArray()) {
					switch(c) {
					case 'U':
						solution.append("UD");
						break;
					case 'D':
						solution.append("DU");
						break;
					case 'L':
						solution.append("LR");
						break;
					case 'R':
						solution.append("RL");
						break;
					}
				}				
				String out = solution.toString();
				System.out.println("Solver: solution found: " + out);

				byte[] outContent = out.getBytes();

				exchange.sendResponseHeaders(200, outContent.length);
				exchange.getResponseBody().write(outContent);
				exchange.getResponseBody().close();
			}
		});
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println("Solver: started");
	}

	public static void main(String[] args) {
		try {
			new MockSolver();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
