import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.Vector;

public class GA {
	
	point[] citys;
	point[] bestpath;
	
	Vector<int[]> population;
	Vector<int[]> newpopulation;
	
	double[] adapter;
	double[] liveper;
	int gSize = 1000;
	int maxgen = 8000;
	int count = 0;
	double PMUTATION = 0.15;
	double PXOVER = 0.9;
	double bestlength = 0;
	double bestfitness = 1;
	int bestsolve = 0;
	
	//生成初代种群
    private void init() {
    	adapter = new double[gSize];
    	population = new Vector<int[]>();
    	newpopulation = new Vector<int[]>();
		for(int i =0; i < gSize; i++) {
			int[] temp = new int[count];
	        for(int j = 0; j < count; j++) {
	        	temp[j] = j;
	        }
	        for (int j = 0; j < count; j++) {
	            Random rand = new Random();
	            int pos = rand.nextInt(count);
	            int temPoint = temp[pos];
	            temp[pos] = temp[j];
	            temp[j] = temPoint;
	        }
	        population.add(temp);
		}
    }
	
    //获得路径长度
	private double getLength(int[] p) {
		double length = 0;
		for(int i = 0; i< p.length-1;i++) {
			length += citys[p[i]].dist(citys[p[i+1]]);
		}
		length += citys[p[0]].dist(citys[p[p.length-1]]);
		return length;
	}
	
	   public int readTSP(String filename) {
	        try {

	            FileReader fr = new FileReader(filename);
	            BufferedReader bf = new BufferedReader(fr);

	            bf.readLine();
	            bf.readLine();
	            bf.readLine();
	            String numstr = bf.readLine();
	            int num = Integer.parseInt(numstr.split(" : ")[1]);
	            citys = new point[num];
	            bf.readLine();
	            bf.readLine();

	            String str;
	            String thepoint[];
	            while ((str = bf.readLine()) != null) {
	            	if(str == "EOF ") {
	            		break;
	            	}
	                thepoint = str.split(" ");
	                System.out.print("id: " + thepoint[0] + " x: " + thepoint[1] + " y: " + thepoint[2] + "\n");
	                citys[count++] = new point(Integer.parseInt(thepoint[0]), Integer.parseInt(thepoint[1]),
	                		Integer.parseInt(thepoint[2]));
	            }
	            bf.close();
	            fr.close();
	        } catch (Exception e) {
	            //handle exception
	        	System.out.println(e);
	        }
	        return count;
	    }
	
	//评价函数
	private void assess() {
		double alllive = 0;
		
		//计算总路径
		adapter = new double[population.size()];
		for(int i =0; i < population.size();i++) {
			adapter[i] = 1 / getLength(population.get(i));
		}
		
		bestfitness = 0;
		
		Zscore();
		
		//计算适应度
		liveper = new double[population.size()];
		for(int i = 0; i < population.size(); i++) {
			liveper[i] = adapter[i];
			alllive += liveper[i];
			if(liveper[i] > bestfitness) {
				bestfitness = liveper[i];
				bestsolve = i;
			}
		}
		bestlength = getLength(population.get(bestsolve));
		
		for(int i = 0; i < population.size(); i++) {
			liveper[i] = liveper[i]/alllive;
		}
		
		//计算累积适应度
		for(int i = 1; i < population.size(); i++ ) {
			liveper[i] = liveper[i] + liveper[i-1];
		}
		
		bestpath = new point[count];
		
		for(int i = 0; i < count; i++) {
			bestpath[i] = citys[population.get(bestsolve)[i]];
		}
	}
	
	//选择交配个体
	private void select() {
		newpopulation.removeAllElements();
		for(int i = 0; i < population.size(); i++) {
			Random rand = new Random();
			double p = rand.nextInt(1000)/1000.0;
			
			if(p<liveper[0]) {
				int[] temp = new int[count];
				copyArray(temp,population.get(0));
				newpopulation.add(temp);
			}else {
				for(int j = 0; j < population.size(); j++) {
					if(p >= liveper[j] && p < liveper[j+1]) {
						int[] temp = new int[count];
						copyArray(temp,population.get(j+1));
						newpopulation.add(temp);
					}
				}
			}
		}
	}
	
	//进行交配
	private void crossover() {
		int first = 0; // 被选择的个数。
		int one = 0;
		for(int i = 0; i< newpopulation.size(); i++) {
			Random rand = new Random();
			double p = rand.nextInt(1000)/1000.0;
			if(p < PXOVER)
			{
				first++;
				if(first%2 == 0) {
					Xover(one,i);
				}else {
					one = i;
				}
			}
		}
	}
	
