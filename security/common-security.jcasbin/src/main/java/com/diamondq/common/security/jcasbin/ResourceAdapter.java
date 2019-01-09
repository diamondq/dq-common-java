package com.diamondq.common.security.jcasbin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;
import org.casbin.jcasbin.persist.Helper;

public class ResourceAdapter implements Adapter {

  private final Class<?> mClass;

  private final String   mResourceName;

  public ResourceAdapter(Class<?> pClass, String pResourceName) {
    super();
    mClass = pClass;
    mResourceName = pResourceName;
  }

  /**
   * @see org.casbin.jcasbin.persist.Adapter#loadPolicy(org.casbin.jcasbin.model.Model)
   */
  @Override
  public void loadPolicy(Model pModel) {
    Helper.loadPolicyLineHandler<String, Model> handler = Helper::loadPolicyLine;

    try {
      try (InputStream is = mClass.getResourceAsStream(mResourceName)) {
        if (is == null)
          throw new IllegalArgumentException();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = br.readLine()) != null) {
          handler.accept(line, pModel);
        }

        br.close();
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
      throw new Error("IO error occurred");
    }
  }

  /**
   * @see org.casbin.jcasbin.persist.Adapter#savePolicy(org.casbin.jcasbin.model.Model)
   */
  @Override
  public void savePolicy(Model pModel) {
    throw new Error("not implemented");
  }

  /**
   * @see org.casbin.jcasbin.persist.Adapter#addPolicy(java.lang.String, java.lang.String, java.util.List)
   */
  @Override
  public void addPolicy(String pSec, String pPtype, List<String> pRule) {
    throw new Error("not implemented");
  }

  /**
   * @see org.casbin.jcasbin.persist.Adapter#removePolicy(java.lang.String, java.lang.String, java.util.List)
   */
  @Override
  public void removePolicy(String pSec, String pPtype, List<String> pRule) {
    throw new Error("not implemented");
  }

  /**
   * @see org.casbin.jcasbin.persist.Adapter#removeFilteredPolicy(java.lang.String, java.lang.String, int,
   *      java.lang.String[])
   */
  @Override
  public void removeFilteredPolicy(String pSec, String pPtype, int pFieldIndex, String... pFieldValues) {
    throw new Error("not implemented");
  }

}
