# IOT-router
Data router with IOT support to detect and control IOT devices with in the network.

To build IOT router for  RaspberryPi(2) clone below projects

cd IOT-router

git clone git://git.yoctoproject.org/poky 

git clone git://git.openembedded.org/meta-openembedded 

git clone git://git.yoctoproject.org/meta-raspberrypi 

How to use it:

1) source poky/oe-init-build-env rpi-router-build

Add needed layer to bblayers.conf:

2) meta-raspberrypi: bitbake-layers add-layer ../meta-raspberrypi

3) meta-oic: bitbake-layers add-layer ../meta-oic

4) meta-openembedded/meta-oe: bitbake-layers add-layer ../meta-openembedded/meta-oe/


5) Set MACHINE to "raspberrypi"/"raspberrypi2/"raspberrypi3" in conf/local.conf. (see note on sdl): echo 'MACHINE = "raspberrypi2"' >> conf/local.conf


dd to a SD card the generated sdimg file (use xzcat if rpi-sdimg.xz is used)
Boot your RPI.
