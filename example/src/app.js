const messagingPage = require('./pages/messaging');
const analyticsPage = require('./pages/analytics');

const pages = [messagingPage, analyticsPage];

const navigationView = new tabris.NavigationView({
  left: 0, top: 0, right: 0, bottom: 0
}).appendTo(tabris.ui.contentView);

const mainPage = new tabris.Page({
  title: 'Firebase examples'
}).appendTo(navigationView);

pages.forEach((pageConstructor) => {
  const page = pageConstructor.create();
  new tabris.Button({
    left: 16, top: 'prev() 16', right: 16,
    text: `Show "${page.title.toLowerCase()}" example`
  }).on('select', () => page.appendTo(navigationView))
    .appendTo(mainPage);
});
