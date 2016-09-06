package com.diamondq.common.deployment.basic;

import com.google.common.collect.ImmutableList;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.chef.InstallChefUsingOmnibus;
import org.jclouds.scriptbuilder.statements.git.InstallGit;

public class SetupNode {

	private final String mChefVersion;

	public SetupNode(String pChefVersion) {
		mChefVersion = pChefVersion;
	}

	public void run(ComputeService pComputeService, String pNodeId) {

		/* First set is to identify the components that are already present or not present */

		NodeInfo info = getNodeInfo(pComputeService, pNodeId);

		/* Now, based on the results, perform different actions */

		if (info.gitInstalled == false)
			installGit(pComputeService, pNodeId);

		if (info.chefInstalled == false)
			installChef(pComputeService, pNodeId);
		else {
			if (info.correctVersionOfChefInstalled == false)
				upgradeChef(pComputeService, pNodeId);
		}

		if (info.dependencyCookbooksInstalled == false)
			installDependencyCookbooks(pComputeService, pNodeId);

		if (info.mainCookbookInstalled == false)
			installMainCookbook(pComputeService, pNodeId);
		else {
			if (info.correctVersionOfMainCookbookInstalled == false)
				upgradeMainCookbook(pComputeService, pNodeId);
		}

		/* Now, execute a chef run to make sure that the environment is good */

		runChef(pComputeService, pNodeId);

	}

	private void runChef(ComputeService pComputeService, String pNodeId) {
		StringBuilder sb = new StringBuilder();
		sb.append("chef-client --local-mode --runlist 'recipe[DiamondNode]' --config-option chef_repo_path=/var/chef");
		checkSuccess(pComputeService.runScriptOnNode(pNodeId, Statements.exec(sb.toString())), "Unable to run chef");
	}

	private void upgradeMainCookbook(ComputeService pComputeService, String pNodeId) {
		// throw new UnsupportedOperationException();
	}

	private void installMainCookbook(ComputeService pComputeService, String pNodeId) {
		ImmutableList.Builder<Statement> statementBuilder = ImmutableList.builder();

		// statementBuilder.add(Statements.extractTargzIntoDirectory(targz, directory))

		checkSuccess(pComputeService.runScriptOnNode(pNodeId, new StatementList(statementBuilder.build())),
			"Unable to install main cookbook");
	}

	private void installDependencyCookbooks(ComputeService pComputeService, String pNodeId) {
		ImmutableList.Builder<Statement> statementBuilder = ImmutableList.builder();
		String[] recipeList = new String[] {"docker", "compat_resource"};
		for (String recipe : recipeList) {
			statementBuilder.add(Statements.exec("{rm} -rf /var/chef/cookbooks/" + recipe));
			statementBuilder.add(TestGitClone.builder().repository("git://github.com/chef-cookbooks/" + recipe + ".git")
				.directory("/var/chef/cookbooks/" + recipe) //
				.build());
		}
		checkSuccess(pComputeService.runScriptOnNode(pNodeId, new StatementList(statementBuilder.build())),
			"Failed to install dependency cookbooks");
	}

	private void upgradeChef(ComputeService pComputeService, String pNodeId) {
		throw new UnsupportedOperationException();
	}

	private void installChef(ComputeService pComputeService, String pNodeId) {
		checkSuccess(pComputeService.runScriptOnNode(pNodeId, new InstallChefUsingOmnibus(mChefVersion)),
			"Failed to install chef");
	}

	/**
	 * Installs Git on the box
	 * 
	 * @param pComputeService
	 * @param pNodeId
	 */
	private void installGit(ComputeService pComputeService, String pNodeId) {
		checkSuccess(pComputeService.runScriptOnNode(pNodeId, new InstallGit()), "Failed to install Git");
	}

