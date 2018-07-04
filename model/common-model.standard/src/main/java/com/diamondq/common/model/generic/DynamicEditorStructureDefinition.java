package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.EditorComponentDefinition;
import com.diamondq.common.model.interfaces.EditorDisplayType;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.TreeMap;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DynamicEditorStructureDefinition implements EditorStructureDefinition {

	private final Scope												mScope;

	private final StructureDefinition								mStructureDef;

	private ImmutableMultimap<String, String>						mKeywords;

	private ImmutableList<? extends EditorComponentDefinition<?>>	mComponents;

	public DynamicEditorStructureDefinition(Scope pScope, StructureDefinition pStructureDefinition) {
		mScope = pScope;
		mStructureDef = pStructureDefinition;
		mKeywords = ImmutableMultimap.<String, String> builder().build();
		ImmutableList.Builder<EditorComponentDefinition<?>> builder = ImmutableList.builder();

		/* Now, traverse through each property within the structure definition, and define a component */

		TreeMap<String, PropertyDefinition> primaryPDs = new TreeMap<>();
		TreeMap<String, PropertyDefinition> otherPDs = new TreeMap<>();
		for (PropertyDefinition pd : mStructureDef.getAllProperties().values()) {

			String displayName = null;
			TranslatableString label = pd.getLabel();
			if (label != null)
				displayName = label.resolve(null);
			if (displayName == null)
				displayName = pd.getName();
			if (pd.getKeywords().containsEntry(CommonKeywordKeys.EDITOR_VISIBLE, CommonKeywordValues.FALSE) == true)
				continue;
			if (pd.isPrimaryKey() == true)
				primaryPDs.put(displayName, pd);
			else
				otherPDs.put(displayName, pd);

		}

		/* Now add the primary keys */

		int order = 0;
		for (PropertyDefinition pd : primaryPDs.values()) {
			EditorPropertyDefinition epd = buildEditorPropertyDescription(mScope, pd, order++);
			if (epd != null)
				builder.add(epd);
		}

		/* Then add all the other fields */

		for (PropertyDefinition pd : otherPDs.values()) {
			EditorPropertyDefinition epd = buildEditorPropertyDescription(mScope, pd, order++);
			if (epd != null)
				builder.add(epd);
		}

		mComponents = builder.build();
	}

	private static @Nullable EditorPropertyDefinition buildEditorPropertyDescription(Scope pScope,
		PropertyDefinition pPD, int pOrder) {

		switch (pPD.getType()) {
		case Binary: {
			return null;
		}
		case Boolean: {
			return null;
		}
		case Decimal: {
			return null;
		}
		case EmbeddedStructureList: {
			return null;
		}
		case Image: {
			return null;
		}
		case Integer: {
			return null;
		}
		case Long: {
			return null;
		}
		case String: {
			EditorPropertyDefinition epd = pScope.getToolkit().createNewEditorPropertyDefinition(pScope);
			epd = epd.setLabel(pPD.getLabel()).setName(pPD.getName());
			epd = epd.setColumn(0).setDisplayType(EditorDisplayType.SingleLineTextField);
			epd = epd.setOrder(pOrder);
			if (pPD.isPrimaryKey() == true)
				epd = epd.setMandatory(true);
			return epd;
		}
		case StructureRef: {
			return null;
		}
		case StructureRefList: {
			if (pPD.getKeywords().containsEntry("ATStructureChooser", "child") == true)
				return null;
			EditorPropertyDefinition epd = pScope.getToolkit().createNewEditorPropertyDefinition(pScope);
			epd = epd.setLabel(pPD.getLabel()).setName(pPD.getName());
			epd = epd.setColumn(0).setDisplayType(EditorDisplayType.Picker);
			epd = epd.setOrder(pOrder);
			return epd;
		}
		default:
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getName()
	 */
	@Override
	public String getName() {
		return "dynamic-" + mStructureDef.getName();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#getStructureDefinitionRef()
	 */
	@Override
	public StructureDefinitionRef getStructureDefinitionRef() {
		return mStructureDef.getReference();
	}

	@Override
	public List<? extends EditorComponentDefinition<?>> getComponents() {
		return mComponents;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#addComponent(com.diamondq.common.model.interfaces.EditorComponentDefinition)
	 */
	@Override
	public <T extends EditorComponentDefinition<T>> EditorStructureDefinition addComponent(T pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#removeComponent(com.diamondq.common.model.interfaces.EditorComponentDefinition)
	 */
	@Override
	public <T extends EditorComponentDefinition<T>> EditorStructureDefinition removeComponent(T pValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Multimap<String, String> getKeywords() {
		return mKeywords;
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#addKeyword(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public EditorStructureDefinition addKeyword(String pKey, String pValue) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.interfaces.EditorStructureDefinition#removeKeyword(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public EditorStructureDefinition removeKeyword(String pKey, String pValue) {
		throw new UnsupportedOperationException();
	}

}
