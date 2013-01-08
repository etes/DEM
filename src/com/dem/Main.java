package dem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @ Ermias Beyene
 */
public class Main {

	public static void main(String args[]) {

		if(args.length == 1) {
//			to read the header file
			File f = new File(args[0]+".hdr");
			FileReader fr;
			int nRows=0;
			int nColumns=0;
			try {
				fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String string;
				while((string = br.readLine()) != null){
					String s1[]= string.split("=");
					for(int i=0; i<s1.length;i++)
						s1[i]=s1[i].trim();
					if(s1[0].equals("number_of_rows"))
						nRows=Integer.parseInt(s1[1]);
					else if(s1[0].equals("number_of_columns"))
						nColumns=Integer.parseInt(s1[1]);
				}
				br.close();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}
			DEM dem= new DEM(nRows,nColumns);

			//			to read the DEM file
			f = new File(args[0]+".txt");
			try {
				fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String string;
				string = br.readLine();	
				br.close();
				String data[]=string.split(" ");
				int cnt=0;
				for(int row=0; row<dem.getnRows();row++)
					for(int column=0; column<dem.getnColumns();column++){
						float z=Float.parseFloat(data[cnt]);
//						System.out.println(data[cnt]);
						// to exclude all point with near-zero elevation
						if(z<0) z=0;
						dem.setData(row, column, z);
						cnt++;
					}
				dem.calculateNormals();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}catch(IOException e) {
				e.printStackTrace();
			}
			new Renderer(dem);
		}
	}
}
