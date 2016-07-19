package View;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class EraseImageonCanvas extends Application {

        private Pane root = new Pane();

        private void setCanvas(Canvas canvas, Image img) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(img, 0, 0, canvas.getWidth(), canvas.getHeight());
        }

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Erasing the Image");
            Rectangle rect = new Rectangle(400, 400);
            drawBackground(rect);
            root.getChildren().add(rect);
            final Canvas canvas = new Canvas(200, 200);
            canvas.setTranslateX(100);
            canvas.setTranslateY(100);
            //For local images use
            //image = new Image(getClass().getResource(#Path#).openStream());
            BufferedImage bfImage = null;
            try {
                bfImage = ImageIO.read(new File("src/images/bush.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            final Image image = SwingFXUtils.toFXImage(bfImage,null);
            setCanvas(canvas, image);
            final GraphicsContext gc = canvas.getGraphicsContext2D();
            // Clear away portions as the user drags the mouse
            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
                }
            });

            // Reset the Canvas when the user double-clicks
            canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if (t.getClickCount() > 1) {
                        setCanvas(canvas, image);
                    }
                }
            });

            // Add the Canvas to the Scene, and show the Stage
            root.getChildren().add(canvas);
            primaryStage.setScene(new Scene(root, 400, 400));
            primaryStage.show();
        }

        //Draws the background with a RadialGradient
        private void drawBackground(Rectangle rect) {
            rect.setFill(new LinearGradient(0, 0, 1, 1, true,
                    CycleMethod.REFLECT,
                    new Stop(0, Color.RED),
                    new Stop(1, Color.YELLOW)));
        }

        public static void main(String[] args) {
            launch(args);
        }
    }