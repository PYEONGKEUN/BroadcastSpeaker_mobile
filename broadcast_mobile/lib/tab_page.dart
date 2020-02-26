import 'package:flutter/material.dart';

class TabPage extends StatefulWidget {
  @override
  _TabPageState createState() => _TabPageState();
}

class _TabPageState extends State<TabPage> {
  // 선택된 네비게이션 아이템의 정보를 담을 변수
  int _selectedIndex = 0;

  //아이콘에 따라 body의 페이지가 바뀜
  List _pages =[
    Text('Page1'),
    Text('Page2'),
    Text('Page3')
  ];

  @override
  Widget build(BuildContext context) {
    // 이쁜 하얀색 바탕 머테리얼 디자인을 구현하기 위한 도화지
    return Scaffold(
      body: Center(child: _pages[_selectedIndex]),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        items: <BottomNavigationBarItem>[
          BottomNavigationBarItem(icon: Icon(Icons.home), title: Text('Home')),
          BottomNavigationBarItem(
              icon: Icon(Icons.search), title: Text('Search')),
          BottomNavigationBarItem(
              icon: Icon(Icons.account_circle), title: Text('Account'))


        ],
      ),
    );
  }

  void _onItemTapped(int value) {
    setState(() {
      _selectedIndex = value;
    });
  }
}
