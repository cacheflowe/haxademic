class AppStore {

  constructor() {
    this.state = {};
    this.listeners = [];
    this.methods = {};
    window._store = this;
  }

  addListener(obj, key) {
    if(key) {
      if(!this.methods[key]) this.methods[key] = [];
      this.methods[key].push(obj);
    } else {
      this.listeners.push(obj);
    }
  }

  removeListener(obj, key) {
    if(key) {
      if(this.methods[key]) {
        const index = this.methods[key].indexOf(obj);
        if (index !== -1) this.methods[key].splice(index, 1);
      }
    } else {
      const index = this.listeners.indexOf(obj);
      if (index !== -1) this.listeners.splice(index, 1);
    }
  }

  set(key, value) {
    if(typeof key === "undefined") throw new Error('AppStore requires legit keys');
    this.state[key] = value;
    this.listeners.forEach((el, i, arr) => {
      el.storeUpdated(key, value);
    });
    // specific listener methods
    const objs = this.methods[key];
    if(objs) {
      objs.forEach((el) => {
        if(el[key]) el[key](value);
        else throw new Error('AppStore listener has no callback: ' + key);
      });
    }
  }

  get(key) {
    return this.state[key];
  }

  log() {
    for(let key in _store.state) {
      console.log(key, _store.state[key]);
    }
  }

}
