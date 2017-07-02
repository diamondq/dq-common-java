package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.collect.ImmutableList.Builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Properties;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A Persistence Layer that stores the information in Properties files.
 */
public class PropertiesFilePersistenceLayer extends AbstractDocumentPersistenceLayer<Properties> {

	private final File	mStructureBaseDir;

	@SuppressWarnings("unused")
	private final File	mStructureDefBaseDir;

	@SuppressWarnings("unused")
	private final File	mEditorStructureDefBaseDir;

	/**
	 * Default constructor
	 * 
	 * @param pScope the scope
	 * @param pStructureBaseDir the directory for structures
	 * @param pStructureDefBaseDir the directory for structure definitions
	 * @param pEditorStructureDefBaseDir the directory for editor structure definitions
	 */
	public PropertiesFilePersistenceLayer(Scope pScope, File pStructureBaseDir, File pStructureDefBaseDir,
		File pEditorStructureDefBaseDir) {
		super(pScope, pStructureBaseDir != null, true, pStructureDefBaseDir != null, true,
			pEditorStructureDefBaseDir != null, true, false, true);
		mStructureBaseDir = pStructureBaseDir;
		mStructureDefBaseDir = pStructureDefBaseDir;
		mEditorStructureDefBaseDir = pEditorStructureDefBaseDir;
	}

	protected File getStructureFile(String pKey, boolean pCreateIfMissing) {
		String[] parts = pKey.split("/");
		parts[parts.length - 1] = parts[parts.length - 1] + ".properties";
		File structureFile = mStructureBaseDir;
		for (String p : parts)
			structureFile = new File(structureFile, escapeValue(p, sValidFileNamesBitSet, null));
		if (structureFile.exists() == false) {
			if (pCreateIfMissing == false)
				return null;
			if (structureFile.getParentFile().exists() == false)
				structureFile.getParentFile().mkdirs();
		}
		return structureFile;
	}

