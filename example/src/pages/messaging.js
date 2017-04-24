const MARGIN = 16;

exports.create = () => new tabris.Page({
  title: 'Messaging',
  autoDispose: false
}).once('appear', createExample);

function createExample({target: page}) {

  const instanceIdText = new tabris.TextView({
    left: MARGIN, top: MARGIN, right: MARGIN
  }).appendTo(page);

  const tokenText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN
  }).appendTo(page);

  const messageText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: 'Waiting for message...'
  }).appendTo(page);

  if (firebase.Messaging.launchData) {
    messageText.text = 'App launched with data:\n\n' + JSON.stringify(firebase.Messaging.launchData);
  }

  firebase.Messaging.on('change:instanceId', updateMessagingDetails);

  firebase.Messaging.on('change:token', updateMessagingDetails);

  firebase.Messaging.on('message', ({data}) => messageText.text = `Message received:\n\n${JSON.stringify(data)}`);

  new tabris.Button({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: 'Reset InstanceId'
  }).on('select', () => firebase.Messaging.resetInstanceId())
    .appendTo(page);

  updateMessagingDetails();

  function updateMessagingDetails() {
    const token = firebase.Messaging.token;
    console.log('Token: ' + token);
    tokenText.text = token ? 'Use the following token to send messages to this device:\n\n' + token : 'Loading token...';
    instanceIdText.text = 'InstanceId: ' + firebase.Messaging.instanceId;
  }

}
