package javafx_face;
                                                                                //Import e
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx_face.Utils;
import javafx.geometry.Rectangle2D;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.stage.Window;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.face.Face;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.face.FaceRecognizer;
import org.opencv.core.CvType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.scene.input.KeyEvent;



                                                                                //import e
                                                                                //creating class MXMLMainController
public class FXMLMainController implements Initializable {
                                                                                //Initializing iteams in FXMLMain.fxml 1s
    @FXML
    private AnchorPane anrpane_main,anrpane_menu,anrpane_display;
    @FXML
    private AnchorPane anrpane_list,anrpane_details;
    @FXML
    private JFXButton btn_min1,btn_max1,btn_close1;
    @FXML
    private JFXButton btn_add,btn_del,btn_save,btn_apply,btn_start;
    @FXML
    private JFXButton btn_mediaPlay1,btn_mediaPause1;
    @FXML
    private JFXToggleButton toggle_detect,toggle_reco;
    @FXML
    private MenuButton menubtn_config;
    @FXML
    private MenuItem menu1_Item1,menu1_Item2,menu1_Item3;
    @FXML
    private Label lable_config;
    @FXML
    private JFXTextField textField_IP,newUserName;
    @FXML
    private ImageView disp_imageview1;
    @FXML
    private MediaView disp_mediaview1;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private JFXProgressBar disp_progressbar1; 
    @FXML
    private JFXTextArea list_recoface;

    
    
                                                                                //Initializing iteams in FXMLMain.fxml 1e
    //index variable for saving face images
    public int index = 0;
    // Ip address text field input string
    String ipAddress=null;
    // New user Name for a training data
    public String newname;
    // Names of the people from the training set
    public HashMap<Integer, String> names = new HashMap<Integer, String>();
    // Random number of a training set
    public int random = (int )(Math.random() * 20 + 3);
    //
    private static Mat frame;
    private static MatOfRect frontalFaces;
      
    // variable initilazation for maximize button 
    private double lastX = 0.0d;
    private double lastY = 0.0d;
    private double lastWidth = 0.0d;
    private double lastHeight = 0.0d;
    
    //variable initilation for opencv predefined class s

    // a timer for acquiring the video stream                         
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    // face cascade classifier
    private CascadeClassifier faceCascade;
    private CascadeClassifier profilefaceCascade;
    private int absoluteFaceSize;

    //variable initilation for opencv predefined class e
  
    protected void init()                                                       //create object at the controller start for detection use 1s
    {
     textField_IP.setText("http://192.168.0.2:4747/video");
     lable_config.setText("WebCam");
     anrpane_list.setVisible(false);
     anrpane_details.setVisible(false);
     btn_start.setVisible(false);
     //set wallpaper
     setImageViewWallpaper();
     //  play hitech video on start
     try {
     setMediaViewPlay();
     } catch (MalformedURLException ex) {
     Logger.getLogger(FXMLMainController.class.getName()).log(Level.SEVERE, null, ex);
     }
     
    this.capture = new VideoCapture();
    this.faceCascade = new CascadeClassifier();
    this.profilefaceCascade = new CascadeClassifier();
    this.absoluteFaceSize = 0;
    
    //load the classifier(s)
    this.faceCascade.load("src/resources/haarcascades/haarcascade_frontalface_alt.xml");
    this.profilefaceCascade.load("src/resources/haarcascades/haarcascade_profileface.xml");
    //this.faceCascade.load("src/resources/lbpcascades/lbpcascade_frontalface.xml");
    
    trainModel();
     }                                                             
                                                                                //create object at the controller start detection use 1e                                                                              
        
