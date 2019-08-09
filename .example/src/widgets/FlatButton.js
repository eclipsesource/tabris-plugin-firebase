const {Composite, TextView} = require('tabris');
const res = require('../resources');

const HORIZONTAL_MARGIN = 8;

class FlatButton extends Composite {

  constructor(properties = {}) {
    super(Object.assign({
      cornerRadius: 2,
      background: 'white',
      highlightOnTouch: true
    }, properties));
  }

  set text(text) {
    (this._getTextLabel() || this._createTextLabel()).text = text.toUpperCase();
  }

  get text() {
    return this._getTextLabel().text || '';
  }

  _getTextLabel() {
    return this.find('#textLabel').first();
  }

  _createTextLabel() {
    return new TextView({
      id: 'textLabel',
      left: HORIZONTAL_MARGIN, right: HORIZONTAL_MARGIN, height: 48,
      font: 'medium 14px',
      textColor: res.color.accent
    }).appendTo(this);
  }

}

module.exports = FlatButton;
