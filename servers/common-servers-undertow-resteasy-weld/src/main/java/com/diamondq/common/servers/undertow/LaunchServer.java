package com.diamondq.common.servers.undertow;

import com.diamondq.common.config.Config;
import com.diamondq.common.config.core.std.StandardBootstrap;

import javax.ws.rs.core.Application;

public class LaunchServer {

	public static void run(String[] pArgs, String pAppName, Class<? extends Application> pAppClass) {
		try {
			/* Set the application name so that the initial bootstrap can load properly */

			System.setProperty("application.name", pAppName);

			/*
			 * Bootstrap initially. NOTE: This bootstrap/config is only used for server setup. A second bootstrap/config
			 * is computed once the CDI system is operational.
			 */

			Config config = new StandardBootstrap().bootstrap();
			@SuppressWarnings("unused")
			UndertowRESTEasyWeldServer server = new UndertowRESTEasyWeldServer(config, pAppClass);

			/* Wait forever */

			while (true) {
				try {
					Thread.sleep(1000000L);
				}
				catch (InterruptedException ex) {
				}
			}
		}
		catch (RuntimeException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

}
