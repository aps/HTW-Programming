package potrace;

/**
 * 
 * @author Arne Poths
 * 
 */
public class Vertex {

	/**
	 * the x position of the contour point
	 */
	public int x;

	/**
	 * the y position of the contour point
	 */
	public int y;

	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vertex(Vertex other) {
		this.x = other.x;
		this.y = other.y;
	}

	@Override
	public String toString() {
		return "V(" + x + ", " + y + ") ";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			Vertex point = (Vertex) obj;
			if (point.x == x && point.y == y) {
				return true;
			} else {
				return false;
			}
		}

		return super.equals(obj);
	}
}