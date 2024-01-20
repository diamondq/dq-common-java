package com.diamondq.common.security.acme;

import com.diamondq.common.config.Config;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.security.acme.model.ChallengeState;
import com.diamondq.common.security.acme.model.PersistedState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

@ApplicationScoped
public class DataService {

  private Toolkit mToolkit;

  private Scope mScope;

  private StructureDefinition mChallengeStateDef;

  private StructureDefinition mPersistedStateDef;

  @Inject
  public DataService(Toolkit pToolkit, Config pConfig) {
    mToolkit = pToolkit;
    mScope = mToolkit.getOrCreateScope("std");

    /* ****************************** */
    /* Create the ChallengeState */
    /* ****************************** */

    StructureDefinition sd = mToolkit.createNewStructureDefinition(mScope, "challengeState");
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "token", PropertyType.String)
      .setPrimaryKey(true));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "response", PropertyType.String));

    mToolkit.writeStructureDefinition(mScope, sd);

    StructureDefinition finalLookup = mToolkit.lookupStructureDefinitionByName(mScope, "challengeState");
    if (finalLookup == null)
      throw new IllegalStateException("Unable to find challengeState immediately after writing it");
    mChallengeStateDef = finalLookup;

    /* ****************************** */
    /* Create the PersistedState */
    /* ****************************** */

    sd = mToolkit.createNewStructureDefinition(mScope, "persistedState");
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "id", PropertyType.String)
      .setPrimaryKey(true));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "acmeServer", PropertyType.String));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope,
      "registrationLocation",
      PropertyType.String
    ));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope,
      "authorizationLocation",
      PropertyType.String
    ));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope,
      "certificateLocation",
      PropertyType.String
    ));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "csr", PropertyType.String));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "userKeyPair", PropertyType.String));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "domainKeyPair", PropertyType.String));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "domainCert", PropertyType.String));
    sd = sd.addPropertyDefinition(mToolkit.createNewPropertyDefinition(mScope, "certChain", PropertyType.String));

    mToolkit.writeStructureDefinition(mScope, sd);

    finalLookup = mToolkit.lookupStructureDefinitionByName(mScope, "persistedState");
    if (finalLookup == null)
      throw new IllegalStateException("Unable to find persistedState immediately after writing it");
    mPersistedStateDef = finalLookup;

  }

  /**
   * Lookup an existing ChallengeState based on the token
   *
   * @param pToken the token
   * @return the matching ChallengeState or null if there is no match
   */
  public @Nullable ChallengeState lookupChallengeState(String pToken) {
    Structure structure = mToolkit.createStructureRefFromParts(mScope,
      null,
      null,
      mChallengeStateDef,
      Collections.singletonList(pToken)
    ).resolve();
    if (structure == null) return null;
    return new ChallengeState(structure);
  }

  /**
   * Delete an existing ChallengeState
   *
   * @param pValue the challengeState
   */
  public void deleteChallengeState(ChallengeState pValue) {
    Structure structure = pValue.getStructure();
    mToolkit.deleteStructure(mScope, structure);
  }

  /**
   * Create a new ChallengeState
   *
   * @return the ChallengeState
   */
  public ChallengeState createChallengeState() {
    return new ChallengeState(mChallengeStateDef.createNewStructure());
  }

  /**
   * Persist the changes of this ChallengeState
   *
   * @param pValue the ChallengeState
   * @return the updated ChallengeState after persistence
   */
  public ChallengeState writeChallengeState(ChallengeState pValue) {
    Structure structure = pValue.getStructure();
    StructureRef ref = structure.getReference();
    mToolkit.writeStructure(mScope, structure);
    structure = ref.resolve();
    if (structure == null) throw new IllegalStateException("Unable to resolve reference immediately after writing");
    return new ChallengeState(structure);
  }

  /**
   * Look up a new PersistedState based on the domain
   *
   * @param pDomain the domain to search
   * @return the PersistedState or null if there is no match
   */
  public @Nullable PersistedState lookupPersistedState(String pDomain) {
    Structure structure = mToolkit.createStructureRefFromParts(mScope,
      null,
      null,
      mPersistedStateDef,
      Collections.singletonList(pDomain)
    ).resolve();
    if (structure == null) return null;
    return new PersistedState(structure);
  }

  /**
   * Delete an existing PersistedState
   *
   * @param pValue the PersistedState
   */
  public void deletePersistedState(PersistedState pValue) {
    Structure structure = pValue.getStructure();
    mToolkit.deleteStructure(mScope, structure);
  }

  /**
   * Create a new PersistedState
   *
   * @return the PersistedState
   */
  public PersistedState createPersistedState() {
    return new PersistedState(mPersistedStateDef.createNewStructure());
  }

  /**
   * Persist the changes of this PersistedState
   *
   * @param pValue the PersistedState
   * @return the updated PersistedState after persistence
   */
  public PersistedState writePersistedState(PersistedState pValue) {
    Structure structure = pValue.getStructure();
    StructureRef ref = structure.getReference();
    mToolkit.writeStructure(mScope, structure);
    structure = ref.resolve();
    if (structure == null) throw new IllegalStateException("Unable to resolve reference immediately after writing");
    return new PersistedState(structure);
  }

}
