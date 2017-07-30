package com.diamondq.common.asyncjobs.api;

import org.checkerframework.checker.nullness.qual.Nullable;

public class TriggerSetup<TT> extends CommonSetup<TT, TriggerSetup<TT>> {

	private @Nullable Action	mAction;

	private boolean				mIsCollection;

	private TriggerSetup(JobSetup pJobSetup, boolean pIsCollection, Class<TT> pClass) {
		super(pJobSetup, pClass);
		mIsCollection = pIsCollection;
	}

	public static <NPT> TriggerSetup<NPT> builder(JobSetup pJobSetup, boolean pIsCollection, Class<NPT> pClass) {
		return new TriggerSetup<NPT>(pJobSetup, pIsCollection, pClass);
	}

	public TriggerSetup<TT> action(Action pAction) {
		mAction = pAction;
		return this;
	}

	/**
	 * Finish this trigger and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobSetup build() {
		mJobSetup.addTrigger(this);
		return mJobSetup;
	}

}
