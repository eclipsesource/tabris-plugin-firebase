
package com.eclipsesource.firebase.messaging;

import android.app.Activity;

import com.eclipsesource.tabris.android.AbstractOperator;
import com.eclipsesource.tabris.android.Properties;
import com.eclipsesource.tabris.android.PropertyHandler;
import com.eclipsesource.tabris.android.TabrisContext;

public class MessagingOperator extends AbstractOperator<Messaging> {

  private static final String TYPE = "com.eclipsesource.firebase.Messaging";
  private static final String METHOD_RESET_INSTANCE_ID = "resetInstanceId";

  private final Activity activity;
  private final TabrisContext tabrisContext;
  private final MessagingPropertyHandler propertyHandler;

  public MessagingOperator( Activity activity, TabrisContext tabrisContext ) {
    this.activity = activity;
    this.tabrisContext = tabrisContext;
    propertyHandler = new MessagingPropertyHandler();
  }

  @Override
  public PropertyHandler<Messaging> getPropertyHandler( Messaging messaging ) {
    return propertyHandler;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Messaging create( String id, Properties properties ) {
    return new Messaging( activity, tabrisContext );
  }

  @Override
  public Object call( Messaging messaging, String method, Properties properties ) {
    if( method.equals( METHOD_RESET_INSTANCE_ID ) ) {
      messaging.resetInstanceId();
    }
    return null;
  }

  @Override
  public void destroy( Messaging messaging ) {
    messaging.unregisterAllListeners();
  }

}
