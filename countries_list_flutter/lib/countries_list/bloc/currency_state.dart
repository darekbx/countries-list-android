import 'package:countries_list_flutter/countries_list/model/country.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

@immutable
abstract class CountriesState extends Equatable {
  CountriesState([List props = const []]);

  @override
  List<Object> get props => [];
}

class InitialCountriesState extends CountriesState { }

class Loading extends CountriesState { }

class Finished extends CountriesState { }

class CountriesLoaded extends CountriesState {
  final List<Country> countries;

  CountriesLoaded(this.countries) : super([countries]);
}

class Error extends CountriesState {
  final String message;

  Error(this.message) : super([message]);
}
