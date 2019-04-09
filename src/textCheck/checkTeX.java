package textCheck;

import java.io.BufferedReader;	//�ַ�����������
import java.io.FileReader;		//���ı�
import java.io.FileWriter;		//д�ı�
import java.io.IOException;		//�쳣����
import java.util.ArrayList;		//��̬������� 
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;			//����
import java.util.Map;
import java.util.Scanner;		//�����ַ���

public class checkTeX {
	public static void main(String[] args) {
		new checkTeX().start();
	}
	
	public void start() {
		List<String> fileTemp=new ArrayList<String>();
		String directionaryPath="./englishDictionary.txt";
		String textPath="./Test.txt";
		
		createeditTree(directionaryPath);

		double recTrd=1;	//�Ƽ�����·����ֵ����
		double editTrd=12;	//���༭·����ֵ����
		double recNum=8;	//����Ƽ����ʸ�������,Ĭ��8 ��
		
		BufferedReader reader=null;	//�ַ�����������
		try {
			reader=new BufferedReader(new FileReader(textPath));	//��ȡ����ļ�
			String textLine;										//��������
			List<String> tChar=null;
			List<String> result=null;
			while((textLine=reader.readLine())!=null) {				//���ж�����
				//String regex = "[^a-zA-Z0-9']";					//������ĸ��������Ż����ַ���
				//String[] word=textLine.split(regex);				
				String[] word=textLine.split(" ");					//��"[: ' . , / | ! ? [ ] ( ) @ # $ & * ]"�����ַ���
				
				for(int i=0;i<word.length;i++) {
					//ȥ���ո�
					if(word[i].equals("")) {						//���Կո�						
						continue;
				    }
										
					char[] wordChar = word[i].toCharArray();
					//��д��Сд 
					for(int num=0;num<wordChar.length;num++) {
		            if(wordChar[num] >= 'A' && wordChar[num] <= 'Z'){
		            	wordChar[num]+=32;
		                }
					}
		            //ȥ����β������
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
					if(st.equals("")) {								//���Ե�������
						continue;
				    }	
														
					//��鵥���Ƿ�������ֵ��б༭·��Ϊ0��ʾ��ͬ
					result=search(word[i], 0);						//�����ѯ�ֵ�
					if(result.size()!=0) {
						continue;
					}else {
						//���༭·��������Ƶ���
						for(int j=0;j<editTrd;j++) {
							result=search(word[i], j);
							if(result.size()<recTrd) {
								continue;
							}
							
							String newWord;							//д���ֵ��´�
							System.out.println("��ʾ���� "+ textLine +" ��");
							System.out.println("��"+String.valueOf(i+1)+"������  "+word[i]+" ����");
							System.out.println("A.���� 0 ���ܵ���  "+word[i]+"����д���ֵ�");
							System.out.println("B.�����Ƽ����ʶ�Ӧ����滻 "+word[i]);
							for(int k=1;k<result.size()+1;k++) {
								System.out.print(k);
								System.out.print(".");
								System.out.print(result.get(k-1));
								System.out.print(" ");
								if(k==recNum) {						//�Ƽ�������ֵ
								break;
								}
							}
							System.out.println("");
							System.out.println("C.ֱ�������滻����");
							Scanner inWord=new Scanner(System.in);
							String in=inWord.nextLine();
							
							//���˵��ʼ����ֵ�
							if(in.equals("0")) {
								newWord=word[i];
								writeToDirectionary(newWord,directionaryPath);
								put(newWord);
							}else if(Character.isDigit(in.charAt(0))){
							//ѡ���Ƽ�����
								int index=Integer.parseInt(in);
								newWord=result.get(index-1);
								word[i]=newWord;
							}else{
							//ֱ���滻����
								newWord=in;
								word[i]=in;
								//�������Ĵ��Ƿ����ֵ���,���������������ֵ�
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
		
		System.out.println("�ı�������");		
		
	}
	
	//�����༭����ı�
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
	
	//����д���ֵ�
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

	//BK���ֵ�β���
	public void put(String term) {
		if(root == null) {
			root=new Node(term);
		}else {
			root.add(term);
		}
	}
	
	//term ����ѯ��Ԫ��         
	//redius ���ƾ�����Χ
	//results ���������Χ����Ԫ��
	public List<String> search(String term,double redius){
		List<String> results=new ArrayList<String>();		//���嶯̬������� results
		if(root!= null) {
			root.search(term, redius, results);
		}
		return results;
	}
	

	private Node root;	
	//����Hashͼ  ��һ��map�����ӽڵ�
	private static final class Node{
		private final String value;
		private final Map<Double,Node> children;
		public Node(String term) {
			this.value=term;
			this.children=new HashMap<Double,Node>();
		}
		public void add(String value) {
			//value�븸�ڵ�ľ���
			double distance=ComputeDistance(this.value,value);
			//n==0 x=y ���
			if(distance ==0) {
				return;
			}
			//�Ӹ��ڵ���ӽڵ��в���child���������Ϊdistance
			Node child=children.get(distance);
			
			//�����븸�ڵ�Ϊdistance���ӽڵ㲻���ڣ���ֱ�����һ���µ��ӽڵ�
			if(child==null) {
				children.put(distance, new Node(value));
			}else {
			//�����븸�ڵ�Ϊdistance�ӽڵ���ڣ���ݹ�Ľ�value��ӵ����ӽڵ���
				child.add(value);
			}
		}
		
		public void search(String term,double redius,List<String> results) {
			double distance = ComputeDistance(this.value,term);
			//�븸�ڵ�ľ���С����ֵ������ӵ�������У�����������Ѱ��
			if(distance<=redius) {
				results.add(this.value);
			}
			
			//�ӽڵ�ľ�������С�����������֮��
			//min = {1,distance -radius}, max = distance + radius
			for(double i=Math.max(distance-redius, 1);i<=distance+redius;++i) {
				Node child=children.get(i);
				//�ݹ�
				if(child!=null) {
					child.search(term, redius, results);
				}
			}
		}
	}
	

	private static double insertCost=1;		//������뺯��
	private static double removeCost=1;		//ɾ�����뺯��
	private static double substitudeCost=1;	//�滻���뺯��      ���¿ɼ���������ھ��뺯��
//	private static double [][] subcostMat= new double [26][26]; //�滻�����	
	//��ʼ���༭����
//	for (int i = 0; i < 26; i++) {
//        Arrays.fill(subcostMat[i], 1);
//    }
	
	//����������֮��ı༭����    Ŀ��  �� ��ѯ
	private static double ComputeDistance(String target,String source) {
		int n=target.trim().length();
		int m=source.trim().length();

		
		double [][] distance= new double [n+1][m+1];
		
		distance[0][0]=0;
		//��ʼ���߽���� ����distance[n][m]���� 
		for(int i=1;i<=n;i++) {
			distance[i][0]=i;
		}
		for(int j=1;j<=m;j++) {
			distance[0][j]=j;
		}
		//����������� Ŀ��Ͳ�ѯ���ʵĵ�һ����ĸ  ��Ӧ��ǰλ�� [i-1][j-1]
		for(int i=1;i<=n;i++) {
			for(int j=1;j<=m;j++) {
				//�������  [i-1][j] �� [i][j]    �൱�ڣ�j-1�� +1 
				double min=distance[i-1][j]+insertCost;				
				//�滻����   charAt ��ǰ�ַ���ͬ   [i-1][j-1] �� [i][j]
				if(target.charAt(i-1)==source.charAt(j-1)) {
					if(min>distance[i-1][j-1])
						min=distance[i-1][j-1];
				}else {	
					//�滻���뺯��
					substitudeCost = subeditCost(target.charAt(i-1),source.charAt(j-1));					
					if(min>distance[i-1][j-1]+substitudeCost)
						min=distance[i-1][j-1]+substitudeCost;
				}				
				//ɾ������ [i][j-1] �� [i][j]
				if(min>distance[i][j-1]+removeCost) {
					min=distance[i][j-1]+removeCost;
				}				
				//ȡ��С����
				distance[i][j]=min;
			}
		}	
		return distance[n][m];
	}
	
	//java�У�char ����' A'��ӦֵΪ65��char 'a'��ӦֵΪ97
	//�ú���������һ����Ӧ���̾����26*26���滻�������subCost[n,m]  �����̾����Ϊ1 2 3  �����滻����
	private static double subeditCost(char a,char b) {
		double subCost=1;
		//�����滻����
		if((a=='r')&&((b=='e')||(b=='f')||(b=='s'))) {
			subCost=0.5;	
		}else if((a=='f')&&((b=='e')||(b=='t')||(b=='s'))) {
			subCost=0.5;
		}		
		//���Ҷ�ά����,��ȡ�滻����
//		subCost=subcostMat[a-'a'][b-'a'];
//		System.out.print(subCost);
		return subCost;
	}
		
}
