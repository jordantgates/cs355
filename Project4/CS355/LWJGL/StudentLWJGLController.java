package CS355.LWJGL;


//You might notice a lot of imports here.
//You are probably wondering why I didn't just import org.lwjgl.opengl.GL11.*
//Well, I did it as a hint to you.
//OpenGL has a lot of commands, and it can be kind of intimidating.
//This is a list of all the commands I used when I implemented my project.
//Therefore, if a command appears in this list, you probably need it.
//If it doesn't appear in this list, you probably don't.
//Of course, your milage may vary. Don't feel restricted by this list of imports.

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POLYGON;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glMatrixMode; //set matrix to modelview vs projection


import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glBegin; //Begins the definition of vertex attributes of a sequence of primitives to be transferred to the GL.
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3d; 

import static org.lwjgl.opengl.GL11.glLoadIdentity; //loads identity matrix, no param
import static org.lwjgl.opengl.GL11.glPushMatrix; //pushes clone of top matrix onto stack, no param
import static org.lwjgl.opengl.GL11.glRotatef; // rotates, angle and 3 components of vector
import static org.lwjgl.opengl.GL11.glTranslatef; // translate x, y, z amounts
import static org.lwjgl.opengl.GL11.glViewport; //initialize viewport

import static org.lwjgl.opengl.GL11.glOrtho; //manipulates current matrix with parallel projection, 6 params
import static org.lwjgl.util.glu.GLU.gluPerspective; //set projection

/**
 *
 * @author Brennan Smith
 */
public class StudentLWJGLController implements CS355LWJGLController 
{
	private final float moveM= .4f;//move multiplier

	private modelTrans camera;
	private List<modelTrans> neighboorhood;

	private int step;
	private boolean currentlyPerspective;
	private final boolean runningWithWarp=true;

	private float aspect;
	private int displayH;
	private int displayW;
	//This is a model of a house.
	//It has a single method that returns an iterator full of Line3Ds.
	//A "Line3D" is a wrapper class around two Point2Ds.
	//It should all be fairly intuitive if you look at those classes.
	//If not, I apologize.
	private WireFrame model = new HouseModel();

	//This method is called to "resize" the viewport to match the screen.
	//When you first start, have it be in perspective mode.
	@Override
	public void resizeGL() 
	{
		displayH=LWJGLSandbox.DISPLAY_HEIGHT;
		displayW=LWJGLSandbox.DISPLAY_WIDTH;
		camera=new modelTrans(0,-5,-15,0);
		aspect=((float)displayW)/((float)displayH);
		glViewport(0,0,displayW ,displayH);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(45f,aspect,.1f,1000.0f);
		currentlyPerspective=true;
		glMatrixMode(GL_MODELVIEW);
		//clear matrix

		neighboorhood=generateGrid();
	}

	@Override
	public void update() 
	{

	}

	//This is called every frame, and should be responsible for keyboard updates.
	//An example keyboard event is captured below.
	//The "Keyboard" static class should contain everything you need to finish
	// this up.
	@Override
	public void updateKeyboard() {
		boolean changed=false;
		//home
		if(Keyboard.isKeyDown(Keyboard.KEY_H)) 
		{
			// return to home position
			camera.x=0;
			camera.y=-5;
			camera.z=-15;
			camera.r=0;
			changed=true;
		}
		//perspective changes
		if(Keyboard.isKeyDown(Keyboard.KEY_O)){
			if(runningWithWarp&&currentlyPerspective){
				currentlyPerspective=false;
				animate(true);
			}
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(-8.0*aspect,8.0*aspect,-8,8,.1,1000);

			glMatrixMode(GL_MODELVIEW);

		}
		if(Keyboard.isKeyDown(Keyboard.KEY_P)){
			//switch to perspective
			currentlyPerspective=true;
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			gluPerspective(45f,aspect,.1f,1000.0f);
			glMatrixMode(GL_MODELVIEW);

		}

		//translation
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			//move left (left from orientations)
			camera.x+=moveM*Math.cos(camera.r*(Math.PI/180.0));
			camera.z+=moveM*Math.sin(camera.r*(Math.PI/180.0)); 
			//myX--;


			changed=true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			//move right
			camera.x-=moveM*Math.cos(camera.r*(Math.PI/180.0));
			camera.z-=moveM*Math.sin(camera.r*(Math.PI/180.0)); 


			changed=true;

		}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			//move forward
			camera.x-=moveM*Math.sin(camera.r*(Math.PI/180.0));
			camera.z+=moveM*Math.cos(camera.r*(Math.PI/180.0)); 

			changed=true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			//move backward
			camera.x+=moveM*Math.sin(camera.r*(Math.PI/180.0));
			camera.z-=moveM*Math.cos(camera.r*(Math.PI/180.0)); 

