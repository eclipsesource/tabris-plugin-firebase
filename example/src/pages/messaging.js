const Card = require('../widgets/Card');
const FlatButton = require('../widgets/FlatButton');
const res = require('../resources');

const MARGIN = res.dimen.margin;
const MARGIN_SMALL = res.dimen.marginSmall;

module.exports = class MessagingPage extends tabris.Page {

  constructor(properties) {
    super(properties);
    this.title = 'Firebase Cloud Messaging';
    this.description = 'Use Cloud Messaging to have custom data pushed to your device without a persistent connection.';
    this.autoDispose = false;
    this.background = res.color.pageBackground;
    this.once('appear', this._createPageContent);
  }

  _createPageContent({target: page}) {

    const scrollView = new tabris.ScrollView({
      left: 0, right: 0, top: 0, bottom: 0
    }).appendTo(page);

    let messageCard = new Card({title: 'Cloud Message'}).appendTo(scrollView);

    new tabris.TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      text: 'Use the following token to send messages to this device:'
    }).appendTo(messageCard);

    const tokenText = new tabris.TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      font: '14px monospace',
      selectable: true
    }).appendTo(messageCard);

    const messageText = new tabris.TextView({
      left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
      text: 'Waiting for message...'
    }).appendTo(messageCard);

    // composite is a workaround for tabris-android bug where setting bottom would collapse messageText
    new tabris.Composite({top: 'prev()', height: MARGIN}).appendTo(messageCard);

    let instanceIdCard = new Card({title: 'Instance Id'}).appendTo(scrollView);

    new tabris.TextView({
      left: MARGIN, top: ['prev()', MARGIN],
      text: 'Instance id:'
    }).appendTo(instanceIdCard);

    const instanceIdText = new tabris.TextView({
      left: ['prev()', MARGIN_SMALL], baseline: 'prev()', right: MARGIN,
      selectable: true,
      font: '14px monospace'
    }).appendTo(instanceIdCard);

    new FlatButton({
      right: MARGIN, top: ['prev()', MARGIN_SMALL], bottom: MARGIN_SMALL,
      text: 'Reset instance id'
    }).on('tap', () => firebase.Messaging.resetInstanceId())
      .appendTo(instanceIdCard);

    new tabris.Composite({top: 'prev()', height: MARGIN}).appendTo(scrollView);

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
      const token = firebase.Messaging.token;
      tokenText.text = token ? token : 'Loading token...';
      instanceIdText.text = firebase.Messaging.instanceId;
    }

  }

};
