
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;

import javax.swing.*;

public class Planet {
	private GL2 gl;
	private Texture myFace;
	private float angle;
	private float distance;
	private float rotationAngle = 0;
	private float speed = 0;


	public Planet(GL2 gl, Texture planetTexture, float speed, float distance) {
		this.gl = gl;
		this.myFace = planetTexture;
		this.speed = speed;
		this.distance = distance;
	}

	public void display() {
		gl.glPushMatrix();
		angle = (angle + speed) % 360f;
		final float x = (float) Math.sin(Math.toRadians(angle)) * distance;
		final float y = 0;
		final float z = (float) Math.cos(Math.toRadians(angle)) * distance;

		gl.glTranslatef(x, y, z);

		drawPrism();

		gl.glPopMatrix();

	}
	
	//draw and texture pentagonal prism planet
	void drawPrism()
	{	
		// Apply texture
		myFace.enable(gl);
		myFace.bind(gl);
		
		rotationAngle = (rotationAngle + 0.1f) % 360f;

		gl.glPushMatrix();
		gl.glRotatef(rotationAngle, 0.2f, 0.1f, 0);
		
		//draw top pentagon
		gl.glBegin(gl.GL_POLYGON);
			//top pentagon
			gl.glVertex3f(0f, -10f, 12.5f);
			gl.glVertex3f(-10f, -3f, 12.5f);
			gl.glVertex3f(-6f, 8f, 12.5f);
			gl.glVertex3f(6f, 8f, 12.5f);
			gl.glVertex3f(10f, -3f, 12.5f);
		gl.glEnd();
		gl.glFlush();
		
		//draw bottom pentagon
		gl.glBegin(gl.GL_POLYGON);
			gl.glVertex3f(0f, -10f, -12.5f);
			gl.glVertex3f(-10f, -3f, -12.5f);
			gl.glVertex3f(-6f, 8f, -12.5f);
			gl.glVertex3f(6f, 8f, -12.5f);
			gl.glVertex3f(10f, -3f, -12.5f);
		gl.glEnd();
		gl.glFlush();
		
		//draw and texture rectangular faces
		gl.glBegin(gl.GL_QUADS);
			//1st rect face
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(0f, -10f, 12.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(-10f, -3f, 12.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(-10f, -3f, -12.5f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(0f, -10f, -12.5f);
			
			//2nd rect face
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(-10f, -3f, 12.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(-6f, 8f, 12.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(-6f, 8f, -12.5f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(-10f, -3f, -12.5f);
			
			//3rd rect face
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(-6f, 8f, 12.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(6f, 8f, 12.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(6f, 8f, -12.5f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(-6f, 8f, -12.5f);
			
			//4th rect face
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(6f, 8f, 12.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(10f, -3f, 12.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(10f, -3f, -12.5f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(6f, 8f, -12.5f);
			
			//5th rect face
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(10f, -3f, 12.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(0f, -10f, 12.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(0f, -10f, -12.5f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(10f, -3f, -12.5f);
		gl.glEnd();
		
		myFace.disable(gl);
		gl.glPopMatrix();
		
	}

}