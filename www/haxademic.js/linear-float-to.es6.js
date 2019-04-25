// A wrapper for LinearFloat that allows us to consistently move towards a new target in the same amount of linear steps.
// Possibly useful.

class LinearFloatTo {

  constructor(val=0, step=0.025) {
    this.startVal = val;
    this.endVal = val;
    this.progress = new LinearFloat(0, step);
  }

  update() {
    this.progress.update();
  }

  value() {
    return this.map(this.progress.value(), 0, 1, this.startVal, this.endVal)
  }

  valuePenner(equation) {
    return this.map(this.progress.valuePenner(equation), 0, 1, this.startVal, this.endVal)
  }

  setTarget(val) {
    if(val == this.endVal) return;
    this.startVal = this.value();
    this.endVal = val;
    this.progress.setValue(0);
    this.progress.setTarget(1);
  }

  map(val, inputMin, inputMax, outputMin, outputMax) {
    return (outputMax - outputMin) * ((val - inputMin) / (inputMax - inputMin)) + outputMin;
  }

}
