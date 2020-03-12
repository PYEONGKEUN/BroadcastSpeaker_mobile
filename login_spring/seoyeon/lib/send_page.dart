

import 'package:flutter/material.dart';
import 'dart:convert';

import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;


class SendPage extends StatefulWidget {
  @override
  _SendPageState createState() => _SendPageState();
}

class _SendPageState extends State<SendPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: <Widget>[
          Row(children: <Widget>[
            Text("위치"),
            DropdownButtonSection(),
          ],
          ),
          textSection(),
          buttonSection(),
        ],
      ),
    );
  }

  String dropdownValue = '고객대기실';
  final List<String> nameList = <String>[
    "전체",
    "고객대기실",
    "직원휴게실",
    "프론트",
    "사무실",
    "휴게실",
    "정비실",
    "사장실",
    "창구",
  ];
  Widget DropdownButtonSection() {
    return Container(
      width: 100.0,
      child: DropdownButton<String>(
        value: dropdownValue,
        icon: Icon(Icons.arrow_downward),
        iconSize: 24,
        elevation: 16,
        style: TextStyle(
            color: Colors.deepPurple
        ),
        underline: Container(
          height: 2,
          color: Colors.deepPurpleAccent,
        ),
        onChanged: (String newValue) {
          setState(() {
            dropdownValue = newValue;
          });
        },
        items: nameList.map((String value) {
          return new DropdownMenuItem<String>(
            value: value,
            child: new Text(value),
          );
        }).toList(),
      ),
    );
  }

  //텍스트 필드
  final TextEditingController txtSectioncontroller = new TextEditingController();

  Container textSection() {
    return Container(
      width: 200.0,
      child: TextFormField(
        controller: txtSectioncontroller,
        keyboardType: TextInputType.numberWithOptions(
            signed: false, decimal: false),
      ),
    );
  }

  bool _isLoading = false;



  // 송신을 위한 hhttp
  postRequest(String place, String number) async {
    SharedPreferences sharedPreferences = await SharedPreferences
        .getInstance();
    Map data = {
      'place': place,
      'number': number
    };
    print(data);
    var jsonResponse = null;
    var response = await http.post(
        "https://itbuddy.iptime.org/broadcastspeaker/callcustomer",
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(data));
    print(response.body);
    jsonResponse = json.decode(response.body);

    if (response.statusCode == 200 &&
        jsonResponse['result'].toString() == "true") {
      if (jsonResponse != null) {
        setState(() {
          _isLoading = false;
        });
      }
    }
    else {
      setState(() {
        _isLoading = false;
      });
      print("else --" + response.body);
    }
  }
  //송신 버튼
  Container buttonSection() {
    return Container(
      width: 200.0,
      height: 40.0,
      padding: EdgeInsets.symmetric(horizontal: 15.0),
      margin: EdgeInsets.only(top: 15.0),
      child: RaisedButton(
        onPressed: txtSectioncontroller.text == "" ? null : () {
          setState(() {
            _isLoading = true;
          });
          //http 호출
          postRequest( dropdownValue,txtSectioncontroller.text);
        },
        elevation: 0.0,
        color: Colors.purple,
        child: Text("수리완료", style: TextStyle(color: Colors.white70)),
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(5.0)),
      ),
    );
  }
}
