package com.haxademic.core.math.easing;

import com.haxademic.core.app.P;

/**
 *
 * @author s. conversy from n. roussel c++ version
 *
 * Copyright 2019
 * 
 * BSD License https://opensource.org/licenses/BSD-3-Clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions
 * and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
/*
 * Converted by @cacheflowe to use floats instead of doubles and be happier in
 * Haxademic
 */
class LowPassFilter {

  float y, a, s;
  boolean initialized;

  void setAlpha(float alpha) throws Exception {
    if (alpha <= 0f || alpha > 1f) {
      throw new Exception("alpha should be in (0f., 1f]");
    }
    a = alpha;
  }

  public LowPassFilter(float alpha) throws Exception {
    init(alpha, 0);
  }

  public LowPassFilter(float alpha, float initval) throws Exception {
    init(alpha, initval);
  }

  private void init(float alpha, float initval) throws Exception {
    y = s = initval;
    setAlpha(alpha);
    initialized = false;
  }

  public float filter(float value) {
    float result;
    if (initialized) {
      result = a * value + (1f - a) * s;
    } else {
      result = value;
      initialized = true;
    }
    y = value;
    s = result;
    return result;
  }

  public float filterWithAlpha(float value, float alpha) throws Exception {
    setAlpha(alpha);
    return filter(value);
  }

  public boolean hasLastRawValue() {
    return initialized;
  }

  public float lastRawValue() {
    return y;
  }
};

public class OneEuroFilter {

  float freq;
  float mincutoff;
  float beta_;
  float dcutoff;
  LowPassFilter x;
  LowPassFilter dx;
  float lasttime;
  static float UndefinedTime = -1;

  public float alpha(float cutoff) {
    float te = 1f / freq;
    float tau = 1f / (2f * P.PI * cutoff);
    return 1f / (1f + tau / te);
  }

  public void setFrequency(float f) throws Exception {
    if (f <= 0) {
      throw new Exception("freq should be >0");
    }
    freq = f;
  }

  public void setMinCutoff(float mc) throws Exception {
    if (mc <= 0) {
      throw new Exception("mincutoff should be >0");
    }
    mincutoff = mc;
  }

  public void setBeta(float b) {
    beta_ = b;
  }

  public void setDerivateCutoff(float dc) throws Exception {
    if (dc <= 0) {
      throw new Exception("dcutoff should be >0");
    }
    dcutoff = dc;
  }

  public OneEuroFilter(float freq) throws Exception {
    init(freq, 1f, 0f, 1f);
  }

  public OneEuroFilter(float freq, float mincutoff) throws Exception {
    init(freq, mincutoff, 0f, 1f);
  }

  public OneEuroFilter(float freq, float mincutoff, float beta_) throws Exception {
    init(freq, mincutoff, beta_, 1f);
  }

  public OneEuroFilter(float freq, float mincutoff, float beta_, float dcutoff) throws Exception {
    init(freq, mincutoff, beta_, dcutoff);
  }

  private void init(float freq,
      float mincutoff, float beta_, float dcutoff) throws Exception {
    setFrequency(freq);
    setMinCutoff(mincutoff);
    setBeta(beta_);
    setDerivateCutoff(dcutoff);
    x = new LowPassFilter(alpha(mincutoff));
    dx = new LowPassFilter(alpha(dcutoff));
    lasttime = UndefinedTime;
  }

  public float filter(float value) throws Exception {
    return filter(value, UndefinedTime);
  }

  public float filter(float value, float timestamp) throws Exception {
    // update the sampling frequency based on timestamps
    if (lasttime != UndefinedTime && timestamp != UndefinedTime) {
      freq = 1f / (timestamp - lasttime);
    }

    lasttime = timestamp;
    // estimate the current variation per second
    float dvalue = x.hasLastRawValue() ? (value - x.lastRawValue()) * freq : 0f; // Fix: 0f or value?
    float edvalue = dx.filterWithAlpha(dvalue, alpha(dcutoff));
    // use it to update the cutoff frequency
    float cutoff = mincutoff + beta_ * Math.abs(edvalue);
    // filter the given value
    return x.filterWithAlpha(value, alpha(cutoff));
  }

}
