package EECS1021Project;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UnitTestingClass {
    //initialize slope, y-int for testing conversion of sensor values to values from 0 to 100
    static double maxMoisture = 100;
    static double minMoisture = 0;
    static double SensorMaxMoisture = 506;
    static double SensorMinMoisture = 706;
    static double Slope = (maxMoisture - minMoisture)/(SensorMaxMoisture - SensorMinMoisture);  //calculate the slope
    static double yIntercept = maxMoisture - (Slope * SensorMaxMoisture);                       //calculate y-intercept

    @Test
    public void testSensorMoistureConversion(){         //test conversion of moisture sensor values to values from 0 to 100
        double proposedSensorValue = 606;               //test value
        double MoistureLevelExpected = Slope * 606 + yIntercept;   //expected value is 50

        double actualMoistureLevelValue = graphTask.MoistureLevelCalculation(proposedSensorValue);   //calculate the actual value
        //run the test
        Assert.assertEquals("Moisture Level conversion test failed.",MoistureLevelExpected, actualMoistureLevelValue, 0.0);
    }
    @Test
    public void DryStateTest(){                                //test dry state detection accuracy
        double proposedDryValue = 35;                         //simulated dry value
        int ExpectedState = 1; //Dry soil state              //expected value is 1 for dry state
        //run the test
        Assert.assertEquals("Dry state Test failed.",ExpectedState, ProjectMain.MoistureStateDetection(proposedDryValue));
    }
    @Test
    public void SemiWetStateTest(){                     //Test semi-wet state detection accuracy
        double proposedMoistureLevel = 65;             //simulated semi-wet value
        int ExpectedState = 2; //Semi-wet state       //expected value is 2 for semi-wet state
        //run the test
        Assert.assertEquals("Semi-wet State Test failed.",ExpectedState, ProjectMain.MoistureStateDetection(proposedMoistureLevel));
    }
    @Test
    public void TestWetState(){                     //Test Wet state detection accuracy
        double proposedWetValue = 88;              //simulated wet state value
        int ExpectedState = 3; //Wet state        //expected value is 3 for wet state
        Assert.assertEquals("Wet State Test failed.",ExpectedState, ProjectMain.MoistureStateDetection(proposedWetValue));
    }
    @Test
    public void outOfBoundTest(){            //Test out of scope value detection accuracy
        double proposedValue = 105;         //simulated out of scope value
        int ExpectedState = 4;             //Expected value is 4 for out of bound state
        Assert.assertEquals("Out of bound Test failed.",ExpectedState, ProjectMain.MoistureStateDetection(proposedValue));
    }
}
