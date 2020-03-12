import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:grouped_buttons/grouped_buttons.dart';
import 'package:seoyeon/request_page.dart';
import 'package:seoyeon/send_page.dart';

class SelectPage extends StatefulWidget {
  @override
  _SelectPageState createState() => _SelectPageState();
}

class _SelectPageState extends State<SelectPage> {

  String _label = "";
  int _index;
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
              colors: [Colors.blue, Colors.teal],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter),
        ),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,

            children: <Container>[
              radioSection(),
              buttonSection(),
            ],
          ),
        ),
      ),
    );
  }

  Container buttonSection() {

    return Container(
      width: 200.0,
      height: 40.0,
      padding: EdgeInsets.symmetric(horizontal: 15.0),
      margin: EdgeInsets.only(top: 15.0),
      child: MaterialButton(
        onPressed: ()=> goPage(_label),
         hoverColor: Colors.red,
        color: Colors.purple,
        child: Text("설정", style: TextStyle(color: Colors.white70)),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(5.0)),
      ),
    );



  }

  goPage(String env) async{
    print(env);
    if(env == null){
      _isLoading = false;
    }else{
      if(env == "송신"){
        Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(builder: (BuildContext context) => SendPage()), (Route<dynamic> route) => false);
      }else if(env == "수신"){
        Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(builder: (BuildContext context) => RequestPage()), (Route<dynamic> route) => false);
      }
      _isLoading = false;

    }

  }


  Container radioSection() {
    String _picked = "수신";
    return Container(
      child: RadioButtonGroup(
        orientation: GroupedButtonsOrientation.HORIZONTAL,
        margin: EdgeInsets.only(top: 200.0),
        //padding: EdgeInsets.all(100.0),
        activeColor: Colors.black54,
        labelStyle: TextStyle(
          color: Colors.white70,
        ),
        onSelected: (String selected) => setState((){
          _picked = selected;
          _label = selected;
          print(_picked+" : "+selected);
          //goPage(selected);
        }),
        //picked: _picked,
        labels: [
          "송신",
          "수신",
        ],

        onChange: (String label, int index) =>
        (){
          _label = label;
          _index = index;
        },

      ),
    );
  }
}