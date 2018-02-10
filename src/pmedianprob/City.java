
package pmedianprob;

public class City {
    private int x;
    private int y;
    private int demand;
    private int index;

    public City() {
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDemand(int d) {
        this.demand = d;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getDemand() {
        return this.demand;
    }

    public int getIndex() {
        return this.index;
    }

    public void getInfo(){
        System.out.println("Index:" + this.index + " X:" + this.x + " Y:" + this.y + " Demand:" + this.demand); 
    }
}
