package CalculatorTDD;

import calculator.Calculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

    @Test
    void testAddition(){
        Calculator calculator = new Calculator();
        int res = calculator.add(5,5);
        Assertions.assertEquals(10,res);
    }

    @Test
    void testMultiplication(){
        Calculator calculator = new Calculator();
        int result = calculator.multiply(5,1);
        Assertions.assertEquals(5,result);
    }
}
