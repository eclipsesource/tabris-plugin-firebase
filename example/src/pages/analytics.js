const {Composite, CheckBox, Page, ScrollView, TextInput, TextView} = require('tabris');
const Card = require('../widgets/Card');
const FlatButton = require('../widgets/FlatButton');
const res = require('../resources');

const MARGIN_SMALL = res.dimen.marginSmall;
const MARGIN = res.dimen.margin;
const MARGIN_LARGE = res.dimen.marginLarge;

module.exports = class AnalyticsPage extends Page {

  constructor(properties) {
    super(Object.assign({
      title: 'Firebase Analytics',
      autoDispose: false,
      background: res.color.pageBackground
    }, properties));
    this._createContent();
  }

  get DESCRIPTION() {
    return 'Use Analytics to log events, track app navigation and record custom user properties.';
  }

  _createContent() {
    firebase.Analytics.analyticsCollectionEnabled = true;
    firebase.Analytics.screenName = 'details_screen';
    firebase.Analytics.userId = 'anonymous_user_id';
    firebase.Analytics.setUserProperty('priority_customer', 'true');

    let scrollView = new ScrollView({
      left: 0, right: 0, top: 0, bottom: 0
    }).appendTo(this);

    let generalCard = this._createCard('General').appendTo(scrollView);

    new CheckBox({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN, bottom: MARGIN,
      text: 'Collect analytics data',
      checked: true
    }).on('checkedChanged', ({value: checked}) => firebase.Analytics.analyticsCollectionEnabled = checked)
      .appendTo(generalCard);

    let screenNameCard = this._createCard('Screen name').appendTo(scrollView);

    let screenName = new TextInput({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN_SMALL],
      message: 'Screen name',
      text: 'details_screen'
    }).appendTo(screenNameCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Set screen name'
    }).on('tap', () => firebase.Analytics.screenName = screenName.text)
      .appendTo(screenNameCard);

    let eventCard = this._createCard('Event logging').appendTo(scrollView);

    new TextView({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN_LARGE],
      text: 'Event name',
      font: 'medium 14px',
      textColor: res.color.textInputLabel
    }).appendTo(eventCard);

    let eventName = new TextInput({
      id: 'eventNameTextInput',
      left: MARGIN, right: MARGIN, top: 'prev()',
      message: 'Event name',
      text: 'select_content'
    }).appendTo(eventCard);

    new TextView({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN_LARGE],
      text: 'Event data',
      font: 'medium 14px',
      textColor: res.color.textInputLabel
    }).appendTo(eventCard);

    let logKey1 = new TextInput({
      left: MARGIN, right: ['#logValue1', MARGIN], top: 'prev()',
      message: 'Log data key 1',
      text: 'content_type'
    }).appendTo(eventCard);
    let logValue1 = new TextInput({
      id: 'logValue1',
      left: logKey1, right: MARGIN, baseline: logKey1,
      message: 'Log data value 1',
      text: 'news article'
    }).appendTo(eventCard);

    let logKey2 = new TextInput({
      left: MARGIN, right: ['#logValue2', MARGIN], top: 'prev()',
      message: 'Log data key 2',
      text: 'item_name'
    }).appendTo(eventCard);
    let logValue2 = new TextInput({
      id: 'logValue2',
      left: logKey2, right: MARGIN, baseline: logKey2,
      message: 'Log data value 2',
      text: 'Plugin released'
    }).appendTo(eventCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Send event'
    }).on('tap', () => {
      firebase.Analytics.logEvent(eventName.text, {
        [logKey1.text]: logValue1.text,
        [logKey2.text]: logValue2.text
      });
    }).appendTo(eventCard);

    let userPropertiesCard = this._createCard('User properties').appendTo(scrollView);

    new TextView({
      id: 'userPropertyKeyLabel',
      left: MARGIN, right: ['#userPropertyValueLabel', MARGIN], top: ['prev()', MARGIN],
      text: 'Key',
      font: 'medium 14px',
      textColor: res.color.textInputLabel
    }).appendTo(userPropertiesCard);
    new TextView({
      id: 'userPropertyValueLabel',
      left: '#userPropertyKeyLabel', right: MARGIN, baseline: '#userPropertyKeyLabel',
      text: 'Value',
      font: 'medium 14px',
      textColor: res.color.textInputLabel
    }).appendTo(userPropertiesCard);

    let userPropertyKey = new TextInput({
      left: MARGIN, right: ['#userPropertyValue', MARGIN], top: 'prev()',
      message: 'User property key',
      text: 'priority_customer'
    }).appendTo(userPropertiesCard);
    let userPropertyValue = new TextInput({
      id: 'userPropertyValue',
      left: userPropertyKey, right: MARGIN, baseline: userPropertyKey,
      message: 'User property value',
      text: 'true'
    }).appendTo(userPropertiesCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Set property'
    }).on('tap', () => firebase.Analytics.setUserProperty(userPropertyKey.text, userPropertyValue.text))
      .appendTo(userPropertiesCard);

    let userIdCard = this._createCard('User id').appendTo(scrollView);

    let userId = new TextInput({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN_SMALL],
      message: 'User id',
      text: 'anonymous_user_id'
    }).appendTo(userIdCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Set user id'
    }).on('tap', () => firebase.Analytics.userId = userId.text)
      .appendTo(userIdCard);

    new Composite({top: 'prev()', height: MARGIN}).appendTo(scrollView);
  }

  _createCard(title) {
    return new Card({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN],
      title
    });
  }

};
