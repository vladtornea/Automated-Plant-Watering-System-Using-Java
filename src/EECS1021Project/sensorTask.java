package EECS1021Project;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import java.io.IOException;
import java.util.TimerTask;
import org.firmata4j.ssd1306.SSD1306;

public class sensorTask extends TimerTask {
    //initialize variable for constructor
    private final SSD1306 Display;
    private final Pin mySensor;
    private final Pin pump;
    private final double Slope;
    private final double yIntercept;
    public static int MoistureState;
    public static int count = 0;   //initialize count

    //class constructor
    public sensorTask(SSD1306 Display, Pin mySensor, Pin pump, double Slope, double yIntercept){
        this.Display = Display;
        this.mySensor = mySensor;
        this.pump = pump;
        this.Slope = Slope;
        this.yIntercept = yIntercept;
    }

    @Override
    public void run() {
        //convert moisture sensor values to values from 0 to 100
        double moistureLevel = ((Slope * mySensor.getValue()) + yIntercept); //use slope and y-int for converting
        System.out.println("Moisture: " + moistureLevel);

        if (moistureLevel <= 40 && moistureLevel >= 0){
            MoistureState = 1; //Dry State
        } else if (moistureLevel >= 41 && moistureLevel <= 70) {
            MoistureState = 2; //Semi-wet sate
        } else if (moistureLevel >= 71 && moistureLevel <= 100) {
            MoistureState = 3; //Wet state
        }
        else{
            MoistureState = 4; //Error state for when moistureLevel < 0 or moistureLevel > 100
            System.out.println("Moisture Level out of Bound.");
        }

        //Conditions to turn on pump and water:
        if(MoistureState == 1){   //Dry State (if soil is dry turn on pump and water it)
            System.out.println("The soil is dry! Time to water!");
            Display.getCanvas().clear();              //Show message on OLED Display
            Display.getCanvas().setTextsize(2);      //Set text size
            Display.getCanvas().drawString(0,0, "Soil Dry!");
            Display.getCanvas().drawString(0,20, "Watering");
            Display.getCanvas().drawString(0,40, "Now!");
            Display.display();  //show the message
            System.out.println("Pump staring to water!");
            try {                              //use try - catch blocks
                pump.setValue(1);             //turn on pump and for 3 seconds
                Thread.sleep(3000);
                pump.setValue(0);            //turn off pump and wait 3 seconds
                System.out.println("Pump off!");
                Thread.sleep(3000);
            } catch (IOException e) {       //use catch in case of errors
                throw new RuntimeException(e);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            count++;                    //increase count by 1
        }
        else if (MoistureState == 2) {      //Semi-wet State
            Display.clear();    //clear the display
            Display.getCanvas().setTextsize(2);
            Display.getCanvas().drawString(0,0, "Soil Moist!");  //show Soil Moist! message on the display
            Display.display(); //update the display
            try {
                Thread.sleep(2000);  //wait 2 seconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (count >= 1){  //if count is >= 1 then turn on pump and water the soil
                System.out.println("Soil is semi-wet. Needs more water!");
                Display.getCanvas().setTextsize(2);
                Display.getCanvas().drawString(0,20,"Watering"); //show watering now message on the display
                Display.getCanvas().drawString(0,40,"Now!");
                Display.display();
                System.out.println("Pump Starting to Water!");
                try{
                    pump.setValue(1);           //turn on pump for 3 seconds
                    Thread.sleep(3000);
                    pump.setValue(0);           //turn off pump and wait 3 seconds
                    System.out.println("Pump off!");
                    Thread.sleep(3000);
                }
                catch (IOException | InterruptedException e){
                }
            }
        }
        else if (MoistureState == 3) { //Wet Soil
            System.out.println("The soil is wet! No need to water!");
            Display.clear();
            Display.getCanvas().setTextsize(2);
            Display.getCanvas().drawString(0,0,"Soil Wet!");  //Show soil wet message on display
            Display.display();
            count = 0;     //Reset count to prevent watering when state turns form wet to semi-wet
            try {
                Thread.sleep(2000);         //wait 2 seconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class ButtonListen implements IODeviceEventListener{         //Button listener //implement IODeviceEventListener mehtods
    //initialize variables
    private final SSD1306 OLEDdisplay;
    private final Pin pinbutton;
    private final Pin Pump;
    ButtonListen(Pin pinbutton, Pin Pump, SSD1306 OLEDdisplay){   //constructor
        this.Pump = Pump;
        this.pinbutton = pinbutton;
        this.OLEDdisplay = OLEDdisplay;
    }

    @Override
    public void onPinChange(IOEvent event) {  //How onPinChange responds to an event
        //compare
        if (event.getPin().getIndex() != pinbutton.getIndex()) {      //was the button pressed?
            return;                                                   //return if event is not form the button
        }
        if (Pump.getValue() == 1 && pinbutton.getValue() == 0) {      //if pump is on at the moment the butotn is pressed
            try {                        //use try and catch blocks
                Pump.setValue(0);        //turn off pump
                var PumpValue = this.Pump.getValue(); //assign value of Pump to PumpValue string
                System.out.println("Pump is " + PumpValue);
                this.OLEDdisplay.getCanvas().clear();          //clear the display
                this.OLEDdisplay.getCanvas().setTextsize(2);   //set text size of display
                this.OLEDdisplay.getCanvas().drawString(0, 0, "Pump is: " + PumpValue); //draw PumpValue to the display
                this.OLEDdisplay.display();      //update the display
                Thread.sleep(5000);        //wait 5 seconds
                sensorTask.MoistureState = 3;   //set moisture state to Wet state
                sensorTask.count = 0;           //reset count
            }
            catch (Exception ex) {     //use catch in case of errors
                ex.printStackTrace();
            }
        }
        if (Pump.getValue() == 0 && pinbutton.getValue() == 1) {    //if pump is not on at the moment the button was pressed
            System.out.println("Pump is already off!");
            OLEDdisplay.clear();
            OLEDdisplay.getCanvas().setTextsize(2);
            OLEDdisplay.getCanvas().drawString(0,0, "Pump Off");        //show pump off currently message on the display
            OLEDdisplay.getCanvas().drawString(0,25, "Currently!");
            OLEDdisplay.display();
            try {
                Thread.sleep(2000);                                         //wait 2 seconds
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override  //unused methods
    public void onMessageReceive(IOEvent ioEvent, String s) {   //empty
    }

    @Override
    public void onStart(IOEvent ioEvent) {                      //empty
    }

    @Override
    public void onStop(IOEvent ioEvent) {                       //empty
    }
}