#realm
-keep class io.realm.kotlin.types.RealmObject
-keep class * implements io.realm.kotlin.types.RealmObject { *; }
-keep class io.realm.kotlin.internal.interop.NotificationCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.CoreErrorUtils {
    *;
}
-keep class io.realm.kotlin.internal.interop.JVMScheduler {
    *;
}
-keep class ** implements io.realm.kotlin.internal.RealmObjectCompanion {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.NetworkTransport {
    *;
}
-keep class io.realm.kotlin.internal.interop.sync.Response {
    *;
}
-keep class io.realm.kotlin.internal.interop.LongPointerWrapper {
    *;
}
-keep class io.realm.kotlin.internal.interop.** { *; }
-keep class io.realm.kotlin.internal.interop.SyncLogCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.SyncErrorCallback {
    *;
}
-keep class io.realm.kotlin.internal.interop.AppCallback {
    *;
}