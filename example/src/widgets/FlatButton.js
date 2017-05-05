const res = require('../ressources');

const HORIZONTAL_MARGIN = 8;

class FlatButton extends tabris.Composite {
  constructor(properties = {}) {
    properties['cornerRadius'] = 2;
    properties['background'] = properties['background'] === undefined ? 'white' : properties['background'];
    properties['highlightOnTouch'] = true;
    const text = properties.text;
    delete properties.text;
    super(properties);
    new tabris.TextView({
      left: HORIZONTAL_MARGIN, right: HORIZONTAL_MARGIN, height: 48,
      text: text.toUpperCase(),
      font: 'medium 14px',
      textColor: res.color.accent
    }).appendTo(this);
  }
}

module.exports = FlatButton;