    private void trainModel () {
     // Read the data from the training set
     File root = new File("resources/trainingset/combined/");           
        
      FilenameFilter imgFilter = new FilenameFilter() {
       public boolean accept(File dir, String name) {
         name = name.toLowerCase();
         return name.endsWith(".png");
        }
       };
            
    File[] imageFiles = root.listFiles(imgFilter);  
    List<Mat> images = new ArrayList<Mat>();
    System.out.println("THE NUMBER OF IMAGES READ IS: " + imageFiles.length);
    List<Integer> trainingLabels = new ArrayList<>();
    Mat labels = new Mat(imageFiles.length,1,CvType.CV_32SC1);
    int counter = 0;
     for (File image : imageFiles) {
      // Parse the training set folder files
      Mat img = Imgcodecs.imread(image.getAbsolutePath());
      // Change to Grayscale and equalize the histogram
      Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
      Imgproc.equalizeHist(img, img);
      // Extract label from the file name
      int label = Integer.parseInt(image.getName().split("\\-")[0]);
      // Extract name from the file name and add it to names HashMap
      String labnname = image.getName().split("\\_")[0];
      String name = labnname.split("\\-")[1];
      names.put(label, name);
      // Add training set images to images Mat
      images.add(img);
      labels.put(counter, 0, label);
      counter++;
        }
 
    //FaceRecognizer faceRecognizer = Face.createFisherFaceRecognizer(0,1500);
    FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
    //FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer(0,50);
    faceRecognizer.train(images, labels);
    faceRecognizer.save("traineddata");
    }
   

    @FXML                                                                       //Deciding Camera type from (lable_config)|called by (toggle_detect) s
    public void cameraType(int val)
    {
     String camtype =lable_config.getText();
     if (camtype=="WebCam")
      { System.out.println(camtype+"1");
        startCamera(""+0,0,val);
       }
     else 
      {if(camtype=="IP Camera") {
        ipAddress = textField_IP.getText();
         System.out.println(camtype+"2");
         System.out.println(ipAddress);
         startCamera(ipAddress,1,val);      
       } else{
        System.out.println(camtype+"3");
        startCamera(""+3,0,val);
         }}
    }
                                                                                //Deciding Camera type from (lable_config)|called by (toggle_detect) s


    @FXML                                                                       //(toggle_detect) face detection start camera 1s
    protected void startCamera(String a, int type, int val)
     { 
       
      if (!this.cameraActive)
       { System.err.println("1in");
        // start the video capture|first chick which type of camera for input
         if(type==0){
          //if input is 0 that is wecam
          this.capture.open(Integer.parseInt(a));
          System.out.println(a);
          System.err.println("2in");
         }
         else{
          //if input is string (ipaddress) that is IP Camera
          this.capture.open(a);
        }
	// is the video stream available?
	if (this.capture.isOpened())
	 {System.err.println("3in");
	  this.cameraActive = true;
	  // grab a frame every 33 ms (30 frames/sec)
	  Runnable frameGrabber = new Runnable()
          {		
	   @Override
	   public void run()
	   {
	    // effectively grab and process a single frame
               System.err.println("4in");
	    frame = grabFrame(val);
            System.err.println("5in");
	    // convert and show the frame
	    Image imageToShow = Utils.mat2Image(frame);
            System.err.println("6in");
	    updateImageView(disp_imageview1, imageToShow);
            System.err.println("7in");
            }
           };
				
	  this.timer = Executors.newSingleThreadScheduledExecutor();
	  this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
          // update the button content
	  this.toggle_detect.setText("ON");
	  }
	else
	 {
	 // log the error
	 System.err.println("Failed to open the camera connection...");
         stopAcquisition();
         this.toggle_detect.setText("OFF");
         toggle_detect.setSelected(false);
         System.err.println("8in");
         // Clear the parameters for new user data collection         
	 }
	}
      else
       {
	// the camera is not active at this point
	// update again the button content
           System.err.println("Camera is opend some were else");
	this.toggle_detect.setText("OFF");
        toggle_detect.setSelected(false);	
	// stop the timer
	this.stopAcquisition();
        System.err.println("9in");
        // Clear the parameters for new user data collection
        }
      }                                                                         
                                                                                //start camera 1e

    //Update the ImageView in the JavaFX main thread
    //@param view the {@link ImageView} to update
    //@param image the {@link Image} to show
    private void updateImageView(ImageView view, Image image)
    {Utils.onFXThread(view.imageProperty(), image);}  

