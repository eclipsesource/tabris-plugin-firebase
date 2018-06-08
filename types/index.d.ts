import { EventObject, Widget, WidgetEvents, WidgetProperties, NativeObject, PropertyChangedEvent } from 'tabris';

declare global {
  namespace firebase {
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

export {};
