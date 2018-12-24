class point {
    private int id;
    private double x, y;
    
    public point(point other) {
    	id = other.getId();
    	x = other.getX();
    	y = other.getY();
    }

    public point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double dist(point r) {
        return Math.sqrt((x - r.x) * (x - r.x) + (y - r.y) * (y - r.y));
    }

    public int getId() {
        return id;
    }
    
    public double getX() {
    	return x;
    }
    
    public double getY() {
    	return y;
    }
}