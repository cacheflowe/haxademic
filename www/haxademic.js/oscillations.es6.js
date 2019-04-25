class Oscillations {

  // borrowed from: https://soulwire.co.uk/math-for-motion/

  static osc1(t) {
    return Math.pow(Math.sin(t), 3);
  }

  static osc2(t) {
    return Math.pow(Math.sin(t * Math.PI), 12);
  }

  static osc3(t) {
    return Math.sin(Math.tan(Math.cos(t) * 1.2));
  }

}
