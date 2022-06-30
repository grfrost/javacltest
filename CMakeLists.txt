cmake_minimum_required(VERSION 3.10)
project(javacltest)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)
include(UseJava)

find_package(OpenCL REQUIRED)

set(CMAKE_CXX_STANDARD 14)
set(CXXFLAGS "-g")

include_directories(${OPENCL_INCLUDE_DIR})

add_jar(cltestjar src/java/org/grfstuff/CLTest.java)
get_target_property(_jarFile cltestjar JAR_FILE)

add_custom_command(
        OUTPUT org_grfstuff_CLTest.h
        COMMAND ${Java_JAVAC_EXECUTABLE}
        -h ${CMAKE_CURRENT_BINARY_DIR} ../src/java/org/grfstuff/CLTest.java
        DEPENDS cltestjar
)

include_directories(${JNI_INCLUDE_DIRS} ${_jarFile} ${CMAKE_CURRENT_BINARY_DIR})


add_library(cltestjni SHARED
        src/cpp/cltestjni.cpp
        org_grfstuff_CLTest.h
        )

if (APPLE)
    target_link_libraries(cltestjni ${JNI_LIBRARIES} "-framework OpenCL")
endif ()
if (UNIX AND NOT APPLE)
    target_link_libraries(cltestjni ${JNI_LIBRARIES} OpenCL)
endif ()