    //Get a frame from the opened video stream (if any)
    //@return the {@link Image} to show
    private Mat grabFrame(int val)
    {
     Mat frame = new Mat();	
     // check if the capture is open
     if (this.capture.isOpened())
      {
       try
	{
	 // read the current frame
	 this.capture.read(frame);	 		
	 
         // if the frame is not empty, process it
	 if (!frame.empty())
	  {  //-----------------------------------------------------------------------------------------------------
           // face detection
	   // this.detectAndDisplay(frame);
             
           //face recog
           //this.recognitionAndDisaplay(frame);
          
           //save crooped faces
           //this.saveFace(frame);
	    prashant(frame,val);
          }		
	 }
       catch (Exception e)
	{
	 // log the (full) error
	 System.err.println("Exception during the image elaboration: " + e);
	 }
       }
		
     return frame;
     }

    
    //@param frame it looks for faces in this frame
    private void detectAndDisplay(Mat frame)                                    //method face detection and tracking 1s
    {
                                                                                //frontalface detection starts
     frontalFaces = new MatOfRect();
     Mat grayFrame = new Mat();
     
     // convert the frame in gray scale
     Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
     // equalize the frame histogram to improve the result
     Imgproc.equalizeHist(grayFrame, grayFrame);
		
     // compute minimum face size (20% of the frame height, in our case)
     if (this.absoluteFaceSize == 0)
     {
      int height = grayFrame.rows();
      if (Math.round(height * 0.2f) > 0)
       {
	   this.absoluteFaceSize = Math.round(height * 0.2f);
	   }
       }
      // detect faces
      this.faceCascade.detectMultiScale(grayFrame, frontalFaces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
      new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
	
      // each rectangle in faces is a face: draw them!
      Rect[] facesArray = frontalFaces.toArray();
      for (int i = 0; i < facesArray.length; i++){
      Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
      
      // Crop the detected faces
      Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
      Mat croppedImage = new Mat(frame, rectCrop);       

      // Change to gray scale
      Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);

      // Equalize histogram
      Imgproc.equalizeHist(croppedImage, croppedImage);

      // Resize the image to a default size
      Mat resizeImage = new Mat();
      Size size = new Size(250,250);
      Imgproc.resize(croppedImage, resizeImage, size);
      
      // put text on front profile face
      for (Rect rect : frontalFaces.toArray())
      Imgproc.putText(frame, "Frontal Face", new Point(rect.x,rect.y-5), 1, 0.7, new Scalar(0,255,0));
      }
           
                                                                                //frontalface detection ends			
      
                                                                                //profileface detection starts
     MatOfRect profileface = new MatOfRect();
     Mat grayFrame2 = new Mat();
    
     // convert the frame in gray scale
     Imgproc.cvtColor(frame, grayFrame2, Imgproc.COLOR_BGR2GRAY);
     // equalize the frame histogram to improve the result
     Imgproc.equalizeHist(grayFrame2, grayFrame2);
    
     // compute minimum face size (20% of the frame height, in our case)
     if (this.absoluteFaceSize == 0)
     {
      int height = grayFrame2.rows();
      if (Math.round(height * 0.2f) > 0)
       {
        this.absoluteFaceSize = Math.round(height * 0.2f);
        }
      }
      // detect profileface
      this.profilefaceCascade.detectMultiScale(grayFrame2, profileface, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
      new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
      
      // each rectangle in faces is a face: draw them!
      Rect[] facesArray1 = profileface.toArray();
      for (int i = 0; i < facesArray1.length; i++)
      Imgproc.rectangle(frame, facesArray1[i].tl(), facesArray1[i].br(), new Scalar(0, 255, 0), 3);
      
      // put text on profile face
      for (Rect rect : profileface.toArray())
      Imgproc.putText(frame, "Left Profile Face", new Point(rect.x,rect.y-5), 1, 0.7, new Scalar(0,255,0));
                                                                                //profileface detection ends                                                                            
     }
                                                                                //method face detection and tracking 1e
                                                                                //method face detection recognition and tracking s1

     public void recognitionAndDisaplay(Mat frame){
        
     frontalFaces = new MatOfRect();
     Mat grayFrame = new Mat();
     
     // convert the frame in gray scale
     Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
     // equalize the frame histogram to improve the result
     Imgproc.equalizeHist(grayFrame, grayFrame);
    
     // compute minimum face size (20% of the frame height, in our case)
     if (this.absoluteFaceSize == 0)
     {
      int height = grayFrame.rows();
      if (Math.round(height * 0.2f) > 0)
       {
      this.absoluteFaceSize = Math.round(height * 0.2f);
       }
     }
      // detect faces
      this.faceCascade.detectMultiScale(grayFrame, frontalFaces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
      new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
  
      // each rectangle in faces is a face: draw them!
      Rect[] facesArray = frontalFaces.toArray();
      for (int i = 0; i < facesArray.length; i++){
      Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
      
      // Crop the detected faces
      Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
      Mat croppedImage = new Mat(frame, rectCrop);       

      // Change to gray scale
      Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);

      // Equalize histogram
      Imgproc.equalizeHist(croppedImage, croppedImage);

      // Resize the image to a default size
      Mat resizeImage = new Mat();
      Size size = new Size(250,250);
      Imgproc.resize(croppedImage, resizeImage, size);
      
      //int prediction = faceRecognition(resizeImage);
      double[] returnedResults = faceRecognition(resizeImage);
      double prediction = returnedResults[0];
      double confidence = returnedResults[1];
      
      //System.out.println("PREDICTED LABEL IS: " + prediction);
      int label = (int) prediction;
      String name = "";
      if (names.containsKey(label)) {
        name = names.get(label);
      } else {
        name = "Unknown";
      }
         String update1 = list_recoface.getText();
      list_recoface.setText(update1+"   "+name);
      
      // Create the text we will annotate the box with:
      //String box_text = "Prediction = " + prediction + " Confidence = " + confidence;
      String box_text = "Prediction = " + name + " Confidence = " + confidence;
      // Calculate the position for annotated text (make sure we don't
      // put illegal values in there):
      double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
      double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
      // And now put it into the image:
      Imgproc.putText(frame, box_text, new Point(pos_x, pos_y), 
      Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));  
      }
        }
                                                                                         //method face detection recognition and tracking 1e
                                                                                         //method face detection recognition and tracking 2s

