var MARGIN = 16;

exports.create = function() {
  return new tabris.Page({
    title: 'Messaging',
    autoDispose: false
  }).once('appear', createExample);
};

function createExample({target: page}) {

  var instanceIdText = new tabris.TextView({
    left: MARGIN, top: MARGIN, right: MARGIN
  }).appendTo(page);

  var tokenText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN
  }).appendTo(page);

  var messageText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: 'Waiting for message...'
  }).appendTo(page);

  if (firebase.Messaging.launchData) {
    messageText.text = 'App launched with data:\n\n' + JSON.stringify(firebase.Messaging.launchData);
  }

  firebase.Messaging.on('change:instanceId', updateMessagingDetails);

  firebase.Messaging.on('change:token', updateMessagingDetails);

  firebase.Messaging.on('message', ({data}) => {
    messageText.text = 'Received message:\n\n' + JSON.stringify(data);
  });

  new tabris.Button({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: 'Reset InstanceId'
  }).on('select', function() {
    firebase.Messaging.resetInstanceId();
  }).appendTo(page);

  updateMessagingDetails();

  function updateMessagingDetails() {
    var token = firebase.Messaging.token;
    console.log('Token: ' + token);
    tokenText.text = token ? 'Use the following token to send messages to this device:\n\n' + token : 'Loading token...';
    instanceIdText.text = 'InstanceId: ' + firebase.Messaging.instanceId;
  }
}
