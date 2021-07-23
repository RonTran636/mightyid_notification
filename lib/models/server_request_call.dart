// To parse this JSON data, do
//
//     final requestCall = requestCallFromJson(jsonString);

import 'dart:convert';

ServerRequestCall requestCallFromJson(String str) =>
    ServerRequestCall.fromJson(json.decode(str));

String requestCallToJson(ServerRequestCall data) => json.encode(data.toJson());

class ServerRequestCall {
  ServerRequestCall({
    this.messageType,
    this.callId,
    this.callerCustomerId,
    this.callerName,
    this.callerEmail,
    this.privacyMode,
    this.photoUrl,
    this.serverMeet,
    this.meetingType,
    this.meetingId,
    this.topicId,
    this.response,
    this.isPrivateCall,
    this.topicName,
    this.jwtToken
  });

  String? messageType;
  int? callId;
  dynamic callerCustomerId;
  String? callerName;
  String? callerEmail;
  String? privacyMode;
  String? photoUrl;
  String? serverMeet;
  String? meetingType;
  String? meetingId;
  String? topicId;
  bool? isPrivateCall;
  String? topicName;
  String? jwtToken;

  String? response;

  factory ServerRequestCall.fromRawJson(String str) =>
      ServerRequestCall.fromJson(json.decode(str));

  String toRawJson() => json.encode(toJson());

  factory ServerRequestCall.fromJson(Map<String, dynamic> json) =>
      ServerRequestCall(
        messageType: json["messageType"] == null ? null : json["messageType"],
        callId: json["callId"] == null ? null : int.parse(json["callId"]),
        callerCustomerId:
        json["fromCustomerId"] == null ? null : json["fromCustomerId"],
        callerName: json["callerName"] == null ? null : json["callerName"],
        callerEmail: json["callerEmail"] == null ? null : json["callerEmail"],
        privacyMode: json["privacy_mode"] == null ? null : json["privacy_mode"],
        photoUrl: json["photo_url"] == null ? null : json["photo_url"],
        serverMeet: json["server_meet"] == null ? null : json["server_meet"],
        meetingType: json["meetingType"] == null ? null : json["meetingType"],
        meetingId: json["meetingId"] == null ? null : json["meetingId"],
        topicId: json["topicId"] == null ? null : json["topicId"],
        response: json["response"] == null ? null : json["response"],
        isPrivateCall: json['isPrivateCall'] == null
            ? null
            : json['isPrivateCall'] == "true",
        topicName: json['topic_name'] == null ? null : json['topic_name'],
        jwtToken: json['jwt'] == null ? null : json['jwt'],
      );

  Map<String, dynamic> toJson() => {
    "messageType": messageType == null ? null : messageType,
    "callId": callId == null ? null : callId,
    "fromCustomerId": callerCustomerId == null ? null : callerCustomerId,
    "callerName": callerName == null ? null : callerName,
    "callerEmail": callerEmail == null ? null : callerEmail,
    "privacy_mode": privacyMode == null ? null : privacyMode,
    "photo_url": photoUrl == null ? null : photoUrl,
    "server_meet": serverMeet == null ? null : serverMeet,
    "meetingType": meetingType == null ? null : meetingType,
    "meetingId": meetingId == null ? null : meetingId,
    "topicId": topicId == null ? null : topicId,
    "response": response == null ? null : response,
    "isPrivateCall": isPrivateCall == null ? null : isPrivateCall,
    "topic_name": topicName == null ? null : topicName,
    "jwt": jwtToken == null ? null : jwtToken,
  };
}
