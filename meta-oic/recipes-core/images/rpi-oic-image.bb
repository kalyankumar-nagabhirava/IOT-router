# Copyright (C) 2016 kalyan kumar nagabhirava <kalyankumar.nagabhirava@gmail.com>
# Released under the  license (see COPYING.MIT for the terms)

SUMMARY = "RPI image  with iotivity"
LICENSE = "MIT"

IMAGE_FEATURES += " \
    package-management \
"
include recipes-core/images/rpi-basic-image.bb

IMAGE_INSTALL_append = "\
		packagegroup-iotivity \
		"
