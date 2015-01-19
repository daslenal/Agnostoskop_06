

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;

@SuppressWarnings("serial")
public class Main extends PApplet {

	public static void main(String[] args) {
		PApplet.main(new String[] { "--present", "Main" });

	}
	HandDetection handDetection;
	Scanner scanner;
	Generator generator;
	PImage startScreen, loadScreen;
	PImage framebuffer;
	boolean showResult = false;
	boolean generatorStart = false;
	int imgTime;
	Movie startVideo;
	public boolean changeRefImg = false;
	Rectangle monitor = new Rectangle();
	

	
	public void setup(){
		/*
		// Get second screen details and save as Rectangle monitor
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		// gs[1] gets the *second* screen. gs[0] would get the primary screen
		GraphicsDevice gd = gs[1];
		GraphicsConfiguration[] gc = gd.getConfigurations();
		monitor = gc[0].getBounds();
		
		//println(monitor.x + " " + monitor.y + " " + monitor.width + " " + monitor.height);
		size(monitor.width, monitor.height, OPENGL);				// Mikrobenwerte angepasst auf Lenco TV
		*/
		size(1280,720, OPENGL);
		
		handDetection = new HandDetection(this);
		println("handDetection generiert");

		scanner = new Scanner(this);
		println("scanner generiert");

		generator = new Generator(this);
		println("generator generiert");

		startVideo = new Movie(this, "data/screens/startScreen_1280.mp4");
		startVideo.loop();

		startScreen = loadImage("data/screens/startScreen_still_1280.png");			// StartScreen
		image(startScreen,0,0);

		loadScreen = loadImage("data/screens/loadScreen_1280.png");									// LoadScreen


	}
 
	public void draw(){
		// FULLSCREEN ON SECOND DEVICE
		//frame.setLocation(monitor.x-42, monitor.y);
		//frame.setAlwaysOnTop(true);

		if(generatorStart){
			generatorStart = false;
			
			int averageColReference = handDetection.getAverageColReference();
			
			scanner.startScan(averageColReference);
			int [] values = scanner.getValues();
			println(values);

			generator.generateImage(values);
			saveFrame("data/saveFrames/generatedImage.tif");
			framebuffer = loadImage("data/saveFrames/generatedImage.tif");
			showResult = true;
			imgTime = millis();

		}
		else{

			if(showResult){

				image(framebuffer,0,0);
				if(millis() > imgTime + 5000){
					
					showResult = false;
				}
			}
			else{
				if(imgTime != 0){
					image(framebuffer,0,0);						// current microbes
				}
				if((millis() > imgTime + 25000) && (imgTime != 0)){
					if(startVideo.available()){
						startVideo.read();
					}
					resetShader();
					image(startVideo, 0, 0);					// StartVideo
				}
				if(!handDetection.getHandDetect()){
					handDetection.detectHand();
				} 

				if(handDetection.getHandDetect()){
					resetShader();
					image(loadScreen,0,0);						// LoadScreen
					generatorStart = true;
					handDetection.setHandDetect(false);

				}
			}
		}

	}



	
	public void keyPressed() {
	    if (keyCode == 's' || keyCode == 'S') {					// SAVE FRAME
	    	saveFrame("saveFrames/saveFrame_######.jpg");
	    	println("key s");
	    	
	    }
	    if (keyCode == 'r' || keyCode == 'R'){
	    	handDetection.setRefImg();							// CHANGE REFERENCE IMAGE
	    	println("key r");
	    }
	}	
	
	


}
