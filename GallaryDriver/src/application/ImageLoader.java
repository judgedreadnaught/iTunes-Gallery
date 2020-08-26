package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.control.Separator;
import javafx.event.ActionEvent;
import javafx.event.*;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;

import com.google.gson.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageLoader extends VBox{
	
	private HBox picBox;
	private ImageView imgView;
	private Image img;
	private String imgs;
	
	public ImageLoader() {
		super();
		picBox = new HBox(10);		
		//picBox = null;
		//imgView = null;
		//img = null;
	}
	public ImageLoader(ArrayList<String> link,int rando) {
		super();
		picBox = new HBox(10);
		//createImage(link,rando);
		img = new Image("http://cobweb.cs.uga.edu/~mec/cs1302/gui/default.png");
		imgView = new ImageView(img);
		imgView.setPreserveRatio(true);
		imgView.setFitHeight(100);
		imgView.setFitWidth(100);		
		this.getChildren().addAll(picBox,imgView);
	}
	
	public String setImageLoader(ArrayList<String> link, int rando) {
		//picBox = new HBox(10);
		ArrayList<String> newLink = link;
		imgs = newLink.get(rando);
		img = new Image(imgs);
		imgView.setImage(img);
		return imgs;
		//imgView.setPreserveRatio(true);
		//imgView.setFitHeight(100);
		//imgView.setFitWidth(100);
		//this.getChildren().addAll(picBox,imgView);	
	}
	
	public String setImageLoaderr(String x) {
		String pastImage = imgs;
		imgs = x;
		img = new Image(x);
		imgView.setImage(img);
		return pastImage;
	}
	
	public void createImage(ArrayList<String> link, int rando) {
		ArrayList<String> newLink = link;
		imgs = newLink.get(rando);
		img = new Image(imgs);
				
	}
	
	public void setImageView() {
		imgView.setImage(img);
		imgView.setPreserveRatio(true);
		imgView.setFitHeight(100);
		imgView.setFitWidth(100);
	}
	
	public String getStringImage() {
		return this.imgs;
	}
	
	
}
