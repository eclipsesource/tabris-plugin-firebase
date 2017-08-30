const {Composite, TextView} = require('tabris');
const res = require('../resources');

const MARGIN = res.dimen.margin;

class Card extends Composite {

  constructor(properties = {}) {
    super(Object.assign({
      background: 'white',
      elevation: 2,
      cornerRadius: 2
    }, properties));
  }

  set title(title) {
    (this._getTitleLabel() || this._createTitleLabel()).text = title.toUpperCase();
  }

  get title() {
    return this._getTitleLabel().text || '';
  }

  _getTitleLabel() {
    return this.find('#titleLabel').first();
  }

  _createTitleLabel() {
    return new TextView({
      id: 'titleLabel',
      left: MARGIN, right: MARGIN, top: MARGIN,
      font: 'medium 16px',
      textColor: res.color.textSecondary
    }).appendTo(this);
  }

}

module.exports = Card;
