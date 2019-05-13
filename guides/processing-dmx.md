# DMX via Processing

## Hardware guide

### DMX interfaces

* [ENTTEC DMX USB Pro](https://www.amazon.com/Enttec-70304-Lighting-Controller-Interface/dp/B077VW1DJH)
  * [5-to-3 pin converter](https://www.amazon.com/American-DJ-5-Pin-Female-Turnaround/dp/B0013XWB14) if you get the ENTTEC
* or [DMXking UltraDMX Micro](https://www.amazon.com/DMXking-UltraDMX-Micro-Adapter-Dongle/dp/B00T8OKM98/) (an ENTTEC clone, but smaller and cheaper)

DMX over ethernet for super long cable runs - standard DMX cables are generally rated for ~75', but ethernet can go hundreds of feet. You need a male & female for the 2 ends of a cat-5 cable

* [DMX cat-5 3-pin female adapter](https://www.amazon.com/TecNec-DMX-3XF-CAT5-3-pin-Female-Adapter/dp/B00KUTZW3Q)
* [DMX cat-5 3-pin male adapter](https://www.amazon.com/TecNec-DMX-3XM-CAT5-3-pin-Adapter-TecNec/dp/B00KUTR7MA)

### Processing library

* [https://github.com/hdavid/dmxP512](https://github.com/hdavid/dmxP512)

My own DMX wrapper has some instructions in the comments about how to identify your USB/serial device. I believe you need the Processing serial library loaded and working (and pointed at the appropriate native library if you're using Eclipse).

* [DMXWrapper](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/core/hardware/dmx/DMXWrapper.java)

Here's a little code that takes Kinect tracking and sets the pan of my motorized spotlight. You can see in my comments which DMX channels control which aspects of the fixture:

* [Demo_DmxKinectStiletto](https://github.com/cacheflowe/haxademic/blob/master/src/com/haxademic/demo/hardware/dmx/Demo_DmxKinectStiletto.java#L81)
* And [the result](https://www.instagram.com/p/BkWHmjunL-0/)

### Custom DMX fixtures

A DMX decoder lets you build custom light fixtures. It's important to use a 12v power supply if your LED strips are 12v (or 24v if your equipment requires that).

* [24-channel DMX Decoder](https://www.amazon.com/gp/product/B01CCBG1SO/)
* [DMX Terminals](https://www.amazon.com/Terminal-Adapter-Converters-Controller-Decoder/dp/B00Q32V2JC/)
* [Power supply](https://www.amazon.com/500W-Power-Supply-Single-Output/dp/B01KZP2CKA/)
  * [Power supply IEC power cable](https://www.amazon.com/TNP-Universal-Power-Cord-Feet/dp/B01N237QI9/)
* [2-conductor Wire (jacketed)](https://www.amazon.com/18AWG-Voltage-Conductor-Jacketed-Speaker/dp/B06XSNQDV1/)
* [2-conductor Wire](https://www.amazon.com/Gauge-Black-Stranded-Conductor-Speaker/dp/B00J36SUWC/)
* [White single-color LED strips (2-conductor 5050 SMD)](https://www.amazon.com/dp/B01ELDJ5X4/)
  * [Solderless clips](https://www.amazon.com/dp/B07N8GLBLL/)
  * [Lever Nuts to combine power cables](https://www.amazon.com/Kalolary-Lever-Nut-Connector-50Pack-Assortment-Connectors/dp/B07NXZNW1K/)
* [RGB LED strips (4-conductor 5050 SMD RGB)](https://www.amazon.com/Alfa-Lighting-Flexible-Remote-Control/dp/B018ZJL0MO/)

Hardware links:

* 2-conductor LED clips 50pc x2		https://www.amazon.com/dp/B07N8GLBLL/
* Lever nut 4-port 50pc				https://www.amazon.com/Kalolary-Lever-Nut-Connector-50Pack-Assortment-Connectors/dp/B07NXZNW1K/
* Ethernet cable 50' x4 				https://www.amazon.com/Ethernet-Internet-Snagless-Connector-Computer/dp/B01NBH6OIN/
