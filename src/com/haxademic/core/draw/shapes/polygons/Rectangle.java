package com.haxademic.core.draw.shapes.polygons;

import processing.core.PGraphics;
import processing.core.PVector;

public class Rectangle {

	// Ported from Java's awt Rectangle, but floats
	
    public float x;
    public float y;
    public float width;
    public float height;
	
	protected boolean collided = false;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle(float width, float height) {
        this(0, 0, width, height);
    }

    public Rectangle(PVector p) {
        this(p.x, p.y, 0, 0);
    }

    public float x() {
        return x;
    }
    public void x(float x) {
    	this.x = x;
    }

    public float y() {
        return y;
    }
    public void y(float y) {
    	this.y = y;
    }

    public float width() {
        return width;
    }
    public void width(float width) {
    	this.width = width;
    }

    public float height() {
        return height;
    }
    public void height(float height) {
    	this.height = height;
    }

    public PVector getLocation() {
        return new PVector(x, y);
    }
    public void setLocation(PVector p) {
        setLocation(p.x, p.y);
    }
    
    public void setRect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(float dx, float dy) {
        float oldv = this.x;
        float newv = oldv + dx;
        if (dx < 0) {
            // moving leftward
            if (newv > oldv) {
                // negative overflow
                // Only adjust width if it was valid (>= 0).
                if (width >= 0) {
                    // The right edge is now conceptually at
                    // newv+width, but we may move newv to prevent
                    // overflow.  But we want the right edge to
                    // remain at its new location in spite of the
                    // clipping.  Think of the following adjustment
                    // conceptually the same as:
                    // width += newv; newv = MIN_VALUE; width -= newv;
                    width += newv - Integer.MIN_VALUE;
                    // width may go negative if the right edge went past
                    // MIN_VALUE, but it cannot overflow since it cannot
                    // have moved more than MIN_VALUE and any non-negative
                    // number + MIN_VALUE does not overflow.
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving rightward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (width >= 0) {
                    // Conceptually the same as:
                    // width += newv; newv = MAX_VALUE; width -= newv;
                    width += newv - Integer.MAX_VALUE;
                    // With large widths and large displacements
                    // we may overflow so we need to check it.
                    if (width < 0) width = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.x = newv;

        oldv = this.y;
        newv = oldv + dy;
        if (dy < 0) {
            // moving upward
            if (newv > oldv) {
                // negative overflow
                if (height >= 0) {
                    height += newv - Integer.MIN_VALUE;
                    // See above comment about no overflow in this case
                }
                newv = Integer.MIN_VALUE;
            }
        } else {
            // moving downward (or staying still)
            if (newv < oldv) {
                // positive overflow
                if (height >= 0) {
                    height += newv - Integer.MAX_VALUE;
                    if (height < 0) height = Integer.MAX_VALUE;
                }
                newv = Integer.MAX_VALUE;
            }
        }
        this.y = newv;
    }
    
	public boolean collided() { return collided; }
	public void collided(boolean collided) { this.collided = collided; }


//    public boolean contains(PVector p) {
//        return contains(p.x, p.y);
//    }

    /**
     * Checks whether or not this <code>Rectangle</code> entirely contains
     * the specified <code>Rectangle</code>.
     *
     * @param     r   the specified <code>Rectangle</code>
     * @return    <code>true</code> if the <code>Rectangle</code>
     *            is contained entirely inside this <code>Rectangle</code>;
     *            <code>false</code> otherwise
     * @since     1.2
     */
    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }

    public boolean contains(PVector r) {
    	return contains(r.x, r.y, 0.001f, 0.001f);
    }
    
    public boolean contains(float X, float Y, float W, float H) {
        float w = this.width;
        float h = this.height;
        if (w < 0 || h < 0 || W < 0 || H < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        float x = this.x;
        float y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        W += X;
        if (W <= X) {
            // X+W overflowed or W was zero, return false if...
            // either original w or W was zero or
            // x+w did not overflow or
            // the overflowed x+w is smaller than the overflowed X+W
            if (w >= x || W > w) return false;
        } else {
            // X+W did not overflow and W was not zero, return false if...
            // original w was zero or
            // x+w did not overflow and x+w is smaller than X+W
            if (w >= x && W > w) return false;
        }
        h += y;
        H += Y;
        if (H <= Y) {
            if (h >= y || H > h) return false;
        } else {
            if (h >= y && H > h) return false;
        }
        return true;
    }

    public boolean intersects(Rectangle r) {
        float tw = this.width;
        float th = this.height;
        float rw = r.width;
        float rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        float tx = this.x;
        float ty = this.y;
        float rx = r.x;
        float ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

    /**
     * Computes the intersection of this <code>Rectangle</code> with the
     * specified <code>Rectangle</code>. Returns a new <code>Rectangle</code>
     * that represents the intersection of the two rectangles.
     * If the two rectangles do not intersect, the result will be
     * an empty rectangle.
     *
     * @param     r   the specified <code>Rectangle</code>
     * @return    the largest <code>Rectangle</code> contained in both the
     *            specified <code>Rectangle</code> and in
     *            this <code>Rectangle</code>; or if the rectangles
     *            do not intersect, an empty rectangle.
     */
    public Rectangle intersection(Rectangle r) {
        float tx1 = this.x;
        float ty1 = this.y;
        float rx1 = r.x;
        float ry1 = r.y;
        float tx2 = tx1; tx2 += this.width;
        float ty2 = ty1; ty2 += this.height;
        float rx2 = rx1; rx2 += r.width;
        float ry2 = ry1; ry2 += r.height;
        if (tx1 < rx1) tx1 = rx1;
        if (ty1 < ry1) ty1 = ry1;
        if (tx2 > rx2) tx2 = rx2;
        if (ty2 > ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Integer.MIN_VALUE) tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE) ty2 = Integer.MIN_VALUE;
        return new Rectangle(tx1, ty1, (float) tx2, (float) ty2);
    }
    
    public float right() {
        return x() + width();
    }
    public float bottom() {
        return y() + height();
    }
    public float centerX() {
        return x() + width() / 2f;
    }
    public float centerY() {
        return y() + height() / 2f;
    }


    public void add(float newx, float newy) {
        float x1 = Math.min(x, newx);
        float x2 = Math.max(right(), newx);
        float y1 = Math.min(y, newy);
        float y2 = Math.max(bottom(), newy);
        setRect(x1, y1, x2 - x1, y2 - y1);
    }

    public void add(PVector pt) {
        add(pt.x, pt.y);
    }

    public boolean isEmpty() {
        return (width <= 0) || (height <= 0);
    }

    public boolean equals(Rectangle obj) {
        Rectangle r = (Rectangle)obj;
        return ((x == r.x) &&
                (y == r.y) &&
                (width == r.width) &&
                (height == r.height));
    }
    
    public void draw(PGraphics pg) {
		pg.stroke(255);
		pg.noFill();
		if(collided) pg.fill(0, 255, 0, 50);
		pg.rect(x, y, width, height);
	}

    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
}
