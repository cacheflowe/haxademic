class PixiSpriteScale {

  static scaleToFillContainer(sprite, width, height) {
    const ratioX = width / sprite.texture.width;
    const ratioY = height / sprite.texture.height;
    const scale = (ratioX > ratioY) ? ratioX : ratioY;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToLetterboxContainer(sprite, width, height) {
    const ratioX = width / sprite.texture.width;
    const ratioY = height / sprite.texture.height;
    const scale = (ratioX < ratioY) ? ratioX : ratioY;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToHeight(sprite, height) {
    const scale = height / sprite.texture.height;
    sprite.scale.set(scale, scale);
    return scale;
  }

  static scaleToWidth(sprite, width) {
    const scale = width / sprite.texture.width;
    sprite.scale.set(scale, scale);
    return scale;
  }

}
