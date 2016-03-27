package com.mu.server;

import java.io.IOException;
import java.net.Socket;

import com.mu.MuApplication;
import com.mu.http.HttpRequest;
import com.mu.http.HttpResponse;
import com.mu.http.ResponseStatusCode;

public class MuServer implements Runnable {
	private Socket sock;
	private MuApplication app;
	
	public MuServer(Socket sock, MuApplication app) {
		this.sock = sock;
		this.app = app;
	}

	@Override
	public void run() {
		try {
			HttpRequest request = new HttpRequest(sock.getInputStream());
			HttpResponse response = new HttpResponse();
			app.handleRequest(request, response);
			response.setResponseStatusCode(ResponseStatusCode.OK);
			response.writeHttpResonse(sock.getOutputStream());
		    sock.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}