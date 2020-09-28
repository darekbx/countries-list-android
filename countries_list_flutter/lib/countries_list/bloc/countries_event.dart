import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

@immutable
abstract class CountriesEvent extends Equatable {
  const CountriesEvent([List props = const []]);

  @override
  List<Object> get props => [];
}

class FetchCountries extends CountriesEvent {
  FetchCountries() : super([]);
}
