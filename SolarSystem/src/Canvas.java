
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;


public class Canvas extends GLCanvas implements GLEventListener, KeyListener {
	private static final float SUN_RADIUS = 12f;
	private FPSAnimator animator;
	private GLU glu;
	private Texture earthTexture;
	private Texture planetTexture;
	private Texture moonTexture;
	private Texture faceTexture;
	private Texture skyTexture; //skybox texture
	private ArrayList<Planet> planets; //if i ever come back and add more planets
	private float Angle = 0;
	private float earthAngle = 0;
	private float systemAngle = 0;
	private float planetAngle = 0;
	private Sun sun;
	private boolean drawAxis = false;
	private boolean setLight = false;

	// Set camera orientation
	float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;
	float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;
	float cameraCoordsPosx = 0.0f, cameraCoordsPosy = 0.0f, cameraCoordsPosz = -20.0f;

	public Canvas(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		planets = new ArrayList<>();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glClearColor(0f, 0f, 0f, 0f);

		this.addKeyListener(this);
		animator = new FPSAnimator(this, 60);
		animator.start();
		
		// add stuff to solar system
		String textureFile = "earthTex.jpg";
		earthTexture = getObjectTexture(gl, textureFile);
		
		textureFile = "sunTex.jpg";
		this.sun = new Sun(gl, glu, getObjectTexture(gl, textureFile));
		
		textureFile = "moonTex.png";
		moonTexture = getObjectTexture(gl, textureFile);

		textureFile = "planetTex.jpg";
		planetTexture = getObjectTexture(gl, textureFile);
		
		textureFile = "faceTex.JPG";
		faceTexture = getObjectTexture(gl, textureFile);
		Planet myFace= new Planet(gl, faceTexture, 0.3f, SUN_RADIUS + 120f);
		planets.add(myFace);
		
		//skybox texture
		textureFile = "starTex.png";
		skyTexture = getObjectTexture(gl, textureFile);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {

	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		if (!animator.isAnimating()) {
			return;
		}

		final GL2 gl = glAutoDrawable.getGL().getGL2();

		setCamera(gl, 300);
		aimCamera(gl, glu);
		moveCamera();
		//set lighting, ambient, and positional light coming from the sun
		setLights(gl);
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		//draw the solar system
		sun.display();
		drawEarthAndMoon(gl);
		drawPlanetAndMoon(gl);
		for (Planet p : planets)
			p.display();
		
		//skybox texture
		skyTexture.bind(gl);
		skyTexture.enable(gl);
		//draw skybox
		drawSkyBox(gl);
		
		//draw axis lines
		//drawXYZ();
		
	}
	
	//draw the earth/moon system
	private void drawEarthAndMoon(GL2 gl) {
		gl.glPushMatrix();
		systemAngle = (systemAngle + 0.4f) % 360f;

		final float distance = SUN_RADIUS + 45f;
		final float x = (float) Math.sin(Math.toRadians(systemAngle)) * distance;
		final float y = 0;
		final float z = (float) Math.cos(Math.toRadians(systemAngle)) * distance;
		gl.glTranslatef(x, y, z);  //rotates earth/moon system around sun

		drawEarth(gl);
		drawMoon(gl);
		gl.glPopMatrix();

	}

	//draw the earth
	private void drawEarth(GL2 gl) 
	{
		//material for better handling light
		float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		
		gl.glPushName(4);
		earthAngle = (earthAngle + 0.3f) % 360f;  

		gl.glPushMatrix();
		gl.glRotatef(earthAngle, 0.4f, 0.2f, 0);  //rotate earth on axis

		//add earth texture
		earthTexture.enable(gl);
		earthTexture.bind(gl);
		
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricTexture(earth, true);
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);

		glu.gluSphere(earth, 7.f, 16, 16);
		
		gl.glPopName();
		glu.gluDeleteQuadric(earth);
		gl.glPopMatrix();
	}

