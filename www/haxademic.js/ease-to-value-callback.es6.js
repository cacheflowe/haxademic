class EaseToValueCallback {

  constructor(value = 0, easeFactor = 10, callback = EaseToValueCallback.noop, finishRange = 0.01 ) {
    if(typeof EasingFloat === 'undefined') return console.warn('EaseToValueCallback requires EasingFloat');
    this.easingFloat = new EasingFloat( value, easeFactor );
    this.callback = callback;
    this.finishRange = finishRange;
  };

  setTarget( value ) {
    this.easingFloat.setTarget( value );
    this.easeToTarget();
  };

  setValue( value ) {
    this.easingFloat.setValue( value );
  };

  easeToTarget(){
    this.callback(this.easingFloat.update());
    if( Math.abs(this.easingFloat.value() - this.easingFloat.target() ) > this.finishRange) {   // keep easing if we're not close enough
      requestAnimationFrame(() => { this.easeToTarget(); });
    } else {
      this.easingFloat.setValue( this.easingFloat.target() );
      this.callback(this.easingFloat.value());                                                  // call the callback one last time with the final value
    }
  }
}

EaseToValueCallback.noop = () => {};
