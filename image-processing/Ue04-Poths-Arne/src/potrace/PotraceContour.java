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
			for (Path p : mPath) {
				p.draw(g, zoom);
			}
		}

	}

	Vector<Path> mPath = new Vector<Path>();

	public void findStraightPaths() {
		int pos = 1;

		Edge start = get(0);
		Path p;
		do {
			p = findMaxPath(start, pos);
			mPath.add(p);

			start = getByStart(mPath.get(mPath.size() - 1).getLast());

			pos = getNewStartPosition();
		} while (mPath.size() < 2
				|| !mPath.get(mPath.size() - 1).contains(mPath.get(0).get(0)));

	}

	private Path findMaxPath(Edge start, int pos) {
		D.ln("findMAx: " + start + " pos= " + pos);
		Path p = new Path(start);
		while (pos < size() && p.add(get(pos))) {
			pos++;

		}
		return p;
	}

}