			changed=true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_R)){
			//move up
			camera.y-=moveM*1;

			changed=true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			//move down
			camera.y+=moveM*1;

			changed=true;
		}

		//turn
		if(Keyboard.isKeyDown(Keyboard.KEY_Q)){
			//turn left
			camera.r--;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_E)){
			//turn right
			camera.r++;

		}
		if(changed)
			System.out.println(camera);
	}



	//This method is the one that actually draws to the screen.
	@Override
	public void render() 
	{
		//This clears the screen.
		glClear(GL_COLOR_BUFFER_BIT);
		for(modelTrans thisModel:neighboorhood){


			//apply translations
			glLoadIdentity();
			glMatrixMode(GL_MODELVIEW);

			glRotatef(camera.r,0,1,0);//applyCamera rotation
			glTranslatef(camera.x,camera.y,camera.z); //apply Camera Translation
			glTranslatef(thisModel.x,thisModel.y,thisModel.z); //apply model translation
			glRotatef(thisModel.r,0,1,0); //apply model rotation

			Iterator<Line3D> lines= model.getLines();
			Line3D currentLine;

			while(lines.hasNext()){
				currentLine=lines.next();

				glBegin(GL_LINES);
				glColor3f(1f,1f,1f);
				glVertex3d(currentLine.start.x,currentLine.start.y,currentLine.start.z);
				glVertex3d(currentLine.end.x,currentLine.end.y,currentLine.end.z);
				glEnd();

				/*glColor3f(1f,1f,0);
				glBegin(GL_POLYGON);
				glVertex3d(.01,.05,.05);
				glVertex3d(-.01,.05,-.05);
				glVertex3d(-.01,-.05,-.05);
				glVertex3d(.01,-.05,.05);
				glEnd();*/

			}
		}

	}
	private void animate(boolean fromPerspective) {
		float preZ=camera.z;
		float preX=camera.x;
		float initHeight=FrustumHeightAtDistance(camera.distantceFromOrigin());
		float steps=1000f;
		float FOV, distance, initDistance, xStepDist, zStepDist;
		initDistance=camera.distantceFromOrigin();
		float j;
		for(int i=0;i<steps;i=i+10){
			if(fromPerspective){
				j=i;
			}
			else
			{
				j=i-steps;
			}
			xStepDist=(float)(j*Math.sin(camera.r*(Math.PI/180.0))/10);
			zStepDist=(float)(j*Math.cos(camera.r*(Math.PI/180.0))/10); 
			distance=camera.distantceFromOrigin()+10-initDistance;

			if(true){
				camera.x+=xStepDist;
				camera.z-=zStepDist;
				FOV=FOVForHeightAndDistance((initHeight*(steps-i)/steps+16f*(i/steps)),distance);

			}

			FOV=FOVForHeightAndDistance((initHeight*(steps-i)/steps+16f*(i/steps)),distance);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();

			gluPerspective(FOV,aspect,.1f+distance,1000.0f+distance);

			glMatrixMode(GL_MODELVIEW);
			render();
			Display.update();
		}
		camera.z=preZ;
		camera.x=preX;


	}
	private float FrustumHeightAtDistance(float distance) {
		return 2.0f * distance * (float)Math.tan(45 * 0.5f * Math.PI/180.0);
	}
	private float FOVForHeightAndDistance(float height, float distance) {
		return (float)( 2.0f * Math.atan(height * 0.5f / distance) * 180.0f/Math.PI);
	}

	private List<modelTrans> generateGrid() {
		ArrayList<modelTrans> result=new ArrayList<modelTrans>();
		result.add(new modelTrans(0,0,0,0));
		result.add(new modelTrans(0,0,30,180));
		result.add(new modelTrans(-13,0,0,0));
		result.add(new modelTrans(-13,0,30,180));
		result.add(new modelTrans(-26,0,0,0));
		result.add(new modelTrans(-26,0,30,180));

		result.add(new modelTrans(-41,0,7,45));
		result.add(new modelTrans(-41,0,23,135));

		result.add(new modelTrans(13,0,0,0));
		result.add(new modelTrans(13,0,30,180));
		result.add(new modelTrans(26,0,0,0));
		result.add(new modelTrans(26,0,30,180));

		result.add(new modelTrans(39,0,-2,20));
		result.add(new modelTrans(39,0, 28,195));
		return result;
	}
	private class modelTrans{
		public float x;
		public float y;
		public float z;
		public float r;

		public modelTrans(){
			x=0f;
			y=0f;
			z=0f;
			r=0f;
		}
		public modelTrans(float X, float Y, float Z, float R){
			x=X;
			y=Y;
			z=Z;
			r=R;
		}
		public String toString(){
			return "Position: x="+(double)x+" y="+(double)y+" z="+(double)z+" Rotation="+r;
		}
		public float distantceFromOrigin(){
			return (float)Math.sqrt((x*x)+(y*y)+(z*z));
		}
	}

}
