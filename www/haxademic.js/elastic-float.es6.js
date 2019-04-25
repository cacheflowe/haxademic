/**
 *  An object that moves a point towards a target, with elastic properties.
 *  @param  x       Starting x coordinate.
 *  @param  y       Starting y coordinate.
 *  @param  fric    Friction value [0-1] - lower numbers mean more friction.
 *  @param  accel   Acceleration value [0-1] - lower numbers mean more slower acceleration.
 *  @return The ElasticFloat public interface.
 *  @use    {@code var _point = new ElasticFloat( 100, 100, 100, 0.75, 0.4 ); }
 */
class ElasticFloat {

  constructor(value = 0, fric = 0.8, accel = 0.2) {
    this.val = value;
    this.fric = fric;
    this.accel = accel;
    this.targetVal = this.val;
    this.speed = 0;
  }

  value() {
    return this.val;
  }

  target() {
    return this.targetVal;
  }

  setCurrent(value) {
    this.val = value;
  }

  setTarget(target) {
    this.targetVal = target;
  }

  setFriction(fric) {
    this.fric = fric;
  }

  setAccel(accel) {
    this.accel = accel;
  }

  update() {
    this.speed = ((this.targetVal - this.val) * this.accel + this.speed) * this.fric;
    this.val += this.speed;
    return this.val;
  }
}
