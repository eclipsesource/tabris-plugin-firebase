var MARGIN = 16;

exports.create = function() {
  return new tabris.Page({
    title: 'Messaging'
  }).once('appear', createExample);
};

function createExample(page) {

  new tabris.TextView({
    left: MARGIN, top: MARGIN, right: MARGIN,
    text: 'InstanceId: ' + firebase.Messaging.instanceId
  }).appendTo(page);

  var token = firebase.Messaging.token;
  console.log('Token: ' + token);

  var tokenText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: token ? 'Use the following token to send messages to this device:\n\n' + token : 'Loading token...'
  }).appendTo(page);

  var messageText = new tabris.TextView({
    left: MARGIN, top: ['prev()', MARGIN], right: MARGIN,
    text: 'Waiting for message...'
  }).appendTo(page);

  if (firebase.Messaging.launchData) {
    messageText.text = 'App launched with data:\n\n' + JSON.stringify(firebase.Messaging.launchData);
  }

  firebase.Messaging.on('tokenRefresh', function(messaging, token) {
    tokenText.text = 'Token refreshed: ' + token;
  });

  firebase.Messaging.on('message', function(messaging, data) {
    messageText.text = 'Received message:\n\n' + JSON.stringify(data);
  });

}
