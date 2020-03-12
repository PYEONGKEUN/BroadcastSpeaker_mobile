import 'dart:io';
import 'dart:convert';
import 'dart:isolate';

import 'dart:async';
import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';

import 'package:shared_preferences/shared_preferences.dart';


class RequestPage extends StatefulWidget {
  @override
  _RequestPageState createState() => _RequestPageState();
}

class _RequestPageState extends State<RequestPage> {
  int _number = -1;
  final String title = "제목";
  bool _loading;
  AudioPlayer audioPlayer = AudioPlayer();
  Isolate _isolate;
  @override
  initState() {
    super.initState();

    // Add listeners to this class
    print("initState called");
//    Isolate.spawn(request, "msg");
    //_isolate = await Isolate.spawn(printPt,    );
  }

   printPt(String msg) async{
    int count = 0;
    while(true){
      print("$msg : $count");
      Future.delayed(const Duration(milliseconds: 1000));
    }
  }

  @override
  Widget build(BuildContext context) {
    print("build called");
    return Scaffold(

      appBar: AppBar(
        title: Text(title),
      ),
      body: Center(
        child: Text(
          _number == -1 ? "Testing" : "$_number",
          style: TextStyle(
            color: Colors.black,
            fontSize: 40.0,
          ),
        ),
      ),
    );
  }


  playLocal(file) async {
    int result = await audioPlayer.play(file, isLocal: true);
  }


  request(String msg) async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    String place = "고객대기실";
    Map data = {"id": "skvudrms54", "place": "$place"};
    print(data);
    var jsonResponse = null;
    while(true){
      var response = await http.post(
          "http://itbuddy.iptime.org/broadcastspeaker/request",
          headers: {"Content-Type": "application/json"},
          body: jsonEncode(data));
      print("response code : " + response.body);
      if (response.statusCode == 200 ) {
        jsonResponse = json.decode(response.body);
        print(jsonResponse['number'].toString());
        _number = int.parse(jsonResponse['number'].toString());
        String file = await _downloadFile("http://itbuddy.iptime.org/broadcastspeaker/stream/"+jsonResponse['fileName'].toString(),jsonResponse['fileName'].toString());
        print("filedownload finished location is : "+ file);
        playLocal(file);
        print("first audio play is end");
        playLocal(file);
        print("seconde audio play is end");
      } else {
        print("else --" + response.body);
      }

      Future.delayed(const Duration(milliseconds: 1000));
    }

  }

  Future<String> _downloadFile(String url, String filename) async {
    http.Client client = new http.Client();
    var req = await client.get(Uri.parse(url));
    var bytes = req.bodyBytes;
    String dir = (await getApplicationDocumentsDirectory()).path;
    File file = new File('$dir/$filename');
    await file.writeAsBytes(bytes);
    return '$dir/$filename';
  }
}
