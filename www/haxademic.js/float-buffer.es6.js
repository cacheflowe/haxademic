class FloatBuffer {

  constructor(size) {
    this.size = size;
    this.initBuffer();
    this.reset();
  }

  initBuffer() {
    this.sampleIndex = 0;
    this.buffer = [];
    for(let i=0; i < this.size; i++) {
      this.buffer.push(0);
    }
  }

  reset() {
    for(let i=0; i < this.size; i++) {
      this.buffer[i] = 0;
    }
  }

  update(value) {
    this.sampleIndex++;
    if(this.sampleIndex == this.size) this.sampleIndex = 0;
    this.buffer[this.sampleIndex] = value;
  }

  toString() {
    return this.buffer.reduce(function(acc, val) {
      return acc + Math.round(val * 10) / 10 + ', ';
    }, '');
  }

  average() {
    return this.sum() / this.size;
  }

  sum() {
    return this.buffer.reduce(function(acc, val) {
      return acc + val;
    }, 0);
  }

  sumPositive() {
    return this.buffer.reduce(function(acc, val) {
      return (val > 0) ? acc + val : acc;
    }, 0);
  }

  sumNegative() {
    return this.buffer.reduce(function(acc, val) {
      return (val < 0) ? acc + val : acc;
    }, 0);
  }

  sumAbs() {
    return this.buffer.reduce(function(acc, val) {
      return acc + Math.abs(val);
    }, 0);
  }

  max() {
    let max = this.buffer[0];
    for(let i=1; i < this.size; i++) {
      if( this.buffer[i] > max ) max = this.buffer[i];
    }
    return max;
  }

  min() {
    let min = this.buffer[0];
    for(let i=1; i < this.size; i++) {
      if( this.buffer[i] < min ) min = this.buffer[i];
    }
    return min;
  }

  maxAbs() {
    let max = Math.abs(this.buffer[0]);
    for(let i=1; i < this.size; i++) {
      if( Math.abs(this.buffer[i]) > max ) max = Math.abs(this.buffer[i]);
    }
    return max;
  }

}
