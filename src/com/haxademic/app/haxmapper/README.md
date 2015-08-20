### HaxMapper

A custom projection-mapped visual system

---

### LUNAR LODGE

* Finish face recorder with: 
	* Face recording special effects in HaxMapper!!!!!!
	* Fix "all" mode: `setAllSameTexture()` - this should activate every time a face session starts, but bring back the recent textures when the face session ends
		* why do other textures not clear out when "all" mode is set? - They are cleared out, but spacebar brings multiples back
* HaxMapper fixes/additions
	* Make overlay particles show better - look at classes drawing into _overlayPG
* Add more textures
	* audioreactive canvases
	* SVG deform with different models & filters?
	* add my own looped videos from processing output? (this is causing chunkiness)
* Hardware:
	* Build a face frame
	* Projector test:
	* Daisy chain projectors?
	* Try focusing on near & far objects
* Performance checks:
	* Look at the names of texture classes that are being removed so we can see who's making the app slowwwwww
	* Make sure SMOOTH_HIGH is okay on main PApplet and PG textures
* Nice-to-haves:
	* Add overall effects to system - change brightness & any other shaders to new system
	* Vertex shader deformations to PShapes that define the mapping mesh?



### Live gear required:

* Computer & power cord
* Tripod 
* Ratchet straps
* Audio cables & dongle for input & XLR long cables & connectors
* Extension power cords
* Video cables
* Projector(s)
* iPad
* Wireless router
* Crates for airflow?


## ToDo:

* Solve performance issues...
* Refactor new MeshLines modes into a better system
* MORE TEXTURES
* Allow dragging polygons in HaxMapper for realtime adjustments?
* Add adjustable speed throttling properties to speed/slow the rate at which program changes happen - use adjustable modulos
* Use solid colors vs. using any alpha, since this is faster


#### Texture manager to distribute textures between groups and group items* 
* sweeping/traversing 
 		
#### Build wireframe animations

* Audio-reactive points between vertices
* Triangle moving from polygon to polygon
* Particle system flying around vertices

#### More textures
* convert existing element/modules from HaxVisual
* add more shaders
* Add a wiping texture
* Kinect textures
* perf - figure out what slows the program down
* mesh line mode: fade random polygon on the beat
* another basic eq mode with vertices instead of bars
* post-processing effects?? / occasional crazy shit with the camera?
* vertex displacement
* More post-processing
* Concave hull to draw around the outer edge and mask the interior


#### Cleanup
* Subdivide quads for less distortion
