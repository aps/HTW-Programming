package potrace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Vector;

public class PotraceContour {

	private LinkedList<Edge> mContour;

	private int type;

	private int newStartPos;

	public static final int TYPE_INNER = 1;
	public static final int TYPE_OUTER = 0;

	public void setType(int type) {
		this.type = type;
	}

	public PotraceContour() {
		mContour = new LinkedList<Edge>();
	}

	public void addLine(Vertex from, Vertex to) {
		mContour.addLast(new Edge(from, to));
	}

	public void addLine(Edge line) {
		mContour.addLast(line);
	}

	public Edge get(int position) {
		if (position >= mContour.size()) {
			return null;
		}
		return mContour.get(position);
	}

	public int size() {
		return mContour.size();
	}

	public void addLines(LinkedList<Edge> lines) {
		mContour.addAll(lines);
	}

	public void addLines(Vector<Edge> lines) {
		mContour.addAll(lines);
	}

	public Edge getByStart(Vertex start) {
		Edge edge = null;
		for (int i = 0; i < mContour.size(); i++) {
			if (mContour.get(i).from.equals(start)) {
				this.newStartPos = i;
				return new Edge(mContour.get(i));
			}
			continue;
		}

		return edge;
	}

	public int getNewStartPosition() {
		return newStartPos;
	}

	public boolean contains(Edge obj) {
		return mContour.contains(obj);
	}

	public void printCoordinates() {
		System.out.println("contour: ");
		for (Edge line : mContour) {
			System.out.print(" " + line.toString());
		}
		System.out.println("--------");
	}

	public void drawContour(Graphics g, float zoom) {
		if (type == TYPE_INNER) {
			g.setColor(Color.ORANGE);
		} else {
			g.setColor(Color.RED);
		}

		Graphics2D g2 = (Graphics2D) g;
		if (zoom > 5)
			g2.setStroke(new BasicStroke(2));
		else
			g2.setStroke(new BasicStroke(1));

		for (Edge line : mContour) {
			g.drawLine(Math.round(line.from.x * zoom),
					Math.round(line.from.y * zoom),
					Math.round(line.to.x * zoom), Math.round(line.to.y * zoom));
		}

		if (!mPath.isEmpty()) {
			for (StraigthPath p : mPath) {
				p.draw(g, zoom);
			}
		}

		// if (!mPolygons.isEmpty()) {
		// for (Vector<Path> poly : mPolygons) {
		// for (Path p : poly) {
		// p.draw(g, zoom);
		// }
		// }
		// }

		if (!mPolygons.isEmpty()) {
			Vector<Path> poly = mPolygons.get(0);
			for (Path p : poly) {
				p.draw(g, zoom);
			}
		}

	}

	Vector<StraigthPath> mPath = new Vector<StraigthPath>();

	Vector<Vector<Path>> mPolygons = new Vector<Vector<Path>>();

	protected int[] calculateDistances() {
		int distance[] = new int[mContour.size()];

		Edge startPrevEdge;
		Edge startEdge;
		Edge currentPrevEdge;
		Edge currentEdge;
		StraigthPath tempPrevPath;
		StraigthPath tempPath;

		int size = mContour.size();
		int nextK, k;

		for (int i = 0; i < size; i++) {

			startEdge = mContour.get(i);
			tempPath = new StraigthPath(startEdge);
			k = i;

			// get the new previous start point
			startPrevEdge = (i - 1) > 0 ? mContour.get((i - 1) % size)
					: mContour.get(size - 1);
			tempPrevPath = new StraigthPath(startPrevEdge);
			nextK = i + 1;
			do {
				k++;
				currentEdge = mContour.get(k % size);

				nextK++;
				currentPrevEdge = mContour.get(nextK % size);
			} while (tempPath.add(currentEdge)
					&& tempPrevPath.add(currentPrevEdge));
			distance[i] = k;
		}
		return distance;
	}

	private void findAllStraightPaths() {
		Vector<StraigthPath> closedPath = new Vector<StraigthPath>();
		Vector<StraigthPath> newPath;
		for (int i = 0; i < mContour.size(); i++) {
			newPath = findClosedPath(mContour.get(i), i);
			if (closedPath.size() == 0 || newPath.size() < closedPath.size()) {
				closedPath = newPath;
			}
		}
	}

	public Vector<StraigthPath> findClosedPath(Edge start, int offset) {
		Vector<StraigthPath> temp = new Vector<StraigthPath>();
		int pos = 1;

		StraigthPath p;

		do {
			p = findMaxPath(start, pos + offset);
			mPath.add(p);

			start = getByStart(mPath.get(mPath.size() - 1).getLast());
			pos = getNewStartPosition();

		} while ((mPath.size() < 2 || !mPath.get(mPath.size() - 1).contains(
				mPath.get(0).get(0))));

		return temp;
	}

	public void findStraightPaths() {
		int pos = 1;

		Edge start = get(0);
		StraigthPath p;

		do {
			p = findMaxPath(start, pos);
			mPath.add(p);

			start = getByStart(mPath.get(mPath.size() - 1).getLast());
			pos = getNewStartPosition();

		} while ((mPath.size() < 2 || !mPath.get(mPath.size() - 1).contains(
				mPath.get(0).get(0))));

	}

	private StraigthPath findMaxPath(Edge start, int pos) {
		// D.ln("findMax: " + start + " pos= " + pos);
		StraigthPath p = new StraigthPath(start);
		while (pos < size() && p.add(get(pos % size()))) {
			pos++;
		}
		return p;
	}

	public void run() {
		// calculate valid segments
		int[] distances = calculateDistances();

		Vector<Vector<Path>> maxDistancePolygones = generateMaxDistancePolygons(distances);
		mPolygons.addAll(maxDistancePolygones);

	}

	/**
	 * Generate polygons based on the max distance of the straight paths
	 * 
	 * @param distances
	 * @return
	 */
	private Vector<Vector<Path>> generateMaxDistancePolygons(int[] distances) {
		Vector<Vector<Path>> polygones = new Vector<Vector<Path>>();
		Vector<Path> currentPolygon;

		int start, k, i;
		boolean run = true;

		Path p;
		for (int index = 0; index < distances.length; index++) {
			run = true;
			currentPolygon = new Vector<Path>();

			start = index;
			i = start;
			k = distances[i];

			do {
				p = new Path(get(i % distances.length).from, get(k
						% distances.length).from);
				currentPolygon.add(p);
				i = k;
				k = distances[i % distances.length];

				// check condition
				if ((i % distances.length) == start
						|| (i - start) >= distances.length - 1
						|| (i > (k % distances.length) && i > start && (k % distances.length) > start)
						|| (i < start && k > start)) {
					run = false;
				}

			} while (run);
			if (i % distances.length != start) {
				p = new Path(get(i % distances.length).from, get(start).from);
				currentPolygon.add(p);
			}

			// add only shortest polygons

			if (polygones.isEmpty()
					|| polygones.get(0).size() >= currentPolygon.size()) {
				if (!polygones.isEmpty()
						&& polygones.get(0).size() > currentPolygon.size()) {
					polygones.clear();
				}
				polygones.add(currentPolygon);
			}
			// polygones.add(currentPolygon);
		}
		return polygones;
	}
}
