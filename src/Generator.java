import java.io.File;
import java.io.FilenameFilter;

import processing.core.*;
import processing.opengl.PShader;

public class Generator {
	PApplet parent;
	
	// Graphics
	PShader cr_effectSh, alphaFilter, alphaFilter_invert;
	PGraphics pg_bg;
	PGraphics pg_nukleus, pg_nInner,  pg_geisseln, pg_gInner;
	PGraphics pg_geisselnArr [];
	PGraphics pg_gInnerArr [];
	PGraphics pg_texNukleus;
	PImage bg, bgLight, nukleusBuffer, nInvertBuffer, nInnerBuffer, geisselnBuffer;
	PImage texNukleus;
	PImage gr_right, gr_leftBottom, gr_topLeft, gr_topRight;
	PGraphics pg_right, pg_leftBottom, pg_topLeft, pg_topRight;
	PImage rightBuffer, leftBottomBuffer;

	PGraphics pg_circles, pg_circleBuffer;
	
	PGraphics pg_body, pg_bodyBG, pg_bodyFG, pg_bodyBuffer;
	PGraphics pg_gradient;
	
	// Textures
	String [] nukleusFilenames;
	String [] bgFilenames;
	String nukleusPath = "data/textures/Nukleus"; // forward slashes
	String bgPath = "data/textures/BG"; // forward slashes

	int bgCol1, bgCol2, bgCol3;
	int nukleusCol1, nukleusCol2, nukleusCol3;
	int texNum;
	
	// General
	float radius;
	int posX, posY;
	int xPos, yPos;
	int numMikrobe, numFg, numBg;
	Mikrobe_10 myMikrobe;
	boolean restart = false;
	
	// Body
	int numNukleus;
	float noiseval, nvStep;

	// Legs
	int numGeisseln, segNum;
	float randAng;
	
	
	// CONSTRUCTOR
	