	protected File getStructureDir(String pKey, boolean pCreateIfMissing) {
		String[] parts = (pKey == null ? new String[0] : pKey.split("/"));
		File structureFile = mStructureBaseDir;
		for (String p : parts)
			structureFile = new File(structureFile, escapeValue(p, sValidFileNamesBitSet, null));
		if (structureFile.exists() == false)
			if (pCreateIfMissing == false)
				return null;
		structureFile.mkdirs();
		return structureFile;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected Properties loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		boolean pCreateIfMissing) {
		File structureFile = getStructureFile(pKey, pCreateIfMissing);
		if (structureFile == null)
			return null;

		Properties p = new Properties();
		if (structureFile.exists() == true) {
			try {
				try (FileInputStream fis = new FileInputStream(structureFile)) {
					p.load(fis);
				}
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		return p;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#getStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	protected <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig, boolean pIsMeta,
		String pKey, PropertyType pType) {
		String value = pConfig.getProperty(pKey);
		switch (pType) {
		case String: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case Boolean: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Boolean.FALSE : (Boolean) Boolean.parseBoolean(value));
			return result;
		}
		case Integer: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Integer.valueOf(0) : (Integer) Integer.parseInt(value));
			return result;
		}
		case Decimal: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? new BigDecimal(0.0) : new BigDecimal(value));
			return result;
		}
		case PropertyRef: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case StructureRef: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case StructureRefList: {
			String[] strings = (value == null ? "" : value).split(",");
			for (int i = 0; i < strings.length; i++)
				strings[i] = unescape(strings[i]);
			@SuppressWarnings("unchecked")
			R result = (R) strings;
			return result;
		}
		case Binary: {
			throw new UnsupportedOperationException();
		}
		case EmbeddedStructureList: {
			throw new UnsupportedOperationException();
		}
		case Image: {
			throw new UnsupportedOperationException();
		}
		case Timestamp: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Long.valueOf(0) : (Long) Long.parseLong(value));
			return result;
		}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#isStructureConfigChanged(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object)
	 */
	@Override
	protected boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, Properties pConfig) {
		return false;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#removeStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	protected boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig,
		boolean pIsMeta, String pKey, PropertyType pType) {
		return pConfig.remove(pKey) != null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#hasStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String)
	 */
	@Override
	protected boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig, boolean pIsMeta,
		String pKey) {
		return pConfig.containsKey(pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#persistContainerProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
	 *      com.diamondq.common.model.interfaces.Property)
	 */
	@Override
	protected boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure, Property<?> pProp) {
		return false;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#setStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType, java.lang.Object)
	 */
	@Override
	protected < R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig,
		boolean pIsMeta, String pKey, PropertyType pType, R pValue) {
		switch (pType) {
		case String: {
			pConfig.setProperty(pKey, (String) pValue);
			break;
		}
		case Boolean: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		case Integer: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		case Decimal: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		case PropertyRef: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		case StructureRef: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		case StructureRefList: {
			String[] strings = (String[]) pValue;
			String[] escaped = new String[strings.length];
			for (int i = 0; i < strings.length; i++)
				escaped[i] = escape(strings[i]);
			String escapedStr = String.join(",", escaped);
			pConfig.setProperty(pKey, escapedStr);
			break;
		}
		case Binary: {
			throw new UnsupportedOperationException();
		}
		case EmbeddedStructureList: {
			throw new UnsupportedOperationException();
		}
		case Image: {
			throw new UnsupportedOperationException();
		}
		case Timestamp: {
			pConfig.setProperty(pKey, pValue.toString());
			break;
		}
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	protected void saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Properties pConfig) {
		File structureFile = getStructureFile(pKey, true);

		try {
			try (FileOutputStream fos = new FileOutputStream(structureFile)) {
				pConfig.store(fos, "");
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private String unescape(String pValue) {
		try {
			return URLDecoder.decode(pValue, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	private String escape(String pValue) {
		try {
			return URLEncoder.encode(pValue, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pKey, Structure pStructure) {
		File structureFile = getStructureFile(pKey, false);
		if (structureFile == null)
			return;
		structureFile.delete();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalPopulateChildStructureList(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition, com.google.common.collect.ImmutableList.Builder)
	 */
	@Override
	protected void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope, Properties pConfig,
		StructureDefinition pStructureDefinition, String pStructureDefName, String pKey, PropertyDefinition pPropDef,
		Builder<StructureRef> pStructureRefListBuilder) {
		File structureDir = getStructureDir(pKey, false);
		if (structureDir == null)
			return;
		File childDir = (pPropDef == null ? structureDir : new File(structureDir, pPropDef.getName()));
		if (childDir.exists() == false)
			return;
		File[] listTypeDirs = childDir.listFiles((File pFile) -> pFile.isDirectory());
		StringBuilder refBuilder = new StringBuilder();
		if (pKey != null)
			refBuilder.append(pKey).append('/');
		if (pPropDef != null)
			refBuilder.append(pPropDef.getName()).append('/');
		int preTypeOffset = refBuilder.length();
		for (File listTypeDir : listTypeDirs) {

			/* If the PropertyDefinition has type restrictions, then make sure that this directory/type is valid */

			String typeName = unescapeValue(listTypeDir.getName());

			if (pPropDef != null) {
				Collection<StructureDefinitionRef> referenceTypes = pPropDef.getReferenceTypes();
				if (referenceTypes.isEmpty() == false) {
					boolean match = false;
					for (StructureDefinitionRef sdr : referenceTypes) {
						StructureDefinition sd = sdr.resolve();
						String testName = sd.getName();
						if (typeName.equals(testName)) {
							match = true;
							break;
						}
					}
					if (match == false)
						continue;
				}
			}
			else if (pKey == null) {
				/*
				 * Special case where there is no parent key. In this case, the StructureDefinition is the restriction
				 */

				if (typeName.equals(pStructureDefName) == false)
					continue;
			}

			refBuilder.setLength(preTypeOffset);
			refBuilder.append(typeName).append('/');
			int preNameOffset = refBuilder.length();

			File[] listFiles = listTypeDir.listFiles((File pDir, String pName) -> pName.endsWith(".properties"));
			for (File f : listFiles) {
				String name = unescapeValue(f.getName().substring(0, f.getName().length() - ".properties".length()));

				refBuilder.setLength(preNameOffset);
				refBuilder.append(name);
				pStructureRefListBuilder
					.add(mScope.getToolkit().createStructureRefFromSerialized(mScope, refBuilder.toString()));
			}
		}
		return;
	}

}
