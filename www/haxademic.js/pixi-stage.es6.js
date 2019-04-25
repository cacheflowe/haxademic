class PixiStage {

  constructor(el, bgColor, id) {
    this.el = el;
    this.elSize = this.el.getBoundingClientRect();
    console.log(this.elSize);
    this.devicePixelRatio = window.devicePixelRatio;
    this.renderer = PIXI.autoDetectRenderer(this.elSize.width, this.elSize.height, {
      backgroundColor: bgColor,
      resolution: this.devicePixelRatio
    });
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
