const {CollectionView, Composite, NavigationView, Page, TextView, ui} = require('tabris');
const MessagingPage = require('./pages/messaging');
const AnalyticsPage = require('./pages/analytics');
const res = require('./resources');

const MARGIN = res.dimen.margin;
const MARGIN_LARGE = res.dimen.marginLarge;

let pages = [new MessagingPage(), new AnalyticsPage()];

let navigationView = new NavigationView({
  left: 0, top: 0, right: 0, bottom: 0
}).appendTo(ui.contentView);

let mainPage = new Page({
  title: 'Firebase Examples',
  background: res.color.pageBackground
}).appendTo(navigationView);

new CollectionView({
  left: 0, top: 0, right: 0, bottom: 0,
  itemCount: pages.length,
  cellHeight: index => index === 0 ? 96 : 88,
  createCell: () => {
    let composite = new Composite({highlightOnTouch: true});
    new TextView({
      id: 'titleView',
      left: MARGIN, right: MARGIN, top: MARGIN,
      maxLines: 1,
      font: '16px',
      textColor: res.color.textPrimary
    }).appendTo(composite);
    new TextView({
      id: 'descriptionView',
      maxLines: 2,
      left: MARGIN, top: 'prev()', right: MARGIN,
      font: '14px',
      textColor: res.color.textSecondary
    }).appendTo(composite);
    return composite;
  },
  updateCell: (cell, index) => {
    let titleView = cell.find('#titleView').first();
    titleView.text = pages[index].title;
    titleView.top = index === 0 ? MARGIN_LARGE : MARGIN;
    cell.find('#descriptionView').first().text = pages[index].DESCRIPTION;
  }
}).on('select', ({index}) => pages[index].appendTo(navigationView))
  .appendTo(mainPage);

