cmake_minimum_required(VERSION 3.10)
project(javacltest)
if (APPLE)
   set(JAVA_HOME /Users/garyfrost/Library/Java/JavaVirtualMachines/openjdk-18.0.1.1/Contents/Home)
   set(JNI_SUBDIR darwin)
   set(OpenCL_FRAMEWORK "-framework OpenCL")
endif ()
if (UNIX AND NOT APPLE)
   set(JAVA_HOME /usr/lib/jvm/jdk-18)
   set(JNI_SUBDIR linux)
   set(OpenCL_FRAMEWORK "OpenCL")
endif ()

find_package(OpenCL REQUIRED)

set(CMAKE_CXX_STANDARD 14)
set(CXXFLAGS "-g")

include_directories(${OPENCL_INCLUDE_DIR})

add_custom_command(
   OUTPUT org_grfstuff_CLTest.h
   OUTPUT cltest.jar
   COMMAND ${JAVA_HOME}/bin/javac -h ${CMAKE_CURRENT_BINARY_DIR} ${CMAKE_CURRENT_SOURCE_DIR}/src/java/org/grfstuff/CLTest.java
   COMMAND ${JAVA_HOME}/bin/jar cvfM cltest.jar -C ${CMAKE_CURRENT_SOURCE_DIR}/src/java org/grfstuff/CLTest.class
   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/src/java/org/grfstuff/CLTest.java
   )

add_custom_target(javacltest
   COMMAND ${JAVA_HOME}/bin/java -Djava.library.path=${CMAKE_CURRENT_BINARY_DIR} -classpath ${CMAKE_CURRENT_BINARY_DIR}/cltest.jar org.grfstuff.CLTest
   DEPENDS cltest.jar cltestjni
   )

include_directories(${JAVA_HOME}/include ${JAVA_HOME}/include/${JNI_SUBDIR} ${CMAKE_CURRENT_BINARY_DIR})

add_library(cltestjni SHARED
   src/cpp/cltestjni.cpp
   org_grfstuff_CLTest.h
   )

target_link_libraries(cltestjni ${JNI_LIBRARIES} ${OpenCL_FRAMEWORK})
