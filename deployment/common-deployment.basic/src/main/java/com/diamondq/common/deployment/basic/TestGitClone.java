package com.diamondq.common.deployment.basic;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import org.jclouds.scriptbuilder.domain.GitRepoAndRef;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.git.CloneGitRepo;

public class TestGitClone extends CloneGitRepo {

  public static Builder builder() {
    return new TestBuilder();
  }

  public static class TestBuilder extends Builder {

    public TestBuilder() {
      super();
    }

    @Override
    public CloneGitRepo build() {
      return new TestGitClone(gitRepoAndRef.build(), directory);
    }
  }

  protected TestGitClone(GitRepoAndRef pGitRepoAndRef, Optional<String> pDirectory) {
    super(pGitRepoAndRef, pDirectory);
  }

  @Override
  public String render(OsFamily pArg0) {
    StringBuilder command = new StringBuilder();
    command.append("git clone -q");
    if (gitRepoAndRef.getBranch().isPresent())
      command.append(" -b ").append(gitRepoAndRef.getBranch().get());
    command.append(' ').append(gitRepoAndRef.getRepository().toASCIIString());
    if (directory.isPresent())
      command.append(' ').append(directory.get());
    command.append("{lf}");
    command.append("{cd} ").append(directory
      .or(Iterables.getLast(Splitter.on('/').split(gitRepoAndRef.getRepository().getPath())).replace(".git", "")));
    if (gitRepoAndRef.getTag().isPresent()) {
      command.append("{lf}").append("git checkout ").append(gitRepoAndRef.getTag().get());
    }
    return new StatementList(Statements.literal("trap '' 1"), Statements.exec(command.toString())).render(pArg0);
  }

}
