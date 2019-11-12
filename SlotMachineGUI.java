/*************************************************************************
*Name: Joe Maurer										Date: 7/23/2019
*Program Name: SlotMachineGUI
*Description: This program simulates a slot machine.
*Inputs: TextField txtBetAmount
*Outputs: Label lblSpinWinnings, Label lblTotalWinnings
*************************************************************************/
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.util.Random;

public class SlotMachineGUI extends Application{
    
    private TextField txtBetAmount; //TextField for bet amount from user
    private Label lblSpinWinnings;  //Label to display current spin's winnings
    private Label lblTotalWinnings; //Label to display total winnings
    private Button btnSpin;         //Button to allow user to spin
    private ImageView leftImage;    //ImageView to represent left spinner
    private ImageView centerImage;  //ImageView to represent center spinner
    private ImageView rightImage;   //ImageView to represent right spinner
    private Image[] fruits;         //Image Array of fruit images used
    private double totalWinnings;   //holds the user's total winnings
    private double winnings;        //holds the winnings for the current spin
    
    public static void main(String[] args){
        //launch application
        launch(args); 
        
        System.exit(0);
    }
    
    @Override
    public void start(Stage primaryStage){
        //////////////////////////
        // Spinner Image setup
        //////////////////////////
        //Initialize fruits array, change length to increase/decrease odds
        fruits = new Image[5]; 
        
        //Comment or uncomment fruit images depending on length of fruits[]
        fruits[0] = new Image("https://i.imgur.com/1Bs9pbR.png");
        fruits[1] = new Image("https://i.imgur.com/lYIpubZ.png");
        fruits[2] = new Image("https://i.imgur.com/DSIpezi.png");
        fruits[3] = new Image("https://i.imgur.com/Zbf1lhI.png");
        fruits[4] = new Image("https://i.imgur.com/LwpOiK5.png");
        //fruits[5] = new Image("https://i.imgur.com/0Pf6puN.png");
        
        /* fruits[0] = new Image("file:images/Apple.png");
        fruits[1] = new Image("file:images/Banana.png");
        fruits[2] = new Image("file:images/Cherries.png");
        fruits[3] = new Image("file:images/Grapes.png");
        fruits[4] = new Image("file:images/Lime.png"); 
        fruits[5] = new Image("file:images/Pear.png");
        fruits[6] = new Image("file:images/Lemon.png");
        fruits[7] = new Image("file:images/Orange.png");
        fruits[8] = new Image("file:images/Strawberry.png");
        fruits[9] = new Image("file:images/Watermelon.png");*/
        
        //background/border image
        Image bg = new Image("https://i.imgur.com/8cZd1Sf.png");
        //Image bg = new Image("file:images/_start.png");
        
        //initalize spinner image views
        leftImage = new ImageView();
        leftImage.setFitWidth(200);
        leftImage.setFitHeight(200);
        
        centerImage = new ImageView();
        centerImage.setFitWidth(200);
        centerImage.setFitHeight(200);
        
        rightImage = new ImageView();
        rightImage.setFitWidth(200);
        rightImage.setFitHeight(200);
        
        //use StackPane to overlay the ImageViews on top of the background image
        StackPane leftStack = new StackPane(new ImageView(bg),leftImage);
        StackPane centerStack = new StackPane(new ImageView(bg),centerImage);
        StackPane rightStack = new StackPane(new ImageView(bg),rightImage);
        
        //Combine StackPanes and align center to create spinner display
        HBox hbFruits = new HBox(10, leftStack, centerStack, rightStack);
        hbFruits.setAlignment(Pos.CENTER);
        
        //////////////////////////
        // User I/O Setup
        //////////////////////////
        //create Label and TextField input for user bet
        Label lblBetAmount = new Label("Amount inserted: $");
        txtBetAmount = new TextField();
        
        //group bet Label/TextField
        HBox hbBetAmount = new HBox(lblBetAmount, txtBetAmount);
        hbBetAmount.setAlignment(Pos.CENTER);
        
        //create Labels to output current spin winnings to user
        Label lblSpin = new Label("Amount Won This Spin: $");
        lblSpinWinnings = new Label(String.format("%.2f", 0.00));
        
        //combine spin winning labels
        HBox hbSpinWinnings = new HBox(lblSpin, lblSpinWinnings);
        
        //create Labels to output total winnings to user
        Label lblTotal = new Label("Total Amount Won: $");
        lblTotalWinnings = new Label(String.format("%.2f", 0.00));
        
        //combine total winning labels
        HBox hbTotalWinnings = new HBox(lblTotal,lblTotalWinnings);
        hbTotalWinnings.setAlignment(Pos.CENTER_RIGHT);
        
        //combine both sets of labels 
        VBox vbOutput = new VBox(hbSpinWinnings,hbTotalWinnings);
        
        //combine bet controls and label outputs & style
        HBox hbMiddle = new HBox(200,hbBetAmount,vbOutput);        
        hbMiddle.setPadding(new Insets(10,0,10,0));
        hbMiddle.setAlignment(Pos.CENTER);
        
        //////////////////////////
        // Button + Event Handler
        //////////////////////////
        btnSpin = new Button("Spin");
        btnSpin.setOnAction(event -> {
            btnSpin.setDisable(true); //disable button to prevent the user from flooding the program
            new Thread(new Spin()).start();
        });
        
        //////////////////////////
        // Container
        ////////////////////////// 
        //combine spinner/io/spin button & style
        VBox vbox = new VBox(hbFruits,hbMiddle,btnSpin);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(15,30,15,30));
        
