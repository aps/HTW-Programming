package potrace;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

public class Path {

	private Vector<Vertex> mVertecies = new Vector<Vertex>(10);

	private Vertex mConstraintZero = new Vertex(0, 0);

	private Vertex mConstraintOne = new Vertex(0, 0);

	private byte directions = (byte) 0x0;

	public Path(Edge start) {
		mVertecies.add(start.from);
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
			mVertecies.add(add.to);
			directions |= add.getDirection();
			return true;
		}
		return false;
	}

	public Vertex get(int position) {
		if (position >= mVertecies.size()) {
			return null;
		}
		return mVertecies.get(position);
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
		if (dir == 7 || dir == 11 || dir == 13 || dir == 14) {
			D.ln("bit not allowed ..");
			allwoed = false;
		}

		// 2.
		if (allwoed) {
			Vertex Vi = mVertecies.get(0);
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
		d.x = (a.y >= 0 && (a.y > 0 || a.x < 0)) ? a.x + 1 : a.x - 1;
		d.y = (a.x <= 0 && (a.x < 0 || a.y < 0)) ? a.y + 1 : a.y - 1;

		if (cross(c, d) >= 0) {
			return d;
		} else {
			return c;
		}
	}

	private Vertex updateConstraintOne(Vertex a, Vertex c) {
		Vertex d = new Vertex(0, 0);
		d.x = (a.y <= 0 && (a.y < 0 || a.x < 0)) ? a.x + 1 : a.x - 1;
		d.y = (a.x >= 0 && (a.x > 0 || a.y < 0)) ? a.y + 1 : a.y - 1;

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
		return this.mVertecies.contains(other);
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

		Vertex from = mVertecies.get(0);
		Vertex to = mVertecies.get(mVertecies.size() - 1);
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
		for (Vertex v : mVertecies) {
			sb.append(v);
		}
		return sb.append("]").toString();
	}

	public Vertex getLast() {
		return mVertecies.get(mVertecies.size() - 1);
	}
}
