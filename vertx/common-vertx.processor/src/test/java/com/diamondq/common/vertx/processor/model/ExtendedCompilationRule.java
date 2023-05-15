package com.diamondq.common.vertx.processor.model;

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.jetbrains.annotations.Nullable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.testing.compile.Compilation.Status.SUCCESS;
import static com.google.testing.compile.Compiler.javac;

public class ExtendedCompilationRule implements TestRule {
  private static final JavaFileObject DUMMY = JavaFileObjects.forSourceLines("Dummy", "final class Dummy {}");

  private Elements mElements;

  private Types mTypes;

  private ProcessingEnvironment mProcessingEnv;

  @SuppressWarnings("null")
  public ExtendedCompilationRule() {
  }

  /**
   * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement, org.junit.runner.Description)
   */
  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      /**
       * @see org.junit.runners.model.Statement#evaluate()
       */
      @Override
      public void evaluate() throws Throwable {
        EvaluatingProcessor evaluatingProcessor = new EvaluatingProcessor(base);
        Compilation compilation = javac().withProcessors(evaluatingProcessor).compile(DUMMY);
        checkState(compilation.status().equals(SUCCESS), compilation);
        evaluatingProcessor.throwIfStatementThrew();
      }
    };
  }

  /**
   * Returns the {@link Elements} instance associated with the current execution of the rule.
   *
   * @return the mElements
   * @throws IllegalStateException if this method is invoked outside the execution of the rule.
   */
  public Elements getElements() {
    checkState(mElements != null, "Not running within the rule");
    return mElements;
  }

  /**
   * Returns the {@link Types} instance associated with the current execution of the rule.
   *
   * @return the mTypes
   * @throws IllegalStateException if this method is invoked outside the execution of the rule.
   */
  public Types getTypes() {
    checkState(mElements != null, "Not running within the rule");
    return mTypes;
  }

  public ProcessingEnvironment getProcessingEnv() {
    checkState(mProcessingEnv != null, "Not running within the rule");
    return mProcessingEnv;
  }

  final class EvaluatingProcessor extends AbstractProcessor {

    final Statement base;

    @Nullable Throwable thrown;

    EvaluatingProcessor(Statement pBase) {
      this.base = pBase;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
      return ImmutableSet.of("*");
    }

    @Override
    public synchronized void init(ProcessingEnvironment pProcessingEnv) {
      super.init(pProcessingEnv);
      mProcessingEnv = pProcessingEnv;
      mElements = pProcessingEnv.getElementUtils();
      mTypes = pProcessingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      // just run the test on the last round after compilation is over
      if (roundEnv.processingOver()) {
        try {
          base.evaluate();
        }
        catch (Throwable e) {
          thrown = e;
        }
      }
      return false;
    }

    /**
     * Throws what the base {@link Statement} threw, if anything.
     */
    void throwIfStatementThrew() throws Throwable {
      if (thrown != null) {
        throw thrown;
      }
    }
  }
}
