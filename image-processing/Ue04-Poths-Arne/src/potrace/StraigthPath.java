package potrace;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

public class StraigthPath {

	private Vector<Vertex> mVertices = new Vector<Vertex>(10);

	private Vertex mConstraintZero = new Vertex(0, 0);
	private Vertex mConstraintOne = new Vertex(0, 0);

	/**
	 * Contains the current directions
	 */
	private byte directions = (byte) 0x0;

	public StraigthPath(Vertex from, Vertex to) {
		mVertices.add(from);
		mVertices.add(to);
	}

	public StraigthPath(Edge start) {
		mVertices.add(start.from);
		add(start);
	}

	/**
	 * Adding the edge to the path
	 * 
	 * @param add
	 * @return
	 */
	public boolean add(Edge add) {
		if (isAllowed(add)) {
			mVertices.add(add.to);
			directions |= add.getDirection();
			return true;
		}
		return false;
	}

	public Vertex get(int position) {
		if (position >= mVertices.size()) {
			return null;
		}
		return mVertices.get(position);
	}

	/**
	 * check the conditions for adding the vertex
	 * 
	 * @param add
	 * @return
	 */
	private boolean isAllowed(Edge add) {
		boolean allwoed = true;

		// 1. direction check
		byte dir = (byte) (directions | add.getDirection());
		if (dir == 15) {
			allwoed = false;
		}

		// 2.
		if (allwoed) {
			Vertex Vi = mVertices.get(0);
			Vertex ViVk = new Vertex(add.to.x - Vi.x, add.to.y - Vi.y);

			if (cross(mConstraintZero, ViVk) < 0
					|| cross(mConstraintOne, ViVk) > 0) {
				allwoed = false;
			}
			if (!(lenghtOf(ViVk.x) <= 1 && lenghtOf(ViVk.y) <= 1)) {
				mConstraintZero = updateConstraintZero(ViVk, mConstraintZero);
				mConstraintOne = updateConstraintOne(ViVk, mConstraintOne);
			}
		}

		return allwoed;
	}

	private Vertex updateConstraintZero(Vertex a, Vertex c) {
		Vertex d = new Vertex(0, 0);
		d.x = a.x + ((a.y >= 0 && (a.y > 0 || a.x < 0)) ? 1 : -1);
		d.y = a.y + ((a.x <= 0 && (a.x < 0 || a.y < 0)) ? 1 : -1);

		if (cross(c, d) >= 0) {
			return d;
		} else {
			return c;
		}
	}

	private Vertex updateConstraintOne(Vertex a, Vertex c) {
		Vertex d = new Vertex(0, 0);
		d.x = a.x + ((a.y <= 0 && (a.y < 0 || a.x < 0)) ? 1 : -1);
		d.y = a.y + ((a.x >= 0 && (a.x > 0 || a.y < 0)) ? 1 : -1);

		if (cross(c, d) <= 0) {
			return d;
		} else {
			return c;
		}
	}

	private double lenghtOf(int a) {
		return Math.sqrt(a * a);
	}

	public boolean contains(Vertex other) {
		return this.mVertices.contains(other);
	}

	/**
	 * Calculate the cross product
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private int cross(Vertex a, Vertex b) {
		return (a.x * b.y - a.y * b.x);
	}

	public void draw(Graphics g, float zoom) {
		g.setColor(Color.BLUE);

		Vertex from = mVertices.get(0);
		Vertex to = mVertices.get(mVertices.size() - 1);
		g.drawLine(Math.round(from.x * zoom), Math.round(from.y * zoom),
				Math.round(to.x * zoom), Math.round(to.y * zoom));

		// g.setColor(Color.MAGENTA);
		// g.drawLine(Math.round(from.x * zoom), Math.round(from.y * zoom),
		// Math.round((from.x + mConstraintZero.x) * zoom),
		// Math.round((from.y + mConstraintZero.y) * zoom));
		//
		// g.setColor(Color.CYAN);
		// g.drawLine(Math.round(from.x * zoom), Math.round(from.y * zoom),
		// Math.round((from.x + mConstraintOne.x) * zoom),
		// Math.round((from.y + mConstraintOne.y) * zoom));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Path [");
		for (Vertex v : mVertices) {
			sb.append(v);
		}
		return sb.append("]").toString();
	}

	public Vertex getLast() {
		return mVertices.get(mVertices.size() - 1);
	}
}
