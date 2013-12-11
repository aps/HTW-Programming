package potrace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * 
 * 
 * @author Arne Poths
 * 
 */
public class Path {

	protected Vertex from;
	protected Vertex to;

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

	public Vertex getMid() {
		Vertex mid = new Vertex(0, 0);

		return mid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Path) {
			Path other = (Path) obj;
			return from.equals(other.from) && to.equals(other.to);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return new StringBuilder("Path [").append(from).append(to).append("]")
				.toString();
	}

	public void drawStart(Graphics2D g2, float zoom) {
//		g2.drawString("" + from, Math.round((from.x) * zoom),
//				Math.round((from.y) * zoom));
		g2.setColor(Color.CYAN);
		g2.drawRect(Math.round((from.x) * zoom), Math.round((from.y) * zoom),
				Math.round(zoom), Math.round(zoom));
	}
}
