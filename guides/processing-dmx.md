# DMX with Processing

### DMX interfaces

* [ENTTEC DMX USB Pro](https://www.amazon.com/Enttec-70304-Lighting-Controller-Interface/dp/B077VW1DJH)
  * [5-to-3 pin converter](https://www.amazon.com/American-DJ-5-Pin-Female-Turnaround/dp/B0013XWB14) if you get the ENTTEC
* or [DMXking UltraDMX Micro](https://www.amazon.com/DMXking-UltraDMX-Micro-Adapter-Dongle/dp/B00T8OKM98/) (an ENTTEC clone, but smaller and cheaper)

Use DMX over ethernet for super long cable runs - standard DMX cables are generally rated for ~75', but ethernet can go hundreds of feet. You need a [male](https://www.amazon.com/TecNec-DMX-3XM-CAT5-3-pin-Adapter-TecNec/dp/B00KUTR7MA) & [female](https://www.amazon.com/TecNec-DMX-3XF-CAT5-3-pin-Female-Adapter/dp/B00KUTZW3Q) adapter for the 2 ends of a standard cat-5 network cable.

### DMX lights

There's a whole world of DMX lights and DMX-controlled devices, like winches (link) and power outlets. Most DMX lights have a manual RGB mode, which can be set via a control panel on the back of the devices. By setting the starting channel of your device, you can target multiple daisy-chained lights by sending out DMX values on different channels. If a light has an RGB mode, you will likely use 3 consecutive DMX channels to target R, G, & B. Other channels might be used to control pan/tilt on motorized light fixtures, for example.

### Code

Get the `dmxP512` library and import the Processing Serial library to help you find your DMX USB device. If you're using Eclipse, make sure you've pointed the Serial library at the appropriate native library.

* [https://github.com/hdavid/dmxP512](https://github.com/hdavid/dmxP512)

My own DMX wrapper has some instructions in the comments about how to identify your USB/serial device based on your operating system. OS X seems to need a virtual serial device driver to properly register your DMX USB device.

* [DMXWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/dmx/DMXWrapper.java)

Here's a little code that takes Kinect tracking and sets the pan of my motorized spotlight. You can see in my comments which DMX channels control which aspects of the fixture:

* [Demo_DmxKinectStiletto](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/demo/hardware/dmx/Demo_DmxKinectStiletto.java#L81)
* And [the result](https://www.instagram.com/p/BkWHmjunL-0/)

### Custom DMX fixtures

A DMX decoder lets you build custom light fixtures or voltage-controlled electronic devices. It's important to use a 12v power supply if your LED strips are 12v (24v is another common voltage for LEDs and components). LED strips are not addressable - the entire strip will change to the same color. You'll want to look for SMB 5050 LED lights, which will come in single channel (color) and RGB varieties.

Voltage is sent through the decoder's wire terminals via PWM. Here are some components that you'll need if you want to build a custom DMX device.

<img src="images/dmx-decoder-setup.jpg" alt="custom DMX decoder"/>

The basic unit:

* [24-channel DMX Decoder](https://www.amazon.com/gp/product/B01CCBG1SO/)
* [Power supply](https://www.amazon.com/500W-Power-Supply-Single-Output/dp/B01KZP2CKA/)
  * [Power supply IEC power cable](https://www.amazon.com/TNP-Universal-Power-Cord-Feet/dp/B01N237QI9/)
* [DMX Terminals](https://www.amazon.com/Terminal-Adapter-Converters-Controller-Decoder/dp/B00Q32V2JC/)

Wires to connect the basic unit components and LED strips or devices:

* [2-conductor Wire (jacketed)](https://www.amazon.com/18AWG-Voltage-Conductor-Jacketed-Speaker/dp/B06XSNQDV1/)
* [2-conductor Wire](https://www.amazon.com/Gauge-Black-Stranded-Conductor-Speaker/dp/B00J36SUWC/)

LED strips:

* [White single-color LED strips (2-conductor 5050 SMD)](https://www.amazon.com/dp/B01ELDJ5X4/)
* [RGB LED strips (4-conductor 5050 SMD RGB)](https://www.amazon.com/Alfa-Lighting-Flexible-Remote-Control/dp/B018ZJL0MO/)

Solderless clips for prototyping:

* [Solderless clips for single-channel LED strip](https://www.amazon.com/dp/B07N8GLBLL/)
* [Lever Nuts to combine power cables](https://www.amazon.com/Kalolary-Lever-Nut-Connector-50Pack-Assortment-Connectors/dp/B07NXZNW1K/)

### Other options

* jmej dmx via midi (link)
* get a traditional lighting console
* DMX program player
