class StringFormatter {

  /**
   *  Returns a standardized phone number string.
   *  @param  str An unformatted phone number.
   *  @return A standardized phone number string.
   *  @use    {@code var phone = StringFormatter.formatPhone('3035558888');}
   */
  static formatPhone(str) {
    return (str + '').replace(/[() -]*(?:\d?)[() -.]*(\d{3})[() -.]*(\d{3})[() -.]*(\d{4})[() -]*/, '($1) $2-$3');
  };

  /**
   *  Returns a standardized social security number string.
   *  @param  str An unformatted social security number.
   *  @return A standardized social security number string.
   *  @use    {@code var ssn = StringFormatter.formatSSN('333002222');}
   */
  static formatSSN(str) {
    return (str + '').replace(/(\d{3})[ -]*(\d{2})[ -]*(\d{4})/, '$1-$2-$3');
  };

  /**
   *  Returns a standardized credit card number string.
   *  @param  str An unformatted credit card number.
   *  @return A standardized credit card number string.
   *  @use    {@code var cc = StringFormatter.formatCreditCard('1111-2222-3333-4444');}
   */
  static formatCreditCard(str) {
    return (str + '').replace(/(\d{4})[ -]*(\d{4})[ -]*(\d{4})[ -]*(\d{4})/, '$1 $2 $3 $4');
  };

  /**
   *  Returns a number, removing non-numeric characters.
   *  @param  str A number, without too much extra non-numeric junk in there.
   *  @return A number (in string format), stripped of non-numeric characters.
   *  @use    {@code var number = StringFormatter.formatNumber('$303.33');}
   */
  static formatNumber(str) {
    let float = str.match(/\d+\.?\d+/);
    if (float && float.length > 0) {
      return float[0];
    } else {
      return str;
    }
  };

  /**
   *  Returns a number with the traditional US currency format.
   *  @param  str A numberic monetary value.
   *  @return A number (in string format), with traditional US currency formatting.
   *  @use    {@code var moneyVal = StringFormatter.formatDollarsCents('303.333333');}
   */
  static formatDollarsCents(str) {
    var numParts;
    numParts = (str + '').split('.');
    if (numParts.length === 1) {
      numParts.push('00');
    } else {
      while (numParts[1].length < 2) {
        numParts[1] += '0';
      }
      numParts[1] = numParts[1].substr(0, 2);
    }
    return '$' + numParts.join('.');
  };

  /**
   *  Returns a string, formatted with commas in between every 3 numbers.
   *  @param  str A number.
   *  @return A formatted number (in string format).
   *  @use    {@code var formattedNumber = StringFormatter.addCommasToNumber('3000000');}
   */
  static addCommasToNumber(str) {
    let x = (str + '').split('.');
    let x1 = x[0];
    let x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
      x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
  };

  /**
   *  Returns a time as a string, with or without hours.
   *  @param  seconds   A number of seconds.
   *  @param  showHours Boolean flag for showing hours or not.
   *  @return A formatted time.
   *  @use    {@code var time = StringFormatter.timeFromSeconds(30000, true);}
   */
  static timeFromSeconds(seconds, showHours) {
    var h = Math.floor(seconds / 3600);
    var m = Math.floor(seconds % 3600 / 60);
    var s = Math.floor(seconds % 3600 % 60);
    var hStr = (h < 10 ? "0" : "") + h;
    var mStr = (m < 10 ? "0" : "") + m;
    var sStr = (s < 10 ? "0" : "") + s;
    if(showHours == true) {
      return hStr + ':' + mStr + ':' +sStr;
    } else {
      return mStr + ':' +sStr;
    }
  };

}
