FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.1.21"

SRCREV = "ff45bc0e8917c77461b2901e2743e6339bb70413"
SRC_URI = "git://github.com/raspberrypi/linux.git;protocol=git;branch=rpi-4.1.y \
           file://0001-dts-add-overlay-for-pitft22.patch \
          "

require linux-raspberrypi.inc
