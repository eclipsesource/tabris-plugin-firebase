package com.eclipsesource.firebase.reference;

import com.eclipsesource.tabris.android.Properties;
import com.eclipsesource.tabris.android.PropertyHandler;

class ReferencePropertyHandler implements PropertyHandler<Reference> {

  private static final String PROP_KEY = "key";

  @Override
  public Object get(Reference reference, String property) {
    switch (property) {
      case PROP_KEY:
        return reference.getKey();
    }
    return null;
  }

  @Override
  public void set(Reference reference, Properties properties) {
    // setting any properties is not supported
  }

}
