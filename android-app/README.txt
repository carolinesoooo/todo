# Todo App - Android 项目
#
# 打包方式（三选一）：
#
# 1. Android Studio（推荐）
#    - 安装 Android Studio
#    - 打开 android-app 目录
#    - 点击 Build → Build APK
#    - APK 在 app/build/outputs/apk/debug/ 下
#
# 2. 在线编译（不需要安装任何东西）
#    把 android-app 目录打包成 zip
#    上传到 https://appcenter.ms 或 https://build.appcircle.io 在线编译
#
# 3. 命令行（需安装 JDK 17 + Android SDK）
#    cd android-app
#    ./gradlew assembleDebug
#    APK 在 app/build/outputs/apk/debug/app-debug.apk
#
# 注意：本 APK 是纯离线版，所有 HTML/CSS/JS 打包在 APK 内
#       不需要网络即可使用
