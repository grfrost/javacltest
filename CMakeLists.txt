cmake_minimum_required(VERSION 3.10)
project(javacltest)
if (APPLE)
   if (EXISTS /Users/grfrost/java/jdk-19.jdk/Contents/Home)
      set(JAVA_HOME /Users/grfrost/java/jdk-19.jdk/Contents/Home)
   elseif (EXISTS /Users/grfrost/java/jdk-19.jdk/Contents/Home)
      set(JAVA_HOME /Users/grfrost/java/jdk-19.jdk/Contents/Home)
   elseif (EXISTS /Users/grfrost/java/jdk-18.0.1.1.jdk/Contents/Home)
      set(JAVA_HOME /Users/grfrost/java/jdk-18.0.1.1.jdk/Contents/Home)
   elseif(EXISTS /Users/garyfrost/java/jdk-18.0.1.1.jdk/Contents/Home)
       set(JAVA_HOME /Users/garyfrost/java/jdk-18.0.1.1.jdk/Contents/Home)
   else()
       message("--------------")
       message("NO JAVA_HOME")
   endif()
   set(JNI_SUBDIR darwin)
   set(OpenCL_FRAMEWORK "-framework OpenCL")
   set(LIBSUFFIX dylib)
   execute_process(
      COMMAND uname -m
      RESULT_VARIABLE result
      OUTPUT_VARIABLE OSX_ARCH
      OUTPUT_STRIP_TRAILING_WHITESPACE
   )
   if (OSX_ARCH STREQUAL "arm64")
      set(ARCH aarch64)
   else()
      set(ARCH x86_64)
   endif()
   set(PLAT osx)
endif ()
if (UNIX AND NOT APPLE)
   if (EXISTS /usr/lib/jvm/jdk-18)
      set(JAVA_HOME /usr/lib/jvm/jdk-18)
   else()
       message("--------------")
       message("NO JAVA_HOME")
   endif()
   set(JNI_SUBDIR linux)
   set(OpenCL_FRAMEWORK "OpenCL")
   set(LIBSUFFIX so)
   set(ARCH x86_64)
   set(PLAT linux)
endif ()

message("JAVA_HOME=${JAVA_HOME}")
message("JNI_SUBDIR=${JNI_SUBDIR}")
message("LIBSUFFIX=${LIBSUFFIX}")
message("ARCH=${ARCH}")
message("PLAT=${PLAT}")

find_package(OpenCL REQUIRED)
include_directories(${OPENCL_INCLUDE_DIR})

set(CMAKE_CXX_STANDARD 14)
set(CXXFLAGS "-g")
set(CLASSES "${CMAKE_CURRENT_BINARY_DIR}/classes")
set(BCEL_JAR ${CMAKE_CURRENT_SOURCE_DIR}/thirdparty/bcel-6.5.0.jar)
set(APARAPI_JAR ${CMAKE_CURRENT_BINARY_DIR}/aparapi.jar)
set(GENERATED_INCLUDE  ${CMAKE_CURRENT_BINARY_DIR}/include)

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
