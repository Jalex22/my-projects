
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;

public class Sun {
	private GL2 gl;
	private GLU glu;
	private Texture sunTexture;
	private float angle = 0;

	public Sun(GL2 gl, GLU glu, Texture sunTexture) {
		this.gl = gl;
		this.glu = glu;
		this.sunTexture = sunTexture;
	}

	public void display() 
	{
		float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);
		
		// sun texture.
		sunTexture.enable(gl);
		sunTexture.bind(gl);
		gl.glPushName(6);
		angle = (angle + 0.7f) % 360f;

		gl.glPushMatrix();
		//gl.glRotatef(angle, 0.8f, 0.1f, 0);

		// Draw sun
		gl.glColor3f(1f, 1f, 1f);
		GLUquadric sun = glu.gluNewQuadric();
		glu.gluQuadricTexture(sun, true);
		glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
		glu.gluQuadricNormals(sun, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sun, GLU.GLU_OUTSIDE);
		
		glu.gluSphere(sun, 20f, 16, 16);
		
		glu.gluDeleteQuadric(sun);

		gl.glPopMatrix();
		gl.glPopName();
	}

}
