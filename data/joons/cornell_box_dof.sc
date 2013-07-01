% avoid writing comments, as it may confuse the SC reader

image {
  resolution 800 600
  aa 0 0
  samples 5
  filter gaussian
}

trace-depths {
  diff 4
  refl 3
  refr 2
}

photons {
  caustics 1000000 kd 100 0.5
}

camera {
   type thinlens
   eye 7.0 -6.0 5.0
   target 6.0 -5.0 4.0
   up -0.30 0.30 0.90
   fov 49.134 
   aspect 1.333 
   fdist 158.0 
   lensr 8
}

gi {
  type igi
  samples 64         % number of virtual photons per set
  sets 1             % number of sets (increase this to translate shadow boundaries into noise)
  b 0.00003          % bias - decrease this values until bright spots dissapear
  bias-samples 0     % set this >0 to make the algorithm unbiased
}

shader {
  name debug_caustics
  type view-caustics
}

shader {
  name debug_globals
  type view-global
}

shader {
  name debug_gi
  type view-irradiance
}

shader {
  name DefaultGrey
  type diffuse
  diff 0.7 0.7 0.7
}

shader {
  name Blue
  type diffuse
  diff 0.25 0.25 0.8
}

shader {
  name Red
  type diffuse
  diff 0.8 0.25 0.25
}

shader {
  name Mirror
  type mirror
  refl 0.7 0.7 0.7
}

shader {
  name Glass
  type glass
  eta 1.6
  color 1 1 1
}

shader {
   name Glossy
   type shiny
   diff 0.8 0.8 0.8
   refl 0.3
}

object {
  shader none
  type cornellbox
  corner0 -50 -50 -10
  corner1  50  50 90
  left    0.8 0.25 0.25
  right   0.25 0.25 0.8
  top     0.7 0.7 0.7
  bottom  0.7 0.7 0.7
  back    0.7 0.7 0.7
  emit    15 15 15
  samples 64
}
