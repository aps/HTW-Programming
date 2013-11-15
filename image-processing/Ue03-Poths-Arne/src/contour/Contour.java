package contour;
import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

public class Contour {

	public static final int TYPE_INNER = 1;
	public static final int TYPE_OUTER = 2;

	private LinkedList<ContourLine> contour;

	int type = -1;

	/**
	 * 
	 * @param contourType
	 *            defines if the contour is a inner or outer one
	 */
	public Contour(int contourType) {
		contour = new LinkedList<ContourLine>();

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


}
