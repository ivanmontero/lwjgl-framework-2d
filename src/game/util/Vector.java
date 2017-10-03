package game.util;

import game.Render;

import java.awt.*;

public class Vector {

    public double x, y;

    public Vector(){
        this.x = 0.0;
        this.y = 0.0;
    }

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector(Point p1, Point p2){
        this.x = p2.x - p1.x;
        this.y = p2.y - p1.y;
    }

    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(this.x + v.x, this.y + v.y);
    }

    public Vector add(Point p) {
        return new Vector(this.x + p.x, this.y + p.y);
    }

    public Vector sub(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y);
    }

    public Vector mul(double a) {
        return new Vector(this.x * a, this.y * a);
    }

    public Vector negate() {
        return new Vector(-this.x, -this.y);
    }

    public Vector normalize(){
        double length = length();
        return new Vector(this.x/length, this.y/length);
    }

    public float getAngle(){
        return Render.toPositiveAngle((float)Math.toDegrees(Math.atan2(y, x)));
    }

    public double length(){
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public static Vector getVector(Point p1, Point p2){
        return new Vector(p2.x - p1.x, p2.y - p1.y);
    }

    public static Vector getVector(double x1, double y1, double x2, double y2){
        return new Vector(x2 - x1, y2 - y1);
    }

    public static Vector addVectors(Vector v1, Vector v2){
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector addVectors(Vector... vecs){
        Vector sum = new Vector();
        for(Vector v : vecs){
            sum = addVectors(sum, v);
        }
        return sum;
    }

    public static Vector multiplyVectors(Vector v1, Vector v2){
        return new Vector(v1.x * v2.x, v1.y * v2.y);
    }

    public static Vector multiplyVectors(Vector... vecs){
        Vector product = new Vector();
        for(Vector v : vecs){
            product = multiplyVectors(product, v);
        }
        return product;
    }

    public static Point toPoint(Vector v){
        return new Point((int) v.x, (int) v.y);
    }

    public static Vector toPoint(Point p){
        return new Vector(p.x, p.y);
    }

    public float angularDistanceBetween(Vector v1, Vector v2){
        float phi = Math.abs(v1.getAngle() - v2.getAngle()) % 360;
        float distance = phi > 180.0f ? 360.0f - phi : phi;
        return distance;
    }
}
