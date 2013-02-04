package dem;

/**
 * (c) Ermias B. Tesfamariam 2010
 *
 * @author Ermias
 * @since 09:01:39 - 21.03.2010
 */
public class DEM {

	//number of rows in the DEM xy plane
	private int nRows;	
	//number of columns in the DEM xy plane
	private int nColumns;	
	//the 2x2 data array of the DEM heights. each pair of indexes represents an x, y position
	private float[][] data;	 
	
	//normals of the DEM vertices
	private float[][][] normal;
	//normals of the DEM triangle surfaces between every 3 points in the height matrix
	private float[][][] normalTriangle;

	public DEM() {
	}
	public DEM(int nRows, int nColumns) {
		super();
		this.nRows = nRows;
		this.nColumns = nColumns;
		data= new float[this.nRows][this.nColumns];
	}

//	public void initialize (){
//		//initialise the numbers of our vertex normals to the number of rows, columns 
//		normal = new float[nRows][nColumns][3];
//		//initialise the numbers of our triangle normals to the possible number of triangles
//		normalTriangle = new float[nRows][2*nColumns][3];
//	}
	public void calculateNormals() {
		
		//initialise the numbers of our vertex normals to the number of rows, columns 
		normal = new float[nRows][nColumns][3];
		//initialise the numbers of our triangle normals to the possible number of triangles
		normalTriangle = new float[nRows][2*nColumns][3];
		
			for(int row = 0; row < nRows-1; row++) {
				for(int col = 0; col < nColumns-2; col++) {
					normalTriangle[row][2*col] = crossProduct(row, col);
					normalTriangle[row][2*col+1] = crossProduct2(row, col+1);
				}
			}
			normal[0][0] = normalTriangle[0][0];
			for(int col = 1; col < nColumns-1; col++) {
				normal[0][col] = calculateNormal3Top(0, col);
			}
			normal[0][nColumns-1][0] = (normalTriangle[0][2*(nColumns-2)][0] +
					normalTriangle[0][2*(nColumns-1)-1][0])/2;
			
			normal[1][nColumns-1][1] = (normalTriangle[0][2*(nColumns-2)][1] +
					normalTriangle[0][2*(nColumns-1)-1][1])/2;
			
			normal[2][nColumns-1][2] = (normalTriangle[0][2*(nColumns-2)][2] +
					normalTriangle[0][2*(nColumns-1)-1][2])/2;
			
			for(int row = 1; row < nRows-2; row++) {
				normal[row][0] = calculateNormal3Left(row, 0);
				for(int col = 1; col < nColumns-2; col++) {
					normal[row][col] = calculateNormal6(row, col);
				}
				normal[row][nColumns-1] = calculateNormal3Right(row, nColumns-1);
			}
			
			normal[nRows-2][0][0] = (normalTriangle[nRows-2][0][0] +
					normalTriangle[nRows-2][1][0])/2;
			
			normal[nRows-2][0][1] = (normalTriangle[nRows-2][0][1] +
					normalTriangle[nRows-2][1][1])/2;
			
			normal[nRows-2][0][2] = (normalTriangle[nRows-2][0][2] +
					normalTriangle[nRows-2][1][2])/2;
			
			for(int col = 1; col < nColumns-2; col++) {
				normal[nRows-2][col] = calculateNormal3Bottom(nRows-2, col);
			}
			normal[nRows-2][nColumns-1] = normalTriangle[nRows-2][2*(nColumns-1)-1];
		}
		
		private float[] calculateNormal3Bottom(int row, int col) {
			float result[] = new float[3];

			result[0] = (normalTriangle[row][2*(col-1)][0] +
					normalTriangle[row][2*(col-1)+1][0] +
					normalTriangle[row][2*(col)][0])/3;
			
			result[1] = (normalTriangle[row][2*(col-1)][1] +
					normalTriangle[row][2*(col-1)+1][1] +
					normalTriangle[row][2*(col)][1])/3;
			
			result[2] = (normalTriangle[row][2*(col-1)][2] +
					normalTriangle[row][2*(col-1)+1][2] +
					normalTriangle[row][2*(col)][2])/3;
			
			return result;
		}
		
