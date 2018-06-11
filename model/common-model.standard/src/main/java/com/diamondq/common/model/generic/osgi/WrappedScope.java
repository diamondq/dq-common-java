package com.diamondq.common.model.generic.osgi;

import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.generic.PersistenceLayer;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.persistence.CombinedPersistenceLayer;
import com.diamondq.common.model.persistence.NewMemoryPersistenceLayer;
import com.diamondq.common.utils.misc.builders.IBuilder;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappedScope implements Scope {
	private static final Logger														sLogger			=
		LoggerFactory.getLogger(WrappedScope.class);

	protected Scope																	mScope;

	protected Toolkit																mToolkit;

	protected @Nullable String														mName;

	protected final ConcurrentMap<PersistenceLayer, Map<String, Object>>			mLayers			=
		new ConcurrentHashMap<>();

	protected final ConcurrentMap<IBuilder<PersistenceLayer>, Map<String, Object>>	mBuilderLayers	=
		new ConcurrentHashMap<>();

	protected @Nullable Filter[]													mFilters;

	protected volatile boolean														mInitialized	= false;

	private static final @NonNull String[]											sFILTER_KEYS	=
		new @NonNull String[] {".structure_filter", ".structure_definition_filter",
				".editor_structure_definition_filter", ".resource_filter"};

	@SuppressWarnings("null")
	public WrappedScope() {
		mScope = null;
		mFilters = new Filter[sFILTER_KEYS.length];
	}

	public Scope getScope() {
		return mScope;
	}

	public void setToolkit(Toolkit pToolkit) {
		mToolkit = pToolkit;
	}

	public void addPersistenceLayer(PersistenceLayer pLayer, Map<String, Object> pProps) {
		sLogger.trace("addPersistenceLayer({}, {}) from {}", pLayer, pProps, this);
		ImmutableMap<String, Object> props = ImmutableMap.copyOf(pProps);
		mLayers.put(pLayer, props);
		processLayers();
	}

	public void removePersistenceLayer(PersistenceLayer pLayer) {
		sLogger.trace("removePersistenceLayer({}) from {}", pLayer, this);
		mLayers.remove(pLayer);
		processLayers();
	}

	public void addBuilder(IBuilder<PersistenceLayer> pLayer, Map<String, Object> pProps) {
		sLogger.trace("addBuilder({}, {}) from {}", pLayer, pProps, this);
		ImmutableMap<String, Object> props = ImmutableMap.copyOf(pProps);
		mBuilderLayers.put(pLayer, props);
		processLayers();
	}

	public void removeBuilder(IBuilder<PersistenceLayer> pLayer) {
		sLogger.trace("removeBuilder({}) from {}", pLayer, this);
		mBuilderLayers.remove(pLayer);
		processLayers();
	}

	public void onActivate(ComponentContext pContext, Map<String, Object> pProps) {
		sLogger.trace("onActivate({}, {}) from {}", pContext, pProps, this);
		mName = PropertiesParsing.getNonNullString(pProps, "name", "default");
		mScope = mToolkit.getOrCreateScope(mName);

		/* Now see if any the layers match the criteria */

		for (int i = 0; i < sFILTER_KEYS.length; i++) {
			String filter = PropertiesParsing.getNullableString(pProps, sFILTER_KEYS[i]);
			if (filter != null) {
				try {
					mFilters[i] = pContext.getBundleContext().createFilter(filter);
				}
				catch (InvalidSyntaxException ex) {
					throw new RuntimeException(ex);
				}
			}
			else
				mFilters[i] = null;
		}

		mInitialized = true;
		processLayers();
	}

	/**
	 * Adds the PersistenceLayer if the filter matches the properties
	 * 
	 * @param pLayer
	 * @param pProps
	 */
	private void processLayers() {
		sLogger.trace("processLayers()");

		if (mInitialized == false)
			return;

		@SuppressWarnings("unchecked")
		List<Pair<Integer, PersistenceLayer>>[] layers = new List[sFILTER_KEYS.length];
		for (int o = 0; o < sFILTER_KEYS.length; o++)
			layers[o] = Lists.newArrayList();

		for (Map.Entry<PersistenceLayer, Map<String, Object>> pair : mLayers.entrySet()) {
			for (int i = 0; i < sFILTER_KEYS.length; i++) {
				Filter filter = mFilters[i];
				if (filter == null)
					continue;

				Map<String, Object> props = pair.getValue();
				if (filter.matches(props) == true) {

					Object rankingObj = props.get(Constants.SERVICE_RANKING);
					int ranking;
					if (rankingObj == null)
						ranking = Integer.MAX_VALUE;
					else if (rankingObj instanceof Integer)
						ranking = ((Integer) rankingObj);
					else
						ranking = Integer.parseInt(rankingObj.toString());

					layers[i].add(Pair.with(ranking, pair.getKey()));
				}
			}
		}

		for (Map.Entry<IBuilder<PersistenceLayer>, Map<String, Object>> pair : mBuilderLayers.entrySet()) {
			for (int i = 0; i < sFILTER_KEYS.length; i++) {
				Filter filter = mFilters[i];
				if (filter == null)
					continue;

				Map<String, Object> props = pair.getValue();
				if (filter.matches(props) == true) {

					Object rankingObj = props.get(Constants.SERVICE_RANKING);
					int ranking;
					if (rankingObj == null)
						ranking = Integer.MAX_VALUE;
					else if (rankingObj instanceof Integer)
						ranking = ((Integer) rankingObj);
					else
						ranking = Integer.parseInt(rankingObj.toString());

					layers[i].add(Pair.with(ranking, pair.getKey().build()));
				}
			}
		}

		/* Add default values */

		for (int i = 0; i < sFILTER_KEYS.length; i++) {
			if (layers[i].isEmpty() == true)
				layers[i].add(Pair.with(Integer.MAX_VALUE, new NewMemoryPersistenceLayer()));
		}

		/* Now sort the lists */

		@SuppressWarnings("unchecked")
		ImmutableList.Builder<PersistenceLayer>[] sortedLayers = new ImmutableList.Builder[sFILTER_KEYS.length];
		for (int i = 0; i < sFILTER_KEYS.length; i++) {
			final int o = i;
			Collections.sort(layers[o], (a, b) -> {
				return a.getValue0() - b.getValue0();
			});
			sortedLayers[o] = ImmutableList.builder();
			layers[o].forEach((p) -> {
				sortedLayers[o].add(p.getValue1());
			});
		}

		CombinedPersistenceLayer combinedPersistenceLayer = new CombinedPersistenceLayer(sortedLayers[0].build(),
			sortedLayers[1].build(), sortedLayers[2].build(), sortedLayers[3].build());
		if (mToolkit instanceof GenericToolkit)
			((GenericToolkit) mToolkit).setPersistenceLayer(mScope, combinedPersistenceLayer);
		else if (mToolkit instanceof WrappedToolkit)
			((WrappedToolkit) mToolkit).setPersistenceLayer(mScope, combinedPersistenceLayer);
		else
			return;

	}

	public void onDeactivate() {
		String name = mName;
		if (name != null) {
			mName = null;
			mToolkit.removeScope(name);
		}
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Scope#getName()
	 */
	@Override
	public String getName() {
		return mScope.getName();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.Scope#getToolkit()
	 */
	@Override
	public Toolkit getToolkit() {
		return mScope.getToolkit();
	}
}
