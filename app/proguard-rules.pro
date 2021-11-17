# General reflection
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn javax.annotation.**
-keepclassmembers enum * { *; }

# General
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses
-keep public class * extends java.lang.Exception

# ProGuard issue
# https://sourceforge.net/p/proguard/bugs/573/
-optimizations !class/unboxing/enum

# These classes are used via kotlin reflection and the keep might not be required anymore once Proguard supports
# Kotlin reflection directly.
-keep class kotlin.Metadata

# class [META-INF/versions/9/module-info.class] unexpectedly contains class [module-info]
-dontwarn module-info

# ViewPager2 slow scroll hack
-keepclassmembers class androidx.viewpager2.widget.ViewPager2 {
    private <fields>;
}

# Fragments declared in XML
-keepnames class * extends androidx.fragment.app.Fragment
