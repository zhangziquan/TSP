import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;



public class GUI {
	
	private boolean finish = true;
	private boolean finish2 = true;
	private boolean finish3 = true;
	
	static String file = "pr299.tsp";
	
	private myPanel displayPanel;
	private myPanel displayPanel2;
	private myPanel displayPanel3;
	private SA sa;
	private localSearch ls;
	private GA ga;
	
	private point[] path;
	private point[] path2;
	private point[] path3;
	
	public GUI() {
		
    	JFrame frame = new JFrame("TSP问题"); // 设置标题
    	
    	frame.setLayout(new GridLayout(2,3,10,10));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        
		sa = new SA();
        sa.readTSP(file);
        sa.init();
        //sa.saSearch();
        
		ls = new localSearch();
		ls.readTSP(file);
        ls.init();
        //ls.localSearchTSP();
        
        ga = new GA();
        ga.readTSP(file);
        //ga.GASearch();
        
        path = ls.getPath();
        
        double minX = 10000000000.0;
    	double minY = 10000000000.0;
    	double maxX = 0;
    	double maxY = 0;
        
        for(int i = 0; i< path.length;i++) {
        	double x = path[i].getX();
        	double y = path[i].getY();
        	if(minX > x) {
        		minX = x;
        	}
        	if(maxX < x) {
        		maxX = x;
        	}
        	if(minY > y) {
        		minY = y;
        	}
        	if(maxY < y) {
        		maxY = y;
        	}
        }
        
        displayPanel = new myPanel("LS", minX, minY, maxX, maxY);
        displayPanel2 = new myPanel("SA", minX, minY, maxX, maxY);
        displayPanel3 = new myPanel("GA", minX, minY, maxX, maxY);
        
        displayPanel.setBackground(Color.WHITE);
        displayPanel2.setBackground(Color.WHITE);
        displayPanel3.setBackground(Color.WHITE);
        frame.add(displayPanel,0);
        frame.add(displayPanel2,1);
        frame.add(displayPanel3,2);
        frame.pack();
	}

    public void start() {
        rx.Observable<Object> observable = Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> o) {
            	ls.localSearchTSP();
            	o.onCompleted();
            }
            
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
        observable.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            	finish = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
            	
            }
        });
        
        rx.Observable<Object> observable2 = Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> o) {
            	sa.saSearch();
            	o.onCompleted();
            }
            
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
        observable2.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            	finish2 = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
            	
            }
        });
        
        rx.Observable<Object> observable3 = Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> o) {
                ga.GASearch();
            	o.onCompleted();
            }
            
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
        observable3.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
            	finish3 = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
            	
            }
        });        
        rx.Observable<Object> observable4 = Observable.create(new Observable.OnSubscribe<Object>() {

            @Override
            public void call(Subscriber<? super Object> o) {
            	while(true) {
            		try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            		if(finish) {
            			path = ls.getPath();
                  		double length = ls.getLength();
                		displayPanel.repaint();	
                		displayPanel.setPath(path,length);
            		}
            		
            		if(finish2) {            		
            			path2 = sa.getPath();
            			double length2 = sa.getLength();
                		displayPanel2.repaint();
                		displayPanel2.setPath(path2,length2);	
            		}
            		
            		if(finish3) {
            			path3 = ga.getPath();
                		double length3 = ga.getLength();
                		displayPanel3.repaint();	
                		displayPanel3.setPath(path3,length3);
            		}
            	}
            }
            
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread());
        observable4.subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
            	
            }
        });
    }
	
    public static void main(String[] args) {
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GUI myGUI = new GUI();
        myGUI.start();
    }
}
