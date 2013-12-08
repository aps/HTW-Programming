package potrace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Path {

	Vertex from;
	Vertex to;

	public Path(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
	}

	public void draw(Graphics g, float zoom) {
		g.setColor(Color.BLUE);

		Graphics2D g2 = (Graphics2D) g;

		if (zoom > 10) {
			g2.setStroke(new BasicStroke(2));
		}

		g2.drawLine(Math.round(from.x * zoom), Math.round(from.y * zoom),
				Math.round(to.x * zoom), Math.round(to.y * zoom));
	}

	@Override
	public String toString() {
		return new StringBuilder("Path [").append(from).append(to).append("]")
				.toString();
	}
}
