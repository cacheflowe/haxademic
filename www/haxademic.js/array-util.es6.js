class ArrayUtil {

  static removeElement(array, element) {
    const index = array.indexOf(element);
    if (index !== -1) return array.splice(index, 1);
    return null;
  }

  static clear(array) {
    array.splice(0, array.length);
  }

  static shuffle(array) {
    array.sort(() => {return 0.5 - Math.random()});
  }

  static randomElement(array) {
    return array[MathUtil.randRange(0, array.length - 1)];
  }

  static moveLastToFirst(array) {
    array.unshift(array.pop());
  }

  static uniqueArrayCopy(array) {
    return array.filter((el, i, arr) => {
      return arr.indexOf(el) === i;   // only return the first instance of an element
    });
  }

  static uniqueArrayCopy2(array) {
    return [...new Set(array)]; // via @addyosmani
  }

  static crossfadeEnds(array, fadeSize=0.1) {
    // number of elements to fade on either end
    let numToFade = Math.round(array.length * fadeSize);
    // average of start/end values
    let endAvg = (array[array.length - 1] + array[0]) / 2;
    for(var i = 0; i <= numToFade; i++) {
      // lerp strength increases
      let lerpStrength = i / numToFade;
      // indices go from inland towards the edges, increasingly fading towards the ends average
      let endIndex = array.length - numToFade - 1 + i;
      array[endIndex] = MathUtil.lerp(array[endIndex], endAvg, lerpStrength);
      let startIndex = numToFade - i;
      array[startIndex] = MathUtil.lerp(array[startIndex], endAvg, lerpStrength);
    }
  }

}
