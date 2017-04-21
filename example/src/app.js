var messagingPage = require('./pages/messaging');

var pages = [messagingPage];

var navigationView = new tabris.NavigationView({
  left: 0, top: 0, right: 0, bottom: 0
}).appendTo(tabris.ui.contentView);

var mainPage = new tabris.Page({
  title: 'Firebase examples'
}).appendTo(navigationView);

pages.forEach(createPageButton);

function createPageButton(pageConstructor) {
  var page = pageConstructor.create();
  new tabris.Button({
    left: 16, top: 'prev() 16', right: 16,
    text: 'Show \'' + page.title.toLowerCase() + '\' example'
  }).on('select', function() {
    page.appendTo(navigationView);
  }).appendTo(mainPage);
}
