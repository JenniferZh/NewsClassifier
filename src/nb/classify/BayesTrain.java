package nb.classify;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import nb.common.ClassModel;
import nb.util.DataProcess;

public class BayesTrain {
	
	public int dicSize = 61188;
	public int classNum = 20;
	public ArrayList<ClassModel> models = new ArrayList<>();
	
	public String classInfoPath = "data/train.map";
	public String classLabelPath = "data/train.label";
	public String classDataPath = "data/train.data";
	
	public String testDataPath = "data/test.data";
	public String testLabelPath = "data/test.label";
	
	public BayesTrain() {
		DataProcess.ReadClassInfo(classInfoPath, models);
		HashMap<Integer, ArrayList<Integer>> pHashMap = DataProcess.PriorProbCompute(classLabelPath, models);
		HashMap<Integer, HashMap<Integer, Integer>> tHashMap = DataProcess.DocStatistics(classDataPath);
		
		for(int i = 1; i <= classNum; i++) {
			HashMap<Integer, Integer> TypeStatistics = new HashMap<>();
			ArrayList<Integer> docList = pHashMap.get(i);
			for(Integer docid: docList) {
				HashMap<Integer, Integer> wordInfo = tHashMap.get(docid);
				for(Integer wordId: wordInfo.keySet()) {
					int cnt = wordInfo.get(wordId) + TypeStatistics.getOrDefault(wordId, 0);
					TypeStatistics.put(wordId, cnt);
				}
			}
			HashMap<Integer, Double> wordProb = new HashMap<>();
			int sum = 0;
			for(Integer wordid: TypeStatistics.keySet()) sum += TypeStatistics.get(wordid);
			for(Integer wordid: TypeStatistics.keySet()) wordProb.put(wordid, ((double)TypeStatistics.get(wordid)+1)*15/(sum+dicSize));
			
			if(i == 1) {
				System.out.println("hehe"+TypeStatistics.get(12)+" "+sum);
			}
			models.get(i-1).wordProb = wordProb;
			
		}
		
		System.out.println(models.get(1).defaultprob);

	}
	
	public void BayesClassify() {
		//store predicted result of each doc, <docid, label> 
		HashMap<Integer, Integer> result = new HashMap<>();
		HashMap<Integer, HashMap<Integer, Integer>> tHashMap = DataProcess.DocStatistics(testDataPath);
		
		int docNum = tHashMap.size();
		System.out.println(tHashMap.size());
		
		for(Integer doc: tHashMap.keySet()) {
			System.out.println(doc);
		}
		
		
		for(Integer doc: tHashMap.keySet()) {
			System.out.println(doc);
			//一个文档中的单词统计信息
			HashMap<Integer, Integer> docstatis = tHashMap.get(doc);
			
			
			
			//存储所有模型计算出的结果
			HashMap<Integer, BigDecimal> postProb = new HashMap<>();
			for(ClassModel model: models) {
				HashMap<Integer, Double> wordProb = model.wordProb;
				double p = 1.0e308;
				
				BigDecimal pBigDecimal = new BigDecimal("1.0");
				
				for(Integer i: docstatis.keySet()) {
					int wordId = i;
					int wordcnt = docstatis.get(i);
					for(int j = 0; j < wordcnt; j++) {
						BigDecimal pBigDecimalb = BigDecimal.valueOf(wordProb.getOrDefault(wordId, model.defaultprob));
						pBigDecimal = pBigDecimal.multiply(pBigDecimalb);
						//p = p*wordProb.getOrDefault(wordId, model.defaultprob);
					}
				}
				//p = p*model.classProb;
				pBigDecimal = pBigDecimal.multiply(BigDecimal.valueOf(model.classProb));
				//System.out.println(doc+ " "+pBigDecimal);
				postProb.put(model.classId, pBigDecimal);
			}
			
			int label = 1;
			BigDecimal max = postProb.get(1);
			
			for(Integer i: postProb.keySet()) {
				if(postProb.get(i).compareTo(max) == 1) {
					max = postProb.get(i);
					label = i;
				}
			}
			
			result.put(doc, label);
			
			
			
		}
		
		for(Integer i: result.keySet()) {
			System.out.println(i+ " "+result.get(i));
		}
	}
	
	public static void BayesCompare() {
		int[] result = new int[8000];
		int[] label = new int[8000];
		try {
			BufferedReader file = new BufferedReader(new FileReader("data/test.nb.result"));
			BufferedReader file2 = new BufferedReader(new FileReader("data/test.label"));
			String line;
			String line2;
			int k = 1;
			while ((line = file.readLine()) != null) {
				line2 = file2.readLine();
				
				StringTokenizer st = new StringTokenizer(line);
				StringTokenizer st2 = new StringTokenizer(line2);
				
				if(!st.hasMoreTokens())	continue;
				
				String index2 = st.nextToken();
				String label1 = st.nextToken();
				
				String label2 = st2.nextToken();
				
				result[Integer.parseInt(index2)] = Integer.parseInt(label1);
				label[k++] = Integer.parseInt(label2);
				
				
				
			}
			file.close();
			file2.close();
		} catch (IOException e) {
		      System.err.println("Exception while reading the class information:"); 
			  System.err.println(e);
			  System.exit(1);
		}
		int cnt = 0;
		for(int i = 1; i <= 7505; i++ ) {
			if(result[i] != label[i]) cnt++;
		}
		System.out.println("准确率："+(7505-cnt)*1.0/7505);
		
		
	
	}
	
	
	public static void main(String[] args) {
		
		//BayesTrain train = new BayesTrain();
		//train.BayesClassify();
		BayesTrain.BayesCompare();
		
		
		
	}
	

}
