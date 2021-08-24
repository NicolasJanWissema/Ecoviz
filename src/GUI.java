import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
 
public class GUI extends Application {

    float [][] height; // regular grid of height values
	int dimx, dimy; // data dimensions
	WritableImage img; // greyscale image for displaying the terrain top-down
    Group root;
    Plant[] plants;
    float relativeSize;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));   
        root = new Group() ;
        primaryStage.setTitle("EcoViz");
        ImageView imageView = new ImageView(new Image("CapStone.jpeg"));
        root.getChildren().add(imageView);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
    }

    public void addPlants() {
        for (int i = 0; i < plants.length; i++) {
            float[] pos = plants[i].getPostion();
            addPlant(pos[0],pos[1],plants[i].getHeight());
        }
    }

    public void addPlant(float x, float y, float rad) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(rad);
        circle.setFill(Color.GREEN);
        root.getChildren().addAll(circle);
    }

    void deriveImage() {
		img = new WritableImage(dimx, dimy);
        PixelWriter pw = img.getPixelWriter();
		float maxh = -10000.0f, minh = 10000.0f;
		
		// determine range of heights
		for(int x=0; x < dimx; x++)
			for(int y=0; y < dimy; y++) {
				float h = height[x][y];
				if(h > maxh)
					maxh = h;
				if(h < minh)
					minh = h;
			}
		
		for(int x=0; x < dimx; x++)
			for(int y=0; y < dimy; y++) {
				 // find normalized height value in range
				float val = (height[x][y] - minh) / (maxh - minh);
				Color col = new Color(val, val, val, 1.0f);
                pw.setColor(x, y, col);
				 
			}
	}
    
    
}