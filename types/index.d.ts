import { EventObject, NativeObject, PropertyChangedEvent, Widget, WidgetEvents, WidgetProperties } from 'tabris';

declare global {
  namespace firebase {
    const Performance: Performance;
    interface Performance extends NativeObject {
      performanceCollectionEnabled: boolean;
      startTrace(name: string): void;
      stopTrace(name: string): void;
      incrementMetrics(name1: string, name2: string): void;
      incrementMetrics(name1: string, name2: string, value: number): void;
    }
    const Crashlytics: Crashlytics;
    interface Crashlytics extends NativeObject {
      setCrashlyticsCollectionEnabled(enabled: boolean): void;
      setUserIdentifier(id: string): void;
      makeCrash(): void;
      log(value: string): void;
      log(priority: 0 | 1 | 2 | 3 | 4, tag: string, value: string): void;
      // Customizing
      setBool(key: string, value: boolean): void;
      setString(key: string, value: string): void;
      setInt(key: string, value: number): void;
    }

    const Analytics: Analytics;
    const Messaging: Messaging;
    const MessagingEvents: MessagingEvents;
    const MessageEvent: MessageEvent;
    type AnalyticsProperties = Partial<PropertyMixins.Analytics>;

    interface Analytics extends NativeObject, PropertyMixins.Analytics {
      logEvent(eventName: string, parameters?: { [key: string]: string }): void;
      setUserProperty(propertyName: string, value: string): void;
      set(properties: AnalyticsProperties): this;
      set(property: string, value: any): this;
    }

    interface Messaging extends NativeObject {
      readonly instanceId: string;
      readonly token: string;
      readonly launchData: object;
      readonly pendingMessages: {
        getAll: () => object[],
        clearAll: () => void
      };
      resetInstanceId(): void;
      requestPermissions(): void;
      on(type: string, listener: (event: any) => void, context?: object): this;
      on(listeners: MessagingEvents): this;
      off(type: string, listener: (event: any) => void, context?: object): this;
      off(listeners: MessagingEvents): this;
      once(type: string, listener: (event: any) => void, context?: object): this;
      once(listeners: MessagingEvents): this;
    }

    interface MessagingEvents {
      instanceIdChanged?(event: PropertyChangedEvent<Messaging, string>): void;
      tokenChanged?(event: PropertyChangedEvent<Messaging, string>): void;
      message?(event: MessageEvent): void;
    }

    interface MessageEvent extends EventObject<Messaging> {
      data: any;
    }

    namespace PropertyMixins {
      interface Analytics {
        analyticsCollectionEnabled: boolean;
        screenName: string;
        userId: string;
      }
    }
  }
}

export { };
