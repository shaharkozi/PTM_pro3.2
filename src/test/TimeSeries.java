package test;

import java.util.*;
// import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
// import java.io.Reader;

public class TimeSeries {

	private Hashtable<String, Float[]> DataTable = new Hashtable<>();
	private String[] keys;
	private int colmuns;

	public TimeSeries(String csvFileName) {
		ReadCvs(csvFileName);
	}

	public TimeSeries(List<String[]> data) {

	}

	private void ReadCvs(String csvFileName) {
		List<String[]>data = new ArrayList<>();
		int colmunsnum = 0;
		int linesnum = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tmp = line.split(",");
				data.add(tmp);
				linesnum++;
				colmunsnum = tmp.length;
				this.colmuns = colmunsnum;
			}
		}
		catch (IOException e){

		}
		String[][] arr = new String[linesnum][colmunsnum];
		for(int i = 0; i< linesnum; i++)
		{
			for(int j = 0; j<colmunsnum; j++)
			{
				arr[i][j] = data.get(i)[j];
			}
		}
		String[] tmp = new String[linesnum -1];
		String[] tmp1 = new String[colmunsnum];
		for(int i = 0; i<colmunsnum; i++)
		{
			tmp1[i] = arr[0][i];
		}
		for(int i = 0; i<colmunsnum; i++)
		{
			for (int j = 0; j<linesnum - 1; j++)
			{
				tmp[j] = arr[j+1][i];
			}
			this.DataTable.put(tmp1[i],floatToString(tmp));
		}
		this.keys = new String[tmp1.length];
		this.keys = tmp1;
//		printdata(linesnum-1,colmunsnum);
	}
	public void printdata(int valuslen, int columns)
	{
		String[] tmp = new String[columns];
		Enumeration<String> keys = DataTable.keys();
		List<String> keyslist = Collections.list(keys);
		for(int r = 0; r<columns; r++)
		{
			System.out.print("key: " + keyslist.get(r) + "\n" + "values: ");
			Float[] tmpval = new Float[valuslen];
			tmpval = this.DataTable.get(keyslist.get(r));
			for(int j = 0; j<valuslen; j++)
			{
				System.out.print(tmpval[j] + ", ");
			}
			System.out.print("\n");
		}

	}
	public String getKey(int index)
	{
		return this.keys[index];
	}
	public Float getVal(String keyname, int index){
		return this.DataTable.get(keyname)[index-1];
	}
	public float[] getValarr(String keyname){
		float[] arr = new float[this.DataTable.get(keyname).length];
		for(int i = 0; i< this.DataTable.get(keyname).length;i++)
		{
			arr[i] = this.DataTable.get(keyname)[i].floatValue();
		}
		return arr;
	}
	public Float[] floatToString(String[] arr)
	{
		int size = arr.length;
		Float[] ret = new Float[size];
		for(int i=0; i<size;i++)
		{
			ret[i] = Float.parseFloat(arr[i]);
		}
		return ret;
	}
	public int getColumns()
	{
		return colmuns;
	}

}