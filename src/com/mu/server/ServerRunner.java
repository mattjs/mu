package com.mu.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.mu.MuApplication;

public class ServerRunner {
	static ServerSocket serverSocket;
	static int port = 3000;
	
	public static void start(MuApplication app) {
		System.out.println("Starting mu server");
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + port);
			System.out.println(e);
			System.exit(1);
		}
		System.out.println("Listening on port " + port);
		while (true) {
			Socket sock = null;
			try {
				sock = serverSocket.accept();
			} catch (IOException e) {
				System.out.println("Connection failed");
				System.out.println(e);
				System.exit(1);
			}
			System.out.println("Connection accepted");
			new Thread(new MuServer(sock, app)).start();
		}
	}
}