        Scene scene = new Scene(vbox);
        
        //////////////////////////
        // Stage
        //////////////////////////
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Slot Machine");
        
        primaryStage.show();
    }
    
    //This method update's the UI after a spin completes
    public void updateUI(){
        lblSpinWinnings.setText(String.format("%.2f", winnings));
        lblTotalWinnings.setText(String.format("%.2f", totalWinnings));
        
        //re-enable spin button
        btnSpin.setDisable(false);
    }

    class Spin implements Runnable{
        
        //This class handles the logic of the slotmachine for when the user clicks the spin button
        @Override
        public void run(){
            
        winnings = 0;                                            //holds to current spin's winnings
        int[] numbers = roll();                                  //create numbers array and get 3 random numbers
        int[] matches = new int[fruits.length];                  //create array for determining if any numbers match
        double bet = Double.parseDouble(txtBetAmount.getText()); //get user's bet
        
        Image[] currentFruit = new Image[numbers.length];        //create Image array to hold fruit images for current spin
        
        //iterate through numbers array and increment corresponding match[] element
        for(int i = 0; i < numbers.length; i++){
            System.out.println("Number" + (i+1) + ": " + numbers[i]);
            matches[numbers[i]]++;
        }
        
        //iterate through matches array to find matches & determine winnings
        for(int i = 0; i < matches.length; i++){
            System.out.println("Fruit " + i + ": " + matches[i] + " matches");
            if(matches[i] == 2){
                winnings = bet * 2.0;
                break;
            } else if(matches[i] == 3){
                winnings = bet * 3.0;
                break;
            } 
        }
        
        //iterate through currentFruit array and set each element to the corresponding fruit image
        for(int i = 0; i < currentFruit.length; i++){
            currentFruit[i] = fruits[numbers[i]];
        }
        
        //calculate total winnings
        totalWinnings = totalWinnings + (winnings - bet);
        
        /* Enhancement - added slot machine "effect" */
        //create new Random object for showing random fruit images
        Random randomFruit = new Random();
        
        //loop through setting each spinner to a random fruit, delaying in between each time
        for(int i = 0; i < 15; i++){
            leftImage.setImage(fruits[randomFruit.nextInt(fruits.length)]);
            centerImage.setImage(fruits[randomFruit.nextInt(fruits.length)]);
            rightImage.setImage(fruits[randomFruit.nextInt(fruits.length)]);
            try {
                Thread.sleep(75);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        
        //set spinners to correct fruit for the current spin
        leftImage.setImage(currentFruit[0]);
        centerImage.setImage(currentFruit[1]);
        rightImage.setImage(currentFruit[2]);
        
        //update the UI after the thread finishes
        Platform.runLater(() -> updateUI());
        }
        
        //rolls numbers for the slot machine
        private int[] roll(){
            //create a new integer array to hold the numbers rolled & a new Random object
            int[] numbers = new int[3];
            Random randomNumbers = new Random();

            //for loop to iterate through the array 
            for(int i = 0; i < numbers.length ; i++){

                //assign a random number to each element
                numbers[i] = randomNumbers.nextInt(fruits.length);
            }

            //return the number array
            return numbers;
        }
    }    
}