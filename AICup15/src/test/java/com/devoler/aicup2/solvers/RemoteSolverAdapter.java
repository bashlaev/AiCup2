package com.devoler.aicup2.solvers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public final class RemoteSolverAdapter {
	public RemoteSolverAdapter(final String name,
			final CallableSolver callableSolver, int port) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		final ExecutorService runner = Executors
				.newCachedThreadPool(new BasicThreadFactory.Builder().daemon(
						true).build());
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
				System.out.println(name + ": received task " + in);
				String out;
				Future<String> future = runner.submit(callableSolver.solverFor(in));
				try {
					out = future.get(1, TimeUnit.MINUTES);
				} catch (InterruptedException | ExecutionException
						| TimeoutException e) {
					future.cancel(true);
					exchange.close();
					return;
				}
				System.out.println(name + ": solution found: " + out);

				byte[] outContent = out.getBytes();

				exchange.sendResponseHeaders(200, outContent.length);
				exchange.getResponseBody().write(outContent);
				exchange.getResponseBody().close();
			}
		});
		server.setExecutor(Executors.newCachedThreadPool());
		server.start();
		System.out.println(name + ": started at " + port);
	}
}
