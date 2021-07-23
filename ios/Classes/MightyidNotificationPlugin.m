#import "MightyidNotificationPlugin.h"
#if __has_include(<mightyid_notification/mightyid_notification-Swift.h>)
#import <mightyid_notification/mightyid_notification-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mightyid_notification-Swift.h"
#endif

@implementation MightyidNotificationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMightyidNotificationPlugin registerWithRegistrar:registrar];
}
@end
