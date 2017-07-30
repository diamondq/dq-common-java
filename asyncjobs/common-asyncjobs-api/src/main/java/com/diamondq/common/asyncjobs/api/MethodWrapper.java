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

import org.checkerframework.checker.nullness.qual.Nullable;

public class MethodWrapper {

	private @Nullable Consumer1<?>								mConsumer1;

	private @Nullable Consumer2<?, ?>							mConsumer2;

	private @Nullable Consumer3<?, ?, ?>						mConsumer3;

	private @Nullable Consumer4<?, ?, ?, ?>						mConsumer4;

	private @Nullable Consumer5<?, ?, ?, ?, ?>					mConsumer5;

	private @Nullable Consumer6<?, ?, ?, ?, ?, ?>				mConsumer6;

	private @Nullable Consumer7<?, ?, ?, ?, ?, ?, ?>			mConsumer7;

	private @Nullable Consumer8<?, ?, ?, ?, ?, ?, ?, ?>			mConsumer8;

	private @Nullable Consumer9<?, ?, ?, ?, ?, ?, ?, ?, ?>		mConsumer9;

	private @Nullable Function0<?>								mFunction0;

	private @Nullable Function1<?, ?>							mFunction1;

	private @Nullable Function2<?, ?, ?>						mFunction2;

	private @Nullable Function3<?, ?, ?, ?>						mFunction3;

	private @Nullable Function4<?, ?, ?, ?, ?>					mFunction4;

	private @Nullable Function5<?, ?, ?, ?, ?, ?>				mFunction5;

	private @Nullable Function6<?, ?, ?, ?, ?, ?, ?>			mFunction6;

	private @Nullable Function7<?, ?, ?, ?, ?, ?, ?, ?>			mFunction7;

	private @Nullable Function8<?, ?, ?, ?, ?, ?, ?, ?, ?>		mFunction8;

	private @Nullable Function9<?, ?, ?, ?, ?, ?, ?, ?, ?, ?>	mFunction9;

	public <T> MethodWrapper(Consumer1<T> pConsumer) {
		mConsumer1 = pConsumer;
	}

	public <T1, T2> MethodWrapper(Consumer2<T1, T2> pConsumer) {
		mConsumer2 = pConsumer;
	}

	public <T1, T2, T3> MethodWrapper(Consumer3<T1, T2, T3> pConsumer) {
		mConsumer3 = pConsumer;
	}

	public <T1, T2, T3, T4> MethodWrapper(Consumer4<T1, T2, T3, T4> pConsumer) {
		mConsumer4 = pConsumer;
	}

	public <T1, T2, T3, T4, T5> MethodWrapper(Consumer5<T1, T2, T3, T4, T5> pConsumer) {
		mConsumer5 = pConsumer;
	}

	public <T1, T2, T3, T4, T5, T6> MethodWrapper(Consumer6<T1, T2, T3, T4, T5, T6> pConsumer) {
		mConsumer6 = pConsumer;
	}

	public <T1, T2, T3, T4, T5, T6, T7> MethodWrapper(Consumer7<T1, T2, T3, T4, T5, T6, T7> pConsumer) {
		mConsumer7 = pConsumer;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8> MethodWrapper(Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> pConsumer) {
		mConsumer8 = pConsumer;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> MethodWrapper(Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> pConsumer) {
		mConsumer9 = pConsumer;
	}

	public <R> MethodWrapper(Function0<R> pFunction) {
		mFunction0 = pFunction;
	}

	public <T, R> MethodWrapper(Function1<T, R> pFunction) {
		mFunction1 = pFunction;
	}

	public <T1, T2, R> MethodWrapper(Function2<T1, T2, R> pFunction) {
		mFunction2 = pFunction;
	}

	public <T1, T2, T3, R> MethodWrapper(Function3<T1, T2, T3, R> pFunction) {
		mFunction3 = pFunction;
	}

	public <T1, T2, T3, T4, R> MethodWrapper(Function4<T1, T2, T3, T4, R> pFunction) {
		mFunction4 = pFunction;
	}

	public <T1, T2, T3, T4, T5, R> MethodWrapper(Function5<T1, T2, T3, T4, T5, R> pFunction) {
		mFunction5 = pFunction;
	}

	public <T1, T2, T3, T4, T5, T6, R> MethodWrapper(Function6<T1, T2, T3, T4, T5, T6, R> pFunction) {
		mFunction6 = pFunction;
	}

	public <T1, T2, T3, T4, T5, T6, T7, R> MethodWrapper(Function7<T1, T2, T3, T4, T5, T6, T7, R> pFunction) {
		mFunction7 = pFunction;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, R> MethodWrapper(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> pFunction) {
		mFunction8 = pFunction;
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> MethodWrapper(
		Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> pFunction) {
		mFunction9 = pFunction;
	}

}
