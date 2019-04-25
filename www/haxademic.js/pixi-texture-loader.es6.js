class PixiTextureLoader {

  constructor(assetsLoadedCallback, keepFinalCallback=false) {
    this.assetsLoadedCallback = assetsLoadedCallback;
    this.keepFinalCallback = keepFinalCallback;
    this.numTextures = 0;
    this.numLoaded = 0;
  }

  loadTexture(filePath, callback) {
    const texture = PIXI.Texture.fromImage(filePath);
    if(texture.width >= 2) {
      texture.baseTexture.scaleMode = PIXI.settings.SCALE_MODE;
      callback(new PIXI.Sprite(texture));
    } else {
      this.numTextures++;
      texture.baseTexture.on('loaded', () => {
        texture.baseTexture.scaleMode = PIXI.settings.SCALE_MODE; // NEAREST;
        callback(new PIXI.Sprite(texture));
        this.numLoaded++
        if(this.numLoaded == this.numTextures) {
          if(this.assetsLoadedCallback) this.assetsLoadedCallback();
          if(this.keepFinalCallback == false) this.assetsLoadedCallback = null;
        }
      });
    }
  }

  loadTextures(filePathsArr, callback) {
    let numLoaded = 0;
    const spritesArr = new Array(filePathsArr.length);
    for(let i=0; i < filePathsArr.length; i++) {
      (() => {
        const index = i;
        const file = filePathsArr[index];
        this.loadTexture(file, (sprite) => {
          spritesArr[index] = sprite;
          numLoaded++;
          if(numLoaded >= filePathsArr.length) callback(spritesArr);
        });
      })();
    }
  }

  static svgElToPixiTexture(el, callback) {
    const base64Img = SVGUtil.svgElToBase64(el);
    const texture = PIXI.Texture.fromImage(base64Img);
    texture.baseTexture.on('loaded', () => {
      callback(new PIXI.Sprite(texture));
    });
  }

}
