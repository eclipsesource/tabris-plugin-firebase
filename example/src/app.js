const MessagingPage = require('./pages/messaging');
const AnalyticsPage = require('./pages/analytics');
const res = require('./resources');

const MARGIN = res.dimen.margin;
const MARGIN_LARGE = res.dimen.marginLarge;

const pages = [new MessagingPage(), new AnalyticsPage()];

const navigationView = new tabris.NavigationView({
  left: 0, top: 0, right: 0, bottom: 0
}).appendTo(tabris.ui.contentView);

const mainPage = new tabris.Page({
  title: 'Firebase Examples',
  background: res.color.pageBackground
}).appendTo(navigationView);

new tabris.CollectionView({
  left: 0, top: 0, right: 0, bottom: 0,
  itemCount: pages.length,
  cellHeight: (index) => index === 0 ? 96 : 88,
  createCell: () => {
    const composite = new tabris.Composite({highlightOnTouch: true});
    new tabris.TextView({
      id: 'titleView',
      left: MARGIN, right: MARGIN, top: MARGIN,
      maxLines: 1,
      font: '16px',
      textColor: res.color.textPrimary
    }).appendTo(composite);
    new tabris.TextView({
      id: 'descriptionView',
      maxLines: 2,
      left: MARGIN, top: ['prev()', 0], right: MARGIN,
      font: '14px',
      textColor: res.color.textSecondary
    }).appendTo(composite);
    return composite;
  },
  updateCell: (cell, index) => {
    const titleView = cell.find('#titleView').first();
    titleView.text = pages[index].title;
    titleView.top = index === 0 ? MARGIN_LARGE : MARGIN;
    cell.find('#descriptionView').first().text = pages[index].description;
  }
}).on('select', ({index}) => pages[index].appendTo(navigationView))
  .appendTo(mainPage);