	private NodeInfo getNodeInfo(ComputeService pComputeService, String pNodeId) {
		ImmutableList.Builder<Statement> statementBuilder = ImmutableList.builder();

		statementBuilder.add(Statements.literal("pattern='git version.*'"));
		statementBuilder.add(Statements.literal("if [[ $(git --version) =~ $pattern ]]; then"));
		statementBuilder.add(Statements.literal("	echo -n 'gitInstalled=true'"));
		statementBuilder.add(Statements.literal("else"));
		statementBuilder.add(Statements.literal("	echo -n 'gitInstalled=false'"));
		statementBuilder.add(Statements.literal("fi"));
		statementBuilder.add(Statements.literal(""));
		statementBuilder.add(Statements.literal("chefpattern='Chef: ([0-9]+.[0-9]+)'"));
		statementBuilder.add(Statements.literal("if [[ $(chef-solo --version) =~ $chefpattern ]]; then"));
		statementBuilder.add(Statements.literal("	echo -n '&chefInstalled=true'"));
		statementBuilder.add(Statements.literal("	if [ ${BASH_REMATCH[1]} = '" + mChefVersion + "' ]; then"));
		statementBuilder.add(Statements.literal("		echo -n '&correctVersionOfChefInstalled=true'"));
		statementBuilder.add(Statements.literal("	else"));
		statementBuilder.add(Statements.literal("		echo -n '&correctVersionOfChefInstalled=false'"));
		statementBuilder.add(Statements.literal("	fi"));
		statementBuilder.add(Statements.literal("else"));
		statementBuilder.add(Statements.literal("	echo -n '&chefInstalled=false'"));
		statementBuilder.add(Statements.literal("fi"));

		statementBuilder.add(Statements
			.literal("if [ -d '/var/chef/cookbooks/docker' ] && [ -d '/var/chef/cookbooks/compat_resource' ]; then"));
		statementBuilder.add(Statements.literal("	echo -n '&dependencyCookbooksInstalled=true'"));
		statementBuilder.add(Statements.literal("else"));
		statementBuilder.add(Statements.literal("	echo -n '&dependencyCookbooksInstalled=false'"));
		statementBuilder.add(Statements.literal("fi"));

		statementBuilder.add(Statements.literal("if [ -d '/var/chef/cookbooks/DiamondNode' ]; then"));
		statementBuilder.add(Statements.literal("        echo -n '&mainCookbookInstalled=true'"));
		statementBuilder.add(Statements.literal(""));
		statementBuilder.add(Statements.literal("	md5pattern='(\\S+)\\s+'"));
		statementBuilder.add(Statements
			.literal("	[[ $(tar cf - /var/chef/cookbooks/DiamondNode 2>/dev/null | md5sum -b) =~ $md5pattern ]]"));
		statementBuilder.add(Statements.literal(""));
		statementBuilder
			.add(Statements.literal("	if [ ${BASH_REMATCH[1]}	= '7899a07b7bc0abc2621d92092705ef00' ]; then"));
		statementBuilder.add(Statements.literal("		echo -n '&correctVersionOfMainCookbookInstalled=true'"));
		statementBuilder.add(Statements.literal("	else"));
		statementBuilder.add(Statements.literal("		echo -n '&correctVersionOfMainCookbookInstalled=false'"));
		statementBuilder.add(Statements.literal("	fi"));
		statementBuilder.add(Statements.literal(""));
		statementBuilder.add(Statements.literal("else"));
		statementBuilder.add(Statements.literal("        echo -n '&mainCookbookInstalled=false'"));
		statementBuilder.add(Statements.literal("fi"));
		statementBuilder.add(Statements.literal("echo"));

		ExecResponse results = pComputeService.runScriptOnNode(pNodeId, new StatementList(statementBuilder.build()));
		checkSuccess(results, "Unable to get node information");

		String output = results.getOutput();
		String[] split = output.split("&");
		NodeInfo n = new NodeInfo();
		for (String s : split) {
			String[] parts = s.split("=");
			if ("gitInstalled".equals(parts[0]))
				n.gitInstalled = Boolean.valueOf(parts[1]);
			if ("chefInstalled".equals(parts[0]))
				n.chefInstalled = Boolean.valueOf(parts[1]);
			if ("correctVersionOfChefInstalled".equals(parts[0]))
				n.correctVersionOfChefInstalled = Boolean.valueOf(parts[1]);
			if ("dependencyCookbooksInstalled".equals(parts[0]))
				n.dependencyCookbooksInstalled = Boolean.valueOf(parts[1]);
			if ("mainCookbookInstalled".equals(parts[0]))
				n.mainCookbookInstalled = Boolean.valueOf(parts[1]);
			if ("correctVersionOfMainCookbookInstalled".equals(parts[0]))
				n.correctVersionOfMainCookbookInstalled = Boolean.valueOf(parts[1]);
		}
		return n;
	}

	private void checkSuccess(ExecResponse pResponse, String pError) {
		if (pResponse.getExitStatus() == 0)
			return;
		throw new IllegalStateException(pError + "\n" + pResponse.getError());
	}

}
