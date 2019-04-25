class PixiStage {

  constructor(el, bgColor, id) {
    this.el = el;
    this.elSize = this.el.getBoundingClientRect();
    this.devicePixelRatio = window.devicePixelRatio;
    PIXI.settings.PRECISION_FRAGMENT = 'highp'; //this makes text looks better
    this.renderer = PIXI.autoDetectRenderer(this.elSize.width, this.elSize.height, {
      backgroundColor: bgColor,
      transparent: false,
      resolution: this.devicePixelRatio
    });
    // this.renderer.roundPixels = true; //and this too
    // this.pixiApp = new PIXI.Application(); // alternate/new PIXI app/renderer. info: http://pixijs.download/dev/docs/PIXI.Application.html
    // this.el.appendChild(app.view);
    this.renderer.view.classList.add(id);
    this.el.appendChild(this.renderer.view);
    this.stage = new PIXI.Container();
    this.stage.interactive = true;
    this.resize();
  }

  container() {
    return this.stage;
  }

  width() {
    return this.renderer.width / this.devicePixelRatio;
  }

  height() {
    return this.renderer.height / this.devicePixelRatio;
  }

  render() {
    this.renderer.render(this.stage);
  }

  resize() {
    this.elSize = this.el.getBoundingClientRect();
    this.renderer.resize(this.elSize.width, this.elSize.height);
  }

}
