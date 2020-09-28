import 'package:equatable/equatable.dart';

class Currency extends Equatable {
  final String code;
  final String symbol;

  Currency(this.code, this.symbol);

  @override
  List<Object> get props => [code, symbol];


  Map<String, dynamic> toJson() {
    return {
      'code': code,
      'symbol': symbol
    };
  }

  static Currency fromJson(Map<String, dynamic> json) {
    return Currency(
        json['code'],
        json['symbol']
    );
  }
}
