import 'package:countries_list_flutter/countries_list/model/currency.dart';
import 'package:equatable/equatable.dart';

class Country extends Equatable {
  final String name;
  final List<String> callingCodes;
  final List<String> topLevelDomain;
  final List<Currency> currencies;

  Country(this.name, this.callingCodes, this.topLevelDomain, this.currencies);

  @override
  List<Object> get props => [name, callingCodes, topLevelDomain, currencies];

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'callingCodes': callingCodes,
      'topLevelDomain': topLevelDomain,
      'currencies': List<dynamic>.from(currencies.map((currency) => currency.toJson())),
    };
  }

  static Country fromJson(Map<String, dynamic> json) {
    return Country(
        json['name'],
        List<String>.from(json['callingCodes']),
        List<String>.from(json['topLevelDomain']),
        List<Currency>.from(json['currencies'].map((x) => Currency.fromJson(x)))
    );
  }
}
