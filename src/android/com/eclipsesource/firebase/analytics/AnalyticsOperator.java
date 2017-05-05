
package com.eclipsesource.firebase.analytics;

import android.app.Activity;

import com.eclipsesource.tabris.android.AbstractOperator;
import com.eclipsesource.tabris.android.Properties;
import com.eclipsesource.tabris.android.PropertyHandler;
import com.eclipsesource.tabris.android.TabrisContext;

public class AnalyticsOperator extends AbstractOperator<Analytics> {

  private static final String TYPE = "com.eclipsesource.firebase.Analytics";
  private static final String METHOD_LOG_EVENT = "logEvent";
  private static final String METHOD_SET_USER_PROPERTY = "setUserProperty";
  private static final String PROP_NAME = "name";
  private static final String PROP_DATA = "data";
  private static final String PROP_VALUE = "value";
  private static final String PROP_KEY = "key";

  private final AnalyticsPropertyHandler propertyHandler;
  private final Activity activity;

  public AnalyticsOperator( Activity activity, TabrisContext tabrisContext ) {
    this.activity = activity;
    propertyHandler = new AnalyticsPropertyHandler();
  }

  @Override
  public PropertyHandler<Analytics> getPropertyHandler( Analytics analytics ) {
    return propertyHandler;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public Analytics create( String id, Properties properties ) {
    return new Analytics( activity );
  }

  @Override
  public Object call( Analytics analytics, String method, Properties properties ) {
    switch( method ) {
      case METHOD_LOG_EVENT:
        analytics.logEvent( properties.getString( PROP_NAME ),
            properties.getProperties( PROP_DATA ).getAll() );
        break;
      case METHOD_SET_USER_PROPERTY:
        analytics.setUserProperty( properties.getString( PROP_KEY ),
            properties.getString( PROP_VALUE ) );
        break;
    }
    return null;
  }
}
