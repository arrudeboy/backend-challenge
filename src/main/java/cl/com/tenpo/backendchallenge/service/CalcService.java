package cl.com.tenpo.backendchallenge.service;

public sealed interface CalcService permits CalcWithPercentageService{

    Number calculate(Number num1, Number num2);
}
