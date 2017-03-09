package nb.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import nb.common.ClassModel;

public class DataProcess {
	
	public static void ReadClassInfo(String dataPath, ArrayList<ClassModel> model) {
		try {
			BufferedReader file = new BufferedReader(new FileReader(dataPath));
			String line;
			while ((line = file.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if(!st.hasMoreTokens())	continue;
				String className = st.nextToken();
				String classId = st.nextToken();
				
				ClassModel tmpModel = new ClassModel(Integer.parseInt(classId), className);
				model.add(tmpModel);
				
			}
			file.close();
		} catch (IOException e) {
		      System.err.println("Exception while reading the class information:"); 
			  System.err.println(e);
			  System.exit(1);
		}

	}
	
	public static HashMap<Integer, ArrayList<Integer>> PriorProbCompute(String dataPath, ArrayList<ClassModel> model) {
		int num = model.size();
		
		HashMap<Integer, ArrayList<Integer>> DocType = new HashMap<>();
		for(int i = 1; i <= num; i++) DocType.put(i, new ArrayList<>());
		
		try {
			
			BufferedReader file = new BufferedReader(new FileReader(dataPath));
			String line;
			
			
			int[] cnt = new int[num];
			
			int docid = 1;
			
			while ((line = file.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if(!st.hasMoreTokens())	continue;
				int classId = Integer.parseInt(st.nextToken());
				
				ArrayList<Integer> tmp = DocType.get(classId);
				tmp.add(docid);
				DocType.put(classId, tmp);
				docid++;
				
				cnt[classId-1]++;
			}
			file.close();
			
			
			long sum = 0;
			for(int tmp: cnt) sum += tmp;
			
			for(int i = 0; i < num; i++) {
				model.get(i).classProb = ((double)cnt[i])/sum;
			}
			
			
		} catch (IOException e) {
		      System.err.println("Exception while computing the prior probability:"); 
			  System.err.println(e);
			  System.exit(1);
		}
		return DocType;
	}
	
	
	/**
	 * 
	 * @param dataPath label文件，表示文章对应的类型
	 * @param num 有几种类型
	 * @return <类型序号，训练集中属于这个类型的文章id的list>
	 */
	public static HashMap<Integer, Integer> DocTyeCompute(String dataPath) {
		HashMap<Integer, Integer> DocType = new HashMap<>();
		
		
		try {
			
			BufferedReader file = new BufferedReader(new FileReader(dataPath));
			String line;
			
			int docid = 1;
			
			while ((line = file.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if(!st.hasMoreTokens())	continue;
				int classId = Integer.parseInt(st.nextToken());

				DocType.put(docid, classId);
				docid++;
				

			}
			file.close();
					
		} catch (IOException e) {
		      System.err.println("Exception while computing the prior probability:"); 
			  System.err.println(e);
			  System.exit(1);
		}
		return DocType;
	}
	
	public static HashMap<Integer, HashMap<Integer, Integer>> DocStatistics(String dataPath) {
		HashMap<Integer, HashMap<Integer, Integer>> docInfo = new HashMap<>();
		try {
			BufferedReader file = new BufferedReader(new FileReader(dataPath));
			String line;
			
			while ((line = file.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				if(!st.hasMoreTokens())	continue;
				
				int docid = Integer.parseInt(st.nextToken());
				int wordid = Integer.parseInt(st.nextToken());
				int cnt = Integer.parseInt(st.nextToken());
				
				if(docInfo.get(docid) == null) {
					HashMap<Integer, Integer> wordhash = new HashMap<>();
					docInfo.put(docid, wordhash);
				}
				
				HashMap<Integer, Integer> tMap = docInfo.get(docid);
				tMap.put(wordid, cnt);
				docInfo.put(docid, tMap);
			}
			file.close();
			
		} catch (IOException e) {
			// TODO: handle exception
			System.err.println("Exception while computing construct the doc statistics:"); 
			System.err.println(e);
			System.exit(1);
		}
		return docInfo;
	}
	


}
