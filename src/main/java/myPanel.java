import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

class myPanel extends JPanel{
 
	String title;
	double minX;
	double minY;
	double maxX;
	double maxY;
	long startTime;
	int scale = 8;
	point[] path;
	double length = 0;

	private static final long serialVersionUID = 1L;

	public myPanel(String title,double minx,double miny,double maxx,double maxy) {
		this.title = title;
		minX = minx - 200;
		minY = miny - 500;
		maxX = maxx + 200;
		maxY = maxy + 200;
		scale = (int) Math.max((maxX-minX)/768, (maxY-minY)/400);
		startTime = System.currentTimeMillis();
	}
		@Override
        public void paint(Graphics g) {
			
            super.paint(g);
 
            Graphics2D g2d = (Graphics2D)g;//强制类型转换
            
            Font font=new Font("SansSerif",Font.ITALIC,24);
            
            g2d.setFont(font);
       
            long now = System.currentTimeMillis();
            int slength = (int) length;
            g2d.drawString(title + " Length: " + slength + "   " + "Time: " + (now - startTime) + "ms", 10, 30);
   
            int strokeWidth = 3;//设置笔画宽度
            g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if(path == null) {
            	return;
            }
            
            for (int i = 0; i < path.length-1; i++) {
            	g2d.setColor(Color.blue);
            	Line2D line = new Line2D.Double((path[i].getX() - minX)/scale, (path[i].getY()- minY)/scale, (path[i+1].getX() - minX)/scale, (path[i+1].getY()-minY)/scale);
            	g2d.draw(line);
            	g2d.setColor(Color.RED);
            	g2d.fillOval((int)(path[i].getX() - minX)/scale, (int)(path[i].getY() - minY)/scale, 6, 6);
            }
            
            Line2D line = new Line2D.Double((path[0].getX() - minX)/scale, (path[0].getY() - minY)/scale, (path[path.length-1].getX()-minX)/scale, (path[path.length-1].getY()-minY)/scale);
            g2d.draw(line);
        }
 
        @Override
        public Dimension getPreferredSize(){
            return new Dimension((int)(maxX- minX)/scale, (int)(maxY - minY)/scale);
        }
        
        public void setPath(point[] path, double length) {
        	this.path = path;
        	this.length = length;
        }
    }