	// draw earth's moon
	private void drawMoon(GL2 gl) 
	{
		//material for better handling light
		float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

		gl.glPushMatrix();
		//add texture
		moonTexture.enable(gl);
		moonTexture.bind(gl);
		
		gl.glPushName(5);
		Angle = (Angle + 1f) % 360f;
		
		final float distance = 15f;
		final float x = (float) Math.sin(Math.toRadians(Angle)) * distance;
		final int y = (int) ((float) Math.cos(Math.toRadians(Angle)) * distance);
		final float z = 0;
		gl.glTranslatef(x, y, z); //rotate around earth
		gl.glRotatef(Angle, 0, 0, -1);  //rotate moon on axis
		gl.glRotatef(45f, 0, 1, 0);
		
		GLUquadric moon = glu.gluNewQuadric();
		glu.gluQuadricTexture(moon, true);
		glu.gluQuadricDrawStyle(moon, GLU.GLU_FILL);
		glu.gluQuadricNormals(moon, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(moon, GLU.GLU_INSIDE);
		glu.gluSphere(moon, 3.5f, 16, 16);

		gl.glPopMatrix();
		gl.glPopName();
	}
	
	private void drawPlanetAndMoon(GL2 gl) {
		gl.glPushMatrix();
		planetAngle = (planetAngle + 0.2f) % 360f;

		final float distance = SUN_RADIUS + 90f;
		final float x = (float) Math.sin(Math.toRadians(planetAngle)) * distance;
		final float y = 0;
		final float z = (float) Math.cos(Math.toRadians(planetAngle)) * distance;
		gl.glTranslatef(x, y, z);  //rotates planet/moon system around sun

		drawPlanet(gl);
		drawMoonP(gl);
		gl.glPopMatrix();

	}
	
	//draw other planet
	private void drawPlanet(GL2 gl) 
	{
		////material for better handling light
		float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		
		gl.glPushName(4);
		planetAngle = (planetAngle + 0.5f) % 360f;

		gl.glPushMatrix();
		gl.glRotatef(planetAngle, 0.5f, 0.8f, 0); //rotate planet on axis
		
		
		//add planet texture
		planetTexture.enable(gl);
		planetTexture.bind(gl);

		GLUquadric planet = glu.gluNewQuadric();
		glu.gluQuadricTexture(planet, true);
		glu.gluQuadricDrawStyle(planet, GLU.GLU_FILL);
		glu.gluQuadricNormals(planet, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(planet, GLU.GLU_OUTSIDE);

		glu.gluSphere(planet, 9f, 16, 16);
	
		gl.glPopName();
		glu.gluDeleteQuadric(planet);
		gl.glPopMatrix();
	}
	
	//draw other planet's moon
	private void drawMoonP(GL2 gl) {

		gl.glPushMatrix();
		//add texture
		moonTexture.enable(gl);
		moonTexture.bind(gl);
		
		gl.glPushName(5);
		Angle = (Angle + 1f) % 360f;
		
		final float distance = 15f;
		final float x = 0;
		final int y = (int) ((float) Math.sin(Math.toRadians(Angle)) * distance);
		final float z = (float) Math.cos(Math.toRadians(Angle)) * distance;
		gl.glTranslatef(x, y, z);  //rotate around planet
		gl.glRotatef(Angle, 0, 0, -1);  //rotate moon on axis
		gl.glRotatef(45f, 0, 1, 0);

		
		GLUquadric moon = glu.gluNewQuadric();
		glu.gluQuadricTexture(moon, true);
		glu.gluQuadricDrawStyle(moon, GLU.GLU_FILL);
		glu.gluQuadricNormals(moon, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(moon, GLU.GLU_INSIDE);
		glu.gluSphere(moon, 3.5f, 16, 16);

		gl.glPopMatrix();
		gl.glPopName();
	}

		// draw stars skybox
		private void drawSkyBox(GL gl) 
		{
			skyTexture.enable(gl);
			skyTexture.bind(gl);

			final float radius = 150f;
			final int slices = 16;
			final int stacks = 16;
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_DST_ALPHA);
			GLUquadric sky = glu.gluNewQuadric();
			glu.gluQuadricTexture(sky, true);
			glu.gluQuadricDrawStyle(sky, GLU.GLU_FILL);
			glu.gluQuadricNormals(sky, GLU.GLU_FLAT);
			glu.gluQuadricOrientation(sky, GLU.GLU_INSIDE);
			glu.gluSphere(sky, radius, slices, stacks);
			
			gl.glDisable(GL.GL_BLEND);	//fixes transparency problem

		}
	
	// gets the texture for a planet
	private Texture getObjectTexture(GL2 gl, String fileName) {
		InputStream stream = null;
		Texture tex = null;
		String extension = fileName.substring(fileName.lastIndexOf('.'));
		try {
			stream = new FileInputStream(new File(fileName));
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream, false, extension);
			tex = TextureIO.newTexture(data);
		} catch (FileNotFoundException e) {
			System.err.println("Error loading the file!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO Exception!");
			e.printStackTrace();
		}

		return tex;
	}

		//add ambient and positional light
		private void setLights(GL2 gl) {
			
			if(setLight == true) //light on, spacebar
			{
				//yellow point but it is inside sun
				gl.glColor3f(1.0f, 1.0f, 0.0f);
				gl.glPointSize(10.0f);
				gl.glBegin(GL.GL_POINTS);
					gl.glVertex3f(0f, 0f, 0f);
				gl.glEnd();
				gl.glFlush();
				
				float[] lightPos = { 0, 0, 0, 1 };
				float[] ambient = { 0.5f, 0.5f, 0.5f, 1f };
				float[] specular = { 0.8f, 0.8f, 0.8f, 1f };  //light comes from sun (sunlight)
				
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);

				gl.glEnable(GL2.GL_LIGHT1);
				gl.glEnable(GL2.GL_LIGHTING);
			}
			else //light off, spacebar
			{
				float[] lightPos = { 0, 0, 0, 0 };
				float[] ambient = { 0.5f, 0.5f, 0.5f, 1f };
				float[] specular = { 0f, 0f, 0f, 1f };
				
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, specular, 0);

				gl.glEnable(GL2.GL_LIGHT1);
				gl.glEnable(GL2.GL_LIGHTING);
			}
		}
	
	private void setCamera(GL2 gl, float distance) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
		glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();
		gl.glViewport(80, 80, width, height);
	}

	public void moveCamera() {
		float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);

		cameraCoordsPosx += tmp[0];
		cameraCoordsPosy += tmp[1];
		cameraCoordsPosz += tmp[2];
	}

	public void aimCamera(GL2 gl, GLU glu) {
		gl.glLoadIdentity();

		float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);

		float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);

		cameraUpx = camUp[0];
		cameraUpy = camUp[1];
		cameraUpz = camUp[2];

		glu.gluLookAt(cameraCoordsPosx, cameraCoordsPosy, cameraCoordsPosz, cameraCoordsPosx + tmp[0],
				cameraCoordsPosy + tmp[1], cameraCoordsPosz + tmp[2], cameraUpx, cameraUpy, cameraUpz);
	}

	private float[] polarToCartesian(float azimuth, float length, float altitude) {
		float[] result = new float[3];
		float x, y, z;

		// Do x-z calculation
		float theta = (float) Math.toRadians(90 - azimuth);
		float tantheta = (float) Math.tan(theta);
		float radian_alt = (float) Math.toRadians(altitude);
		float cospsi = (float) Math.cos(radian_alt);

		x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
		z = tantheta * x;

		x = -x;

		if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
			x = -x;
			z = -z;
		}

		// Calculate y, and adjust x and z
		y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));

		if (length < 0) {
			x = -x;
			z = -z;
			y = -y;
		}

		x = x * cospsi;
		z = z * cospsi;

		result[0] = x;
		result[1] = y;
		result[2] = z;

		return result;
	}
	//draw the world axes
	public void drawXYZ()
	{
		 GL2 gl = (GL2) GLContext.getCurrentGL();
		 if(drawAxis == true) 
		 {
			 // x
			 gl.glLineWidth(2f);
			 gl.glColor3f(1.0f,0.0f,0.0f); // red x
			 gl.glBegin(GL.GL_LINES);
			 	gl.glVertex3f(-100.0f, 0.0f, 0.0f);
			 	gl.glVertex3f(100.0f, 0.0f, 0.0f);
			 gl.glEnd();
			 gl.glFlush();
	 
			 // y 
			 gl.glLineWidth(2f);
			 gl.glColor3f(0.0f,1.0f,0.0f); // green y
			 gl.glBegin(GL.GL_LINES);
			 	gl.glVertex3f(0.0f, -100.0f, 0.0f);
			 	gl.glVertex3f(0.0f, 100.0f, 0.0f); 
			 	gl.glEnd();
			 	gl.glFlush();
	 
			 	// z 
			 	gl.glLineWidth(2f);
			 	gl.glColor3f(0.0f,0.0f,1.0f); // blue z
			 	gl.glBegin(GL.GL_LINES);
			 		gl.glVertex3f(0.0f, 0.0f ,-100.0f);
			 		gl.glVertex3f(0.0f, 0.0f , 100.0f);
			 	gl.glEnd();
			 	gl.glFlush();
		 }
	}

	// move camera with keys
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_UP) {
			cameraElevation -= 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			cameraElevation += 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
			cameraAzimuth -= 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_LEFT) {
			cameraAzimuth += 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_A) {
			cameraCoordsPosx += 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_D) {
			cameraCoordsPosx -= 2;
		}
		
		if (event.getKeyCode() == KeyEvent.VK_E) {
			cameraCoordsPosy -= 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_Q) {
			cameraCoordsPosy += 2;
		}
		
		if (event.getKeyCode() == KeyEvent.VK_W) {
			cameraCoordsPosz += 2;
		}

		if (event.getKeyCode() == KeyEvent.VK_S) {
			cameraCoordsPosz -= 2;
		}
		
		if (event.getKeyCode() == KeyEvent.VK_SPACE) {
			if(drawAxis == false)	//control axis
				drawAxis = true;
			else
				drawAxis = false;
			if(setLight == false)	//control light
				setLight = true;
			else
				setLight = false;
		}

		if (event.getKeyCode() < 250)
			keys[event.getKeyCode()] = true;

		if (cameraAzimuth > 359)
			cameraAzimuth = 1;

		if (cameraAzimuth < 1)
			cameraAzimuth = 359;
	}

	private boolean[] keys = new boolean[250];

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
