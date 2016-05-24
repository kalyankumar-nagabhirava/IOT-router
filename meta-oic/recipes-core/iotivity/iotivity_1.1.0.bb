SUMMARY = "IoTivity framework and SDK sponsored by the Open Connectivity Foundation."
DESCRIPTION = "IoTivity is an open source software framework enabling seamless device-to-device connectivity to address the emerging needs of the Internet of Things."
HOMEPAGE = "https://www.iotivity.org/"
DEPENDS = "boost virtual/gettext chrpath-replacement-native expat openssl util-linux curl glib-2.0"
EXTRANATIVEPATH += "chrpath-native"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://resource/include/OCApi.h;beginline=1;endline=19;md5=fc5a615cf1dc3880967127bc853b3e0c"
SRC_URI = "https://mirrors.kernel.org/iotivity/1.1.0/iotivity-1.1.0.tar.gz;name=iotivity \
           git://github.com/01org/tinycbor.git;protocol=https;name=cbor;destsuffix=${S}/extlibs/tinycbor/tinycbor \
           https://googletest.googlecode.com/files/gtest-1.7.0.zip;name=gtest \
           https://github.com/dascandy/hippomocks/archive/2f40aa11e31499432283b67f9d3449a3cd7b9c4d.zip \
           http://www.sqlite.org/2015/sqlite-amalgamation-3081101.zip;name=sqlite3 \
           file://arch.patch;patch=1 \
           file://yocto_paths.patch;patch=1 \
           file://hippomocks_mips_patch \
           file://gcc5.patch \
          "
SRC_URI[iotivity.md5sum] = "98f8d5b0304c357520c4343d983491e2"
SRC_URI[iotivity.sha256sum] = "1ec4ae706d1acebfc267802ce1a4855bb2041875d0ae391429d5129dfaa7a216"
SRCREV_cbor = "b80318ab0640efa98147836380c7937a59dc327d"
SRC_URI[gtest.md5sum] = "2d6ec8ccdf5c46b05ba54a9fd1d130d7"
SRC_URI[gtest.sha256sum] = "247ca18dd83f53deb1328be17e4b1be31514cedfc1e3424f672bf11fd7e0d60d"
SRC_URI[md5sum] = "d54eb32ea45d6d2b624c87e117f6c0cf"
SRC_URI[sha256sum] = "0f57fa8cc1e2f76f1769891a266f2715295baf2333d504f628c674767646ac48"
SRC_URI[sqlite3.md5sum] = "94907e831502e2080b76e281cfa24dde"
SRC_URI[sqlite3.sha256sum] = "a3b0c07d1398d60ae9d21c2cc7f9be6b1bc5b0168cd94c321ede9a0fce2b3cd7"

inherit scons

python () {
    IOTIVITY_TARGET_ARCH = d.getVar("TARGET_ARCH", True)
    EXTRA_OESCONS = "TARGET_OS=yocto TARGET_ARCH=" + IOTIVITY_TARGET_ARCH + " RELEASE=1 WITH_RD=1 ROUTING=GW"
    d.setVar("EXTRA_OESCONS", EXTRA_OESCONS)
    d.setVar("IOTIVITY_TARGET_ARCH", IOTIVITY_TARGET_ARCH)
}

IOTIVITY_BIN_DIR = "/opt/iotivity"
IOTIVITY_BIN_DIR_D = "${D}${IOTIVITY_BIN_DIR}"

do_compile_prepend() {
    if [ ! -f "${S}/extlibs/sqlite3/sqlite3.c" ]; then
        cp ${WORKDIR}/sqlite-amalgamation-3081101/sqlite3.c ${S}/extlibs/sqlite3
        cp ${WORKDIR}/sqlite-amalgamation-3081101/sqlite3.h ${S}/extlibs/sqlite3
    fi
    if [ ! -d "${S}/extlibs/gtest/gtest-1.7.0/" ]; then
        mv ${WORKDIR}/gtest-1.7.0 ${S}/extlibs/gtest/
    fi
    if [ ! -d "${S}/extlibs/hippomocks-master/" ]; then
        mv ${WORKDIR}/hippomocks-2f40aa11e31499432283b67f9d3449a3cd7b9c4d ${S}/extlibs/hippomocks-master
    fi
    if cd ${S} && patch -p1 --dry-run < ${WORKDIR}/hippomocks_mips_patch; then
        cd ${S} && patch -p1 < ${WORKDIR}/hippomocks_mips_patch
    fi
}

