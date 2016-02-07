package com.dragonfruitstudios.brokenbonez.BoundingShapes;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.dragonfruitstudios.brokenbonez.GameView;
import com.dragonfruitstudios.brokenbonez.VectorF;

public class Circle {
    VectorF center;
    float radius;

    /**
     * Note: You're better off using the other constructor if you already have a position vector.
     * @param cx
     * @param cy
     * @param radius
     */
    public Circle(float cx, float cy, float radius) {
        this.center = new VectorF(cx, cy);
        this.radius = radius;
    }

    public Circle(VectorF center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public void setCenter(float cx, float cy) {
        center.set(cx, cy);
    }

    public void setCenter(VectorF center) {
        this.center = center;
    }

    public VectorF getCenter() {
        return center;
    }

    public float getRadius() {
        return radius;
    }

    public boolean collidesWith(Intersector shape) {
        // Based on answer here: http://stackoverflow.com/a/402019/492186

        // Check whether Circle's centre lies within the rectangle.
        if (shape.collidesWith(center)) { return true; }

        // Check whether either of the sides intersect with the circle.
        for (Line line : shape.getLines()) {
            if (collidesWith(line.getStart(), line.getFinish())) {
                return true;
            }
        }
        return false;

    }

    public Manifold collisionTest(Intersector shape) {
        //Manifold pointResult = shape.collisionTest(center);
        //if (pointResult.isCollided()) {
        //    pointResult.setPenetration(pointResult.getPenetration() + radius);
        //    return pointResult;
        //}


        for (Line line : shape.getLines()) {
            Manifold res = collisionTest(line.getStart(), line.getFinish());
            if (res.isCollided()) {
                return res;
            }
        }
        return new Manifold(null, -1, false);
    }

    public Manifold collisionTest(VectorF a, VectorF b) {
        VectorF BA = new VectorF(b.x - a.x, b.y - a.y);
        VectorF CA = new VectorF(center.x - a.x, center.y - a.y);
        float l = BA.magnitude();

        BA.normalise();
        float u = CA.dotProduct(BA);
        if (u <= 0) {
            CA.set(a.x, a.y);
        }
        else if (u >= l) {
            CA.set(b.x, b.y);
        }
        else {
            BA.mult(u);
            CA.set(BA.x + a.x, BA.y + a.y);
        }

        float x = center.x - CA.x;
        float y = center.y - CA.y;

        boolean collided = x * x + y * y <= radius*radius;
        if (collided) {
            float depth = radius - (float)Math.sqrt(x * x + y * y);

            VectorF startToFinish = b.subtracted(a);
            //Log.d("Normal", "Angle is: " + startToFinish.angle() + " " + center.angle());
            VectorF normal = new VectorF(-startToFinish.getY(), startToFinish.getX());
            //Log.d("Normal", "The normal is: " + normal);
            // -150, 300
            // -250, 0
            if (startToFinish.angle() > center.angle()) {
                normal = new VectorF(startToFinish.getY(), -startToFinish.getX());
                //Log.d("Normal", "The new normal is: " + normal);
            }

            normal.normalise();

            return new Manifold(normal, depth, collided);
        }
        else {
            return new Manifold(null, -1, false);
        }
    }

    /**
     * Determines whether Line AB intersects with this Circle.
     */
    public boolean collidesWith(VectorF a, VectorF b) {
        VectorF BA = new VectorF(b.x - a.x, b.y - a.y);
        VectorF CA = new VectorF(center.x - a.x, center.y - a.y);
        float l = BA.magnitude();

        BA.normalise();
        float u = CA.dotProduct(BA);
        if (u <= 0) {
            CA.set(a.x, a.y);
        }
        else if (u >= l) {
            CA.set(b.x, b.y);
        }
        else {
            BA.mult(u);
            CA.set(BA.x + a.x, BA.y + a.y);
        }

        float x = center.x - CA.x;
        float y = center.y - CA.y;

        boolean result = x * x + y * y <= radius*radius;
        //Log.d("Collision", String.format("(%.1f, %.1f) to (%.1f, %.1f) x Circle(%.1f, %.1f, %.1f) = %b",
        //        a.x, a.y, b.x, b.y, center.x, center.y, radius, result));
        return result;
    }

    /**
     * This is just for debugging purposes to show where the bounding circle is.
     * @param view
     */
    public void draw(GameView view) {
        view.drawCircle(center.x, center.y, radius, Color.parseColor("#ff279c"), Paint.Style.STROKE);
    }
}
