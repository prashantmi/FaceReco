package javafx_face;
                                                                                //import s
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.opencv.core.Core;
                                                                                //import e
                                                                                //creating class JavaFX_Face s
public class JavaFX_Face extends Application
{
    private double x0ffset = 0;                                                 //variable initilization for draging app. window
    private double y0ffset = 0;
    
    @Override                                                                   //creating method for application stage 1s
    public void start(Stage stage) throws Exception
    {
      // Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));
     FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));//
     Parent root =loader.load();//

     stage.initStyle(StageStyle.TRANSPARENT);      
                                                                                
     root.setOnMousePressed(new EventHandler<MouseEvent>()                      //functionality mouse click draging app. window 1s
     {
      @Override
      public void handle(MouseEvent event)
      {
       x0ffset = event.getSceneX();
       y0ffset = event.getSceneY();
       }
      });
        
     root.setOnMouseDragged(new EventHandler<MouseEvent>()
     {
      @Override
      public void handle(MouseEvent event)
      {
      stage.setX(event.getScreenX() - x0ffset);
      stage.setY(event.getScreenY() - y0ffset);
       }       
     });                                                                        //functionality mouse click draging app. window 1e
     
     Scene scene = new Scene(root);
     scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
     stage.setScene(scene);
     stage.show();     
     // init the controller
     FXMLMainController controller = loader.getController();
     controller.init();
     }                                                                          //closing method for application stage 1e
    
    public static void main(String[] args) {       
        //main method
        // load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
       System.loadLibrary("opencv_ffmpeg341_64");
        launch(args);
    }  
    
}                                                                               //closing class JavaFX_Face e
