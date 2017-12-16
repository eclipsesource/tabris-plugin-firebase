package com.eclipsesource.firebase.reference;

import android.app.Activity;

import com.eclipsesource.tabris.android.AbstractOperator;
import com.eclipsesource.tabris.android.Properties;
import com.eclipsesource.tabris.android.PropertyHandler;
import com.eclipsesource.tabris.android.TabrisContext;


import android.util.Log;


import com.google.firebase.database.FirebaseDatabase;

public class ReferenceOperator extends AbstractOperator<Reference> {

  private static final String TYPE = "com.eclipsesource.firebase.Reference";


  
  private static final String METHOD_SET_VALUE = "setValue";
  private static final String METHOD_CHILD = "child";
  private static final String METHOD_PUSH = "push";
  private static final String METHOD_GET_KEY = "getKey";
  private static final String METHOD_KEEP_SYNCED = "keepSynced";
  private static final String METHOD_CREATE = "create";
  private static final String METHOD_REGISTER_LISTENERS = "registerListeners";




  private final Activity activity;
  private final TabrisContext tabrisContext;
  private final ReferencePropertyHandler propertyHandler;

  public ReferenceOperator(Activity activity, TabrisContext tabrisContext) {
    this.activity = activity;
    this.tabrisContext = tabrisContext;
    propertyHandler = new ReferencePropertyHandler();
  }

  @Override
  public PropertyHandler<Reference> getPropertyHandler(Reference ref) {
    return propertyHandler;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Reference create(String id, Properties properties) {
    FirebaseDatabase firebase = FirebaseDatabase.getInstance();;
    Reference ref = new Reference(this.activity, this.tabrisContext);
    return ref;
  }

  @Override
  public Object call(Reference ref, String method, Properties properties) {
    switch (method) {
      case METHOD_CREATE:
        ref.create(FirebaseDatabase.getInstance().getReference(properties.getString("path")));
        break;
      case METHOD_PUSH:
        return ref.push().getKey(); 
      case METHOD_KEEP_SYNCED:
        ref.keepSynced(properties.getBooleanSafe("keepSynced"));
        break;
      case METHOD_SET_VALUE:
        ref.setValue(properties.get("value"));
        break;
    }
    return null;
  }

  @Override
  public void destroy(Reference ref) {
    ref.unregisterAllListeners();
  }

}
