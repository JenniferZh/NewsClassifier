package nb.common;

import java.util.HashMap;

/**
 * ClassModel stores the parameters needed for naive bayes classifier, which include
 * classProb	- 即P(C_i) 			C_i在训练样本中出现的概率
 * wordProb	 	- 即P(w_j | C_i) 	<j probability_of_j>字典中的第j个单词在C_i类文章中出现的概率，使用laplace smoothing
 * @author new
 *
 */
public class ClassModel {
	public int classId;
	public String className;
	public double classProb;
	public HashMap<Integer, Double> wordProb;
	public double defaultprob = 15/(double) 61188;
	
	public ClassModel(int classId, String className) {
		// TODO Auto-generated constructor stub
		this.classId = classId;
		this.className = className;
	}
	
	public String toString() {
		return classId + " " + className + " "+classProb;
	}

}
