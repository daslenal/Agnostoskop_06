
import processing.core.*;

public class Scanner {

	PImage handCapture;
	PGraphics imgBuffer;


	int averageColor, averageColRef;
	int width, height;
	int min, max;
	
	PApplet parent;


	public Scanner( PApplet _parent) {
	  parent = _parent;
	  imgBuffer = parent.createGraphics(680, 450, parent.OPENGL);
		
	}

	public void startScan(int averageColReference){
	 handCapture = parent.createImage(680, 450, parent.RGB);

		min = 70;			// biggest Hand
		max = averageColReference;		// smallest hand
		
		handCapture = parent.loadImage("bin/hand.jpg");
		handCapture.resize(parent.width, parent.height);
		averageColor = getAverageColor(handCapture);
	}
	
	

    private int getAverageColor(PImage handCapture) {
      handCapture.loadPixels();
      int r = 0, g = 0, b = 0;
      for (int i=0; i<handCapture.pixels.length; i++) {
        int c = handCapture.pixels[i];
        r += c>>16&0xFF;
        g += c>>8&0xFF;
        b += c&0xFF;
      }
      r /= handCapture.pixels.length;
      g /= handCapture.pixels.length;
      b /= handCapture.pixels.length;
    //  parent.println(r);

      return r;
    }

	@SuppressWarnings("static-access")
	public int[] getValues(){
		int []values = new int[12];
		values[0] = (int) parent.map(averageColor, 0, max, 7, 15);		//numGeisseln
		values[1] = (int) parent.map(averageColor, 0, max, 140, 360);		//randAng
		values[2] = (int) parent.map(averageColor, 0, max, 15, 20);		//segNum
		
		values[3] = (int) parent.map(averageColor, min, max, 130, 240);	//nukleusCol1
		values[4] = (int) parent.map(averageColor+10, min, max, 145, 240);	//nukleusCol2
		values[11] = (int) parent.map(averageColor-20, min, max, 150, 240); //nukleuscol3
		
		values[5] = (int) parent.map(averageColor, min, max, 210, 240);	//bgCol1
		values[6] = (int) parent.map(averageColor+50, min, max, 170, 230);	//bgCol2
		values[7] = (int) parent.map(averageColor+100, min, max, 160, 220);	//bgCol3
		
		values[8] = averageColor; // nukleusTex
		values[9] = min;
		values[10] = max;
		
		
		return values;
	}
}
	
