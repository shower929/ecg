# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

add_library( # Specifies the name of the library.
             swm_ecg_algo

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/swm_ecg_algo_jni.cpp
             src/main/cpp/AlgorithmWare/SWM_ECG_ALGO.cpp
             src/main/cpp/AlgorithmWare/SWM_Filter.cpp
             src/main/cpp/SWM_algo_HRV.cpp
             src/main/cpp/mymath.cpp
            )
find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )

target_link_libraries(swm_ecg_algo ${log-lib})