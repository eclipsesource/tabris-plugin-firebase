
package com.eclipsesource.firebase.analytics;

import com.eclipsesource.tabris.android.Properties;
import com.eclipsesource.tabris.android.PropertyHandlerAdapter;

import java.util.Map;

class AnalyticsPropertyHandler extends PropertyHandlerAdapter<Analytics> {

  private static final String PROP_ANALYTICS_COLLECTION_ENABLED = "analyticsCollectionEnabled";
  private static final String PROP_SCREEN_NAME = "screenName";
  private static final String PROP_USER_ID = "userId";

  @Override
  public void set( Analytics analytics, Properties properties ) {
    Map<String, Object> all = properties.getAll();
    for( String key : all.keySet() ) {
      switch( key ) {
        case PROP_ANALYTICS_COLLECTION_ENABLED:
          analytics.setAnalyticsCollectionEnabled( properties.getBoolean( PROP_ANALYTICS_COLLECTION_ENABLED ) );
          break;
        case PROP_SCREEN_NAME:
          analytics.setScreenName( properties.getString( PROP_SCREEN_NAME ) );
          break;
        case PROP_USER_ID:
          analytics.setUserId( properties.getString( PROP_USER_ID ) );
          break;
      }
    }
  }

}
