package contour;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Contour {

	public static final int TYPE_OUTER = 0;
	public static final int TYPE_INNER = 1;

	private LinkedList<ContourLine> contour;
	private LinkedList<Coordinates> coorinates;

	int type = -1;

	/**
	 * 
	 * @param contourType
	 *            defines if the contour is a inner or outer one
	 */
	public Contour(int contourType) {
		contour = new LinkedList<ContourLine>();
		coorinates = new LinkedList<Contour.Coordinates>();

		if (contourType != TYPE_INNER && contourType != TYPE_OUTER) {
			throw new UnknownError("this contour type is not supported.");
		}

		this.type = contourType;
	}

	public void addLine(ContourLine line) {
		contour.add(line);
	}

	public void addLines(LinkedList<ContourLine> lines) {
		contour.addAll(lines);
	}

	public void addCoorodinates(int u, int v) {
		coorinates.add(new Coordinates(u, v));
	}

	public void drawContour(Graphics g, double zoom) {
		if (type == TYPE_INNER) {
			g.setColor(Color.orange);
		} else {
			g.setColor(Color.red);
		}

		for (ContourLine line : contour) {
			g.drawLine((int) (line.from.x * zoom), (int) (line.from.y * zoom),
					(int) (line.to.x * zoom), (int) (line.to.y * zoom));
		}
	}

	public void printCoordinates(int height) {

		for (Coordinates coord : coorinates) {
			System.out.print(" " + (coord.mU + (coord.mV * height)));
		}

	}

	private class Coordinates {

		int mU, mV;

		public Coordinates(int u, int v) {
			mU = u;
			mV = v;
		}

		@Override
		public String toString() {
			return "U: " + mU + " V: " + mV;
		}

	}

}
