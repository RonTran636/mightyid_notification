
import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:mightyid_notification/definitions.dart';
import 'package:mightyid_notification/models/server_request_call.dart';

class MightyidNotification {

  final StreamController<ServerRequestCall>
  _actionSubject = StreamController<ServerRequestCall>();

  /// Stream to capture all actions (tap) over notifications
  Stream<ServerRequestCall> get actionStream {
    return _actionSubject.stream;
  }

  dispose(){
    _actionSubject.close();
  }

  static const MethodChannel _channel =
      const MethodChannel('mightyid_notification');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> showCallNotification({
    required String callId,
    required String meetingType,
    required String callerName,
    required ServerRequestCall payload,
    String? callerPhotoUrl
  }) async {
    final bool _notificationShown = await _channel.invokeMethod('showCallNotification',{
      "call_id": callId,
      "meeting_type": meetingType,
      "caller_name": callerName,
      "caller_photo_url": callerName,
      "payload": payload.toRawJson()
    });
    return _notificationShown;
  }

  void initMessagesHandler() {
    _channel.setMethodCallHandler(_handleMethod);
  }

  Future<dynamic> _handleMethod(MethodCall call) {
    final payload = call.arguments as String;
    final _dataReceive = ServerRequestCall.fromRawJson(payload);
    switch (call.method) {
      case CALL_STATUS_ACCEPTED:
        _actionSubject.add(_dataReceive);
        break;
      case CALL_STATUS_REJECTED:
        _actionSubject.add(_dataReceive);
        break;
      default:
        throw UnsupportedError("Unrecognized JSON message");
    }

    return Future.value();
  }
}
