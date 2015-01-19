import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Mikrobe_10 {
//	PGraphics pg_innerNukleus, pg_ellipse;

	
	// General
	PApplet parent;
	float radius;
	int posX, posY;
	int xPos, yPos;

	// Body
	int numNukleus;
	float radiusX, radiusY, colRadius;
	float x, y;
	float centX, centY;
	float noiseval, nvStep;
	float [][] _nukleusArr;	
	
	// Legs
	int numGeisseln;
	float [][] _pos;
	float [] angle2Arr;
	PVector yAxis;
	float randAng, angle1, angle2, angleRot, angleOr;
	float deviation, segLengthDev;
	float segLength, segNum;
	boolean rSide = false;
	boolean up = false;
	PImage pi_nukleus;
	
	
	public Mikrobe_10(PApplet _parent, float _radius, int _numNukleus, float _noiseval, int _numGeisseln, float _randAng, int _segNum, int _xPos, int _yPos){
		
		parent = _parent;
		radius = _radius;
		numNukleus = _numNukleus;
		noiseval = _noiseval;
		numGeisseln = _numGeisseln;
		randAng = _randAng;
		segNum = _segNum;
		xPos = _xPos;
		yPos = _yPos;
		
		centX = 0;
		centY = 0;
			
		yAxis = new PVector (0,1);
		angle2Arr = new float[numGeisseln];

				
	}
	
	public void display(PGraphics pg_nukleus, PGraphics pg_nInner, PGraphics pg_geisseln, PGraphics pg_gInner){
		generateNukleus(pg_nukleus);
		generateInnerNukleus(pg_nInner);
		generateGeisseln(pg_geisseln, 255);
		generateGeisseln(pg_gInner, 0);
	}
	
	
	/* ***************************************
	 * NUKLEUS / BODY
	 * ***************************************/
	
	public void generateNukleus(PGraphics pg_nukleus){

		pi_nukleus = parent.createGraphics(parent.width, parent.height);
		// circle with noise
		float radVariance, thisRadius, rad;		
		float firstX = 0;
		float firstY = 0;
		float mult = parent.random(1.8f, 2.2f); //2.1f;
		float mag =  parent.random(0.2f, 0.26f); //0.3f;
		
		pg_nukleus.fill(255);
		pg_nukleus.stroke(0);
		pg_nukleus.strokeWeight(2f);
		pg_nukleus.beginShape();

		for (float ang = 0; ang <=360; ang+=4){
			rad = parent.radians(ang);

			noiseval += 0.2f;
			radVariance = 2 * customNoise(noiseval);
			thisRadius = radius/1.7f + radVariance;
			

			
			float ax = parent.cos(rad);
			float ay = parent.sin(rad);
			float axm = ax*mult;
			float aym = ay*mult;

			parent.noiseDetail(1);
			
			x = xPos + (thisRadius * ax * (1.0f + (parent.noise(axm, aym)-0.5f) * mag));
			y = yPos + (thisRadius * ay * (1.0f + (parent.noise(axm, aym)-0.5f) * mag));
	
			// close shape 
			if(ang == 0){
				firstX = x;
				firstY = y;
			}		
			if(ang >= 352){
				x = firstX;
				y = firstY;
			}
			pg_nukleus.curveVertex(x,y);

		}
		pg_nukleus.endShape();


	}
	
	
	/* ***************************************
	 * INNER NUCLEUS
	 * ***************************************/	
	
	
	public void generateInnerNukleus(PGraphics pg_nInner){
		//create inner nuclei
		_nukleusArr = new float [numNukleus][5];
		
		for(int i = 0; i<numNukleus; i++){
			
			innerNukleusData(i);
	
		} 

		// check inner nuklei for collision
		checkCollision();
		
		//if no further collision occurs draw all inner nuklei
		drawInnerNukleus(pg_nInner); 		
	}
	
	// generate Data for each inner nucleus
	public void innerNukleusData(int i){

		if(radius < 90){
			radiusX = parent.random(3,radius/3);
			radiusY = parent.random(3, radius/3);
		}else{
			radiusX = parent.random(5,radius/3);
			radiusY = parent.random(5, radius/3);			
		}

		// bigger radius used for Collision Detection
		if(radiusX >= radiusY){
			colRadius = radiusX;
		}else{
			colRadius = radiusY;
		}
		
		// random position within body
		x = parent.random(centX-radius/2+colRadius,centX+radius/2-colRadius)+xPos;
		y = parent.random(centY-radius/2+colRadius,centY+radius/2-colRadius)+yPos;

		//fill array with data of inner nuclei
		_nukleusArr[i][0] = x;
		_nukleusArr[i][1] = y;
		_nukleusArr[i][2] = radiusX;
		_nukleusArr[i][3] = radiusY;
		_nukleusArr[i][4] = colRadius;
	}

	// check inner nuclei for collision
	public void checkCollision(){
		for (int i = 0; i<_nukleusArr.length; i++){
			for(int j=0; j<_nukleusArr.length; j++){
				if(_nukleusArr[j] != _nukleusArr[i]){
					float dis = PApplet.dist(_nukleusArr[i][0],_nukleusArr[i][1], _nukleusArr[j][0], _nukleusArr[j][1]);

					if(dis < (_nukleusArr[i][4] + _nukleusArr[j][4])){ 		// if they touch
						innerNukleusData(i);	//create new Nukleus Data
						i -= 1;			//and continue to test with new Nukleus 			
						break;
					}
				}
			}	
		}

	}

	public void drawInnerNukleus(PGraphics pg_nInner){

		pg_nInner.strokeWeight(2);
		pg_nInner.stroke(255);
		pg_nInner.fill(80);
		
		for(int i=0; i<_nukleusArr.length; i++){
			
			// rotate inner nuclei
			pg_nInner.pushMatrix();
			pg_nInner.translate(xPos, yPos);
			pg_nInner.rotate(parent.random(PConstants.TWO_PI));
			pg_nInner.translate(-xPos, -yPos);


			pg_nInner.ellipse(_nukleusArr[i][0],_nukleusArr[i][1], _nukleusArr[i][2], _nukleusArr[i][3]);
			
			pg_nInner.popMatrix();

		}


	}
	
	public float customNoise(float value){
		float retValue = parent.pow(parent.sin(value),3);	
		return retValue;
	}	
	/* ***************************************
	 * GEISSELN / LEGS
	 * ***************************************/
	
	public void generateGeisseln(PGraphics pg_geisseln, int geisselCol){
		_pos = new float[numGeisseln][2];
		segLength = 3; 
		int col = 255;
		int i = 0;
		float x, y;
		PVector pVec, tangent;
		float symAngle;		

		for(float ang = 0; ang < (numGeisseln*randAng)-1; ang += randAng){
			float rad = PApplet.radians(ang);

			//rotate around center with random degree
			x =  (radius/1.7f * PApplet.cos(rad));
			y =  (radius/1.7f * PApplet.sin(rad));

			//save position in array
			_pos[i][0] = x;
			_pos[i][1] = y;

			i++;
		}
		
		// Symmetry axis
		float firstX = _pos[0][0];
		float firstY = _pos[0][1];
		float lastX = _pos[_pos.length-1][0];
		float lastY = _pos[_pos.length-1][1];

		PVector symAxis = new PVector((firstX+lastX)/2, (firstY+lastY)/2);
		symAngle = PConstants.TWO_PI -(PVector.angleBetween(yAxis, symAxis) + PConstants.HALF_PI);
		
//		parent.stroke(255,0,0);
//		parent.strokeWeight(1);
//		parent.line((-symAxis.x*10), (-symAxis.y*10), center.x, center.y);
		
		for (int j=0; j<_pos.length; j++){
			x = _pos[j][0];
			y = _pos[j][1];
			pVec = new PVector (x,y);
						
			// tangent vector of each point
			tangent = new PVector(-y,x);
			tangent.mult(0.5f);

			// angle between point and yAxis
			float angle = PVector.angleBetween(yAxis, pVec);
			
			// correction to get values between 0 - 360
			angle = PApplet.radians(j*randAng);
			
			while(angle/ PConstants.TWO_PI >=1){
				angle -= PConstants.TWO_PI;
			}

			
			// determine on which side of symmetry axis 
			// if exactly or close (threshold) to symmetry axis, don't draw point
			
			float threshold = PApplet.radians(2);

			if (symAngle <= PConstants.PI){	// ascending axis
				up = true;
				if (angle >= PConstants.PI + PConstants.HALF_PI){	// point lies in 1. quadrant
					if (angle > symAngle + PConstants.PI + threshold){ // right side of axis
						parent.stroke(col);
						rSide = true;
					}else if (angle < symAngle + PConstants.PI - threshold){	// left side of axis
						parent.stroke(col);
						rSide = false;
					}else{
						parent.noStroke();
					}
				}else if (angle >= PConstants.PI && angle < PConstants.PI + PConstants.HALF_PI){ // point lies in 2. quadrant
					parent.stroke(col);	// left side of axis
					rSide = false;
				}else if (angle >= PConstants.HALF_PI && angle < PConstants.PI){ // point lies in 3. quadrant
					if(angle < symAngle - threshold){	// right side of axis
						parent.stroke(col);
						rSide = true;

					}else if (angle > symAngle + threshold){		// left side of axis
						parent.stroke(col);
						rSide = false;
					}else{
						parent.noStroke();
					}
				}else if (angle < PConstants.HALF_PI){ // point lies in 4. quadrant
					parent.stroke(col);	// right side of axis
					rSide = true;
				}
				
			}else if (symAngle > PConstants.PI){	// descending axis
				up = false;
				if (angle >= PConstants.PI + PConstants.HALF_PI){	// point lies in 1. quadrant
					parent.stroke(col);	// right side of axis
					rSide = true;

				}else if (angle >= PConstants.PI && angle < PConstants.PI + PConstants.HALF_PI){ // point lies in 2. quadrant
					if(angle >= symAngle + threshold){	// right side of axis
						parent.stroke(col);
						rSide = true;
					}else if(angle < symAngle - threshold){
						parent.stroke(col);	// left side of axis
						rSide = false;
					}else{
						parent.noStroke();
					}
				}else if (angle >= PConstants.HALF_PI && angle < PConstants.PI){  // point lies in 3. quadrant
					parent.stroke(col);	// left side of axis
					rSide = false;
					
				}else if (angle < PConstants.HALF_PI){  	// point lies in 4. quadrant
					if(angle < symAngle - PConstants.PI - threshold){  // right side of axis
						parent.stroke(col);						
						rSide = true;
					}else if(angle >symAngle - PConstants.PI + threshold){	// left side of axis
						parent.stroke(col);
						rSide = false;
					}else{
						parent.noStroke();
					}
				}
			}
			
			
			
			// angle to rotate legs along tangent
			angleRot = PVector.angleBetween(pVec, yAxis);

			// correction to get values 0 - 360
			while(angleRot/ PConstants.TWO_PI >=1){
				angleRot -= PConstants.TWO_PI;
			}
			
			
			// rotate legs so they point in one direction (vector of symmetry axis)
			if(up){
				if(x>0){
					angleRot *= -1;
				}
				if(rSide){
					angleRot -= PConstants.PI;
				}
			}else if(!up){
				if(rSide){
					if(x>0){
						angleRot *= -1;
						}
				}else if (!rSide){
					if(x>0){
						angleRot *=-1;
					}					
					angleRot -= PConstants.PI;	
				}
			}
			
			// angle between point and symmetry axis
			angleOr = (PVector.angleBetween(pVec, symAxis));
			if (!up){
				angleOr = -(angleOr - PConstants.PI);
			}
			
			// angle for curvature of legs
			// buffer angle for inner legs
			if(geisselCol == 255){
				angle2 = parent.random(0.1f, 0.2f) ;
				angle2Arr[j] = angle2;

			}else{
				angle2 = angle2Arr[j];
			}
			
			
			
			// the closer a leg is to the symmetry axis
			// - the longer it gets
			// - the greater the deviation from its tangent 
			
			//je näher Geissel an Symmetrie-Achse ist, desto stärker gleicht sich
			//die Rotation dem Richtungsvektor der Symmetrie-Achse an
			if(!up){
				if(angleOr <= PConstants.HALF_PI){
					deviation = PConstants.HALF_PI - angleOr + ((segNum-1)/2 *angle2);
					segLength += angle;
					
				}else{
					deviation = segNum/2 * angle2;
				}
				if(rSide){
					angleRot += deviation;
					
				}else{
					angleRot -= deviation;
				}
			}else if(up){
				
				if(angle > PConstants.TWO_PI){
					deviation = PConstants.HALF_PI - angleOr + ((segNum-1)/2*angle2);
				}else{
					deviation = segNum/2 * angle2;
					segLength += angleOr*2;
				}
				if(rSide){
					angleRot -= deviation ;
				}else{
					angleRot += deviation;
							
				}

			}
			
		drawGeissel(x,y, pg_geisseln, geisselCol);

	}	

		
		
	}
	
	public void geisselSegment(float x, float y, float a, PGraphics pg_geisseln, int col, int alpha){
		pg_geisseln.translate(x, y);
		pg_geisseln.rotate(a);
		pg_geisseln.noFill();
		pg_geisseln.stroke(col, alpha);
		pg_geisseln.line(0,0, segLength, 0);		
	}

	public void drawGeissel(float x, float y,  PGraphics pg_geisseln, int geisselCol){

		x += xPos;		// correction for PGraphics Object
		y += yPos;
		

		
		float stroke_counter;
		float strokeStep;
		int alpha = 255;
		pg_geisseln.pushMatrix();

		angle1 = angleRot;
		if(rSide){
			angle2 *= -1;
		}
		
		if(geisselCol == 255){
			strokeStep = 1.1f;
			
		}else{
			strokeStep = 0.15f;
		}
		
		stroke_counter=segNum/(1/strokeStep);
		
		pg_geisseln.strokeWeight(stroke_counter);
		geisselSegment(x, y, angle1, pg_geisseln, geisselCol, alpha);
		
		for(int i=0; i<segNum; i++){
			if(stroke_counter >= strokeStep){
				stroke_counter -= strokeStep;
			}
			if(geisselCol == 255){
				alpha -= 30;
			}
			pg_geisseln.strokeWeight(stroke_counter);
			geisselSegment(segLength, 0, angle2, pg_geisseln, geisselCol, alpha);
		}
		
		pg_geisseln.popMatrix();
		segLength = 3;

	}
}
