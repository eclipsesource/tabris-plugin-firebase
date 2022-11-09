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

    if (device.platform === 'iOS') {
      new Button({
        top: ['prev()', MARGIN_SMALL], centerX: 0,
        text: 'Request permissions'
      }).on('tap', () => firebase.Messaging.requestPermissions())
        .appendTo(scrollView);
    }

    new Button({
      top: ['prev()', MARGIN_SMALL], centerX: 0,
      text: 'Print all pending messages'
    }).on('tap', () => console.log(firebase.Messaging.pendingMessages.getAll()))
      .appendTo(scrollView);
    new Button({
      top: ['prev()', MARGIN_SMALL], centerX: 0,
      text: 'Clear all pending messages'
    }).on('tap', () => console.log(firebase.Messaging.pendingMessages.clearAll()))
      .appendTo(scrollView);

    new Composite({top: 'prev()', height: MARGIN}).appendTo(scrollView);

    if (firebase.Messaging.launchData) {
      messageText.text = 'App launched with data:\n\n' + JSON.stringify(firebase.Messaging.launchData);
    }

    firebase.Messaging.on({
      tokenChanged: updateMessagingDetails,
      message: ({data}) => messageText.text = `Message received:\n\n${JSON.stringify(data)}`
    });

    updateMessagingDetails();

    function updateMessagingDetails() {
      let token = firebase.Messaging.token;
      tokenText.text = token ? token : 'Loading token...';
    }

  }

  _createCard(title) {
    return new Card({
      left: MARGIN, right: MARGIN, top: ['prev()', MARGIN],
      title
    });
  }

};
