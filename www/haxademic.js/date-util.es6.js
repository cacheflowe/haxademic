class DateUtil {

  static getMillis() {
    return (new Date()).getTime();
  }

  static getTodayTimeStamp() {
    let today = new Date();
    return (today.getYear() + 1900) + '-' + (today.getMonth() + 1) + '-' + today.getDate();
  }

  static datesAreEqual(date1, date2) {
    return date1.getTime() == date2.getTime()
  }

}

DateUtil.midnightTimeSuffix = 'T00:00:00Z';
