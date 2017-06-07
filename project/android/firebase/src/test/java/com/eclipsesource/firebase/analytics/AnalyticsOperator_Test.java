package com.eclipsesource.firebase.analytics;

import android.app.Activity;

import com.eclipsesource.tabris.android.PropertyHandler;
import com.eclipsesource.tabris.android.TabrisContext;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AnalyticsOperator_Test {

  private AnalyticsOperator operator;

  @Before
  public void setUp() {
    operator = new AnalyticsOperator(mock(Activity.class), mock(TabrisContext.class));
  }

  @Test
  public void testGetType() {
    String type = operator.getType();

    assertThat(type).isEqualTo("com.eclipsesource.firebase.Analytics");
  }

  @Test
  public void testGetPropertyHandler() {
    PropertyHandler propertyHandler = operator.getPropertyHandler(mock(Analytics.class));

    assertThat(propertyHandler).isInstanceOf(AnalyticsPropertyHandler.class);
  }

}