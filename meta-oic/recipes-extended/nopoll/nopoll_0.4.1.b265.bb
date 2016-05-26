# Copyright (C) 2016 kalyan kumar <kalyankumar.nagabhirava@gmail.com>
# Released under the apache-2.0  license (see COPYIN for the terms)

DESCRIPTION = "websocket toolkit "
LICENSE = "APACHE-2.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=f0504124678c1b3158146e0630229298"
DEPENDS = "openssl"
inherit autotools
#SRC_URI = "file://nopoll-0.4.1.b265.tar.gz"
SRC_URI = "http://www.aspl.es/nopoll/downloads/nopoll-${PV}.tar.gz"
SRC_URI[md5sum] = "3a3cc02d51bc58bb525c0eea5836bc6a"
SRC_URI[sha256sum] = "aff130bbf412503982e9d1383e0f9c6f6efca6d3e295f13b8f9f5a6e5ed930df"

do_install_append() {
  install -d ${D}${bindir}
   install -m644  ${S}/../build/test/nopoll-regression-client ${D}${bindir}
   install -m644  ${S}/../build/test/nopoll-regression-listener ${D}${bindir}
}
FILES_${PN} += "${bindir}/*"

ALLOW_EMPTY_$PN ="1"

