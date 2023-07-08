#include <jni.h>
#include <string>
#include <android/log.h>
#include <linux/seccomp.h>
#include <linux/filter.h>
#include <linux/audit.h>
#include <linux/signal.h>
#include <sys/ptrace.h>
#include <sys/syscall.h>
#include <unistd.h>

#include <supercall.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "AKP_TEST", __VA_ARGS__))

extern "C" JNIEXPORT jlong JNICALL
Java_com_bmax_akp_MainActivity_appSu(
        JNIEnv* env,
        jobject /* this */, jstring jskey) {
    pid_t pid = getpid();
    pid_t tid = gettid();
    pid_t ppid = getppid();
    const char *skey = env->GetStringUTFChars(jskey, 0);
//    LOGI("test app_su: skey: %s, pid: %d, tid: %d, ppid: %d\n", skey, pid, tid, ppid);
    long ret = -10;
    ret = sc_su(skey);
    env->ReleaseStringUTFChars(jskey, skey);
    return ret;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_bmax_akp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Just Test";
    return env->NewStringUTF(hello.c_str());
}

