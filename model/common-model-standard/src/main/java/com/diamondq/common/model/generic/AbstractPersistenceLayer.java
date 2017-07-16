package com.diamondq.common.model.generic;

import com.diamondq.common.model.generic.GenericQueryBuilder.GenericWhereInfo;
import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyPattern;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.Ref;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.diamondq.common.model.interfaces.WhereOperator;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractPersistenceLayer implements PersistenceLayer {

	protected final Scope				mScope;

	protected volatile Locale			mGlobalDefaultLocale	= Locale.US;

	protected final ThreadLocal<Locale>	mDefaultLocale			= ThreadLocal.withInitial(() -> mGlobalDefaultLocale);

	protected static final BitSet		sValidFileNamesBitSet;

	protected static final BitSet		sInvalidPrimayKeyBitSet;

	static {
		BitSet b = new BitSet();
		b.set('0', '9' + 1);
		b.set('a', 'z' + 1);
		b.set('A', 'Z' + 1);
		b.set('.');
		b.set('-');
		sValidFileNamesBitSet = b;

		b = new BitSet();
		b.set('$');
		b.set('_');
		b.set('/');
		sInvalidPrimayKeyBitSet = b;
	}

	public AbstractPersistenceLayer(Scope pScope) {
		super();
		mScope = pScope;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public StructureDefinition createNewStructureDefinition(Toolkit pToolkit, Scope pScope, String pName) {
		return new GenericStructureDefinition(mScope, pName, null, false, null, null, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewPropertyDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	public PropertyDefinition createNewPropertyDefinition(Toolkit pToolkit, Scope pScope, String pName,
		PropertyType pType) {
		return new GenericPropertyDefinition(mScope, pName, null, false, 0, pType, null, null, null, null, null, null,
			null, false, PropertyPattern.Normal, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public Structure createNewStructure(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition) {
		return new GenericStructure(mScope, pStructureDefinition, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewProperty(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition,
	 *      boolean, java.lang.Object)
	 */
	@Override
	public <@Nullable T> Property<T> createNewProperty(Toolkit pToolkit, Scope pScope,
		PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue) {
		switch (pPropertyDefinition.getPropertyPattern()) {
		case Normal:
			return new GenericProperty<T>(pPropertyDefinition, pIsValueSet, pValue);
		case StructureDefinitionName:
			@SuppressWarnings("unchecked")
			Property<T> result = (Property<T>) new GenericSDNameProperty(pPropertyDefinition);
			return result;
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewTranslatableString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public TranslatableString createNewTranslatableString(Toolkit pToolkit, Scope pScope, String pKey) {
		return new GenericTranslatableString(mScope, pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorGroupDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorGroupDefinition createNewEditorGroupDefinition(Toolkit pToolkit, Scope pScope) {
		return new GenericEditorGroupDefinition(null, 0, 1, 0, null, null, null, 0, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorPropertyDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public EditorPropertyDefinition createNewEditorPropertyDefinition(Toolkit pToolkit, Scope pScope) {
		return new GenericEditorPropertyDefinition(null, 0, 1, 0, null, null, null, null, null, null, false, null, null,
			null, null, null, null, null, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.StructureDefinitionRef)
	 */
	@Override
	public EditorStructureDefinition createNewEditorStructureDefinition(Toolkit pToolkit, Scope pScope, String pName,
		StructureDefinitionRef pRef) {
		return new GenericEditorStructureDefinition(pName, pRef, null, null);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public StructureRef createStructureRefFromSerialized(Toolkit pToolkit, Scope pScope, String pValue) {
		return new GenericStructureRef(mScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefFromParts(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
	 *      java.lang.String, com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
	 */
	@Override
	public StructureRef createStructureRefFromParts(Toolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
		@Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
		StringBuilder sb = new StringBuilder();
		if (pStructure != null) {
			if ((pDef == null) && (pPropName == null) && (pPrimaryKeys == null))
				sb.append(pStructure.getReference().getSerializedString());
			else if ((pDef != null) && (pPropName == null) && (pPrimaryKeys != null)) {
				sb.append(pStructure.getReference().getSerializedString());
				sb.append('/');
				sb.append("unknown");
				sb.append('/');
				sb.append(pDef.getName());
				sb.append('/');
				sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
			}
			else if ((pDef != null) && (pPropName != null) && (pPrimaryKeys != null)) {
				sb.append(pStructure.getReference().getSerializedString());
				sb.append('/');
				sb.append(pPropName);
				sb.append('/');
				sb.append(pDef.getName());
				sb.append('/');
				sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
			}
			else
				throw new IllegalArgumentException(
					"If Structure is provided, then a StructureDefinition cannot be provided.");
		}
		else {
			if ((pDef != null) && (pPropName == null) && (pPrimaryKeys != null)) {
				sb.append(pDef.getName());
				sb.append('/');
				sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
			}
			else
				throw new IllegalArgumentException(
					"If StructureDefinition is provided, then only the Primary Keys can be provided.");
		}
		String key = sb.toString();
		return new GenericStructureRef(mScope, key);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String)
	 */
	@Override
	public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Toolkit pGenericToolkit, Scope pScope,
		String pValue) {
		return new GenericPropertyRef<>(mScope, pValue);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createDynamicEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public EditorStructureDefinition createDynamicEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition) {
		return new DynamicEditorStructureDefinition(mScope, pStructureDefinition);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureDefinitionRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public StructureDefinitionRef createStructureDefinitionRef(Toolkit pToolkit, Scope pScope,
		StructureDefinition pResolvable) {
		return new GenericStructureDefinitionRef(mScope, pResolvable.getName());
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureDefinitionRefFromSerialized(com.diamondq.common.model.interfaces.Scope,
	 *      java.lang.String)
	 */
	@Override
	public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Scope pScope, String pSerialized) {
		return new GenericStructureDefinitionRef(mScope, pSerialized);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition,
	 *      com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	public PropertyDefinitionRef createPropertyDefinitionRef(Toolkit pToolkit, Scope pScope,
		PropertyDefinition pResolvable, StructureDefinition pContaining) {
		StringBuilder sb = new StringBuilder();
		sb.append(pContaining.getReference().getSerializedString());
		sb.append('#');
		sb.append(pResolvable.getName());
		return new GenericPropertyDefinitionRef(mScope, sb.toString());
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public StructureRef createStructureRef(Toolkit pToolkit, Scope pScope, Structure pResolvable) {
		return new GenericStructureRef(mScope, pToolkit.createStructureRefStr(pScope, pResolvable));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefStr(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public String createStructureRefStr(Toolkit pToolkit, Scope pScope, Structure pResolvable) {
		PropertyRef<?> parentRef = pResolvable.getContainerRef();
		StringBuilder sb = new StringBuilder();
		if (parentRef != null)
			sb.append(parentRef.getSerializedString()).append('/');
		sb.append(pResolvable.getDefinition().getName());
		sb.append('/');
		sb.append(pResolvable.getLocalName());
		return sb.toString();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyRef(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Property,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	public <@Nullable T> PropertyRef<T> createPropertyRef(Toolkit pToolkit, Scope pScope,
		@Nullable Property<T> pResolvable, Structure pContaining) {
		return new GenericPropertyRef<T>(mScope, pContaining.getReference(),
			(pResolvable == null ? null : pResolvable.getDefinition().getName()));
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#collapsePrimaryKeys(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.List)
	 */
	@Override
	public String collapsePrimaryKeys(Toolkit pToolkit, Scope pScope, List<@Nullable Object> pNames) {
		StringJoiner sj = new StringJoiner("$");
		for (Object o : pNames)
			if (o != null) {
				if (o instanceof String)
					sj.add(escapeValue((String) o, null, sInvalidPrimayKeyBitSet));
				else if (o instanceof Ref)
					sj.add(escapeValue(((Ref<?>) o).getSerializedString(), null, sInvalidPrimayKeyBitSet));
				else
					sj.add(escapeValue(o.toString(), null, sInvalidPrimayKeyBitSet));
			}
		return sj.toString();
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#setGlobalDefaultLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
	 */
	@Override
	public void setGlobalDefaultLocale(Toolkit pToolkit, @Nullable Scope pScope, Locale pLocale) {
		mGlobalDefaultLocale = pLocale;
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#setThreadLocale(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
	 */
	@Override
	public void setThreadLocale(Toolkit pToolkit, @Nullable Scope pScope, @Nullable Locale pLocale) {
		if (pLocale == null)
			mDefaultLocale.remove();
		else
			mDefaultLocale.set(pLocale);
	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
	 */
	@Override
	public @Nullable String lookupResourceString(Toolkit pToolkit, Scope pScope, @Nullable Locale pLocale,
		String pKey) {

		/* There are three main passes: provided locale, thread default locale, global default locale */

		/*
		 * For each pass/locale, the locale is tried in successively simplier version (ie. en_US_iowa, en_US, en). When
		 * there is no simplier, then it moves to the next pass
		 */

		for (int pass = 0; pass < 3; pass++) {
			Locale locale = null;
			if (pass == 0)
				locale = pLocale;
			else if (pass == 1)
				locale = mDefaultLocale.get();
			else if (pass == 2)
				locale = mGlobalDefaultLocale;
			if (locale == null)
				continue;

			/* Now, run through each simpler version */

			String language = locale.getLanguage();
			String country = locale.getCountry();
			String variant = locale.getVariant();

			for (int complexity = 2; complexity >= 0; complexity--) {
				Locale testLocale = null;
				if (complexity == 2)
					testLocale = Strings.isNullOrEmpty(variant) == false && Strings.isNullOrEmpty(country) == false
						? new Locale(language, country, variant) : null;
				else if (complexity == 1)
					testLocale = Strings.isNullOrEmpty(country) == false ? new Locale(language, country) : null;
				else if (complexity == 0)
					testLocale = new Locale(language);
				if (testLocale == null)
					continue;

				String result = internalLookupResourceString(pToolkit, pScope, testLocale, pKey);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	/**
	 * This is a highly inefficient implementation that simply scans through all structures until it finds one that
	 * matches. A better implementation would use indexes to do a more directed search.
	 * 
	 * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructuresByQuery(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition,
	 *      com.diamondq.common.model.interfaces.QueryBuilder, java.util.Map)
	 */
	@Override
	public List<Structure> lookupStructuresByQuery(Toolkit pToolkit, Scope pScope,
		StructureDefinition pStructureDefinition, QueryBuilder pBuilder, Map<String, Object> pParamValues) {
		Collection<Structure> allStructures =
			getAllStructuresByDefinition(pToolkit, pScope, pStructureDefinition.getReference());
		List<Structure> results = Lists.newArrayList();
		GenericQueryBuilder gqb = (GenericQueryBuilder) pBuilder;
		List<GenericWhereInfo> whereList = gqb.getWhereList();
		for (Structure test : allStructures) {

			boolean matches = true;
			for (GenericWhereInfo w : whereList) {
				Property<@Nullable Object> prop = test.lookupPropertyByName(w.key);
				if (prop == null)
					continue;
				Object testValue = prop.getValue(test);
				Object actValue;
				if (w.paramKey != null)
					actValue = pParamValues.get(w.paramKey);
				else
					actValue = w.constant;

				/* Now do the comparsion based on the operator */

				switch (w.operator) {
				case eq: {
					matches = Objects.equals(testValue, actValue) == true;
					break;
				}
				case ne: {
					matches = Objects.equals(testValue, actValue) == false;
					break;
				}
				case gt:
				case gte:
				case lt:
				case lte: {
					if ((testValue instanceof Number) && (actValue instanceof Number)) {
						BigDecimal testDec;
						BigDecimal actDec;
						if (testValue instanceof BigInteger)
							testDec = new BigDecimal((BigInteger) testValue);
						else if (testValue instanceof Byte)
							testDec = new BigDecimal((Byte) testValue);
						else if (testValue instanceof Double)
							testDec = new BigDecimal((Double) testValue);
						else if (testValue instanceof Float)
							testDec = new BigDecimal((Float) testValue);
						else if (testValue instanceof Integer)
							testDec = new BigDecimal((Float) testValue);
						else if (testValue instanceof Long)
							testDec = new BigDecimal((Long) testValue);
						else if (testValue instanceof Short)
							testDec = new BigDecimal((Short) testValue);
						else
							throw new UnsupportedOperationException();
						if (actValue instanceof BigInteger)
							actDec = new BigDecimal((BigInteger) actValue);
						else if (actValue instanceof Byte)
							actDec = new BigDecimal((Byte) actValue);
						else if (actValue instanceof Double)
							actDec = new BigDecimal((Double) actValue);
						else if (actValue instanceof Float)
							actDec = new BigDecimal((Float) actValue);
						else if (actValue instanceof Integer)
							actDec = new BigDecimal((Float) actValue);
						else if (actValue instanceof Long)
							actDec = new BigDecimal((Long) actValue);
						else if (actValue instanceof Short)
							actDec = new BigDecimal((Short) actValue);
						else
							throw new UnsupportedOperationException();
						int compareResult = testDec.compareTo(actDec);
						if (w.operator == WhereOperator.gt)
							matches = compareResult > 0;
						else if (w.operator == WhereOperator.gte)
							matches = compareResult >= 0;
						else if (w.operator == WhereOperator.lt)
							matches = compareResult < 0;
						else if (w.operator == WhereOperator.lte)
							matches = compareResult <= 0;
						else
							throw new UnsupportedOperationException();
					}
					else
						matches = false;
				}
				}

				if (matches == false)
					break;
			}
			if (matches == false)
				continue;

			/* Everything matched, so add it */

			results.add(test);
		}
		return ImmutableList.copyOf(results);

	}

	/**
	 * @see com.diamondq.common.model.generic.PersistenceLayer#createNewQueryBuilder(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope)
	 */
	@Override
	public QueryBuilder createNewQueryBuilder(Toolkit pToolkit, Scope pScope) {
		return new GenericQueryBuilder(null);
	}

	/**
	 * This internal method is used to look up a string directly against a locale (don't attempt fallbacks)
	 * 
	 * @param pToolkit the 'primary' toolkit
	 * @param pScope the 'primary' scope
	 * @param pLocale the locale
	 * @param pKey the key
	 * @return the result or null if there is no match
	 */
	protected abstract @Nullable String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
		String pKey);

	/**
	 * Converts a given string into characters that are valid. To guarantee uniqueness, it will escape unsupported
	 * characters
	 * 
	 * @param pValue
	 * @param pValid
	 * @param pInvalid
	 * @return the munged name
	 */
	protected String escapeValue(String pValue, @Nullable BitSet pValid, @Nullable BitSet pInvalid) {
		StringBuilder buffer = new StringBuilder();
		char[] chars = pValue.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char b = chars[i];
			if (((pInvalid == null) || (pInvalid.get(b) == false)) && ((pValid == null) || (pValid.get(b) == true)))
				// if (((b >= 'A') && (b <= 'Z')) || ((b >= 'a') && (b <= 'z')) || ((b >= '0') && (b <= '9')))
				buffer.append(chars[i]);
			else {
				buffer.append('_');
				buffer.append(Integer.toHexString(b));
				buffer.append('_');
			}
		}
		return buffer.toString();
	}

	protected String unescapeValue(String pMunged) {
		StringBuilder sb = new StringBuilder();
		StringBuilder escaped = new StringBuilder();
		char[] chars = pMunged.toCharArray();
		boolean inEscape = false;
		for (int i = 0; i < chars.length; i++) {
			char b = chars[i];
			if (inEscape == true) {
				if (b == '_') {
					int code = Integer.parseUnsignedInt(escaped.toString(), 16);
					char newChar = (char) code;
					sb.append(newChar);
					inEscape = false;
				}
				else
					escaped.append(b);
			}
			else {
				if (b == '_') {
					escaped.setLength(0);
					inEscape = true;
				}
				else
					sb.append(b);
			}
		}
		return sb.toString();
	}
}
