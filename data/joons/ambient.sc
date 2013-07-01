%% please avoid writing comments, because it may confuse the SC reader

image {
  resolution 800 600
  aa 0 2
  filter gaussian
}

trace-depths {
  diff 4
  refl 3
  refr 2
}

camera {
  type pinhole
  eye    0 -205 50
  target 0 0 50
  up     0 0 1
  fov    45
  aspect 1.333333
}

gi {
   type ambocc
   bright { "sRGB nonlinear" 1 1 1 } 
   dark { "sRGB nonlinear" 0 0 0 }
   samples 64
   maxdist 200.0 
}

background {
   color  { "sRGB nonlinear" 1.0 1.0 1.0 }
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
  diff 0.8 0.8 0.8
}

shader {
  name Blue
  type diffuse
  diff 0.25 0.25 0.8
}

shader {
  name ModeBlue
  type diffuse
  diff 0.0 0.784 0.917
}

shader {
  name Green
  type diffuse
  diff 0.25 0.8 0.25
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
