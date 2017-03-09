package lr.classify;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.DoubleConsumer;

import javax.naming.spi.DirStateFactory.Result;

import nb.util.DataProcess;

public class LRClassify {
	
	public String classInfoPath = "data/train.map";
	public String classLabelPath = "data/train.label";
	public String classDataPath = "data/train.data";
	
	public String testDataPath = "data/test.data";
	public String testLabelPath = "data/test.label";
	

	public int dictSize = 61188;
	public int docSize;
	public int classSize = 20;
	
	HashMap<Integer, Integer> pHashMap;
	HashMap<Integer, HashMap<Integer, Integer>> tHashMap;
	
	double theta[][] = new double[classSize][];
	
	public LRClassify() throws IOException {
		//每篇文章对应的类别
		pHashMap = DataProcess.DocTyeCompute(classLabelPath);
		//每个文章对应的单词列表
		tHashMap = DataProcess.DocStatistics(classDataPath);
		//对于每一个类别
		
		//LREstimateSingle(0.001, 1, 500);
		
		for(int i = 1; i <= classSize; i++) {
			//提取出这个类别中的文章，即tHashMap的子集,存在docListInfo中

			HashMap<Integer, HashMap<Integer, Integer>> docListInfo = new HashMap<>();
			
			//对于第j个参数进行LR
			theta[i-1] = LREstimate(0.0001, i);
			
		}
		
		
		// TODO Auto-generated constructor stub
	}
	
	//判断测试集合的结果
	public HashMap<Integer, Integer> LRJudge() {
		
		//测试文章的单词列表
		tHashMap = DataProcess.DocStatistics(testDataPath);
		int testSize = tHashMap.keySet().size();
		HashMap<Integer, Integer> resultMap = new HashMap<>();
		
		for(Integer doc: tHashMap.keySet()) {
			double[] tmp = new double[classSize];
			for(int i = 0; i < classSize; i++)
				tmp[i] = computeP(tHashMap.get(doc), theta[i]);
			double max = tmp[0];
			int k = 1;
			for(int i = 0; i < classSize; i++)
				if(tmp[i] > max) {
					max = tmp[i];
					k = i+1;
				}
			resultMap.put(doc, k);
		}
		return resultMap;
		
		
	}
	
	//计算测试集合的准确率
	public void LRCompare(HashMap<Integer, Integer> result) {
		HashMap<Integer, Integer> trueResult = DataProcess.DocTyeCompute(testLabelPath);
		int cnt = 0;
		for(Integer doc: trueResult.keySet()) {
			if(result.get(doc) != trueResult.get(doc)) cnt++;
		}
		System.out.println("准确率："+((double)cnt)/trueResult.size());
	}
	
	public void LREstimateSingle(double step, int ithClass, int ithPara) {
		//出现了第ithPara个单词的文章,以及这个单词出现的个数
		//<docid, wordcnt>
		HashMap<Integer, Integer> ithParaWordCount = new HashMap<>();
		for(Integer docid: tHashMap.keySet()) {
			if(tHashMap.get(docid).get(ithPara) != null)
				ithParaWordCount.put(docid, tHashMap.get(docid).get(ithPara));
		}
		
		double theta0 = 2.0;
		double theta1 = 1.0;
		int cnt = 50;
		
		for(int time = 0; time < cnt; time++) {
			double grad = 0.0;
			
			for(Integer docid: ithParaWordCount.keySet()) {
				int X_i = ithParaWordCount.get(docid);
				
				int Y = (pHashMap.get(docid) == ithClass)? 1 : 0;
				double p = 1.0/(1+Math.exp(theta0+theta1*X_i));
				grad = grad + X_i*(Y-p);
				
			}
			
			theta0 = theta0 + step*grad;
			theta1 = theta1 + step*grad;
			
			
			System.out.println(theta0+" "+theta1);
		
			
		}
	}
	
	//训练参数
	public double[] LREstimate(double step, int ithClass) throws IOException {
		int cnt = 2;
		String filename = "data/class"+ithClass;
		FileWriter fWriter = new FileWriter(new File(filename));
		
		//假设所有的theta初值为0
		double theta[] = new double[dictSize+1];
		double thetatmp[] = new double[dictSize+1];
		System.out.println("haha");

		for(int time = 0; time < cnt; time++) {
			//对于每一个参数theta
			
			
			HashMap<Integer, Double> pMap = new HashMap<>();
			for(Integer docid: tHashMap.keySet()) {
				
				
				double p = computeP(tHashMap.get(docid), theta);
				pMap.put(docid, p);

			}
			
			for(int i = 0; i <= dictSize; i++) {
				//先计算梯度
			
				double grad = 0.0;
			
				
				for(Integer docid: tHashMap.keySet()) {
					int X_i = tHashMap.get(docid).getOrDefault(i, 0);
					//如果X_i不是0，才需要计算下面的
					if(X_i != 0) {
						
						int Y = (pHashMap.get(docid) == ithClass)? 1 : 0;
						//double p = computeP(tHashMap.get(docid), theta);
						grad = grad + X_i*(Y-pMap.get(docid));
					}
				}
			
				thetatmp[i] = theta[i] + step*grad;
				
			
			}
			for(int i = 0; i <= dictSize; i++)
				theta[i] = thetatmp[i];
			
			for(double t: theta)
				fWriter.write(Double.toString(t)+"\t");
			fWriter.write("\n");
			System.out.println(theta[1]);
		}
		

		
		fWriter.close();

		return theta;
	}
	
	public double computeP(HashMap<Integer, Integer> wordmap, double[] curtheta) {
		double sum = 0.0;
		for(Integer i: wordmap.keySet()) {
			sum = sum + curtheta[0] + curtheta[i]*wordmap.get(i);
		}
		return 1/(1+Math.exp(sum));
	}
	
	public static void main(String[] args) throws IOException   {
		LRClassify pClassify = new LRClassify();
		HashMap<Integer, Integer> pResult = pClassify.LRJudge();
		pClassify.LRCompare(pResult);
	}

}
