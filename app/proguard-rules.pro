-keep class com.atom.vpn.** { *; }
-keep class com.atom.vpn.data.remote.model.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.atom.vpn.**$$serializer { *; }
-keepclassmembers class com.atom.vpn.** { *** Companion; }
-keepclasseswithmembers class com.atom.vpn.** { kotlinx.serialization.KSerializer serializer(...); }
