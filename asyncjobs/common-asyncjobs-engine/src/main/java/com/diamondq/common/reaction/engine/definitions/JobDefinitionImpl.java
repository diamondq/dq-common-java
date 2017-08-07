package com.diamondq.common.reaction.engine.definitions;

import com.diamondq.common.reaction.api.JobDefinition;
import com.diamondq.common.reaction.api.impl.JobBuilderImpl;
import com.diamondq.common.reaction.api.impl.MethodWrapper;
import com.diamondq.common.reaction.api.impl.ParamBuilderImpl;
import com.diamondq.common.reaction.api.impl.PrepResultBuilderImpl;
import com.diamondq.common.reaction.api.impl.ResultBuilderImpl;
import com.diamondq.common.reaction.api.impl.TriggerBuilderImpl;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class JobDefinitionImpl implements JobDefinition {

	public final MethodWrapper					method;

	public final List<ParamDefinition<?>>		params;

	public final Set<ResultDefinition<?>>		results;

	public final Set<PrepResultDefinition<?>>	prepResults;

	public final Set<TriggerDefinition<?>>		triggers;

	public JobDefinitionImpl(JobBuilderImpl pBuilder) {

		/* method */

		MethodWrapper possibleMethod = pBuilder.getMethod();
		if (possibleMethod == null)
			throw new IllegalArgumentException("The mandatory method is not defined");
		method = possibleMethod;

		/* params */

		ImmutableList.Builder<ParamDefinition<?>> paramListBuilder = ImmutableList.builder();
		for (ParamBuilderImpl<?> paramBuilder : pBuilder.getParams())
			paramListBuilder.add(new ParamDefinition<>(paramBuilder));
		params = paramListBuilder.build();

		/* results */

		ImmutableSet.Builder<ResultDefinition<?>> resultSetBuilder = ImmutableSet.builder();
		for (ResultBuilderImpl<?> resultBuilder : pBuilder.getResults())
			resultSetBuilder.add(new ResultDefinition<>(resultBuilder));
		results = resultSetBuilder.build();

		/* prepResults */

		ImmutableSet.Builder<PrepResultDefinition<?>> prepResultSetBuilder = ImmutableSet.builder();
		for (PrepResultBuilderImpl<?> prepResultBuilder : pBuilder.getPrepResults())
			prepResultSetBuilder.add(new PrepResultDefinition<>(prepResultBuilder));
		prepResults = prepResultSetBuilder.build();

		/* triggers */

		ImmutableSet.Builder<TriggerDefinition<?>> triggerSetBuilder = ImmutableSet.builder();
		for (TriggerBuilderImpl<?> triggerBuilder : pBuilder.getTriggers())
			triggerSetBuilder.add(new TriggerDefinition<>(triggerBuilder));
		triggers = triggerSetBuilder.build();

	}

}
