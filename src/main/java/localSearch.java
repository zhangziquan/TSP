import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

/**
 * localSearch
 */
public class localSearch {

	
	private point[] citys;
	
    private double length;

    private int count = 0;

    private int[] path = null;

    public void init() {
        length = 0;
        for (int i = 0; i < count; i++) {
            Random rand = new Random();
            int pos = rand.nextInt(count);
            int temPoint = path[pos];
            path[pos] = path[i];
            path[i] = temPoint;
        }
        for (int i = 0; i < count - 1; i++) {
            length += citys[path[i]].dist(citys[path[i + 1]]);
        }
        length += citys[path[0]].dist(citys[path[count - 1]]);
        //System.out.println(length);
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
            path = new int[num];
            citys = new point[num];
            bf.readLine();
            bf.readLine();

            String str;
            String thepoint[];
            while ((str = bf.readLine()) != null) {
                thepoint = str.split(" ");
                System.out.print("id: " + thepoint[0] + " x: " + thepoint[1] + " y: " + thepoint[2] + "\n");
                citys[count] = new point(Integer.parseInt(thepoint[0]), Integer.parseInt(thepoint[1]),
                        Integer.parseInt(thepoint[2]));
                path[count++] = Integer.parseInt(thepoint[0]) - 1;
            }
            bf.close();
            fr.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }

    public void localSearchTSP() {
        int times = 400000;
        while (times != 0) {
//			try {
//				Thread.sleep(1);	
//			}catch(Exception e) {
//				
//			}
            Random rand = new Random();
            double choic = rand.nextInt(1000)/1000.0;
            if(choic < 0.9) {
                int pos1 = rand.nextInt(count);
                int pos2 = rand.nextInt(count);

                if (pos1 > pos2) {
                    int temp = pos1;
                    pos1 = pos2;
                    pos2 = temp;
                }

                if (pos1 == pos2 || (pos1 == 0 && pos2 == count - 1)) {
                    continue;
                }
                double newdist = citys[path[pos1]].dist(citys[path[(pos2 + 1) % count]])
                        + citys[path[pos2]].dist(citys[path[(pos1 - 1 + count) % count]]);
                double olddist = citys[path[pos1]].dist(citys[path[(pos1 - 1 + count) % count]])
                        + citys[path[pos2]].dist(citys[path[(pos2 + 1) % count]]);

                double offset = newdist - olddist;

                if (offset < 0) {
                    length += offset;
                    while (pos1 < pos2) {
                        int temPoint = path[pos1];
                        path[pos1] = path[pos2];
                        path[pos2] = temPoint;
                        pos1++;
                        pos2--;
                    }
                }
                times--;
                //System.out.println(length);
            }else {
                int[] newpath = new int[count];
                int pos1 = rand.nextInt(count);
                int pos2 = rand.nextInt(count);
                
                while (pos1 == pos2 || (pos1 == 0 && pos2 == count - 1)) {
                    pos1 = rand.nextInt(count);
                    pos2 = rand.nextInt(count);
                }
                
            	newpath[0] = citys[path[pos1]].getId()-1;
            	newpath[1] = citys[path[pos2]].getId()-1;
            	int j = 2;
                for(int i = 0; i < count;i++) {
                	if((citys[path[i]].getId() - 1)!= newpath[0] && (citys[path[i]].getId()-1)!=newpath[1]) {
                		newpath[j++] = citys[path[i]].getId() - 1;
                	}
                }
                double newdist = 0;
                for(int i = 0; i < count - 1; i++) {
                	newdist += citys[newpath[i]].dist(citys[newpath[i+1]]);
                }
                newdist += citys[newpath[0]].dist(citys[newpath[count-1]]);
                
                double offset = newdist - length;
                if(offset<0) {
                	length += offset;
                	for(int i = 0; i < count; i++) {
                		path[i] = citys[newpath[i]].getId() - 1;
                	}
                }
                times--;
                //System.out.println(length);
            }
        }
    }
    
    public point[] getPath() {
    	point[] get = new point[count];
    	for(int i = 0; i<count; i++) {
    		get[i] = new point(citys[path[i]]);
    	}
    	return get;
    }
    
    public double getLength() {
    	return length;
    }
}