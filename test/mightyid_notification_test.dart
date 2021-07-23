import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mightyid_notification/mightyid_notification.dart';

void main() {
  const MethodChannel channel = MethodChannel('mightyid_notification');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MightyidNotification.platformVersion, '42');
  });
}
