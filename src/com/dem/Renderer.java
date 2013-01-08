package dem;

import javax.media.opengl.GL;
import com.sun.opengl.util.GLUT;

import openglwindow.OpenGLWindow;

/**
 * @ Ermias Beyene
 */
public class Renderer extends OpenGLWindow {
	private DEM dem;
	private int displayList;
	private int displayListTree;

	public Renderer(){	
	}

	public Renderer(DEM dem){
		this.dem=dem;
	}

	public void init(GL gl) {
		gl.glEnable(GL.GL_NORMALIZE);
		createDisplayList(gl);
	}

	public void lights(GL gl){
		light1(gl);
		gl.glEnable(GL.GL_LIGHTING);
	}

	public void render(GL gl) {
		gl.glPushMatrix();
		gl.glColor3d(0.0f, 0.0f, 0.0f);
		gl.glCallList(displayList);
		forest(gl);
		gl.glPopMatrix();
	}
	
	private boolean createDisplayList(GL gl){
		displayList= gl.glGenLists(1);
		if(displayList !=0){
			gl.glNewList(displayList, GL.GL_COMPILE);
			triangleStrips(gl);
			gl.glEndList();
		}
		displayListTree= gl.glGenLists(1);
		if(displayListTree !=0){
			gl.glNewList(displayListTree, GL.GL_COMPILE);
			tree(gl);
			gl.glEndList();
			return true;
		}
		return false;
	}

	/**
	 * draw the landscape as a canvas of triangles using the triangle strip primitive.
	 */
	private void triangleStrips(GL gl){
		
		materials(gl);
		float data, data1;
		gl.glLineWidth(1.0f);
		gl.glColor3d(0.5f, 1.0f, 0.2f);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(1.0f, 1.0f);
		for(int i=0; i<dem.getnRows()-1;i++){
			gl.glBegin(GL.GL_TRIANGLE_STRIP);
			for(int j=0; j<dem.getnColumns();j++){
				data=dem.getData(i, j);
				data1=dem.getData(i+1, j);
				gl.glNormal3fv(dem.getNormalAT(i, j), 0);
				gl.glVertex3f((float)i/100- dem.getnRows()/200,(float)j/100- dem.getnColumns()/200,data/600.f);
				gl.glNormal3fv(dem.getNormalAT(i + 1, j), 0);
				gl.glVertex3f((float)(i+1)/100- dem.getnRows()/200,(float)j/100- dem.getnColumns()/200,data1/600.f);
			}
			gl.glEnd();	
		}
	}
//	We popluate the landscape with trees
	private void forest(GL gl){
		for(int row=20; row<dem.getnRows();row+=40){
			for(int column=20; column<dem.getnColumns(); column+=40){
				float z = dem.getData(row, column);
				if (z!=0) {
					gl.glPushMatrix();
					gl.glTranslated((float)row/100- dem.getnRows()/200,(float)column/100- dem.getnColumns()/200,z/600.0f);
					gl.glCallList(displayListTree);
					gl.glPopMatrix();}
			}
		}
	}
	/**
	 * Draw the tree object  using 1 cylinder for trunk 
	 * and 3 cones for leaves separated by translation
	 * material is defined for the trunk and leaf separately
	 */
	public void tree(GL gl){
		
		GLUT glut = new GLUT();
		gl.glPushMatrix();
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(1.0f, 1.0f);
		gl.glPushMatrix();
		gl.glScalef(0.1f, 0.1f, 0.1f);
		//set material for the tree trunk
		//draw the trunk using GLUT cylinder
		gl.glPushMatrix();
		trunkMaterials(gl);
		glut.glutSolidCylinder(0.1, 0.8, 25, 10);
		gl.glPopMatrix();
		//set material for the canopy
		//draw the canopy using the 3 translated GLUT cones
		leafMaterials(gl);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 0.4f);
		glut.glutSolidCone(0.2, 0.5, 20, 10);
		gl.glTranslatef(0, 0, 0.1f);
		glut.glutSolidCone(0.2, 0.5, 20, 10);
		gl.glTranslatef(0, 0, 0.1f);
		glut.glutSolidCone(0.2, 0.5, 20, 10);
		gl.glPopMatrix();
		gl.glPopMatrix();
		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPopMatrix();
	}
	/**
	 * Define characteristics of the source light
	 */
	public void light1(GL gl){
		float ambient[] = {0.5f, 0.5f, 0.5f, 1.0f};
		float diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
		float specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
		float position[] = {0.0f, 0.0f, 1.0f, 0.0f};
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, ambient, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuse, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, specular, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
		gl.glEnable(GL.GL_LIGHT1);
	}
//	Define surface material
	public void materials(GL gl){
		float ambient[] = {0.0f, 0.5f, 0.0f, 1.0f};
		float diffuse[] = {0.0f, 0.5f, 0.0f, 1.0f};
		float specular[] = {0.0f, 0.2f, 0.0f, 1.0f};
//		float zero[] = {0.0f, 0.0f, 0.0f, 1.0f};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, specular, 0);
		gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,100.0f);
	}
	
	public void leafMaterials(GL gl){
		float ambient[] = {0.0f, 0.3f, 0.0f, 1.0f};
		float diffuse[] = {0.0f, 0.3f, 0.0f, 1.0f};
		float specular[] = {0.0f, 0.2f, 0.0f, 1.0f};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, specular, 0);
		gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 30.0f);
	}

	public void trunkMaterials(GL gl){
		float ambient[] = {0.3f, 0.1f, 0.0f, 1.0f};
		float diffuse[] = {0.3f, 0.1f, 0.0f, 1.0f};
		float specular[] = {0.1f, 0.1f, 0.0f, 1.0f};
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, ambient, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, specular, 0);
		gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, 100.0f);
	}
}
