/*  Student Name: Brennan Johnston
 *  Username:		ua###		<--- this needs to be correct
 *  Date:			10/3/2021
 *  Class:          CS 3103 - Algorithms
 *  Filename:       UASort.java
 *  Description:    Quicksort implementation for String and Integer arrays.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class UASort {

	static boolean ascending = false;
	static boolean isText = false;
	
	public static void main(String[] args) {
		String inputPath, outputPath, dataType, order;
		File inputDir, outputDir;
		
		if(args.length < 4) {
			print("Invalid number of arguments, expected 4.");
			return;
		}
		
		inputPath = args[0];
		outputPath = args[1];
		dataType = args[2].toLowerCase();
		order = args[3].toLowerCase();
		
		// PARAMETER VALIDATION =======================================
		if(dataType.compareTo("numeric") != 0 && dataType.compareTo("text") != 0) {
			print("Parameter 3 invalid, expected 'numeric' or 'text'.");
			return;
		}
		
		if(order.compareTo("ascending") != 0 && order.compareTo("descending") != 0) {
			print("Parameter 4 invalid, expected 'ascending' or 'descending'.");
			return;
		}
		
		if(dataType.compareTo("text") == 0) {
			isText = true;
		}
		
		if(order.compareTo("ascending") == 0) {
			ascending = true;
		}
		
		inputDir = new File(inputPath);
		if(!inputDir.exists()) {
			print("Input file path invalid. Path received: " + inputPath);
			return;
		}

		outputDir = new File(outputPath);
		if(!outputDir.exists()) {
			print("Output file path invalid. Path received: " + outputPath);
			return;
		}
		
		if(!inputDir.isDirectory()) {
			print("Input file is not a directory.");
			return;
		}
		
		if(!outputDir.isDirectory()) {
			print("Output file is not a directory.");
			return;
		}
		// END PARAMETER VALIDATION ===================================================
		
		// iterate all files in inputDir
		File[] inputFiles = inputDir.listFiles();
		for(int f = 0; f < inputFiles.length; f++) {
			File inputFile = inputFiles[f];
			Object[] data = parseData(inputFile);
			if(data.length > 1) {
				QuickSort(data, 0, data.length-1);
				writeData(outputDir, inputFile.getName(), data);
			}
		}
	}
	
	static void QuickSort(Object[] data, int p, int r) {
		if(p < r) {
			int q = Partition(data, p, r);
			QuickSort(data, p, q-1);
			QuickSort(data, q+1, r);
		}
	}
	
	static int Partition(Object[] data, int p, int r) {
		int medianIndex = selectPivot(data, p, r);
		swap(data, medianIndex, r);
		if(isText) {
			String x = (String)data[r];
			int i = p - 1;
			for(int j = p; j <= r - 1; j++) {
				int comparison = ((String) data[j]).compareToIgnoreCase(x);
				if(ascending) {
					if(comparison <= 0) {
						i++;
						swap(data, i, j);
					}
				} else {
					if(comparison > 0) {
						i++;
						swap(data, i, j);
					}
				}
			}
			
			swap(data, i + 1, r);
			return i + 1;
		} else {
			int x = (int)data[r];
			int i = p - 1;
			for(int j = p; j <= r - 1; j++) {
				if(ascending) {
					if((int)data[j] <= x) {
						i++;
						swap(data, i, j);
					}
				} else {
					if((int)data[j] > x) {
						i++;
						swap(data, i, j);
					}
				}
			}
			
			swap(data, i + 1, r);
			return i + 1;
		}
		
		
	}
	
	static int selectPivot(Object[] A, int p, int r) {
		if(Math.abs((r+1)-p) < 3)
			return r;
		
		int mid = (int)Math.floor((p+r+1)/2);
		
		if(isText) {
			int prCompare = ((String) A[p]).compareToIgnoreCase((String) A[r]);
			int pmidCompare = ((String) A[p]).compareToIgnoreCase((String) A[mid]);
			int rmidCompare = ((String) A[r]).compareToIgnoreCase((String) A[mid]);
			if((prCompare > 0 && pmidCompare < 0) || (prCompare < 0 && pmidCompare > 0))
				return p;
			else if((prCompare > 0 && rmidCompare > 0) || (prCompare < 0 && rmidCompare < 0))
				return r;
			else
				return mid;
		} else {
			int pvalue = (int)A[p];
			int rvalue = (int)A[r];
			int midvalue = (int)A[mid];
			if((pvalue < rvalue && pvalue > midvalue) || (pvalue > rvalue && pvalue < midvalue))
				return p;
			else if((rvalue < pvalue && rvalue > midvalue) || (rvalue > pvalue && rvalue < midvalue))
				return r;
			else
				return mid;
		}
	}
	
	static void writeData(File outputDir, String filename, Object[] data) {
		File outputFile = new File(outputDir.getAbsolutePath() + "/" + filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			for(int i = 0; i < data.length; i++) {
				bw.write(data[i] + "\n");
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	static Object[] parseData(File dataFile) {
		int dataCount = getFileLineCount(dataFile);
		Object[] data = new Object[dataCount];
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			int index = 0;
			while((line = br.readLine()) != null) {
				if(!isText) {
					data[index] = Integer.parseInt(line);
					index++;
				} else {
					data[index] = line;
					index++;
				}
			}
			
			br.close();
			
			// One or more integers failed to parse. Downsize dataset
			if(index < dataCount) {
				Object[] tmp = Arrays.copyOfRange(data, 0, index);
				data = tmp;
				print("Downsized dataset, failed to parse " + (dataCount - index) + " integers.");
			}
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return data;
	}
	
	static int getFileLineCount(File f) {
		int count = 0;
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			while(br.readLine() != null) count++;
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	static void swap(Object[] list, int x, int y) {
		Object temp = list[x];
		list[x] = list[y];
		list[y] = temp;
	}
	
	static void print(String s) {
		System.out.println(s);
		
		return;
	}
}