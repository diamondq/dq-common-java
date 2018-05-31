package com.diamondq.common.storage.cloudant.snippets;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;

import java.util.List;

public class TestConnection {

	public static void main(String[] args) {

		CloudantClient client = ClientBuilder.account("6bbb2907-7cbe-48b6-a1b6-1816a6ee6faa-bluemix")
			.username("6bbb2907-7cbe-48b6-a1b6-1816a6ee6faa-bluemix")
			.password("d399f0b7c00cb8fede26cab36667fdeab15e6216ede06612e7640d64b417af29").build();

		System.out.println("Server Version: " + client.serverVersion());

		// Get a List of all the databases this Cloudant account
		List<String> databases = client.getAllDbs();
		System.out.println("All my databases : ");
		for (String db : databases) {
			System.out.println(db);
		}

	}
}
