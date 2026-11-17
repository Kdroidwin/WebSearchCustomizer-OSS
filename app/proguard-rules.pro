# R8 / ProGuard rules for Web Search Customizer (OSS)

# Keep line numbers for readable crash logs
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# androidx.preference reads widget/layout attributes from XML; keep default rules are sufficient,
# but be explicit about preference classes referenced from XML resources.
-keep class androidx.preference.** { *; }