    private double[] faceRecognition(Mat currentFace)
    {    
    // predict the label
    int[] predLabel = new int[1];
    double[] confidence = new double[1];
    int result = -1;
            
    FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
    faceRecognizer.load("traineddata");
    faceRecognizer.predict(currentFace,predLabel,confidence);
    //result = faceRecognizer.predict_label(currentFace);
    result = predLabel[0];
          
    return new double[] {result,confidence[0]};
      } 
                                                                                       //method face detection recognition and tracking 2e

                                                                                      //method face detection,crop,save and tracking s
    public void saveFace(Mat frame)
    {  
    frontalFaces = new MatOfRect();
    Mat grayFrame = new Mat();
     
    // convert the frame in gray scale
    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
    // equalize the frame histogram to improve the result
    Imgproc.equalizeHist(grayFrame, grayFrame);
    
    // compute minimum face size (20% of the frame height, in our case)
    if (this.absoluteFaceSize == 0)
     {
      int height = grayFrame.rows();
      if (Math.round(height * 0.2f) > 0)
       {
    this.absoluteFaceSize = Math.round(height * 0.2f);
     }
    }
    // detect faces
    this.faceCascade.detectMultiScale(grayFrame, frontalFaces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
    new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
  
    // each rectangle in faces is a face: draw them!
    Rect[] facesArray = frontalFaces.toArray();
    for (int i = 0; i < facesArray.length; i++){
     Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
             
    //put text on front profile face
    for (Rect rect : frontalFaces.toArray())
    Imgproc.putText(frame, "Frontal Face", new Point(rect.x,rect.y-5), 1, 0.7, new Scalar(0,255,0));
     
    // Crop the detected faces
    Rect rectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
    Mat croppedImage = new Mat(frame, rectCrop);       

    // Change to gray scale
    Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);

    // Equalize histogram
    Imgproc.equalizeHist(croppedImage, croppedImage);

    // Resize the image to a default size
    Mat resizeImage = new Mat();
    Size size = new Size(250,250);
    Imgproc.resize(croppedImage, resizeImage, size);
      
    // check if 'New user' checkbox is selected
    // if yes start collecting training data (50 images is enough)
    if (!newname.isEmpty()) {
     if (index<50) {
      Imgcodecs.imwrite("resources/trainingset/combined/" +
      random + "-" + newname + "_" + (index++) + ".png", resizeImage);
      
     }}}}
                                                                                           //method face detection,crop,save and tracking 1e

