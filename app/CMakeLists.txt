# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#定义变量ProjectRoot为工程根目录，用相对路径没通过编译，可能是路径写错，以后再试
#本次使用绝对路径作为参数
set(ProjectRoot ${CMAKE_SOURCE_DIR})

include_directories(${ProjectRoot}/include)


#动态方式加载 libcrypto是libxxxx.so的xxxx部分
add_library(libcrypto SHARED IMPORTED)

#设置要连接的so的相对路径，${ANDROID_ABI}表示so文件的ABI类型的路径,这一步引入了动态加入编译的so
set_target_properties(libcrypto  PROPERTIES IMPORTED_LOCATION ${ProjectRoot}/libs/${ANDROID_ABI}/libcrypto.so)




add_library( # Sets the name of the library.  生成函数的名字
             native-lib

             # Sets the library as a shared library. 声明生成的库为动态库，亦为STATIC
             SHARED

             # Provides a relative path to your source file(s). 源文件C的相对路径
             src/main/jni/native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable. 设置path变量的名字
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.  指定查询库的名字
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library. 目标库的名字
                       native-lib
                       libcrypto
                       # Links the target library to the log library  链接目标库到查询库
                       # included in the NDK.
                       #连接的库，根据log-lib变量对应liblog.so函数库
                       ${log-lib} )