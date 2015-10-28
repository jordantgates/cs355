package CS355.LWJGL;


//You might notice a lot of imports here.
//You are probably wondering why I didn't just import org.lwjgl.opengl.GL11.*
//Well, I did it as a hint to you.
//OpenGL has a lot of commands, and it can be kind of intimidating.
//This is a list of all the commands I used when I implemented my project.
//Therefore, if a command appears in this list, you probably need it.
//If it doesn't appear in this list, you probably don't.
//Of course, your milage may vary. Don't feel restricted by this list of imports.

import java.util.Iterator;

import org.lwjgl.input.Keyboard;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;

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
import static org.lwjgl.opengl.GL11.glViewport; //intialize viewport
import static org.lwjgl.opengl.GL11.glOrtho; //manipulates current matrix with parallel projection, 6 params

import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 *
 * @author Brennan Smith
 */
public class StudentLWJGLController implements CS355LWJGLController 
{

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
		glViewport(0,0,640,480);
		glMatrixMode(GL_MODELVIEW);
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
	public void updateKeyboard() 
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) 
		{
			System.out.println("You are pressing W!");
		}
		//perspective changes
		if(Keyboard.isKeyDown(Keyboard.KEY_O)){
			glMatrixMode(GL_MODELVIEW);
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_P)){

			glMatrixMode(GL_PROJECTION);
		}


	}

	//This method is the one that actually draws to the screen.
	@Override
	public void render() 
	{
		//This clears the screen.
		glClear(GL_COLOR_BUFFER_BIT);
		Iterator<Line3D> lines= model.getLines();
		Line3D currentLine;

		while(lines.hasNext()){
			currentLine=lines.next();
			glBegin(GL_LINES);
			glColor3f(1f,1f,1f);
			glVertex3d(currentLine.start.x,currentLine.start.y,currentLine.start.z);
			glVertex3d(currentLine.end.x,currentLine.end.y,currentLine.end.z);
			glEnd();

		}
	}

}