                                                                                          //method face detection,crop,save and tracking 2s
    @FXML
    protected void newUserNameSubmitted() {
     if ((newUserName.getText() != null && !newUserName.getText().isEmpty())) {
      newname = newUserName.getText();
     //collectTrainingData(name);
     System.out.println("BUTTON HAS BEEN PRESSED");
     //newUserName.
     newUserName.clear();
      }
       }
                                                                                      //method face detection,crop,save and tracking 2e
     
//    @FXML
//    public void cameraerror()
//     {
//    System.out.println("came in");
//    String camtype =lable_config.getText();
//    System.out.println("came in");
//    stopAcquisition();
//    //String camtype =lable_config.getText();
//    if (camtype=="WebCam")
//     {    
//      FileInputStream inputerror1;
//      try {
//       inputerror1 = inputerror1 = new FileInputStream("src/resources/Images/webcam error.jpg");
//       Image background = new Image(inputerror1);
//       updateImageView(disp_imageview1, background);
//       } catch (FileNotFoundException ex)
//        { Logger.getLogger(FXMLMainController.class.getName()).log(Level.SEVERE, null, ex);} 
//         startCamera(""+0,0);
//         }
//       
//       else {if(camtype=="IP Camera")
//       {
//        FileInputStream inputerror2;
//        try {
//         inputerror2 = inputerror2 = new FileInputStream("src/resources/Images/ipcam error.jpg");
//         Image background = new Image(inputerror2);
//         updateImageView(disp_imageview1, background);
//         } catch (FileNotFoundException ex)
//         {Logger.getLogger(FXMLMainController.class.getName()).log(Level.SEVERE, null, ex);} 
//       
//        ipAddress = textField_IP.getText();
//        startCamera(ipAddress,1);
//        } else{
//         startCamera(""+3,0);
//          }}
//       }
    
                                                                                // Media View Play/Pause/Stop Functionality s
    @FXML
    public  void setMediaViewPlay() throws MalformedURLException
    {
     if (mediaPlayer != null) {
     disp_mediaview1.setVisible(true);
     mediaPlayer.play();
      }
     else {
     disp_mediaview1.setVisible(true);
     Media media = new Media(Paths.get("src/resources/Videos/display.mp4").toUri().toString());
     mediaPlayer = new MediaPlayer(media);
     mediaPlayer.setAutoPlay(true);
     disp_mediaview1.setMediaPlayer(mediaPlayer);
        }
    }
    
    @FXML
    public  void setMediaViewPause()
    {
     if (mediaPlayer != null) {
      mediaPlayer.pause();
      }
        //else{System.out.println("media player is null");}
    }
    
