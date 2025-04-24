package EECS1021Project;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.List;

// By: Vlad Tornea

public class ProjectMain {
    //initialize pins
    static final int sensor = 15;   //Pin of sensor
    static final int pump = 7;      //Pin of pump
    static final int buttonPin = 6; //Pin for button
    static final byte I2C0 = 0x3C;  //For OLED display
    //get slope and y-int for converting sensor values to values from 0-100
    static double maxMoisture = 100; //max user friendly moisture value
    static double minMoisture = 0;   //min user friendly moisture value
    static double SensorMaxMoisture = 506;  //max moisture sensor value
    static double SensorMinMoisture = 706;  //dry sensor value
    static double Slope = (maxMoisture - minMoisture)/(SensorMaxMoisture - SensorMinMoisture);  //slope
    static double yIntercept = maxMoisture - (Slope * SensorMaxMoisture);                       //y-intercept

    public static void main(String[] args)
            throws IOException, InterruptedException
    {
        //set up arduino
        var Arduino = new FirmataDevice("COM3");   //port COM3
        Arduino.start();
        Arduino.ensureInitializationIsDone();

        //set up moisture sensor
        var MoistureSensor = Arduino.getPin(sensor);    //Pin A1 is 15
        MoistureSensor.setMode(Pin.Mode.ANALOG);        //Analog mode for sensor

        //set up pump
        var Pump = Arduino.getPin(pump);                //Pin D7 is 7 for pump
        Pump.setMode(Pin.Mode.OUTPUT);                  //Output mode for pump

        //set up button
        Pin Button = Arduino.getPin(buttonPin);         //Pin D6 for button is 6
        Button.setMode(Pin.Mode.INPUT);                 //Mode is input for button
        //Initialize array list
        ArrayList<Double> SensorValuesforGraph = new ArrayList<Double>();   //array list to store moisture values for graphing

        //set up OLED Display
        I2CDevice i2c = Arduino.getI2CDevice((byte) I2C0);
        SSD1306 OLED = new SSD1306(i2c, SSD1306.Size.SSD1306_128_64);
        OLED.init();        //initialize OLED display

        int duration = 500;                 //duration for sensorTask
        Timer TimerObject = new Timer();

        //set up task to take moisture level readings and turn pump on/off when needed
        var Sensortask = new sensorTask(OLED, MoistureSensor, Pump, Slope, yIntercept);    //schedule sensor task
        new Timer().schedule(Sensortask, 0, duration);                              //schedule sensor task
        //set up task to graph moisture sensor values
        Timer time = new Timer();
        var graphingTask = new graphTask(MoistureSensor, SensorValuesforGraph, Slope, yIntercept);   //Schedule graph task
        new Timer().schedule(graphingTask, 0, 1000);                                    //Schedule graph task

        //set up listener to listen for action from button
        ButtonListen buttonListener = new ButtonListen(Button, Pump, OLED);
        //use addEventListener() method on buttonListener
        Arduino.addEventListener(buttonListener);
    }

                    //////////////////  THE FOLLOWING IS FOR UNIT TESTING ONLY!!!!!  //////////////////

    //set up for Unit Testing for converting moisture sensor values to user values from 0-100
    public static double MoistureLevelCalculation(double ValueFromSensor){      //method from graphTask
        return (Slope * ValueFromSensor) + yIntercept;      //slope and y-int used for conversion
    }
    //set up for unit testing moisture state detection:
    public static int MoistureStateDetection(double MoistureLevel){    //Logic in sensorTask responsible for moisture state detection
        if(0 <= MoistureLevel && MoistureLevel <= 40){  //Dry state
            return 1; //Dry soil state
        } else if (41 <= MoistureLevel && MoistureLevel <= 70) { //Semi-wet state
            return 2; //Semi-wet soil state
        } else if (71 <= MoistureLevel && MoistureLevel <= 100) {  //wet state
            return 3; //Wet soil state
        }
        else {
            return 4; // Moisture out of bound
        }
    }
}
