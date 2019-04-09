package textCheck;

import java.io.BufferedReader;	//字符缓冲输入流
import java.io.FileReader;		//读文本
import java.io.FileWriter;		//写文本
import java.io.IOException;		//异常处理
import java.util.ArrayList;		//动态数组队列 
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;			//序列
import java.util.Map;
import java.util.Scanner;		//解析字符串

public class checkTeX {
	public static void main(String[] args) {
		new checkTeX().start();
	}
	
	public void start() {
		List<String> fileTemp=new ArrayList<String>();
		String directionaryPath="./englishDictionary.txt";
		String textPath="./Test.txt";
		
		createeditTree(directionaryPath);

		double recTrd=1;	//推荐单词路径阈值下限
		double editTrd=12;	//检测编辑路径阈值上限
		double recNum=8;	//输出推荐单词个数上限,默认8 个
		
		BufferedReader reader=null;	//字符缓冲输入流
		try {
			reader=new BufferedReader(new FileReader(textPath));	//读取检查文件
			String textLine;										//定义检查行
			List<String> tChar=null;
			List<String> result=null;
			while((textLine=reader.readLine())!=null) {				//按行读数据
				//String regex = "[^a-zA-Z0-9']";					//按除字母数字外符号划分字符串
				//String[] word=textLine.split(regex);				
				String[] word=textLine.split(" ");					//按"[: ' . , / | ! ? [ ] ( ) @ # $ & * ]"划分字符串
				
				for(int i=0;i<word.length;i++) {
					//去除空格
					if(word[i].equals("")) {						//忽略空格						
						continue;
				    }
										
					char[] wordChar = word[i].toCharArray();
					//改写大小写 
					for(int num=0;num<wordChar.length;num++) {
		            if(wordChar[num] >= 'A' && wordChar[num] <= 'Z'){
		            	wordChar[num]+=32;
		                }
					}
		            //去除结尾标点符号
					String st="";
					if(wordChar[wordChar.length-1] >= 'a' && wordChar[wordChar.length-1] <= 'z'){
		            	st=String.valueOf(wordChar);
		            }
					else {
						wordChar = Arrays.copyOf(wordChar, wordChar.length-1);		            	
						st=String.valueOf(wordChar);
					}		
			        tChar=search(st, 0);					
					if(tChar.size()!=0) {
						continue;
					}
					if(st.equals("")) {								//忽略单个符号
						continue;
				    }	
														
					//检查单词是否存在在字典中编辑路径为0表示相同
					result=search(word[i], 0);						//逐个查询字典
					if(result.size()!=0) {
						continue;
					}else {
						//按编辑路径检测相似单词
						for(int j=0;j<editTrd;j++) {
							result=search(word[i], j);
							if(result.size()<recTrd) {
								continue;
							}
							
							String newWord;							//写入字典新词
							System.out.println("提示：在 "+ textLine +" 中");
							System.out.println("第"+String.valueOf(i+1)+"个单词  "+word[i]+" 错误");
							System.out.println("A.输入 0 接受单词  "+word[i]+"，并写入字典");
							System.out.println("B.输入推荐单词对应序号替换 "+word[i]);
							for(int k=1;k<result.size()+1;k++) {
								System.out.print(k);
								System.out.print(".");
								System.out.print(result.get(k-1));
								System.out.print(" ");
								if(k==recNum) {						//推荐单词阈值
								break;
								}
							}
							System.out.println("");
							System.out.println("C.直接输入替换单词");
							Scanner inWord=new Scanner(System.in);
							String in=inWord.nextLine();
							
							//将此单词加入字典
							if(in.equals("0")) {
								newWord=word[i];
								writeToDirectionary(newWord,directionaryPath);
								put(newWord);
							}else if(Character.isDigit(in.charAt(0))){
							//选择推荐单词
								int index=Integer.parseInt(in);
								newWord=result.get(index-1);
								word[i]=newWord;
							}else{
							//直接替换单词
								newWord=in;
								word[i]=in;
								//检查输入的词是否在字典里,如果不在则将其加入字典
								List<String> list=search(newWord, 0);
								if(list.isEmpty()) {
									writeToDirectionary(newWord,directionaryPath);
									put(newWord);									
								}
							}	
							
							break;
						}
					}
					
					
				}
				String temp="";
				for(String part:word) {
					temp+=part;
					temp+=" ";
				}
				fileTemp.add(temp);
			}
			reader.close();
			
			FileWriter textWriter=new FileWriter(textPath);
			for(String line:fileTemp) {
				textWriter.write(line+"\r\n");
			}
			textWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("文本检测完毕");		
		
	}
	
	//创建编辑检测文本
	public void createeditTree(String path) {
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(path));
			String line;
			while((line=reader.readLine())!=null){
				put(line);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//将词写入字典
	public static void writeToDirectionary(String word,String path) {
		try {
			FileWriter dirWriter=new FileWriter(path,true);
			dirWriter.write(word+"\r\n");
			dirWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{	
			
		}
	}

	//BK树字典尾添加
	public void put(String term) {
		if(root == null) {
			root=new Node(term);
		}else {
			root.add(term);
		}
	}
	
	//term 待查询的元素         
	//redius 相似举例范围
	//results 满足举例范围所有元素
	public List<String> search(String term,double redius){
		List<String> results=new ArrayList<String>();		//定义动态数组队列 results
		if(root!= null) {
			root.search(term, redius, results);
		}
		return results;
	}
	

	private Node root;	
	//建立Hash图  用一个map储存子节点
	private static final class Node{
		private final String value;
		private final Map<Double,Node> children;
		public Node(String term) {
			this.value=term;
			this.children=new HashMap<Double,Node>();
		}
		public void add(String value) {
			//value与父节点的距离
			double distance=ComputeDistance(this.value,value);
			//n==0 x=y 相等
			if(distance ==0) {
				return;
			}
			//从父节点的子节点中查找child，满足距离为distance
			Node child=children.get(distance);
			
			//若距离父节点为distance的子节点不存在，则直接添加一个新的子节点
			if(child==null) {
				children.put(distance, new Node(value));
			}else {
			//若距离父节点为distance子节点存在，则递归的将value添加到该子节点下
				child.add(value);
			}
		}
		
		public void search(String term,double redius,List<String> results) {
			double distance = ComputeDistance(this.value,term);
			//与父节点的距离小于阈值，则添加到结果集中，并继续向下寻找
			if(distance<=redius) {
				results.add(this.value);
			}
			
			//子节点的距离在最小距离和最大距离之间
			//min = {1,distance -radius}, max = distance + radius
			for(double i=Math.max(distance-redius, 1);i<=distance+redius;++i) {
				Node child=children.get(i);
				//递归
				if(child!=null) {
					child.search(term, redius, results);
				}
			}
		}
	}
	

	private static double insertCost=1;		//插入距离函数
	private static double removeCost=1;		//删除距离函数
	private static double substitudeCost=1;	//替换距离函数      如下可加入键盘相邻距离函数
//	private static double [][] subcostMat= new double [26][26]; //替换距离矩	
	//初始化编辑距离
//	for (int i = 0; i < 26; i++) {
//        Arrays.fill(subcostMat[i], 1);
//    }
	
	//计算两个词之间的编辑距离    目标  → 查询
	private static double ComputeDistance(String target,String source) {
		int n=target.trim().length();
		int m=source.trim().length();

		
		double [][] distance= new double [n+1][m+1];
		
		distance[0][0]=0;
		//初始化边界距离 建立distance[n][m]矩阵 
		for(int i=1;i<=n;i++) {
			distance[i][0]=i;
		}
		for(int j=1;j<=m;j++) {
			distance[0][j]=j;
		}
		//建立距离矩阵， 目标和查询单词的第一个字母  对应当前位置 [i-1][j-1]
		for(int i=1;i<=n;i++) {
			for(int j=1;j<=m;j++) {
				//插入距离  [i-1][j] → [i][j]    相当于（j-1） +1 
				double min=distance[i-1][j]+insertCost;				
				//替换距离   charAt 当前字符相同   [i-1][j-1] → [i][j]
				if(target.charAt(i-1)==source.charAt(j-1)) {
					if(min>distance[i-1][j-1])
						min=distance[i-1][j-1];
				}else {	
					//替换距离函数
					substitudeCost = subeditCost(target.charAt(i-1),source.charAt(j-1));					
					if(min>distance[i-1][j-1]+substitudeCost)
						min=distance[i-1][j-1]+substitudeCost;
				}				
				//删除距离 [i][j-1] → [i][j]
				if(min>distance[i][j-1]+removeCost) {
					min=distance[i][j-1]+removeCost;
				}				
				//取最小距离
				distance[i][j]=min;
			}
		}	
		return distance[n][m];
	}
	
	//java中，char 类型' A'对应值为65，char 'a'对应值为97
	//该函数可以做一个对应键盘距离的26*26的替换距离矩阵subCost[n,m]  按键盘距离分为1 2 3  三类替换距离
	private static double subeditCost(char a,char b) {
		double subCost=1;
		//举例替换距离
		if((a=='r')&&((b=='e')||(b=='f')||(b=='s'))) {
			subCost=0.5;	
		}else if((a=='f')&&((b=='e')||(b=='t')||(b=='s'))) {
			subCost=0.5;
		}		
		//查找二维数组,获取替换距离
//		subCost=subcostMat[a-'a'][b-'a'];
//		System.out.print(subCost);
		return subCost;
	}
		
}
