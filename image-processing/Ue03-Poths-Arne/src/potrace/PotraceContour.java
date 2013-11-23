package potrace;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Vector;

public class PotraceContour {

	LinkedList<ContourLine> contour;

	private int type;

	public static final int TYPE_INNER = 1;
	public static final int TYPE_OUTER = 0;

	public void setType(int type) {
		this.type = type;
	}

	public PotraceContour() {
		contour = new LinkedList<ContourLine>();
	}

	public void addLine(ContourPoint from, ContourPoint to) {
		contour.addLast(new ContourLine(from, to));
	}

	public void addLine(ContourLine line) {
		contour.addLast(line);
	}

	public int size() {
		return contour.size();
	}

	public void addLines(LinkedList<ContourLine> lines) {
		contour.addAll(lines);
	}

	public void addLines(Vector<ContourLine> lines) {
		contour.addAll(lines);
	}

	public boolean contains(ContourLine obj) {
		return contour.contains(obj);
	}

	public void printCoordinates() {
		System.out.println("contour: ");
		for (ContourLine line : contour) {
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

		for (ContourLine line : contour) {
			g.drawLine(Math.round(line.from.x * zoom),
					Math.round(line.from.y * zoom),
					Math.round(line.to.x * zoom), Math.round(line.to.y * zoom));
		}
	}

}
