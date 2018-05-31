package com.diamondq.common.deployment.basic;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.byon.BYONApiMetadata;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

public class Test {

	public static void main(String[] args) throws Throwable {

		Properties contextProperties = new Properties();

		StringBuilder nodes = new StringBuilder();
		nodes.append("nodes:\n");
		nodes.append("    - id: mymachine\n");
		nodes.append("      name: my local machine\n");
		nodes.append("      hostname: manager\n");
		nodes.append("      os_arch: ").append(System.getProperty("os.arch")).append("\n");
		nodes.append("      os_family: ").append(OsFamily.LINUX).append("\n");
		nodes.append("      os_description: ").append(System.getProperty("os.name")).append("\n");
		nodes.append("      os_version: 16.04").append("\n");
		nodes.append("      group: ").append("ssh").append("\n");
		nodes.append("      tags:\n");
		nodes.append("          - local\n");
		nodes.append("      username: mmansell").append("\n");
		nodes.append("      credential: redsw*rd").append("\n");
		nodes.append("      sudo_password: redsw*rd").append("\n");
		// nodes.append(" credential_url: file://").append(System.getProperty("user.home")).append("/.ssh/id_rsa")
		// .append("\n");

		contextProperties.setProperty("byon.nodes", nodes.toString());
		ComputeServiceContext context = ContextBuilder.newBuilder(new BYONApiMetadata()).overrides(contextProperties)
			.modules(ImmutableSet.<Module> of(new SshjSshClientModule(), new Log4JLoggingModule()))
			.build(ComputeServiceContext.class);
		ComputeService computeService = context.getComputeService();

		SetupNode setupNode = new SetupNode("12.13");
		setupNode.run(computeService, "mymachine");
	}
}
