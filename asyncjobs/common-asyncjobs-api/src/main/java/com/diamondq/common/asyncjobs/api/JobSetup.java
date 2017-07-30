package com.diamondq.common.asyncjobs.api;

import com.diamondq.common.asyncjobs.api.methods.Consumer1;
import com.diamondq.common.asyncjobs.api.methods.Consumer2;
import com.diamondq.common.asyncjobs.api.methods.Consumer3;
import com.diamondq.common.asyncjobs.api.methods.Consumer4;
import com.diamondq.common.asyncjobs.api.methods.Consumer5;
import com.diamondq.common.asyncjobs.api.methods.Consumer6;
import com.diamondq.common.asyncjobs.api.methods.Consumer7;
import com.diamondq.common.asyncjobs.api.methods.Consumer8;
import com.diamondq.common.asyncjobs.api.methods.Consumer9;
import com.diamondq.common.asyncjobs.api.methods.Function0;
import com.diamondq.common.asyncjobs.api.methods.Function1;
import com.diamondq.common.asyncjobs.api.methods.Function2;
import com.diamondq.common.asyncjobs.api.methods.Function3;
import com.diamondq.common.asyncjobs.api.methods.Function4;
import com.diamondq.common.asyncjobs.api.methods.Function5;
import com.diamondq.common.asyncjobs.api.methods.Function6;
import com.diamondq.common.asyncjobs.api.methods.Function7;
import com.diamondq.common.asyncjobs.api.methods.Function8;
import com.diamondq.common.asyncjobs.api.methods.Function9;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This is a builder used to define and set up a job
 */
public class JobSetup {

	private @Nullable MethodWrapper	mMethod;

	private Set<ParamSetup<?>>		mParams;

	private Set<ResultSetup<?>>		mResults;

	private Set<PrepResultSetup<?>>	mPrepResults;

	private Set<TriggerSetup<?>>	mTriggers;

	private JobSetup() {
		mParams = new HashSet<>();
		mResults = new HashSet<>();
		mPrepResults = new HashSet<>();
		mTriggers = new HashSet<>();
	}

	/**
	 * Create a new JobSetup
	 * 
	 * @return the setup
	 */
	public static JobSetup builder() {
		return new JobSetup();
	}

	/* ********************************************************************** */
	/* METHODS */
	/* ********************************************************************** */

	public <T> JobSetup method(Consumer1<T> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2> JobSetup method(Consumer2<T1, T2> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3> JobSetup method(Consumer3<T1, T2, T3> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4> JobSetup method(Consumer4<T1, T2, T3, T4> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5> JobSetup method(Consumer5<T1, T2, T3, T4, T5> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6> JobSetup method(Consumer6<T1, T2, T3, T4, T5, T6> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7> JobSetup method(Consumer7<T1, T2, T3, T4, T5, T6, T7> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8> JobSetup method(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> JobSetup method(
		Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <R> JobSetup method(Function0<R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T, R> JobSetup method(Function1<T, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, R> JobSetup method(Function2<T1, T2, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, R> JobSetup method(Function3<T1, T2, T3, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, R> JobSetup method(Function4<T1, T2, T3, T4, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, R> JobSetup method(Function5<T1, T2, T3, T4, T5, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, R> JobSetup method(Function6<T1, T2, T3, T4, T5, T6, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7, R> JobSetup method(Function7<T1, T2, T3, T4, T5, T6, T7, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, R> JobSetup method(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> JobSetup method(
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pConsumer) {
		mMethod = new MethodWrapper(pConsumer);
		return this;
	}

	/* Params */

	/**
	 * Define a new parameter for this job. The parameter is defined by the class. NOTE: If there are multiple
	 * parameters that have the same type, you must use the ... When complete defining the param, use the
	 * ParamSetup.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the param setup builder
	 */
	public <PT> ParamSetup<PT> param(Class<PT> pClass) {
		return ParamSetup.builder(this, pClass);
	}

	<PT> void addParam(ParamSetup<PT> pParamSetup) {
		mParams.add(pParamSetup);
	}

	/* Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultSetup.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	public <RT> ResultSetup<RT> result(Class<RT> pClass) {
		return ResultSetup.builder(this, pClass);
	}

	<RT> void addResult(ResultSetup<RT> pResultSetup) {
		mResults.add(pResultSetup);
	}

	/* Prep Results */

	/**
	 * Define a result this job. The result is defined by the class. NOTE: If there are multiple results that have the
	 * same type, you must use the ... When complete defining the result, use the ResultSetup.build() method to return
	 * back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the result setup builder
	 */
	public <RT> PrepResultSetup<RT> prepResult(Class<RT> pClass) {
		return PrepResultSetup.builder(this, pClass);
	}

	<RT> void addPrepResult(PrepResultSetup<RT> pPrepResultSetup) {
		mPrepResults.add(pPrepResultSetup);
	}

	/* Triggers */

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerSetup.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	public <TT> TriggerSetup<TT> trigger(Class<TT> pClass) {
		return TriggerSetup.builder(this, false, pClass);
	}

	/**
	 * Define a trigger for this job. The trigger is defined by the class. When complete defining the result, use the
	 * TriggerSetup.build() method to return back to this Job builder.
	 * 
	 * @param pClass the class
	 * @return the trigger setup builder
	 */
	public <TT> TriggerSetup<TT> triggerCollection(Class<TT> pClass) {
		return TriggerSetup.builder(this, true, pClass);
	}

	<TT> void addTrigger(TriggerSetup<TT> pTriggerSetup) {
		mTriggers.add(pTriggerSetup);
	}

	/* Guards */

	public JobSetup guard(Function1<?, Boolean> pGuardFunction) {
		return this;
	}

	public JobSetup build() {
		return this;
	}
}
