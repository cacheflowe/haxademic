package com.haxademic.core.file;

public class CounterToFile {

  protected String countName;
  protected int count = 0;

  public CounterToFile(String countName) {
    this.countName = countName;
    count = PrefToText.getValueI(countName, count);
  }

  public int count() {
    return count;
  }

  public void increment() {
    increment(1);
  }

  public void decrement() {
    increment(-1);
  }

  public void increment(int amount) {
    count += amount;
    new Thread(new Runnable() { public void run() {
      PrefToText.setValue(countName, count);
    }}).start();
  }
}
