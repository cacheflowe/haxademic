class ThreeScene {

  constructor(el, bgColor = 0xffffff) {
    this.el = el;
    this.bgColor = bgColor;
    this.buildScene();
    this.buildCamera();
    this.buildRenderer();
    this.buildLights();
    this.addToDOM();
  }

  buildScene() {
    this.scene = new THREE.Scene();
  }

  buildCamera() {
    this.getScreenSize();
    this.VIEW_ANGLE = 45;
    this.NEAR = 0.1;
    this.FAR = 20000;
    this.camera = new THREE.PerspectiveCamera(this.VIEW_ANGLE, this.ASPECT, this.NEAR, this.FAR);
    this.scene.add(this.camera);
    this.camera.position.set(0,0,400);
    this.camera.lookAt(this.scene.position);
  }

  buildRenderer() {
    this.renderer = new THREE.WebGLRenderer( {antialias:true} );
    this.renderer.setClearColor(this.bgColor);
    this.renderer.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
    this.renderer.shadowMap.enabled = true;
    this.renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  }

  buildLights() {
    var ambientLight = new THREE.AmbientLight(0x444444);
    this.scene.add(ambientLight);
  }

  addToDOM() {
    this.container = document.createElement('div');
    this.el.appendChild(this.container);
    this.container.appendChild(this.renderer.domElement);
  }

  update() {
    this.renderer.render(this.scene, this.camera);
  }

  getScreenSize() {
    this.SCREEN_WIDTH = window.innerWidth;
    this.SCREEN_HEIGHT = window.innerHeight;
    this.ASPECT = this.SCREEN_WIDTH / this.SCREEN_HEIGHT;
  }

  resize() {
    this.getScreenSize();
    this.container.style.width = this.SCREEN_WIDTH + 'px';
    this.container.style.height = this.SCREEN_HEIGHT + 'px';
    this.camera.aspect = this.ASPECT;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
  }

}
