const {device, Button, Composite, Page, ScrollView, TextView} = require('tabris');
const Card = require('../widgets/Card');
const FlatButton = require('../widgets/FlatButton');
const res = require('../resources');

const MARGIN = res.dimen.margin;
const MARGIN_SMALL = res.dimen.marginSmall;

module.exports = class MessagingPage extends Page {

  constructor(properties) {
    super(Object.assign({
      title: 'Firebase Cloud Messaging',
      autoDispose: false,
      background: res.color.pageBackground,
    }, properties));
    this._createContent();
  }

  get DESCRIPTION() {
    return 'Use Cloud Messaging to have custom data pushed to your device without a persistent connection.';
  }

  _createContent() {

    let scrollView = new ScrollView({
      left: 0, right: 0, top: 0, bottom: 0
    }).appendTo(this);

    let messageCard = this._createCard('Cloud Message').appendTo(scrollView);

    new TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      text: 'Use the following token to send messages to this device:'
    }).appendTo(messageCard);

    let tokenText = new TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      font: '14px monospace',
      selectable: true
    }).appendTo(messageCard);

    let messageText = new TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      text: 'Waiting for message...'
    }).appendTo(messageCard);

    // composite is a workaround for tabris-android bug where setting bottom would collapse messageText
    new Composite({top: 'prev()', height: MARGIN}).appendTo(messageCard);

    let instanceIdCard = this._createCard('Instance Id').appendTo(scrollView);

    new TextView({
      left: MARGIN, top: ['prev()', MARGIN],
      text: 'Instance id:'
    }).appendTo(instanceIdCard);

    let instanceIdText = new TextView({
      left: ['prev()', MARGIN_SMALL], baseline: 'prev()', right: MARGIN,
      selectable: true,
      font: '14px monospace'
    }).appendTo(instanceIdCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Reset instance id'
    }).on('tap', () => firebase.Messaging.resetInstanceId())
      .appendTo(instanceIdCard);

    if (device.platform === 'iOS') {
      new Button({
        top: ['prev()', MARGIN_SMALL], centerX: 0,
        text: 'Request permissions'
      }).on('tap', () => firebase.Messaging.requestPermissions())
        .appendTo(scrollView);
    }

    new Composite({top: 'prev()', height: MARGIN}).appendTo(scrollView);

    if (firebase.Messaging.launchData) {
      messageText.text = 'App launched with data:\n\n' + JSON.stringify(firebase.Messaging.launchData);
    }

    firebase.Messaging.on({
      instanceIdChanged: updateMessagingDetails,
      tokenChanged: updateMessagingDetails,
      message: ({data}) => messageText.text = `Message received:\n\n${JSON.stringify(data)}`
    });

    updateMessagingDetails();

    function updateMessagingDetails() {
      let token = firebase.Messaging.token;
      tokenText.text = token ? token : 'Loading token...';
      instanceIdText.text = firebase.Messaging.instanceId;
    }

  }

  _createCard(title) {
    return new Card({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN],
      title
    });
  }

};
