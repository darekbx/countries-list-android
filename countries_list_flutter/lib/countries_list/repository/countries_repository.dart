import 'dart:async';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:io';
import 'package:countries_list_flutter/countries_list/model/country.dart';

class CountriesRepository {

  String _COUNTRIES_API = "https://restcountries.eu/rest/v2/all";

  Future<List<Country>> fetchCountries() async {
    var response = await http.get(_COUNTRIES_API);
    if (response.statusCode == HttpStatus.ok) {
      var jsonMap = json.decode(response.body);
      return (jsonMap as List).map((item) => Country.fromJson(item)).toList();
    } else {
      throw HttpException("Failed to fetch countries, HTTP code: ${response.statusCode}");
    }
  }
}