	@SuppressWarnings("static-access")
	public Generator(PApplet _parent){
		parent = _parent;
		parent.frameRate(5);
		parent.noStroke();
		
		
		bgFilenames = loadFilenames(bgPath);
		
		bgLight = parent.loadImage("data/textures/Gradients/bgLight.png");
		gr_right = parent.loadImage("data/textures/Gradients/right2.png");
		gr_leftBottom = parent.loadImage("data/textures/Gradients/bottomLeft2.png");
		gr_topLeft = parent.loadImage("data/textures/Gradients/topLeft.png");
		gr_topRight = parent.loadImage("data/textures/Gradients/topRight.png");
	
		gr_right.resize(parent.width, parent.height);
		gr_leftBottom.resize(parent.width, parent.height);
		gr_topLeft.resize(parent.width, parent.height);
		gr_topRight.resize(parent.width, parent.height);
		bgLight.resize(parent.width, parent.height);
		
		pg_right = parent.createGraphics(parent.width, parent.width, parent.OPENGL);
		pg_leftBottom = parent.createGraphics(parent.width, parent.width, parent.OPENGL);
		pg_topLeft = parent.createGraphics(parent.width, parent.width, parent.OPENGL);
		pg_topRight = parent.createGraphics(parent.width, parent.width, parent.OPENGL);
		
		rightBuffer = new PImage(parent.width, parent.width);
		leftBottomBuffer = new PImage(parent.width, parent.width);
		
		
		nukleusFilenames = loadFilenames(nukleusPath);
		//parent.println(nukleusFilenames);
		cr_effectSh = parent.loadShader("cr_effect.glsl");
		alphaFilter = parent.loadShader("alphaFilter.glsl");
		alphaFilter_invert = parent.loadShader("alphaFilter_invert.glsl");
		
		pg_bg = parent.createGraphics(parent.width, parent.width, parent.OPENGL);
		pg_gradient = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		
		
		pg_nukleus = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_nInner = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_geisseln = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_gInner = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		
		pg_geisselnArr = new PGraphics [numMikrobe];
		pg_gInnerArr = new PGraphics [numMikrobe];
		
		pg_texNukleus = parent.createGraphics(parent.width, parent.height, parent.OPENGL);

		pg_circles = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_circleBuffer = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		
		pg_body = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_bodyBG = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_bodyFG = parent.createGraphics(parent.width, parent.height, parent.OPENGL);
		pg_bodyBuffer = parent.createGraphics(parent.width, parent.height, parent.OPENGL);

	}
	
	
	//DRAW
	@SuppressWarnings("static-access")
	public void generateImage(int[] values){
		randomValues(values);	
		parent.resetShader();

			pg_clear();
			
			bg = parent.loadImage(bgPath + "/" + bgFilenames[(int)parent.random(1,bgFilenames.length)]);
			bg.filter(parent.BLUR,2);
			
			pg_leftBottom.beginDraw();
			pg_leftBottom.image(gr_leftBottom,0,0);
			pg_leftBottom.tint(parent.random(100,240), parent.random(120,230), parent.random(80, 230));
			pg_leftBottom.endDraw();
			
			pg_right.beginDraw();
			pg_right.image(gr_right,0,0);
			pg_right.tint(parent.random(100,240), parent.random(120,230), parent.random(80, 230));
			pg_right.endDraw();
			
			pg_gradient.beginDraw();
			pg_gradient.image(gr_leftBottom,0,0);
			pg_gradient.image(gr_right,0,0);
			pg_gradient.endDraw();
			parent.println("nukleus.filenames.length: " + nukleusFilenames.length);
			
			texNum = (int) parent.map(values[8],0, values[10], 0, nukleusFilenames.length);
			parent.println("texNum: " + texNum);
			texNukleus = parent.loadImage(nukleusPath + "/" + nukleusFilenames[texNum]);
			texNukleus.resize(parent.width, parent.height);
			texNukleus.filter(parent.INVERT);

			pg_bg.beginDraw();
			pg_bg.pushMatrix();
			
			pg_bg.translate(parent.width/2, parent.width/2);
			pg_bg.rotate(parent.random(parent.TWO_PI));
			pg_bg.scale(1.5f);
			pg_bg.translate(-parent.width/2, -parent.width/2);
			pg_bg.image(bg,0,0);
			pg_bg.popMatrix();
			pg_bg.endDraw();
			
			pg_texNukleus.beginDraw();
			pg_texNukleus.image(texNukleus,0,0);
			pg_texNukleus.endDraw();
			
			parent.noiseSeed((int)(parent.random(50)));
			
			
			for(int i=0; i < numMikrobe; i++){
				
				
				parent.pushMatrix();
				pg_nukleus.beginDraw();
				pg_nInner.beginDraw();
				pg_geisseln.beginDraw();
				pg_gInner.beginDraw();
				pg_geisselnArr[i].beginDraw();
				pg_gInnerArr[i].beginDraw();
				
				parent.smooth(8);
				
				xPos = (int) parent.random(0, parent.width);
				yPos = (int) parent.random(radius, parent.height-radius);

				float size = parent.random(1, 1.5f);
				float rot = parent.random(parent.TWO_PI);
				
				//rotate and scale legs
				pg_geisselnArr[i].pushMatrix();
				pg_geisselnArr[i].translate(xPos, yPos);
				pg_geisselnArr[i].scale(size);
				pg_geisselnArr[i].rotate(rot);
				pg_geisselnArr[i].translate(-xPos, -yPos);
				
				// rotate and scale inner legs
				pg_gInnerArr[i].pushMatrix();
				pg_gInnerArr[i].translate(xPos, yPos);
				pg_gInnerArr[i].scale(size);
				pg_gInnerArr[i].rotate(rot);
				pg_gInnerArr[i].translate(-xPos, -yPos);
			
				//rotate and scale nucleus
				pg_nukleus.pushMatrix();
				pg_nukleus.translate(xPos,  yPos);
				pg_nukleus.scale(size);
				pg_nukleus.rotate(rot);
				pg_nukleus.translate(-xPos, -yPos);
				
				myMikrobe = new Mikrobe_10(parent, radius, numNukleus, noiseval, numGeisseln, randAng, segNum, xPos, yPos);
				myMikrobe.display(pg_nukleus, pg_nInner, pg_geisselnArr[i], pg_gInnerArr[i]);				
				
				pg_geisseln.image(pg_geisselnArr[i],0,0);
				pg_gInner.image(pg_gInnerArr[i],0,0);

				geisselnBuffer = pg_geisseln;
				nukleusBuffer = pg_nukleus;
				nInnerBuffer = pg_nInner;		
				nInvertBuffer = pg_nukleus;
		
				pg_nukleus.popMatrix();
				pg_gInnerArr[i].popMatrix();
				pg_geisselnArr[i].popMatrix();
		
				pg_gInnerArr[i].endDraw();
				pg_geisselnArr[i].endDraw();
				pg_gInner.endDraw();		
				pg_geisseln.endDraw();
				pg_nInner.endDraw();
				pg_nukleus.endDraw();
						
				parent.popMatrix();
			}	
		
			// BACKGROUND
			pg_bg.filter(parent.BLUR,0);			
			pg_bg.tint(bgCol1+20, bgCol2+20, bgCol3+20);
			parent.image(pg_bg,0,0);
			parent.image(bgLight,parent.random(parent.width/3), parent.random(parent.height/3));
			parent.blend(bgLight, 0, 0, parent.width, parent.height, 0, 0, parent.width, parent.height, parent.ADD);	
			parent.saveFrame("bg.jpg");
			// CIRCLES
			int numCircles = (int)parent.random(10, 40);

			pg_circles.beginDraw();

			for(int j = 0; j <= numCircles; j++){
				float radius = parent.random(1,20);
				float x = parent.random(parent.width)-radius/2;
				float y = parent.random(parent.height)-radius/2;
				pg_circles.stroke(255);
				pg_circles.strokeWeight(parent.random(1f, 1.5f ));
				pg_circles.fill(255,50);
				pg_circles.ellipse(x, y,radius, radius);
			}	

			for(int k = 0; k <= parent.random(3); k++){
				float radius = parent.random(100,parent.width+200);
				pg_circles.stroke(255,parent.random(50,180));
				pg_circles.strokeWeight(parent.random(1.5f, 2.5f ));
				pg_circles.fill(255,10);
				pg_circles.ellipse(parent.random(parent.width), parent.random(parent.height),radius, radius);
			}			
			pg_circles.endDraw();

			pg_circles.filter(parent.BLUR, 1.6f);
			parent.image(pg_circles,-2,3);

			pg_circles.filter(parent.BLUR, 1.6f);
			parent.image(pg_circles,-3,-2);

			pg_circles.filter(parent.BLUR,0.9f);
			pg_circles.tint(255,150);
			parent.image(pg_circles,0,0);

			/* SHADER NOT CORRECT ON pg_body
			 * Workaround: use pg_body only for FG and BG
			 * (outlines and legs, filter are somehow lost)
			 * probably because of pg_body.begin/endDraw()
			 * */
						
			
			
			pg_body.beginDraw();

			// blurry background of body
			pg_nukleus.filter(parent.BLUR,5);
			pg_body.image(pg_nukleus,0,0);	
			
			// texture
			nukleusBuffer.filter(parent.BLUR,1.5f);
			pg_texNukleus.mask(nukleusBuffer);	
			pg_texNukleus.tint(nukleusCol1,nukleusCol2,nukleusCol3);
			pg_body.image(pg_texNukleus,0,0);	
			
			// outline with offset
			alphaFilter.set("alphaStrength", 0.4f);
			pg_nukleus.filter(alphaFilter);
			pg_nukleus.filter(parent.INVERT);
			pg_nukleus.filter(parent.BLUR,1.8f);
			pg_body.image(pg_nukleus,-4,-3);			
			
			// outline
			alphaFilter.set("alphaStrength", 0.8f);
			pg_nukleus.filter(parent.INVERT);
			pg_nukleus.filter(parent.BLUR,1f);
			pg_body.image(pg_nukleus, 0,0);				
			
			// inner nuclei outline
			alphaFilter_invert.set("alphaStrength", 0.8f);	
			pg_nInner.filter(alphaFilter_invert);
			pg_nInner.filter(parent.BLUR,1);
			pg_body.image(pg_nInner,0,0);				
			
			/* SHADER FOR LEGS */
			pg_geisseln.filter(parent.BLUR,1.5f);
			pg_geisseln.tint(parent.random(150,255), parent.random(150,255), parent.random(150,255), 180);
			pg_body.image(pg_geisseln,0,0);
			
			pg_gInner.filter(parent.BLUR,1.2f);
			pg_gInner.tint(255,200);
			pg_body.image(pg_gInner,0,0);			
			
			pg_body.endDraw();

			pg_bodyBuffer = pg_body;

			// BODY BACKGROUND LAYER
			parent.pushMatrix();
			pg_bodyBG.beginDraw();
			
			pg_bodyBG.pushMatrix();
			pg_bodyBG.translate(parent.width/2, parent.height/2);
			pg_bodyBG.rotate(parent.PI);
			pg_bodyBG.translate(-parent.width/2, -parent.height/2);
			pg_bodyBG.scale(1.1f);
			pg_bodyBG.image(pg_bodyBuffer,0,0);
			pg_bodyBG.popMatrix();

			pg_bodyBG.pushMatrix();
			pg_bodyBG.translate(parent.width/2, parent.height/2);
			pg_bodyBG.rotate(parent.HALF_PI);
			pg_bodyBG.scale(1,0.8f);
			pg_bodyBG.translate(-parent.width/2, -parent.height/2);
			pg_bodyBG.tint(255,200);
			pg_bodyBG.image(pg_bodyBuffer,0,0);
			pg_bodyBG.popMatrix();
			
			pg_bodyBG.endDraw();
			parent.popMatrix();

			pg_bodyBG.filter(parent.BLUR,1.5f);
			pg_bodyBG.tint(255,180);
			parent.image(pg_bodyBG,0,0);
			
			
			// BODY MAIN LAYER
			
			// blurry background of body
			pg_nukleus.filter(parent.BLUR,6);
			parent.image(pg_nukleus,0,0);	
			
			// texture
			nukleusBuffer.filter(parent.BLUR,3f);
			pg_texNukleus.mask(nukleusBuffer);	
			pg_texNukleus.tint(nukleusCol1,nukleusCol2,nukleusCol3);
			parent.image(pg_texNukleus,0,0);	
			
			// outline with offset
			alphaFilter.set("alphaStrength", 0.4f);
			pg_nukleus.filter(alphaFilter);
			pg_nukleus.filter(parent.INVERT);
			pg_nukleus.filter(parent.BLUR,1.8f);
			parent.image(pg_nukleus,-4,-3);			
			
			// outline
			alphaFilter.set("alphaStrength", 0.8f);
			pg_nukleus.filter(parent.INVERT);
			pg_nukleus.filter(parent.BLUR,1f);
			parent.image(pg_nukleus, 0,0);				
			
			// inner nuclei outline
			alphaFilter_invert.set("alphaStrength", 0.8f);	
			pg_nInner.filter(alphaFilter_invert);
			pg_nInner.filter(parent.BLUR,1);
			parent.image(pg_nInner,0,0);				
				
			/* SHADER FOR LEGS */
	
			pg_geisseln.filter(parent.BLUR,2f);
			pg_geisseln.tint(parent.random(150,255), parent.random(150,255), parent.random(150,255), 180);
			parent.image(pg_geisseln,0,0);
			
			pg_gInner.filter(parent.BLUR,1.5f);
			pg_gInner.tint(255,150);
			parent.image(pg_gInner,0,0);			
			
			cr_effectSh.set("caIntensity", 1.1f);
			parent.shader(cr_effectSh);
			
			// Gradients
			pg_leftBottom.tint(parent.random(100,230), parent.random(100,230), parent.random(100, 230),parent.random(150,160));
			leftBottomBuffer = pg_leftBottom;
			parent.image(leftBottomBuffer,0,0);
			parent.blend(leftBottomBuffer,0,0,parent.width,parent.height,0,0,parent.width,parent.height, parent.OVERLAY);
			
			pg_right.tint(parent.random(100,230), parent.random(100,230), parent.random(100, 230), parent.random(150,160));
			rightBuffer = pg_right;
			parent.image(rightBuffer, 0, 0);
			parent.blend(rightBuffer,0,0,parent.width,parent.height,0,0,parent.width,parent.height, parent.OVERLAY);
									
			
			// BODY FOREGROUND LAYER
			parent.pushMatrix();
			pg_bodyFG.beginDraw();
			pg_bodyFG.pushMatrix();
			pg_bodyFG.translate(parent.width/2, parent.height/2);
			pg_bodyFG.rotate(-parent.HALF_PI);
			pg_bodyFG.scale(1.5f);
			pg_bodyFG.translate(-parent.width/2, -parent.height/2);
			pg_bodyFG.tint(240,240);
			pg_bodyFG.image(pg_bodyBuffer,0,0);
			pg_bodyFG.popMatrix();	
			pg_bodyFG.endDraw();
			parent.popMatrix();

			
			
			pg_bodyFG.filter(parent.BLUR,1.5f);
			parent.image(pg_bodyFG,0,0);
			
			
			/* SHADER FOR ALL */
			cr_effectSh.set("caIntensity", 1.2f);
			parent.shader(cr_effectSh);
			
			

		
	}

