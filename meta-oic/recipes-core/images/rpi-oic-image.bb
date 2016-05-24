# Copyright (C) 2016 kalyan kumar nagabhirava <kalyankumar.nagabhirava@gmail.com>
# Released under apache  license (see COPYING for the terms)

SUMMARY = "RPI image  with iotivity"
LICENSE = "APACHE2.0"

IMAGE_FEATURES += " \
    package-management \
"
include recipes-core/images/rpi-basic-image.bb

IMAGE_INSTALL_append = "\
		packagegroup-iotivity \
		"
