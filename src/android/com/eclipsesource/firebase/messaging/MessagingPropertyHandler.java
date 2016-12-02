
package com.eclipsesource.firebase.messaging;

import com.eclipsesource.tabris.android.TabrisPropertyHandler;
import com.eclipsesource.tabris.client.core.model.Properties;
import com.google.firebase.iid.FirebaseInstanceId;

class MessagingPropertyHandler implements TabrisPropertyHandler<Messaging> {

  private static final String PROP_INSTANCE_ID = "instanceId";
  private static final String PROP_TOKEN = "token";
  private static final String PROP_LAUNCH_DATA = "launchData";

  @Override
  public Object get( Messaging messaging, String property ) {
    switch( property ) {
      case PROP_INSTANCE_ID:
        return FirebaseInstanceId.getInstance().getId();
      case PROP_TOKEN:
        return FirebaseInstanceId.getInstance().getToken();
      case PROP_LAUNCH_DATA:
        return messaging.getLaunchData();
    }
    return null;
  }

  @Override
  public void set( Messaging messaging, Properties properties ) {
    // setting any properties is not supported
  }

}