	public void pg_clear() {
		pg_bg.beginDraw(); pg_bg.clear();pg_bg.endDraw();
		
		pg_geisseln.beginDraw(); pg_geisseln.clear(); pg_geisseln.endDraw();
		pg_gInner.beginDraw(); pg_gInner.clear(); pg_gInner.endDraw();

		pg_nukleus.beginDraw(); pg_nukleus.clear(); pg_nukleus.endDraw();
		pg_nInner.beginDraw(); pg_nInner.clear(); pg_nInner.endDraw();
		
		pg_circles.beginDraw(); pg_circles.clear(); pg_circles.endDraw();
		
		pg_body.beginDraw(); pg_body.clear(); pg_body.endDraw();
		pg_bodyBG.beginDraw(); pg_bodyBG.clear(); pg_bodyBG.endDraw();
		pg_bodyFG.beginDraw(); pg_bodyFG.clear(); pg_bodyFG.endDraw();

		
		for(int j = 0; j < numMikrobe; j++){
			pg_geisselnArr[j] = parent.createGraphics(parent.width, parent.height);
			pg_gInnerArr[j] = parent.createGraphics(parent.width, parent.height);

			pg_geisselnArr[j].beginDraw(); pg_geisselnArr[j].clear(); pg_geisselnArr[j].endDraw();
			pg_gInnerArr[j].beginDraw(); pg_gInnerArr[j].clear(); pg_gInnerArr[j].endDraw();
		}
	}
	

	
	public void randomValues(int[] values){
		numMikrobe = (int) parent.random(5,10);
				
		pg_geisselnArr = new PGraphics [numMikrobe];
		pg_gInnerArr = new PGraphics [numMikrobe];

		
		//Random Values for Nukleus
		radius = parent.random(80, 110);

		if (radius < 90){
			numNukleus = PApplet.round(parent.random(1,4));
		}else{
			numNukleus = PApplet.round(parent.random(1,6));

		}
		noiseval = parent.random(40);

		
		// Values for Geisseln
		numGeisseln =  values[0]; //(int) parent.random(2,15);
		randAng = values[1]; //parent.random(1,360);
		segNum = values[2]; //Math.round(parent.random(5,15));
		
		//  Colors for Background
		bgCol1 = values[5];		//(int) parent.random(210, 255);
		bgCol2 = values[6];		//(int) parent.random(170, 220);
		bgCol3 = values[7];		//(int) parent.random (160, 220);
		
		// Colors for Nukleus
		nukleusCol1 = values[3]; 	//(int)parent.random(150, 255);
		nukleusCol2 = values[4];	//(int)parent.random(150, 255);
		nukleusCol3 = values[11]; 	//(int)parent.random(150, 255);
		
		
	}
	


	
	String[] loadFilenames(String path) {
	  File folder = new File(path);
	  FilenameFilter filenameFilter = new FilenameFilter() {
		  public boolean accept(File dir, String name) {
		     return name.toLowerCase().endsWith(".jpg"); // change this to any extension you want
		   }
	  };
	  return folder.list(filenameFilter);
	}


	
	
}
