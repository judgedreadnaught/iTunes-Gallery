package application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.control.Separator;
import javafx.event.ActionEvent;
import javafx.event.*;
import javafx.scene.layout.TilePane;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.lang.Runnable;
import java.lang.Thread;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GalleryApp extends Application{
	
	private VBox root;
	private HBox pane;
	private Label search;
	private TextField txtF;
	private Button play;
	private Button update;
	private MenuBar menB;
	private Menu menu;
	private MenuItem menIn;
	private TilePane tp;
	private ArrayList<String> link;
	private ArrayList<String> ogLink;
	private int numResults; // the number of albumn artwork links
	private ImageLoader[] iml;
	private ArrayList<String> usedArtwork;
	private ProgressBar progressBar;
	private HBox bottomBox; // where the progress bar and curtousy of itunes is placed
	private Text itunes;
	private Timeline timeline;
	private ArrayList<String>unusedArtwork;
	private String og;
	private boolean error;
	private String b;
	
	public void start(Stage stage) {
		error = false;
		root = new VBox();
		link = new ArrayList<>();
		ogLink = new ArrayList<>();
		usedArtwork = new ArrayList<>();
		unusedArtwork = new ArrayList<>();
		timeline = new Timeline();
		createBranch();
		swapLists();
		updateAction();
		update.setOnAction((t) -> {
			timeline.pause();
			updateAction();
		});
		play.setOnAction((t) -> {
			changeText();
			changeImage();
			//System.out.println(usedArtwork.size());
		});
		Scene scene = new Scene(root);
		stage.setMaxWidth(1280);
		stage.setMaxHeight(720);
		stage.setTitle("Gallery!");
		stage.setScene(scene);
		stage.sizeToScene();
		stage.show();	
	}
	
	public void swapLists() {
		usedArtwork.clear();
		unusedArtwork.clear();
		//System.out.println(ogLink.size());
		for (int i = 0; i < iml.length; i++) {
			//String x = iml[i].getStringImage();
			//System.out.println(i + ". X : " + x);
			usedArtwork.add(iml[i].getStringImage());
		}
		for (int i = 0; i < ogLink.size(); i++) {
			if (!(usedArtwork.contains(ogLink.get(i)))) {
				unusedArtwork.add(ogLink.get(i));	
			}
		}
		//System.out.println(usedArtwork.size());
		//System.out.println(unusedArtwork.size());
	}
	
	public void changeImage() {
		if (play.getText().equals("Play")) {
			timeline.pause();
		} else {
			EventHandler<ActionEvent> handler = event -> {
				swapLists();
				int random = (int) (Math.random() * 19);
				//String b = "https://is3-ssl.mzstatic.com/image/thumb/Music/v4/6b/f0/69/6bf06937-a8d4-34df-291e-523927ccda05/source/100x100bb.jpg";
				String x = unusedArtwork.get(random);
				/*while(usedArtwork.contains(link.get(random))) {
					random = (int) (Math.random() * 19);
					x = link.get(random);
				}*/
				usedArtwork.add(x);
				unusedArtwork.remove(random);
				//iml[random].setImageLoader(link, random); // BETWEEN 0 AND 19, SO 1 AND 20 I GUESS, REMEMBER MATH.RANDOM * 20 ONLYYY
				String usedCover = iml[random].setImageLoaderr(x);
				unusedArtwork.add(usedCover);
				int index = usedArtwork.indexOf(usedCover);
				usedArtwork.remove(index);
				tp.getChildren().set(random,iml[random]);
			};
			KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
			timeline = new Timeline();
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.getKeyFrames().add(keyFrame);
			timeline.play();
		}
		/*
		 * First you have a list of artWork urls. Then you use those artwork urls in the list to fill up the tile pane. Then
		 * I have another list, with the original amount of links. Then as I am filling the tile pane with the links, i remove 
		 * each link being added, and add it to OTHER list. Then from that OTHER list, I use that artwork to replace artwork on
		 * the main tile page. And everytime I replace the artwork, i remove the og artwork from the tilepane and put it in the 
		 * OTHER list. And I take the artwork being added, and put it in the list that had its contents removed. 
		 */
		//usedArtwork.remove(random);
				//System.out.println(x);
				//link.set(1, b); // link is the list of links, and we just set the second element of that link to the above album cover
	}
	
	public void changeText() {
		if (play.getText().equals("Pause")) {
			play.setText("Play");
		} else {
			play.setText("Pause");
			timeline.pause();
		}
	}
	public void updateAction() {
		if (txtF.getText().trim().equals("")) {
			parseImages(og);
		} else {
			b = og;
			og = txtF.getText().trim();
			parseImages(txtF.getText().trim());
		}
		//progressBar.setProgress(0);
		if (play.getText().equals("Play")) {
			timeline.pause();
		}
		if (ogLink.size() < 21) { // this messes with the program if they input in something that causes less than 21 images
			//System.exit(1);
			System.out.println("Try again");
		} else {
			System.out.println("Reset Progress Bar: " + progressBar.getProgress());
			runNow(() -> { // NEWTHREAD
				searchChange();
				Platform.runLater(() -> {
					modSG();
					if (play.getText().equals("Pause")) {
						timeline.play();
					}
				});
			}); // NEW THREAD
		}
	}
	public void searchChange() {
		int rando;
		int[] usedNums = new int[30];
		progressBar.setProgress(0);
		//play.setDisable(true);
		for (int i = 0; i < iml.length; i++) {
			rando = (int) (Math.random() * numResults);
			for (int x = 0; x < usedNums.length; x++) {
				if (usedNums[x] == rando) {
					rando = (int) (Math.random() * numResults);
					x = -1;
				} // if
			} // for
			usedNums[i] = rando;
			//iml[i].setImageLoader(link, i);
			noModSG(i,rando);
			manageProg(i);
			//tp.getChildren().set(i, iml[i]);
		} // for
		//play.setDisable(false);
		//swapLists();
	}
	
	public void modSG() {
		for (int i = 0; i < iml.length; i++) {
			iml[i].setImageView();
			tp.getChildren().set(i, iml[i]);
		}
	}
	
	public void noModSG(int i,int rando) {
		iml[i].createImage(ogLink,rando);
	}
	
	// Instantiates almost all of the 
	// RAN WHEN THE PROGRAM FIRST STARTS 
	public void createBranch() {
		menIn = new MenuItem("Exit"); // creates exit menu item
		menB = new MenuBar();
		menu = new Menu("File"); // creates file menu button
		menu.getItems().add(menIn);
		menB.getMenus().add(menu);
		root.getChildren().add(menB);
		menIn.setOnAction((event) -> { // if exit is clicked, the program closes
			Platform.exit();
			System.exit(1);
		});
		pane = new HBox(10); // this is for the hbox right below the menu
		play = new Button("Play");
		play.setAlignment(javafx.geometry.Pos.CENTER);
		play.setMaxWidth(Double.MAX_VALUE);
		update = new Button("Update Images");
		update.setMaxWidth(Double.MAX_VALUE);
		txtF = new TextField(); // where there will be writing 
		search = new Label("   Search Query:",play); // just a label
		search.setMaxWidth(Double.MAX_VALUE);
		search.setAlignment(javafx.geometry.Pos.CENTER);
		pane.getChildren().addAll(play,search,txtF,update);
		og = "rock";
		parseImages(og); // this is what will be shown when the program first loads
		play.setDisable(true);
		tp = new TilePane();
		tp.setHgap(0);
		tp.setPrefColumns(5);
		tp.setPrefRows(4);
		iml = new ImageLoader[20];
		bottomBox = new HBox(10);
		progressBar = new ProgressBar();
		progressBar.setProgress(0.0);
		int rando;
		int[] usedNums = new int[30];
		for(int i = 0; i < iml.length; i++) {
			rando = (int) ((Math.random() * numResults)); // change 5 back to num results
			for (int x = 0; x < usedNums.length; x++) { // change back to usedNums.length
				if(usedNums[x] == rando) {
					rando = (int) (Math.random() * numResults);
					x = -1;
				} // if
			} // for 
			iml[i] = new ImageLoader(ogLink,rando); // modifies scene graph
			tp.getChildren().add(iml[i]);
			usedNums[i] = rando;
		} // for
		//swapLists();
		bottomBox.getChildren().add(progressBar);
		root.getChildren().addAll(pane,tp,bottomBox);
	}
	
	// in charge of changing the artwork when the user puts something in the search bar
	
	public void manageProg(double i) {
		//i = i + .1;
		progressBar.setProgress((i*.05) + (.05));
		System.out.println(progressBar.getProgress());
		
	}
	
	public void parseImages(String searchTerm) {
		link.clear();
		ogLink.clear();
		usedArtwork.clear();
		unusedArtwork.clear();
		String stringU = "https://itunes.apple.com/search?term=" + searchTerm + "&entity=album";
		try {
			URL url = new URL(stringU);
			InputStreamReader r = new InputStreamReader(url.openStream());
			JsonElement je = JsonParser.parseReader(r);
			JsonObject root = je.getAsJsonObject();
			JsonArray results = root.getAsJsonArray("results");
			numResults = results.size();
			if (numResults < 21) {
				if (play.getText().equals("Pause")) {
					changeText();
				}
				play.setDisable(true);
				System.out.println("Not enough artwork");
				Alert a = new Alert(AlertType.ERROR);
				a.setContentText("ERROR: NOT ENOUGH ARTWORK \n\n"
						+ "PLAY BUTTON DISABLED UNTIL VALID INPUT IN PLACED IN"
						+ " SEARCH BAR!");
				a.show();
				error = true;
				//System.exit(1);
			} else {
				play.setDisable(false);
				for (int i = 0; i < numResults; i++) {
					JsonObject result = results.get(i).getAsJsonObject();
					JsonElement artWork = result.get("artworkUrl100");
					if (artWork != null) {
						String artUrl = artWork.getAsString();
						link.add(artUrl);
						ogLink.add(artUrl);
					} 
				}
				link = removeDuplicates(link);
				ogLink = removeDuplicates(link);
			}
		} catch (Exception e) {
			System.out.println("Error");
		}
	}
	
	private ArrayList<String> removeDuplicates(ArrayList<String> x) {
		LinkedHashSet<String> hash = new LinkedHashSet<>(x);
		ArrayList<String> newList = new ArrayList<>(hash);
		return newList;
	}

	
	private void runNow(Runnable x) {
		Thread thread1 = new Thread(x);
		thread1.setDaemon(true);
		thread1.start();
	}
	
}
	
