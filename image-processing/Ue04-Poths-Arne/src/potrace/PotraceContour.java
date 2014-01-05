package potrace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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

		if (mSettings.get("image")) {

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
						Math.round(line.to.x * zoom),
						Math.round(line.to.y * zoom));
			}
		}

		if (!mPath.isEmpty()) {
			for (StraigthPath p : mPath) {
				p.draw(g, zoom);
			}
		}

		// if (!mPolygons.isEmpty()) {
		// for (Polygon poly : mPolygons) {
		// // poly.get(0).drawStart(g2, zoom);
		// for (Path p : poly) {
		// p.draw(g, zoom);
		// }
		// }
		// }
		
		if (mSettings.containsKey("polygon") && mSettings.get("polygon")) {
			if (!mPolygons.isEmpty()) {
				Polygon poly = mPolygons.get(0);

				// poly.get(0).drawStart(g2, zoom);

				for (Path p : poly) {
					p.draw(g, zoom);
				}
			}
		}

	}

	Vector<StraigthPath> mPath = new Vector<StraigthPath>();

	Vector<Polygon> mPolygons = new Vector<Polygon>();

	private int[] mDistances;

	private Sum[] mSums;

	private HashMap<String, Boolean> mSettings;

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
			startPrevEdge = (i - 1) > 0 ? mContour.get(i - 1) : mContour
					.get(size - 1);
			tempPrevPath = new StraigthPath(startPrevEdge);
			nextK = i + 1;

			do {

				currentEdge = mContour.get(++k % size);
				currentPrevEdge = mContour.get(++nextK % size);

			} while ((k - i) <= (size - 3) && tempPath.add(currentEdge)
					&& tempPrevPath.add(currentPrevEdge));

			// k - 1 is the last allowed index for the i
			distance[i] = k - 1;
		}
		return distance;
	}

	public void run() {
		// calculate the sums for the penalties
		mSums = calculateSum();

		// calculate valid segments
		int[] distances = calculateDistances();

		mDistances = distances;
		try {
			PrintWriter write = new PrintWriter("distance11.txt");

			for (int i = 0; i < distances.length; i++) {
				write.println(i + " = " + distances[i]);
			}
			write.flush();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Vector<Polygon> maxDistancePolygones = generateMaxDistancePolygons(distances);
		mPolygons.addAll(maxDistancePolygones);

	}

	/**
	 * Generate polygons based on the max distance of the straight paths
	 * 
	 * @param distances
	 * @return
	 */
	private Vector<Polygon> generateMaxDistancePolygons(int[] distances) {
		Vector<Polygon> polygones = new Vector<Polygon>();
		Polygon currentPolyone;

		int start, k, i;
		boolean run = true;

		Path p;
		for (int index = 0; index < distances.length; index++) {
			run = true;
			currentPolyone = new Polygon();

			start = index;
			i = start;
			k = distances[i];

			do {
				p = new Path(get(i % distances.length).from, get(k
						% distances.length).from);
				currentPolyone.add(p);
				i = k;
				k = distances[i % distances.length];

				// check condition
				if (!cycle(i, k, start, distances.length)) {
					run = false;
				}

			} while (run);
			if (i % distances.length != start) {
				p = new Path(get(i % distances.length).from, get(start).from);
				currentPolyone.add(p);
			}

			// add only shortest polygons
			if (currentPolyone.size() > 3
					&& (polygones.isEmpty() || polygones.get(0).size() >= currentPolyone
							.size())) {
				if (!polygones.isEmpty()
						&& polygones.get(0).size() > currentPolyone.size()) {
					polygones.clear();

				}
				polygones.add(currentPolyone);
			}
			// polygones.add(currentPolygon);
		}
		return polygones;
	}

	private boolean cycle(int i, int k, int start, int distances) {
		return !((i % distances) == start
				|| (i - start) >= distances - 1
				|| (i > (k % distances) && i > start && (k % distances) > start) || (i < start && k > start));
	}

	private double penalty(Path p1) {

		return 0d;
	}

	private Sum[] calculateSum() {
		int i, x, y;
		int n = mContour.size();

		Sum[] sums = new Sum[mContour.size() + 1];

		for (i = sums.length - 1; i >= 0; i--) {
			sums[i] = new Sum();
		}

		// origin
		int x0 = mContour.get(0).from.x;
		int y0 = mContour.get(0).from.y;

		// initialize to 0
		sums[0].x2 = sums[0].y2 = sums[0].xy = sums[0].x = sums[0].y = 0d;

		for (i = 0; i < n; i++) {
			x = mContour.get(i).from.x - x0;
			y = mContour.get(i).from.y - y0;
			sums[i + 1].x = sums[i].x + x;
			sums[i + 1].y = sums[i].y + y;
			sums[i + 1].x2 = sums[i].x2 + x * x;
			sums[i + 1].xy = sums[i].xy + x * y;
			sums[i + 1].y2 = sums[i].y2 + y * y;
		}

		return sums;
	}

	public void setSettings(HashMap<String, Boolean> settings) {
		this.mSettings = settings;
	}
}
