package application;

import javafx.application.Application;

public class GalleryDriver {
	
	public static void main(String [] args) {
		try {
			Application.launch(GalleryApp.class, args);
		} catch (UnsupportedOperationException e) {
			System.out.println(e);
			System.err.println("If this is a problem, then it is becacuse of your X server connection");
			System.exit(1);
		}
		
	}

}