make_dir() {
    install -d $1
}

copy_file() {
    install -c -m 444 $1 $2
}

copy_exec() {
    install -c -m 555 $1 $2
}

copy_file_recursive() {
    cd $1 && find . -type d -exec install -d $2/"{}" \;
    cd $1 && find . -type f -exec install -c -m 444 "{}" $2/"{}" \;
}

copy_exec_recursive() {
    cd $1 && find . -executable -exec install -c -m 555 "{}" $2/"{}" \;
}

do_install() {
    make_dir ${D}${libdir}
    #Resource
    #C++ APIs
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/liboc.so ${D}${libdir}

    #Logger
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/liboc_logger.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/liboc_logger_core.so ${D}${libdir}

    #CSDK Shared
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/liboctbstack.so ${D}${libdir}

    #CSDK Static
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libconnectivity_abstraction.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libcoap.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/liboctbstack.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libc_common.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libocsrm.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libroutingmanager.a ${D}${libdir}

    #Resource C++ Apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/oic_svr_db_client.dat ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/oic_svr_db_server.dat ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/presenceclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/presenceserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/groupclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/groupserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/roomclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/roomserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/simpleclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/simpleclientserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/simpleserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/fridgeclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/fridgeserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/garageclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/garageserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/simpleclientHQ ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/simpleserverHQ ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/devicediscoveryserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/devicediscoveryclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/threadingsample ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/examples/lightserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/examples/OICMiddle/OICMiddle ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    chrpath -d `find ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp/ -type f -perm /u+x`

    #Resource CSDK Apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/occlientcoll ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/ocrouting ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/ocserver ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/ocserverbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/occlientslow ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/ocserverslow ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/occlientbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/ocservercoll ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/SimpleClientServer/occlient ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    chrpath -d ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer/*

    make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/secure/ocamsservice ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/secure/ocserverbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/secure/occlientbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/secure/oic_svr_db_client.dat ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/samples/linux/secure/oic_svr_db_server.dat ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure

    #Resource Tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/resource
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/c_common/ocrandom/test/randomtests ${IOTIVITY_BIN_DIR_D}/tests/resource/ocrandom_tests
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/unittests/unittests ${IOTIVITY_BIN_DIR_D}/tests/resource/oc_unittests
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/unittests/oic_svr_db_client.dat ${IOTIVITY_BIN_DIR_D}/tests/resource
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/stack/test/stacktests ${IOTIVITY_BIN_DIR_D}/tests/resource/octbstack_tests
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/connectivity/test/catests ${IOTIVITY_BIN_DIR_D}/tests/resource/ca_tests
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/oc_logger/examples/examples_cpp ${IOTIVITY_BIN_DIR_D}/tests/resource/logger_test_cpp
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/oc_logger/examples/examples_c ${IOTIVITY_BIN_DIR_D}/tests/resource/logger_test_c
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/resource/csdk/security/unittest/unittest ${IOTIVITY_BIN_DIR_D}/tests/resource/security_tests
    copy_file ${S}/extlibs/gtest/gtest-1.7.0/lib/.libs/libgtest.so ${D}${libdir}
    copy_file ${S}/extlibs/gtest/gtest-1.7.0/lib/.libs/libgtest_main.so ${D}${libdir}
    chrpath -d ${D}${libdir}/libgtest.so
    chrpath -d ${D}${libdir}/libgtest_main.so

    #Resource headers
    make_dir ${D}${includedir}/iotivity/resource/stack/
    make_dir ${D}${includedir}/iotivity/resource/logger/
    make_dir ${D}${includedir}/iotivity/resource/connectivity/api
    make_dir ${D}${includedir}/iotivity/resource/connectivity/external
    make_dir ${D}${includedir}/iotivity/resource/connectivity/common
    make_dir ${D}${includedir}/iotivity/resource/security/
    make_dir ${D}${includedir}/iotivity/resource/ocrandom/
    make_dir ${D}${includedir}/iotivity/resource/oc_logger/

    copy_file_recursive ${S}/resource/include ${D}${includedir}/iotivity/resource
    copy_file_recursive ${S}/resource/csdk/stack/include ${D}${includedir}/iotivity/resource/stack
    copy_file_recursive ${S}/resource/csdk/logger/include  ${D}${includedir}/iotivity/resource/logger
    copy_file_recursive ${S}/resource/csdk/connectivity/inc  ${D}${includedir}/iotivity/resource/connectivity
    copy_file_recursive ${S}/resource/csdk/connectivity/api ${D}${includedir}/iotivity/resource/connectivity/api
    copy_file_recursive ${S}/resource/csdk/connectivity/common/inc ${D}${includedir}/iotivity/resource/connectivity/common
    copy_file_recursive ${S}/resource/csdk/security/include  ${D}${includedir}/iotivity/resource/security
    copy_file_recursive ${S}/resource/c_common/ocrandom/include ${D}${includedir}/iotivity/resource/ocrandom
    copy_file_recursive ${S}/resource/oc_logger/include ${D}${includedir}/iotivity/resource/oc_logger
    copy_file ${S}/resource/c_common/oic_string/include/oic_string.h ${D}${includedir}/iotivity/resource
    copy_file ${S}/resource/c_common/oic_malloc/include/oic_malloc.h ${D}${includedir}/iotivity/resource

    #ZigBee Plugin
    #Libraries
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libzigbee_wrapper.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libtelegesis_wrapper.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libplugin_interface.a ${D}${libdir}

    #Headers
    make_dir ${D}${includedir}/iotivity/plugins
    copy_file_recursive ${S}/plugins/include ${D}${includedir}/iotivity/plugins

    #Samples
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/plugins/samples/linux/iotivityandzigbeeserver ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/plugins/samples/linux/iotivityandzigbeeclient ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee
    chrpath -d ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee/*

    #Tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/plugins/zigbee/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/plugins/unittests/piunittests ${IOTIVITY_BIN_DIR_D}/tests/plugins/zigbee
    chrpath -d ${IOTIVITY_BIN_DIR_D}/tests/plugins/zigbee/*

    #Service Components
    #Resource container
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_container.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_container.a ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libBMISensorBundle.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libDISensorBundle.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libHueBundle.so ${D}${libdir}

    #Resource container sample apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/HeightSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/THSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/THSensorApp1 ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/WeightSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/ContainerSample ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/ContainerSampleClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/examples/ResourceContainerConfig.xml ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/

    #Resource container tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/unittests/container_test ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/unittests/libTestBundle.so ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/unittests/ResourceContainerInvalidConfig.xml ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/unittests/ResourceContainerTestConfig.xml ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-container/unittests/libTestBundle.so ${D}${libdir}

    #Resource encapsulation
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_client.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_client.a ${D}${libdir}

    #Resource encapsulation common
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_common.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_common.a ${D}${libdir}

    #Resource encapsulation server builder
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_server.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/librcs_server.a ${D}${libdir}

    #Resource encapsulation sample apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/examples/linux/sampleResourceClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/examples/linux/sampleResourceServer ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/examples/linux/nestedAttributesClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/examples/linux/nestedAttributesServer ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation

    #Resource encapsulation test
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/resource-broker
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/resource-cache
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/common
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/server-builder
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/unittests/rcs_client_test ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/src/resourceBroker/unittest/broker_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/resource-broker
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/src/resourceCache/unittests/cache_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/resource-cache
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/src/common/rcs_common_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/common
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-encapsulation/src/serverBuilder/rcs_server_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/server-builder

    #Resource hosting
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libresource_hosting.a ${D}${libdir}

    #Resource hosting sample app
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-hosting
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-hosting/SampleApp/linux/sampleconsumer ${IOTIVITY_BIN_DIR_D}/examples/service/resource-hosting
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-hosting/SampleApp/linux/sampleprovider ${IOTIVITY_BIN_DIR_D}/examples/service/resource-hosting
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-hosting/SampleApp/linux/sampleresourcehosting ${IOTIVITY_BIN_DIR_D}/examples/service/resource-hosting

    #Things manager
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libTGMSDKLibrary.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libTGMSDKLibrary.a ${D}${libdir}

    #Things manager apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/things-manager/configuration
    copy_exec_recursive ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/things-manager/sampleapp/linux/configuration ${IOTIVITY_BIN_DIR_D}/examples/service/things-manager/configuration

    #Things manager test
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/things-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/things-manager/unittests/ThingsManagerTest ${IOTIVITY_BIN_DIR_D}/tests/service/things-manager

    #Resource directory
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libresource_directory.a ${D}${libdir}

    #Resource directory samples
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-directory
    copy_exec_recursive ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/resource-directory/samples ${IOTIVITY_BIN_DIR_D}/examples/service/resource-directory
    chrpath -d ${IOTIVITY_BIN_DIR_D}/examples/service/resource-directory/*

    #Easy setup
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libESMediatorCSDK.so ${D}${libdir}
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libESMediatorRich.so ${D}${libdir}
    chrpath -d ${D}${libdir}/*.so

    #Easy setup app
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/easy-setup/sampleapp/mediator/linux/csdk_sample/mediator_c ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/easy-setup/sampleapp/mediator/linux/richsdk_sample/mediator_rich ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup

    #Easy setup tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/easy-setup
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/easy-setup/mediator/csdk/unittests/mediator_csdk_test ${IOTIVITY_BIN_DIR_D}/tests/service/easy-setup
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/easy-setup/mediator/richsdk/unittests/mediator_richsdk_test ${IOTIVITY_BIN_DIR_D}/tests/service/easy-setup

    #Scene manager
    copy_file ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/libscene_manager.a ${D}${libdir}

    #Scene manager apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/scene-manager/sampleapp/linux/fanserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/scene-manager/sampleapp/linux/lightserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/scene-manager/sampleapp/linux/sceneclient ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/service/scene-manager/sampleapp/linux/sceneserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager

    #Scene manager tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/scene_action_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/scene_collection_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/scene_list_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/scene_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager

    #Service Headers
    make_dir ${D}${includedir}/iotivity/service/resource-container/
    make_dir ${D}${includedir}/iotivity/service/resource-encapsulation/
    make_dir ${D}${includedir}/iotivity/service/resource-hosting/
    make_dir ${D}${includedir}/iotivity/service/resource-directory/
    make_dir ${D}${includedir}/iotivity/service/things-manager/
    make_dir ${D}${includedir}/iotivity/service/easy-setup/enrollee/
    make_dir ${D}${includedir}/iotivity/service/easy-setup/mediator/csdk/
    make_dir ${D}${includedir}/iotivity/service/easy-setup/mediator/richsdk/
    make_dir ${D}${includedir}/iotivity/service/scene-manager/

    #Resource container
    copy_file_recursive ${S}/service/resource-container/include ${D}${includedir}/iotivity/service/resource-container

    #Resource Encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/common/expiryTimer/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/common/primitiveResource/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/common/utils/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/resourceBroker/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/resourceCache/include ${D}${includedir}/iotivity/service/resource-encapsulation
    copy_file_recursive ${S}/service/resource-encapsulation/src/serverBuilder/include ${D}${includedir}/iotivity/service/resource-encapsulation

    #Resource hosting
    copy_file_recursive ${S}/service/resource-hosting/include ${D}${includedir}/iotivity/service/resource-hosting

    #Resource directory
    copy_file_recursive ${S}/service/resource-directory/include ${D}${includedir}/iotivity/service/resource-directory

    #Things manager
    copy_file_recursive ${S}/service/things-manager/sdk/inc ${D}${includedir}/iotivity/service/things-manager

    #Easy setup
    copy_file_recursive ${S}/service/easy-setup/inc ${D}${includedir}/iotivity/service/easy-setup
    copy_file_recursive ${S}/service/easy-setup/mediator/csdk/inc/ ${D}${includedir}/iotivity/service/easy-setup/mediator/csdk/
    copy_file_recursive ${S}/service/easy-setup/mediator/richsdk/inc/ ${D}${includedir}/iotivity/service/easy-setup/mediator/richsdk/

    #Scene manager
    copy_file_recursive ${S}/service/scene-manager/include ${D}${includedir}/iotivity/service/scene-manager/

    #Misc headers
    make_dir ${D}${includedir}/iotivity/extlibs/cjson
    make_dir ${D}${includedir}/iotivity/extlibs/timer
    copy_file ${S}/extlibs/cjson/cJSON.h ${D}${includedir}/iotivity/extlibs/cjson
    copy_file ${S}/extlibs/timer/timer.h ${D}${includedir}/iotivity/extlibs/timer
    copy_file ${S}/resource/c_common/platform_features.h ${D}${includedir}/iotivity/resource
    copy_file ${S}/resource/c_common/platform_features.h ${D}${includedir}/iotivity/resource/stack
}

#IOTIVITY packages:
#Resource: iotivity-resource, iotivity-resource-dev, iotivity-resource-thin-staticdev, iotivity-resource-dbg
#Resource Samples: iotivity-resource-samples, iotivity-resource-samples-dbg
#Service: iotivity-service, iotivity-service-dev, iotivity-service-staticdev, iotivity-service-dbg
#Service Samples: iotivity-service-samples, iotivity-service-samples-dbg
#Tests: iotivity-tests, iotivity-tests-dbg

FILES_iotivity-resource-dev = "\
                        ${includedir}/iotivity/resource \
                        ${inclidedir}/iotivity/extlibs"

FILES_iotivity-resource-thin-staticdev = "\
                        ${libdir}/libocsrm.a \
                        ${libdir}/libconnectivity_abstraction.a \
                        ${libdir}/liboctbstack.a \
                        ${libdir}/libcoap.a \
                        ${libdir}/libc_common.a \
                        ${libdir}/libroutingmanager.a \
                        ${libdir}/libtimer.a"

FILES_iotivity-plugins-staticdev = "\
                        ${includedir}/iotivity/plugins \
                        ${libdir}/libplugin_interface.a \
                        ${libdir}/libzigbee_wrapper.a \
                        ${libdir}/libtelegesis_wrapper.a"

FILES_iotivity-plugins-dbg = "\
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/plugins"

FILES_iotivity-resource = "\
                        ${libdir}/liboc.so \
                        ${libdir}/liboctbstack.so \
                        ${libdir}/liboc_logger.so \
                        ${libdir}/liboc_logger_core.so"

FILES_iotivity-resource-dbg = "\
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/resource \
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/extlibs \
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/examples \
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/out \
                        ${libdir}/.debug/liboc.so \
                        ${libdir}/.debug/liboctbstack.so \
                        ${libdir}/.debug/liboc_logger.so \
                        ${libdir}/.debug/liboc_logger_core.so"

FILES_iotivity-resource-samples-dbg = "\
                      ${IOTIVITY_BIN_DIR}/examples/resource/cpp/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/resource/c/SimpleClientServer/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/resource/c/secure/.debug"

FILES_iotivity-resource-samples = "\
                      ${IOTIVITY_BIN_DIR}/examples/resource"

FILES_iotivity-plugins-samples = "\
                      ${IOTIVITY_BIN_DIR}/examples/plugins"

FILES_iotivity-plugins-samples-dbg = "\
                      ${IOTIVITY_BIN_DIR}/examples/plugins/zigbee/.debug"

FILES_iotivity-service-dbg = "\
                        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/service \
                        ${libdir}/.debug"

FILES_iotivity-service-dev = "\
                        ${includedir}/iotivity/service"

FILES_iotivity-service = "\
                        ${libdir}/libBMISensorBundle.so \
                        ${libdir}/libDISensorBundle.so \
                        ${libdir}/librcs_server.so \
                        ${libdir}/librcs_common.so \
                        ${libdir}/librcs_container.so \
                        ${libdir}/libHueBundle.so \
			${libdir}/libTGMSDKLibrary.so \
                        ${libdir}/libESMediatorCSDK.so \
			${libdir}/libESMediatorRich.so \
                        ${libdir}/librcs_client.so \
                        ${libdir}/libTestBundle.so"

FILES_iotivity-service-staticdev = "\
                        ${libdir}/librcs_client.a \
                        ${libdir}/librcs_server.a \
                        ${libdir}/librcs_common.a \
                        ${libdir}/librcs_container.a \
                        ${libdir}/libTGMSDKLibrary.a \
                        ${libdir}/libresource_hosting.a \
                        ${libdir}/libresource_directory.a \
                        ${libdir}/libscene_manager.a"

FILES_iotivity-service-samples-dbg = "\
                      ${IOTIVITY_BIN_DIR}/examples/service/things-manager/groupaction/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/resource-encapsulation/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/resource-container/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/resource-hosting/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/resource-directory/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/easy-setup/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/things-manager/configuration/.debug \
                      ${IOTIVITY_BIN_DIR}/examples/service/scene-manager/.debug"

FILES_iotivity-service-samples = "\
                      ${IOTIVITY_BIN_DIR}/examples/service"

FILES_iotivity-tests-dbg = "\
                      ${libdir}/.debug/libgtest.so \
                      ${libdir}/.debug/libgtest_main.so \
                      ${IOTIVITY_BIN_DIR}/tests/service/easy-setup/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/resource/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/service/things-manager/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/service/resource-container/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/service/resource-encapsulation/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/service/scene-manager/.debug \
                      ${IOTIVITY_BIN_DIR}/tests/plugins/zigbee/.debug"

FILES_iotivity-tests = "\
                      ${libdir}/libgtest.so \
                      ${libdir}/libgtest_main.so \
                      ${IOTIVITY_BIN_DIR}/tests"

PACKAGES = "iotivity-tests-dbg iotivity-tests iotivity-plugins-dbg iotivity-plugins-staticdev iotivity-plugins-samples-dbg iotivity-plugins-samples iotivity-resource-dbg iotivity-resource iotivity-resource-dev iotivity-resource-thin-staticdev iotivity-resource-samples-dbg iotivity-resource-samples iotivity-service-dbg iotivity-service iotivity-service-dev iotivity-service-staticdev iotivity-service-samples-dbg iotivity-service-samples ${PN}-dev ${PN}"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "boost"
RRECOMMENDS_${PN} += "iotivity-resource iotivity-service"
RRECOMMENDS_${PN}-dev += "iotivity-resource-dev iotivity-resource-thin-staticdev iotivity-plugins-staticdev iotivity-service-dev iotivity-service-staticdev"
RDEPENDS_iotivity-resource += "glib-2.0"
RRECOMMENDS_iotivity-plugins-staticdev += "iotivity-resource-dev iotivity-resource-thin-staticdev iotivity-resource"
RRECOMMENDS_iotivity-resource-thin-staticdev += "iotivity-resource-dev"
RRECOMMENDS_iotivity-service-dev += "iotivity-service iotivity-service-staticdev iotivity-resource"
RDEPENDS_iotivity-plugins-samples += "iotivity-resource glib-2.0"
RDEPENDS_iotivity-resource-samples += "iotivity-resource glib-2.0"
RDEPENDS_iotivity-tests += "iotivity-resource iotivity-service glib-2.0"
RDEPENDS_iotivity-service-samples += "iotivity-service iotivity-resource glib-2.0"
RDEPENDS_iotivity-service += "iotivity-resource glib-2.0"
BBCLASSEXTEND = "native nativesdk"
