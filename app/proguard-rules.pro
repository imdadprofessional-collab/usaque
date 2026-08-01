# Keep Room entities and Firebase models used via reflection
-keep class com.cdlpermitprep.usa.data.local.entity.** { *; }
-keep class com.cdlpermitprep.usa.data.firebase.model.** { *; }
-keepattributes *Annotation*
-dontwarn kotlinx.coroutines.**
