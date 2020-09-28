import 'package:countries_list_flutter/countries_list/bloc/countries.dart';
import 'package:countries_list_flutter/countries_list/model/country.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class CountriesListPage extends StatefulWidget {

  CountriesListPage({Key key}) : super(key: key);

  @override
  _CountriesListState createState() => _CountriesListState();
}

class _CountriesListState extends State<CountriesListPage> {

  CountriesBloc _countriesBloc;

  @override
  void initState() {
    _countriesBloc = CountriesBloc();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Countries List")),
      body: Container(
          child: _buildBody()
      ),
    );
  }

  Widget _buildBody() {
    return BlocProvider(
        create: (context) => _countriesBloc,
        child: BlocBuilder<CountriesBloc, CountriesState>(
            builder: (context, state) {
              if (state is Loading) {
                return _showLoadingProgress();
              } else if (state is InitialCountriesState) {
                _countriesBloc.add(FetchCountries());
                return _showLoadingProgress();
              } else if (state is Finished) {
                return _showStatus("Finished?");
              } else if (state is Error) {
                return _showStatus("Error, while loading countries: ${state.message}");
              } else if (state is CountriesLoaded) {
                return _showCountriesList(state.countries);
              }
            })
    );
  }

  Widget _showCountriesList(List<Country> countries) {
    return ListView.builder(
        itemCount: countries.length,
        itemBuilder: (context, index) {
          Country country = countries[index];
          return ListTile(
            title: Row(
              children: <Widget>[
                Text(country.name, style: TextStyle(fontWeight: FontWeight.bold)),
                Text(" (${country.topLevelDomain.join(", ")})"),
              ],
            ),
            subtitle: Row(
                children: <Widget>[
                  Text("Currencies: ", style: TextStyle(color: Colors.black38)),
                  Text(country.currencies.map((currency) => "${currency.code} (${currency.symbol})").join(", ")),
                ]
            ),
            trailing: Text(country.callingCodes.map((code) => "+$code").join(", "))
          );
        }
    );
  }

  Widget _showStatus(String status) {
    return Center(
      child: Padding(
          padding: EdgeInsets.all(16),
          child: Text(status, style: TextStyle(color: Colors.black87, fontSize: 14))
      ),
    );
  }

  Widget _showLoadingProgress() {
    return Center(
      child: SizedBox(
          width: 60,
          height: 60,
          child: CircularProgressIndicator()
      ),
    );
  }
}