		private float[] calculateNormal3Top(int row, int col) {
			float result[] = new float[3];
			
			result[0] = (normalTriangle[row][2*(col-1)][0] +
					normalTriangle[row][2*(col-1)+1][0] +
					normalTriangle[row][2*col][0])/3;
			
			result[1] = (normalTriangle[row][2*(col-1)][1] +
					normalTriangle[row][2*(col-1)+1][1] +
					normalTriangle[row][2*col][1])/3;
			
			result[2] = (normalTriangle[row][2*(col-1)][2] +
					normalTriangle[row][2*(col-1)+1][2] +
					normalTriangle[row][2*col][2])/3;
			
			return result;
		}
		
		private float[] calculateNormal6(int row, int col) {
			float result[] = new float[3];
			
			result[0] = (normalTriangle[row-1][2*(col-1)+1][0] +
					normalTriangle[row-1][2*col][0] +
					normalTriangle[row-1][2*col+1][0] +
					normalTriangle[row][2*(col-1)][0] +
					normalTriangle[row][2*(col-1)+1][0] +
					normalTriangle[row][2*col][0])/6;
			
			result[1] = (normalTriangle[row-1][2*(col-1)+1][1] +
			normalTriangle[row-1][2*col][1] +
			normalTriangle[row-1][2*col+1][1] +
			normalTriangle[row][2*(col-1)][1] +
			normalTriangle[row][2*(col-1)+1][1] +
			normalTriangle[row][2*col][1])/6;
			
			result[2] = (normalTriangle[row-1][2*(col-1)+1][2] +
			normalTriangle[row-1][2*col][2] +
			normalTriangle[row-1][2*col+1][2] +
			normalTriangle[row][2*(col-1)][2] +
			normalTriangle[row][2*(col-1)+1][2] +
			normalTriangle[row][2*col][2])/6;
			
			return result;
		}
		
		private float[] calculateNormal3Left(int row, int col) {
			float result[] = new float[3];
			
			result[0] = (normalTriangle[row-1][2*col][0] + 
			normalTriangle[row-1][2*col+1][0] + 
			normalTriangle[row][2*col][0])/3;
			
			result[1] = (normalTriangle[row-1][2*col][1] + 
			normalTriangle[row-1][2*col+1][1] + 
			normalTriangle[row][2*col][1])/3;
			
			result[2] = (normalTriangle[row-1][2*col][2] +
			normalTriangle[row-1][2*col+1][2] + 
			normalTriangle[row][2*col][2])/3;
			
			return result;
		}
		
		private float[] calculateNormal3Right(int row, int col) {
			float result[] = new float[3];
			
			result[0] = (normalTriangle[row-1][2*(col-1)-1][0] +
			normalTriangle[row][2*(col-2)][0] +
			normalTriangle[row][2*(col-1)-1][0])/3;
			
			result[1] = (normalTriangle[row-1][2*(col-1)-1][1] +
			normalTriangle[row][2*(col-2)][1] +
			normalTriangle[row][2*(col-1)-1][1])/3;
			
			result[2] = (normalTriangle[row-1][2*(col-1)-1][2] +
			normalTriangle[row][2*(col-2)][2] +
			normalTriangle[row][2*(col-1)-1][2])/3;
			
			return result;
		}
		
		private float[] crossProduct2(int row, int col) {
			float product[] = new float[3];
			
			product[0] = data[row+1][col] - data[row][col+1];
			product[1] = data[row][col+1] - data[row+1][col+1];
			product[2] = 20;
			
			float length = (float)Math.sqrt(product[0]*product[0] + product[1]*product[1] +product[2]*product[2]);
			product[0] /= length;
			product[1] /= length;
			product[2] /= length;
			
			return product;
		}
		
		private float[] crossProduct(int row, int col) {
			float product[] = new float[3];
			
			product[0] = data[row][col] - data[row][col+1];
			product[1] = data[row][col] - data[row+1][col];
			product[2] = 20;
			
			float length = (float)Math.sqrt(product[0]*product[0] + product[1]*product[1] +product[2]*product[2]);
			product[0] /= length;
			product[1] /= length;
			product[2] /= length;
			
			return product;
		}
		
		public int getnColumns() {
			return nColumns;
		}
		
		public int getnRows() {
			return nRows;
		}
		public void setData(int row, int column, float data){
			this.data[row][column]=data;
		}
		public float getData(int row, int column){
			return data[row][column];
		}
		
		public float[] getNormalAT(int row, int column) {
			return normal[row][column];
		}
	}