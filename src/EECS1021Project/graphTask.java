package EECS1021Project;
import org.firmata4j.Pin;
import edu.princeton.cs.introcs.StdDraw;
import java.util.TimerTask;
import java.util.ArrayList;
import edu.princeton.cs.introcs.StdOut;

public class graphTask extends TimerTask {
    //initialize variables
    private final Pin MoistureSensor;
    private ArrayList<Double> ListofSensorValuesforGraph;  //for array list
    static double maxMoisture = 100; //max user friendly moisture value
    static double minMoisture = 0;   //min user friendly moisture value
    static double SensorMaxMoisture = 506;  //max moisture sensor value
    static double SensorMinMoisture = 706;  //most dry moisture sensor value
    static double Slope = (maxMoisture - minMoisture)/(SensorMaxMoisture - SensorMinMoisture);  //slope
    static double yIntercept = maxMoisture - (Slope * SensorMaxMoisture);                       //y-intercept

    //set up for converting moisture sensor values to user values from 0-100
    public static double MoistureLevelCalculation(double ValueFromSensor){        //Do unit Testing on this method
        return (Slope * ValueFromSensor) + yIntercept;                           //slope and y-int used for conversion
    }
    //constructor
    public graphTask(Pin sensor, ArrayList<Double> arrayList, double Slope, double yIntercept){
        this.MoistureSensor = sensor;
        this.ListofSensorValuesforGraph = arrayList;   //use and array list
        this.Slope = Slope;
        this.yIntercept = yIntercept;
    }
    @Override           //override run method
    public void run(){
        //convert moisture sensor values to values from 0 to 100:
        double moistureLevel = MoistureLevelCalculation(MoistureSensor.getValue());
        //does the array list contain less than 100 values?
        if (ListofSensorValuesforGraph.size() < 100){
            //Set up graph
            //Set the scale
            StdDraw.setXscale(-10, 100);
            StdDraw.setYscale(-3, 110);

            //set the pen parameters and colour
            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLUE);

            //draw axes
            StdDraw.line(0,0,0,110); //vertical line
            StdDraw.line(0,0,100,0); //horizontal line
            //draw labels
            StdDraw.text(50, -3, "[Time (Seconds)]");
            StdDraw.text(-8, 50, "[Moisture");
            StdDraw.text(-8, 46, "Level");
            StdDraw.text(-8, 42, "(%)]");
            StdDraw.text(50, 110, "Soil Moisture vs Time"); //title
            StdDraw.text(-5, 0, "0%");
            StdDraw.text(-6, 100, "100%");
            StdDraw.text(0, -3, "0");
            StdDraw.text(100, -3, "100");

            ListofSensorValuesforGraph.add(moistureLevel); //add converted values from moisture sensor to array list
            StdDraw.setPenColor(StdDraw.RED);               //use red for plotting the values
            StdDraw.text(ListofSensorValuesforGraph.size(),ListofSensorValuesforGraph.getLast(), "*"); //graph moisture sensor values,
            // value of the size, last array list value
            if (ListofSensorValuesforGraph.size() == 100){  //is array list size = 100?
                StdDraw.clear();                            //if yes then clear the graph
            }
        }
        else {
            ListofSensorValuesforGraph = new ArrayList<Double>(); //if size of array list reaches 100 then reset it
        }
    }
}