    @FXML
    public  void setMediaViewStop(){
     disp_mediaview1.setVisible(false);
     if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer = null;
        }
        //else{System.out.println("media player is null");}
    }
                                                                                // Media View Play/Pause/Stop Functionality e

    @FXML                                                                       // Image View wallpaper function s
    public void setImageViewWallpaper()
    {
     FileInputStream input;
      try {
       input = input = new FileInputStream("src/resources/Images/display.png");
       Image background = new Image(input);
       //disp_imageview1.setImage(background);
       updateImageView(disp_imageview1, background);
      } catch (FileNotFoundException ex) {
        Logger.getLogger(FXMLMainController.class.getName()).log(Level.SEVERE, null, ex);
       } 
     }
                                                                                //Image View wallpaper function e


    //Stop the acquisition from the camera and release all the resources        //stop acquisition s
    private void stopAcquisition()
    {
    this.cameraActive = false;
    if (this.timer!=null && !this.timer.isShutdown())
     {
      try {
    // stop the timer
    this.timer.shutdown();
    this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
    index = 0;
    newname = "";
    }
     catch (InterruptedException e){
    // log any exception
    System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
      }
    }   
     if (this.capture.isOpened())
      {
       // release the camera
       this.capture.release();
       setImageViewWallpaper();
       index = 0;
       newname = "";
       }
    }
                                                                                //stop acquisition e
            
    //On application close & stop acquisition 
    public void setClosed()
    {
     this.stopAcquisition();
     System.exit(0);
     }




                                                                               
    @FXML                                                                       //Creating method handleButtonAction for MouseEvent 1s
    private void handleButtonAction(MouseEvent event)
    {   
    if(event.getSource()== btn_close1)                                          //functionality for btn_close1 close application 
     {
       setClosed();
      } else { if(event.getSource()== btn_max1)                                 //functionality for btn_max1 maximize application 1s
                {
                  Node n = (Node)event.getSource();
                  Window w = n.getScene().getWindow();

                  double currentX = w.getX();
                  double currentY = w.getY();
                  double currentWidth = w.getWidth();
                  double currentHeight = w.getHeight();

                  Screen screen = Screen.getPrimary();
                  Rectangle2D bounds = screen.getVisualBounds();

                  if( currentX != bounds.getMinX() &&
                  currentY != bounds.getMinY() &&
                  currentWidth != bounds.getWidth() &&
                  currentHeight != bounds.getHeight() )
                   {
                       
                    w.setX(bounds.getMinX());
                    w.setY(bounds.getMinY());
                    w.setWidth(bounds.getWidth());
                    w.setHeight(bounds.getHeight());
                    // save old dimensions
                    lastX = currentX;
                    lastY = currentY;
                    lastWidth = currentWidth;
                    lastHeight = currentHeight;
                    } else {
                             // de-maximize the window (not same as minimize)
                             w.setX(lastX);
                             w.setY(lastY);
                             w.setWidth(lastWidth);
                             w.setHeight(lastHeight);
                             }
                  
                  event.consume(); // don't bubble up to title bar 
                                                                                //functionality for btn_max1 maximize application 1e
                 } else { if (event.getSource()==btn_min1)                      //functionality for btn_min1 minimize application 1s
                           {             
                            Stage stage = (Stage)((JFXButton)event.getSource()).getScene().getWindow();
                            // is stage minimizable into task bar. (true | false)
                            stage.setIconified(true);        
                            }
                        }                                                       //functionality btn_min1 maximize application 1e
             }
    }
                                                                                //Closing method handleButtonAction for MouseEvent1e
                                                                                
    @Override                                                                   //Methode initialize 1s
    public void initialize(URL args0, ResourceBundle args1)                     
    {                                                                           //Buttons & toggle buttons listner
        
     //Menu Buuton (menubtn_config) Item listeners s   
     menu1_Item1.setOnAction(event -> {
     System.out.println("Option 1 selected via Lambda");
     lable_config.setText("WebCam");
     System.out.println(lable_config.getText());
     textField_IP.setVisible(false);
      });
     menu1_Item2.setOnAction(event -> {
     System.out.println("Option 2 selected via Lambda");
     lable_config.setText("IP Camera");
     textField_IP.setVisible(true);
      });
     menu1_Item3.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
       System.out.println("Option 3 selected");
       lable_config.setText("USB Cam");
       textField_IP.setVisible(false);
       }});
     //Menu Button (menubtn_config) Item listeners e
     
     //Add Face Button (btn_add) listner s
     btn_add.setOnMouseClicked((MouseEvent e) -> {
     toggle_reco.setSelected(false);
     toggle_detect.setSelected(false);
     btn_start.setVisible(true);
     btn_start.setDisable(false);
     anrpane_details.setVisible(true);
     btn_save.setDisable(true);
     cameraType(3);
     
  
//     if (toggle_reco.isSelected()==true)
//     {System.out.println("stop face recognination before adding face to data base");}
//     else{System.out.println("Adding face to database");
//     }
     });
     //Add Face Button (btn_add) listner e
     
     
     //Delete Face Button (btn_del) listner s
     btn_del.setOnMouseClicked((MouseEvent e) -> {
     if (toggle_reco.isSelected()==true){
      System.out.println("stop face recognination before deleting face from data base");
     }
     else{
     System.out.println("deleting face from database");
     }
     });
     //Delete Face Button (btn_del) listner e
     
     //Save Details Button (btn_save) listner s
     btn_save.setOnMouseClicked((MouseEvent e) -> {
     System.out.println("Clicked! save");
     btn_start.setDisable(false);
     btn_save.setDisable(true);
     newUserNameSubmitted();
     
     });
     //Save Details Button (btn_save) listner e
     
     //Apply Details Button (btn_apply) listner s
     btn_apply.setOnMouseClicked((MouseEvent e) -> {
     System.out.println("Clicked! apply");
     anrpane_details.setVisible(false);
     btn_start.setVisible(false);
     toggle_reco.setSelected(true);
     });
     //Apply Details Button (btn_apply) listner e
     
     //start clicking Button (btn_start) listner s
     btn_start.setOnMouseClicked((MouseEvent e) -> {
     System.out.println("Clicked! start");
     index=0;
     newname="";
     newUserName.clear();
     btn_start.setDisable(true);
     
     });
     //start clicking Button (btn_start) listner e
     
     //ToggleButton(toggle_detect) listner face detection 1s
     toggle_detect.selectedProperty().addListener(new ChangeListener < Boolean >(){
      @Override
      public void changed (ObservableValue<? extends Boolean > arg0, Boolean arg1, Boolean arg2)
      { if (toggle_detect.isSelected()==true)
       {
        //toggle_detect.setText("ON");
        cameraType(1);
        disp_progressbar1.setVisible(true);
        setMediaViewStop();
        } else {
                toggle_detect.setSelected(false);
                toggle_detect.setText("OFF");
                toggle_reco.setSelected(false);
                stopAcquisition();
                disp_progressbar1.setVisible(false);
                setImageViewWallpaper();
                }}
     });
     //Toggle Button(toggle_detect) listner face detection 1e
     
     //ToogleButton(toggle_reco) listner face recog.,hiding(arpane_list) 1s
     toggle_reco.selectedProperty().addListener(new ChangeListener < Boolean >(){   
      @Override
      public void changed (ObservableValue<? extends Boolean > arg0, Boolean arg1, Boolean arg2)
      { if (toggle_reco.isSelected()==true)
        {
          
         toggle_reco.setText("ON");
        toggle_detect.setSelected(false);
         anrpane_list.setVisible(true);
         menubtn_config.setDisable(true);
         trainModel();
         cameraType(2);
        disp_progressbar1.setVisible(true);
        setMediaViewStop();
        
         } else {
                 toggle_reco.setText("OFF");
                 anrpane_list.setVisible(false);
                 menubtn_config.setDisable(false);
                 stopAcquisition();
                disp_progressbar1.setVisible(false);
                setImageViewWallpaper();
                 }}
      });
     //ToogleButton(toggle_reco) listner face recog.,hiding (arpane_list) 1e
     
     
     
    // Media View Play Pause button (btn_mediaPlay1,btn_mediaPause1) listner s                                
    btn_mediaPlay1.setOnMouseClicked((MouseEvent e) -> {
    System.out.println("Clicked! Play");
     try {
      //setMediaViewPlay();
      setMediaViewPlay();
     } catch (MalformedURLException ex) {
      Logger.getLogger(FXMLMainController.class.getName()).log(Level.SEVERE, null, ex);
      }     
     });
        
    btn_mediaPause1.setOnMouseClicked((MouseEvent e) -> {
    System.out.println("Clicked! Paused");
    setMediaViewPause();
     });
     // Media View Play Pause button (btn_mediaPlay1,btn_mediaPause1) listner e
     
                                                                                //buttons & toggle buttons listner                                                         
    }
                                                                                //closing Methode initialize 1e

    private void prashant(Mat frame,int val) {
       if(val==1)
      this.detectAndDisplay(frame);
       if (val==2)
      recognitionAndDisaplay(frame);
       if (val==3)
        this.saveFace(frame);
    }
    
    @FXML
    protected void anu (){
        if ((newUserName.getText() != null && !newUserName.getText().isEmpty())) {
          btn_save.setDisable(false);   
      }else{
            btn_save.setDisable(true);
        }
    }
}
                                                                                //closing class MXMLMainController

    //System.out.println(System.getProperty("user.dir"));
    //System.out.println(faceCascade);