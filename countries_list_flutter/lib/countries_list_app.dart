import 'package:countries_list_flutter/countries_list/countries_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class CountriesListApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '.',
      theme: ThemeData(
        fontFamily: 'Baloo',
        primarySwatch: Colors.green,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: CountriesListPage(),
    );
  }
}
