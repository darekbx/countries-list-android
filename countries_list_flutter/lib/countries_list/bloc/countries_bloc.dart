import 'package:bloc/bloc.dart';
import 'package:countries_list_flutter/countries_list/bloc/countries_event.dart';
import 'package:countries_list_flutter/countries_list/bloc/currency_state.dart';
import 'package:countries_list_flutter/countries_list/model/country.dart';
import 'package:countries_list_flutter/countries_list/repository/countries_repository.dart';

class CountriesBloc extends Bloc<CountriesEvent, CountriesState> {

  final CountriesRepository _countriesRepository = CountriesRepository();

  @override
  CountriesState get initialState => InitialCountriesState();

  @override
  Stream<CountriesState> mapEventToState(CountriesEvent event) async* {
    if (event is FetchCountries) {
      yield Loading();
      yield* _mapFetchEntriesToState();
    }
  }

  Stream<CountriesState> _mapFetchEntriesToState() async* {
    try {
      List<Country> countries = await _countriesRepository.fetchCountries();
      yield CountriesLoaded(countries);
    } catch (e) {
      yield Error("$e");
    }
  }
}
