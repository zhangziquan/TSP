import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

/**
 * SA
 */
public class SA {

    private double E = 2.718281828459;

    private double length;

    private double lastlength;

    private double temperature = 1;
    
    private int times = 1000;

    private int count = 0;

    private point[] points = null;

    public void init() {
        length = 0;
        for (int i = 0; i < count; i++) {
            Random rand = new Random();
            int pos = rand.nextInt(count);
            point temPoint = new point(points[pos]);
            points[pos] = points[i];
            points[i] = temPoint;
        }
        for (int i = 0; i < count - 1; i++) {
            length += points[i].dist(points[i + 1]);
        }
        length += points[0].dist(points[count - 1]);
        System.out.println(length);

        while (receiveRate() < 0.9) {
            temperature *= 1.05;
        }
    }

    private double receiveRate() {
        int receive = 0, times = 300, t = 300;
        while (t != 0) {
            Random rand = new Random();
            int pos1 = rand.nextInt(count);
            int pos2 = rand.nextInt(count);
            if (pos1 > pos2) {
                int temp = pos1;
                pos1 = pos2;
                pos2 = temp;
            }
            if (pos1 == pos2 || (pos1 == 0 && pos2 == count - 1)) {
                receive++;
                continue;
            }
            double newdist = points[pos1].dist(points[(pos2 + 1) % count])
                    + points[pos2].dist(points[(pos1 - 1 + count) % count]);
            double olddist = points[pos1].dist(points[(pos1 - 1 + count) % count])
                    + points[pos2].dist(points[(pos2 + 1) % count]);

            double offset = newdist - olddist;
            if (offset <= 0 || Math.pow(E, -offset * 1.0 / temperature) >= Math.random()) {
                receive++;
            }
            t--;
        }
        return 1.0 * receive / times;
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
            points = new point[num];
            bf.readLine();
            bf.readLine();

            String str;
            String thepoint[];
            while ((str = bf.readLine()) != null) {
                thepoint = str.split(" ");
                System.out.print("id: " + thepoint[0] + " x: " + thepoint[1] + " y: " + thepoint[2] + "\n");
                points[count++] = new point(Integer.parseInt(thepoint[0]), Integer.parseInt(thepoint[1]),
                        Integer.parseInt(thepoint[2]));
            }
            bf.close();
            fr.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    
    public point[] search() {
    	times *= 1.01;
        int t = times;
        while (t != 0) {
            Random rand = new Random();
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
            double newdist = points[pos1].dist(points[(pos2 + 1) % count])
                    + points[pos2].dist(points[(pos1 - 1 + count) % count]);
            double olddist = points[pos1].dist(points[(pos1 - 1 + count) % count])
                    + points[pos2].dist(points[(pos2 + 1) % count]);

            double offset = newdist - olddist;

            if (offset <= 0 || Math.pow(E, -offset * 1.0 / temperature) >= Math.random()) {
                length += offset;
                while (pos1 < pos2) {
                    point temPoint = points[pos1];
                    points[pos1] = points[pos2];
                    points[pos2] = temPoint;
                    pos1++;
                    pos2--;
                }
            }
            t--;
        }
        return points;
    }

    public void saSearch() {
        while (temperature > 0.001) {
			try {
				Thread.sleep(100);	
			}catch(Exception e) {
				
			}
        	search();
            if (length == lastlength) {
                break;
            }
            temperature *= 0.98;
            lastlength = length;
            //System.out.println(length);
        }
    }
    
    public point[] getPath() {
		return points;
    }
    
    public double getLength()
    {
    	return length;
    }
}