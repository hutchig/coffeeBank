package com.ibm.bank;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.reactive.streams.ReactiveStreams;

@ApplicationPath("rest")
public class BankApplication extends Application {

	public static boolean running = false;

}
