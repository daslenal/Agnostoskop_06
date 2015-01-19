
import processing.video.*;
import processing.core.*;

@SuppressWarnings("serial")
public class HandDetection{
	PApplet parent;
	Capture video;
	PImage refImg, currImg;
	PImage prevFrame;
	int threshold = 5;
	boolean handDetected = false;
	PImage img;
	boolean changeRefImg;
	int counter;
	int savedTime;
	int reference, current, cropL, cropR, cropT, cropB;
	
	public HandDetection(PApplet _parent) {
	parent = _parent;
	
	refImg = parent.createImage(parent.width, parent.height,parent.RGB);
	video = new Capture(parent, 640,480);
	//video = new Capture(parent, 640, 480, "Mercury USB2.0 Camera");	// camera modul, 64°, 30cm fixfocus
	refImg = parent.loadImage("data/refImg.jpg");
	currImg = parent.createImage(refImg.width,  refImg.height,  parent.RGB);
	video.start();
	
	cropL = 155;
	cropR = 357;
	cropT = 7;
	cropB = 52;

	}

	
	public void detectHand() {

		//parent.println("hand detect running");
		if (video.available() == true){	
			video.read();
			video.filter(parent.THRESHOLD, 0.8f);

		}
		
		//parent.image(video,0,0);
		currImg = video.get(cropL,cropT,video.width-cropR,video.height-cropB);
		
		parent.loadPixels();
		currImg.loadPixels();
		refImg.loadPixels();
		
		reference = getAverageColor(refImg);
		current = getAverageColor(currImg);
		
		parent.println("ref: " + reference + "curr: " + current);
		
		if(current > reference - threshold){
			// no hand found
			counter = 0;

			handDetected = false;
			parent.println("no hand found");
			
		}else if(current < reference - threshold){
			if (counter == 0){
				savedTime = parent.millis();				
			}
			
			counter ++;
			if(parent.millis() > savedTime + 2000){ // hand has to be there for 2 sec before generating the image
				parent.println("Hand detected");

				handDetected = true;		
				currImg.save("bin/hand.jpg");
			}
		}

	}

	public boolean getHandDetect(){
		return handDetected;
	}
	
	public void setHandDetect(boolean _handDetected){
		handDetected = _handDetected;
	}
	
	public void setRefImg(){
		refImg = video.get(cropL,cropT,video.width-cropR, video.height-cropB);
		refImg.save("data/refImg.jpg");
	}
	
	public int getAverageColReference(){
		return reference;
	}

	private int getAverageColor(PImage img) {
		img.loadPixels();
		int r = 0, g = 0, b = 0;
		for (int i=0; i<img.pixels.length; i++) {
			int c = img.pixels[i];
			r += c>>16&0xFF;
			g += c>>8&0xFF;
			b += c&0xFF;
		}

		r /= img.pixels.length;
		g /= img.pixels.length;
		b /= img.pixels.length;
		img.updatePixels();

		return r;
	}


	
}
	