	//进行染色体互换
	private void Xover(int one, int two) {
		Random rand = new Random();
		int point1 = rand.nextInt(count);
		int point2 = rand.nextInt(count);
		
		//选择交换点
		if(point1>point2) {
			int temp = point1;
			point1 = point2;
			point2 = temp;
		}
		int[] child1 = new int[count];
		int[] child2 = new int[count];
		copyArray(child1, newpopulation.get(one));
		copyArray(child2, newpopulation.get(two));
		
		//交换基因片段
		for(int i = point1; i < point2; i++) {
			child1[i] = newpopulation.get(two)[i];
			child2[i] = newpopulation.get(one)[i];
		}
		
		//解决子代冲突
		for(int i = 0; i< point1;i++) {
			//替换冲突的为父代对应位置的基因
			for(int j = point1; j < point2; j++) {
				if(child1[i] == child1[j]) {
					child1[i] = newpopulation.get(one)[j];
					j = point1 - 1;
				}
			}
		}
		
		for(int i = 0; i< point1;i++) {
			//替换冲突的为父代对应位置的基因
			for(int j = point1; j < point2; j++) {
				if(child2[i] == child2[j]) {
					child2[i] = newpopulation.get(two)[j];
					j = point1 - 1;
				}
			}
		}
		
		for(int i = point2; i<count;i++) {
			for(int j = point1; j < point2; j++) {
				if(child1[i] == child1[j]) {
					child1[i] = newpopulation.get(one)[j];
					j = point1 - 1;
				}
			}
		}
		
		for(int i = point2; i<count;i++) {
			for(int j = point1; j < point2; j++) {
				if(child2[i] == child2[j]) {
					child2[i] = newpopulation.get(two)[j];
					j = point1 - 1;
				}
			}
		}
		newpopulation.remove(one);
		newpopulation.insertElementAt(child1, one);
		newpopulation.remove(two);
		newpopulation.insertElementAt(child2, two);
	}
	
	
	//子代发生变异
	private void mutate() {
		for(int i = 0; i < newpopulation.size(); i++) {
			Random rand = new Random();
			double p = rand.nextInt(1000)/1000.0;
			if(p < PMUTATION) {
	            int pos1 = rand.nextInt(count);
	            int pos2 = rand.nextInt(count);
	            
	            while (pos1 == pos2 || (pos1 == 0 && pos2 == count - 1)) {
	            	pos1 = rand.nextInt(count);
	            }

	            if (pos1 > pos2) {
	                int temp = pos1;
	                pos1 = pos2;
	                pos2 = temp;
	            }
	            
                while (pos1 < pos2) {
                    int temPoint = newpopulation.get(i)[pos1];
                    newpopulation.get(i)[pos1] = newpopulation.get(i)[pos2];
                    newpopulation.get(i)[pos2] = temPoint;
                    pos1++;
                    pos2--;
                }
			}
		}
	}
	
	//遗传算法
	public void GASearch() {
		int generation = 0;
		init();
		while(generation < maxgen) {
			assess();
			select();
			crossover();
			mutate();
			generation ++;
			population.removeAllElements();
			population.addAll(newpopulation);
			/*System.out.println("此时最佳路长为：" + getLength(population.get(bestsolve)) + 
					"适应度为 " + bestfitness);*/
		}
	}
	
	static void copyArray(int[] a, int[] b) {
		for(int i = 0; i<b.length;i++) {
			a[i] = b[i];
		}
	}
	
	//归一化处理
	private void Zscore() {
		double sum = 0;
		for(int i = 0; i< adapter.length; i++) {
			sum += adapter[i];
		}
		double ave = sum/adapter.length;
		double s = 0;
		for(int i = 0; i< adapter.length; i++) {
			s += (adapter[i] - ave) * (adapter[i] - ave);
		}
		s = Math.sqrt(s);
		
		for(int i = 0; i< adapter.length; i++) {
			if(s != 0) {
				adapter[i] = (adapter[i]-ave)/s;
				if(adapter[i] < 0) {
					adapter[i] = 0;
				}
			}else {
				adapter[i] = 1;
			}
		}
	}
	
	//得到当前最佳路径
	public point[] getPath() {
		return bestpath;
	}
	
	public double getLength() {
		return bestlength;
	}
}