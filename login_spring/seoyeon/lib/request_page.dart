import 'dart:io';
import 'dart:convert';
import 'dart:isolate';

import 'dart:async';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';



import 'package:shared_preferences/shared_preferences.dart';

class RequestPage extends StatefulWidget {
  @override
  _RequestPageState createState() => _RequestPageState();
}



class _RequestPageState extends State<RequestPage> {

  Future<int> _number = -1;
  final String title = "제목";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
      ),
      body:FutureBuilder<int>(
        future: _number,
        builder: (context, snapshot) {
          if (snapshot.hasError) print(snapshot.error);

          return Text(
            "$_number",
            textAlign: TextAlign.center,
            style: TextStyle(
              color: Colors.black,
              fontSize: 40.0,
            ),
          );
        },
      ),
    );
  }




  Future<Object> request() async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    String place = "고객대기실";
    Map data = {
      'id': sharedPreferences.getString("id"),
      'place': place
    };
    print(data);
    var jsonResponse = null;
    var response = await http.post(
        "https://itbuddy.iptime.org/broadcastspeaker/request",
        headers: {"Content-Type": "application/json"}, body: jsonEncode(data));
    jsonResponse = json.decode(response.body);

    if (response.statusCode == 200 &&
        jsonResponse['number'].toString() == "true"){
      return jsonResponse;
    }
    else {
//      setState(() {
//        _isLoading = false;
//      });
      print("else --" + response.body);
    }

  }

  Future<File> _downloadFile(String url, String filename) async {
    http.Client client = new http.Client();
    var req = await client.get(Uri.parse(url));
    var bytes = req.bodyBytes;
    String dir = (await getApplicationDocumentsDirectory()).path;
    File file = new File('$dir/$filename');
    await file.writeAsBytes(bytes);
    return file;
  }





}

