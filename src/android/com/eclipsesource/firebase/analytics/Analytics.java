package com.eclipsesource.firebase.analytics;

import android.app.Activity;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;
import java.util.Map.Entry;

class Analytics {

  private final FirebaseAnalytics firebaseAnalytics;
  private final Activity activity;

  Analytics( Activity activity ) {
    firebaseAnalytics = FirebaseAnalytics.getInstance( activity.getApplicationContext() );
    this.activity = activity;
  }

  void setAnalyticsCollectionEnabled( boolean enabled ) {
    firebaseAnalytics.setAnalyticsCollectionEnabled( enabled );
  }

  void setScreenName( String name ) {
    firebaseAnalytics.setCurrentScreen( activity, name, null );
  }

  void setUserProperty( String key, String value ) {
    firebaseAnalytics.setUserProperty( key, value );
  }

  void setUserId( String userId ) {
    firebaseAnalytics.setUserId( userId );
  }

  void logEvent( String name, Map<String, Object> data ) {
    firebaseAnalytics.logEvent( name, createLogDataBundle( data ) );
  }

  private Bundle createLogDataBundle( Map<String, Object> data ) {
    Bundle bundle = new Bundle();
    if( data != null && !data.isEmpty() ) {
      for( Entry<String, Object> entry : data.entrySet() ) {
        Object value = entry.getValue();
        if( value instanceof String ) {
          bundle.putString( entry.getKey(), ( String )value );
        } else if( value instanceof Long ) {
          bundle.putLong( entry.getKey(), ( long )value );
        } else if( value instanceof Integer ) {
          bundle.putLong( entry.getKey(), ( long )value );
        } else if( value instanceof Double ) {
          bundle.putDouble( entry.getKey(), ( double )value );
        } else if( value instanceof Float ) {
          bundle.putDouble( entry.getKey(), ( double )value );
        }
      }
    }
    return bundle;
  }

}
