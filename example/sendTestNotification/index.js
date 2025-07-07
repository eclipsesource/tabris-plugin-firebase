(async () => {
  const { google } = require('googleapis');
  const fs = require('fs');
  const fetch = require('node-fetch');

  if (!fs.existsSync('./service-account-private-key.json')) {
    throw new Error('Place your service account key in ./scripts/service-account-private-key.json. It can be generated on https://console.cloud.google.com/iam-admin/serviceaccounts for your project.\n\nMake sure that the service account has the permission "cloudmessaging.messages.create". It can be configured here: https://console.cloud.google.com/projectselector2/iam-admin/iam');
  }

  if (!process.env.TABRIS_PLUGIN_FIREBASE_EXAMPLE_FCM_TOKEN) {
    throw new Error('The environment variable TABRIS_PLUGIN_FIREBASE_EXAMPLE_FCM_TOKEN must be set to the cloud message token shown in the example app.')
  }

  const NOTIFICATION = { title: "Test title", body: "Test body" };
  const FCM_SERVICE_ACCOUNT_PRIVATE_KEY = JSON.parse(fs.readFileSync('./service-account-private-key.json'));

  let accessToken = await getAccessToken();
  let body = JSON.stringify({
    message: {
      token: process.env.TABRIS_PLUGIN_FIREBASE_EXAMPLE_FCM_TOKEN,
      data: NOTIFICATION,
      apns: { payload: { aps: { alert: { title: NOTIFICATION.title, body: NOTIFICATION.body } } }
      }
    }
  });
  console.log(`> ${body}`);
  const fcmUrl = `https://fcm.googleapis.com/v1/projects/${FCM_SERVICE_ACCOUNT_PRIVATE_KEY.project_id}/messages:send`;
  let response = await fetch(fcmUrl, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json'
    },
    body
  });
  let responseText = await response.text();
  console.log(`< ${responseText}`);
  return response.status === 200 && !!JSON.parse(responseText).name;

  function getAccessToken() {
    let { client_email, private_key } = FCM_SERVICE_ACCOUNT_PRIVATE_KEY;
    return new Promise((resolve, reject) => {
      let jwtClient = new google.auth.JWT(
        client_email,
        null,
        private_key,
        ['https://www.googleapis.com/auth/firebase.messaging'],
        null
      );
      jwtClient.authorize((err, tokens) => {
        if (err) {
          reject(err);
          return;
        }
        resolve(tokens.access_token);
      });
    });
  }
})().catch(console.error);
