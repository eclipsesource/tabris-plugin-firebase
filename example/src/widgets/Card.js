const res = require('../resources');

const MARGIN = res.dimen.margin;

class Card extends tabris.Composite {
  constructor(properties = {}) {
    properties['left'] = MARGIN;
    properties['right'] = MARGIN;
    properties['top'] = ['prev()', MARGIN];
    properties['background'] = 'white';
    properties['elevation'] = 2;
    properties['cornerRadius'] = 2;
    const title = properties.title;
    delete properties.title;
    super(properties);
    new tabris.TextView({
      left: MARGIN, right: MARGIN, top: MARGIN,
      text: title,
      font: 'medium 16px',
      textColor: res.color.textSecondary
    }).appendTo(this);
  }
}

module.exports = Card;
