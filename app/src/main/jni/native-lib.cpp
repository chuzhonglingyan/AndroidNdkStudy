//
// Created by Administrator on 2017/4/8.
//

#include <jni.h>
#include "logUtil.h"
#include "assert.h"
#include "openssl/evp.h"
#include "openssl/rand.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h> // sleep 的头文件


extern "C" {

jmethodID MID_AccessCache_show; //缓存方法id

JNIEXPORT void JNICALL
initIDs(JNIEnv *env, jclass cls)
{
    LOGD("initIDs called!!!\n");
    MID_AccessCache_show = env->GetStaticMethodID(cls,"show","(I)V");
}

JNIEXPORT jstring JNICALL
Java_com_yuntian_androidndkstudy_NativeUtil_staticJNIRegister(JNIEnv *env, jclass type,
                                                              jstring str_) {
    const char *str = env->GetStringUTFChars(str_, 0);

    // TODO
    LOGD("%s", str);
    env->ReleaseStringUTFChars(str_, str);

    return env->NewStringUTF("来自C++的问候");
}

//如果要实现动态注册就必须实现JNI_OnLoad方法，这个是JNI的一个入口函数，我们在Java层通过System.loadLibrary加载完动态库后，
// 紧接着就会去查找一个叫JNI_OnLoad的方法。如果有，就会调用它，而动态注册的工作就是在这里完成的。
// 在这里我们会去拿到JNI中一个很重要的结构体JNIEnv，env指向的就是这个结构体，通过env指针可以找到指定类名的类，
// 并且调用JNIEnv的RegisterNatives方法来完成注册native方法和JNI函数的对应关系。

JNIEXPORT jstring JNICALL
dynamicJNIRegister(JNIEnv *env, jclass type, jstring str_) {
    const char *str = env->GetStringUTFChars(str_, 0);

    // TODO
    LOGD("%s", str);
    env->ReleaseStringUTFChars(str_, str);

    return env->NewStringUTF("来自C++的问候");
}

//。这段代码的意思就是读取一个128位的随机数，然后转换为Java的byte数组
JNIEXPORT jbyteArray  JNICALL
getRandom(JNIEnv *env, jclass type) {
    unsigned char rand_str[128];
    //使用OpenSSL的方法
    RAND_seed(rand_str, 32);
    jbyteArray bytes = env->NewByteArray( 128);
    env->SetByteArrayRegion(bytes, 0, 128, (jbyte *) rand_str);
    return bytes;
}



//模拟压力传感其传递数据
int getPressure() {
    return rand() % 101;
}
//用于控制循环的开关
int monitor;


JNIEXPORT void  JNICALL
startMonitor(JNIEnv *env, jclass type) {
    monitor = 1;
    int pressure;
    while (monitor) {
        //本地方法获取传感器数据
        pressure = getPressure();
        LOGD("%d", pressure);
        //使用反射调用java方法刷新界面显示
        env->CallStaticVoidMethod(type, MID_AccessCache_show,pressure);
        sleep(1);
    }
}

JNIEXPORT void  JNICALL
stopMonitor(JNIEnv *env, jclass type) {
    //结束循环
    monitor = 0;
}

JNIEXPORT void JNICALL
encodeArray(JNIEnv * env, jobject obj, jintArray arr){
    //拿到整型数组的长度以及第0个元素的地址
    //jsize       (*GetArrayLength)(JNIEnv*, jarray);
    int length = env->GetArrayLength(arr);
    // jint*       (*GetIntArrayElements)(JNIEnv*, jintArray, jboolean*);
    int* arrp = env->GetIntArrayElements( arr, 0);
    int i;
    for(i = 0;i<length;i++){
        *(arrp + i) += 10; //将数组中的每个元素加10
    }
}

//参数映射表
static JNINativeMethod methods[] = {
        {"dynamicJNIRegister", "(Ljava/lang/String;)Ljava/lang/String;", (void *) dynamicJNIRegister},
        {"startMonitor", "()V", (void*)startMonitor},
        {"stopMonitor", "()V", (void*)stopMonitor},
        {"encodeArray", "([I)V", (void*)encodeArray},
        {"getRandom","()[B", (void*)getRandom},
        {"initIDs","()V", (void*)initIDs}
        //Java中native方法的名称，不用携带包的路径    方法签名 参数类型+返回类型  JNI层对应函数的函数指针，注意它是void*类型
        // 这里可以有很多其他映射函数
};

//解决这个问题我们可以自行将本地函数向VM进行登记，然后让VM自行调registerNativeMethods()函数。
//自定义函数，为某一个类注册本地方法，调运JNI注册方法

//更加有效率去找到C语言的函数　　
//可以在执行期间进行抽换，因为自定义的JNINativeMethod类型的methods[]数组是一个名称-函数指针对照表，
// 在程序执行时，可以多次调运registerNativeMethods()函数来更换本地函数指针，从而达到弹性抽换本地函数的效果。
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    //JNI函数，参见系列教程2
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

//自定义函数
static int registerNatives(JNIEnv *env) {
    const char *kClassName = "com/yuntian/androidndkstudy/NativeUtil";//指定要注册的类
    return registerNativeMethods(env, kClassName, methods, sizeof(methods) / sizeof(methods[0]));
}

//如果要实现动态注册就必须实现JNI_OnLoad方法，这个是JNI的一个入口函数，我们在Java层通过System.loadLibrary加载完动态库后
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    //OnLoad方法是没有JNIEnv参数的，需要通过vm获取。
    LOGD("customer---------------------------JNI_OnLoad-----into.\n");
    JNIEnv *env = NULL;
    jint result = -1;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    //动态注册，自定义函数
    if (!registerNatives(env)) {
        return -1;
    }

    return JNI_VERSION_1_6;
}


}
