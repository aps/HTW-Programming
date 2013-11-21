package potrace;

import java.util.LinkedList;

import contour.ContourLine;
import contour.ContourPoint;

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

	public void printCoordinates() {

		System.out.println("Coords");
		for (ContourLine line : contour) {
			System.out.print(" " + line.from);
		}
		System.out.println("");
		System.out.println("----");

	}

}
