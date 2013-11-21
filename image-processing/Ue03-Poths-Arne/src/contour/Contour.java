package contour;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;

public class Contour {

	public static final int TYPE_OUTER = 0;
	public static final int TYPE_INNER = 1;

	private LinkedList<ContourLine> contour;
	private LinkedList<Coordinates> coordinates;

	int type = -1;

	/**
	 * 
	 * @param contourType
	 *            defines if the contour is a inner or outer one
	 */
	public Contour(int contourType) {
		contour = new LinkedList<ContourLine>();
		coordinates = new LinkedList<Contour.Coordinates>();

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
		coordinates.add(new Coordinates(u, v));
	}

	public void drawContour(Graphics g, double zoom) {
		if (type == TYPE_INNER) {
			g.setColor(Color.ORANGE);
		} else {
			g.setColor(Color.RED);
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		for (ContourLine line : contour) {
			g2.drawLine((int) (line.from.x * zoom), (int) (line.from.y * zoom),
					(int) (line.to.x * zoom), (int) (line.to.y * zoom));
		}
	}

	public void printCoordinates(int height) {
		for (Coordinates coord : coordinates) {
			System.out.print(" " + (coord.mU + (coord.mV * height)));
		}
	}

	public void printCoordinates() {
		for (Coordinates coord : coordinates) {
			System.out.print(" (" + (coord.mU + ", " + coord.mV + ")"));
		}
	}

	public void calculatePaths() {

		for (Coordinates coord : coordinates) {
			// is coordinate valid?
			contour.addLast(calculateLine(coord));

		}
	}

	private ContourLine calculateLine(Coordinates coord) {
		if (type == TYPE_INNER) {
			return getInnerLine(coord);
		}
		return getOuterLine(coord);
	}

	private ContourLine getInnerLine(Coordinates coord) {
		return null;
	}

	private ContourLine getOuterLine(Coordinates coord) {
		return null;
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
