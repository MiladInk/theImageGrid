package View;


import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;

import static sun.plugin.javascript.navig.JSType.Image;

/**
 * Created by milad on 7/18/16.
 */
public class ImageEditorView extends Application {
    private Button imageFileOpenerBtn;//the button that let the user choose the image he/she wants
    private Button saveImageBtn;//the button that let the user save the image that is showing
    private Spinner cellSizeTxt;//it will store the size of the grid that we are making in the photo
    private CheckBox tileButton;//it will tile the picture that is showing
    private CheckBox gridButton;
    private ImageView imageView;
    private Image image;//the image that is under edit
    private HBox pictureRegion;
    private Canvas canvas;//the canvas for the image to edit it
    private Canvas canvasSmall;//this canvas is being shown
    @Override
    public void start(Stage primaryStage) throws Exception {
        //---making the controls
        makeControls();//it will initialize the controls
        setCanvas(canvasSmall,canvas);
        attachCode(primaryStage);

        //tilize(canvas, 10);
        //---making the grid pane that is going to be shown
        GridPane root = new GridPane();


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pictureRegion);
        //root pane config
        root.setAlignment(Pos.TOP_CENTER);
        root.setHgap(10);
        root.setVgap(20);
        root.setPadding(new Insets(10,10,10,10));
        //add the controls to the grid
        root.add(imageFileOpenerBtn,0,0);
        root.add(cellSizeTxt,1,0);
        root.add(tileButton,2,0);
        root.add(gridButton,3,0);
        root.add(saveImageBtn,4,0);
        root.add(scrollPane,0,1,5,1);
        //---show the pane
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Image Gridder");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void attachCode(Stage stage){
        saveImageBtn.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               FileChooser fc = new FileChooser();
               fc.setInitialDirectory(new File("src"));
               fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
               fc.setTitle("Save Map");
               File file = fc.showSaveDialog(stage);
               if(file != null){
                   try {
                       ImageIO.write(snapCanvas(canvas),"png",file);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }
       });
        imageFileOpenerBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setInitialDirectory(new File("src"));
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
                fc.setTitle("Open");
                File file = fc.showOpenDialog(stage);
                if(file != null) {
                    setImage(readImage(file.getPath()));
                    handleIt();
                }

            }
        });
        cellSizeTxt.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleIt();
        });
        tileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               handleIt();
            }
        });

        gridButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleIt();
            }
        });

    }

    private void makeControls(){
        Font font = new Font("Comic Sans",20);
        imageFileOpenerBtn = new Button("Open image");
        imageFileOpenerBtn.setFont(font);
        imageFileOpenerBtn.setAlignment(Pos.CENTER);

        saveImageBtn = new Button("Save image");
        saveImageBtn.setFont(font);
        saveImageBtn.setAlignment(Pos.CENTER);

        tileButton = new CheckBox("Tile");
        tileButton.setFont(font);
        tileButton.setAlignment(Pos.CENTER);

        gridButton = new CheckBox("Grid");
        gridButton.setFont(font);
        gridButton.setAlignment(Pos.CENTER);


        cellSizeTxt = new Spinner(1.0,100.0,10.0);
        cellSizeTxt.getEditor().setFont(font);

        image = readImage("src/images/bush.png");
        //set the image showing controls
        canvas = new Canvas(200,200);
        setCanvas(canvas,image);
        canvasSmall = new Canvas(1000,1000);
        setCanvas(canvasSmall,canvas);


        //storing it in a Hbox
        pictureRegion = new HBox();
        pictureRegion.setAlignment(Pos.CENTER);
        pictureRegion.getChildren().add(canvasSmall);

    }

    private Image readImage(String path) {
        Image image1 = null;
        BufferedImage bfImage = null;
        try {
            bfImage = ImageIO.read(new File(path));
            image1 = SwingFXUtils.toFXImage(bfImage, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image1;
    }

    private void setCanvas(Canvas canvas, Image img) {
        canvas.setWidth(img.getWidth());
        canvas.setHeight(img.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.drawImage(img, 0, 0,canvas.getWidth(),canvas.getHeight());
        //makeGrid(canvas,(Double) cellSizeTxt.getValue());
    }

    private void setCanvas(Canvas canvasOut, Canvas canvasIn) {
        Image cImage = SwingFXUtils.toFXImage(snapCanvas(canvasIn),null);
        double width = cImage.getWidth();
        double height = cImage.getHeight();
        double cwidth = 500.0 , cheight = 500.0;
        double pref = 1000.0;
        if(width < pref && height<pref){
            cwidth = width;
            cheight = height;
        }
        else{
            double ratio = 1;
            if(width>height){
                ratio = width / pref;
            }
            else{
                ratio = height/pref;
            }
            cwidth = width/ratio;
            cheight = height/ratio;
        }
        canvasOut.getGraphicsContext2D().clearRect(0,0,pref,pref);
        canvasOut.getGraphicsContext2D().drawImage(cImage,0,0,cwidth,cheight);
    }

    private void makeGrid(Canvas canvas, double step, Image myImage){
        setCanvas(canvas,myImage);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        //gc.setGlobalAlpha(1);

        double normalWidth = 1.0;
        double ThickWidth = 2.0;
        double lift =  0.5;
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int counter = 0;
        for(double x1 = lift; x1<width; x1+=step){
            counter++;
            if( counter%10 ==0){
              gc.setLineWidth(ThickWidth);
            }
            else{
                gc.setLineWidth(normalWidth);
            }
            if(counter!=0)
                gc.strokeLine(x1, 0, x1, height);
        }
        counter = 0;
        for(double y1 = lift; y1<height; y1+=step){
            counter++;
            if( counter%10 ==0){
                gc.setLineWidth(ThickWidth);
            }
            else{
                gc.setLineWidth(normalWidth);
            }
            if(counter!=0)
                gc.strokeLine(0,y1,width,y1);
        }
    }

    private BufferedImage snapCanvas(Canvas canvas){
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        WritableImage wi = new WritableImage(width,height);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.WHITE);
        canvas.snapshot(sp,wi);
        return SwingFXUtils.fromFXImage(wi,null);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void setImage(Image image) {
        this.image = image;
    }

    private void tilize(Canvas canvas, double step){
        Canvas canvas1 = new Canvas(image.getWidth(),image.getHeight());
        setCanvas(canvas1,image);
        WritableImage wi = canvas1.snapshot(null,null);
        double width = wi.getWidth();
        double height = wi.getHeight();
        for(int  x = 0; x<width; x+=step)
            for(int y =0; y<height; y+=step){
                double sumRed= 0,sumBlue = 0,sumGreen = 0;
                double num = 0;
                for(int x1 = x;x1<x+step && x1<width;x1++)
                    for(int y1 = y;y1<y+step && y1<height; y1++){
                        num++;
                        int colorInt = wi.getPixelReader().getArgb(x1,y1);
                        java.awt.Color color = new java.awt.Color(colorInt);
                        sumRed+= color.getRed();
                        sumBlue+= color.getBlue();
                        sumGreen+= color.getGreen();
                    }
                double avgRed = sumRed/num;
                double avgBlue = sumBlue/num;
                double avgGreen = sumGreen/num;
                Color avgColor = Color.rgb((int)avgRed,(int)avgGreen,(int)avgBlue);
                for(int x1 = x;x1<x+step && x1<width;x1++)
                    for(int y1 = y;y1<y+step && y1<height; y1++){
                        wi.getPixelWriter().setColor(x1,y1,avgColor);
                    }
            }
        setCanvas(canvas,wi);
        setCanvas(canvasSmall,canvas);

    }

    private void handleIt(){
        if(tileButton.isSelected()){
            tilize(canvas,(Double) cellSizeTxt.getValue());
            if(gridButton.isSelected()) {
                makeGrid(canvas, (Double) cellSizeTxt.getValue(), canvas.snapshot(null, null));
                setCanvas(canvasSmall,canvas);
            }
        }
        else{
            setCanvas(canvas,image);
            if(gridButton.isSelected()) {
                makeGrid(canvas, (Double) cellSizeTxt.getValue(), canvas.snapshot(null, null));
                setCanvas(canvasSmall,canvas);
            }
            setCanvas(canvasSmall,canvas);
        }

    }
}
