package potrace;

public class Edge implements Comparable<Edge> {

	/**
	 * 0x01
	 */
	public static final int DIRECTION_RIGHT = 0x01;

	/**
	 * 0x02
	 */
	public static final int DIRECTION_LEFT = 0x02;

	/**
	 * 0x04
	 */
	public static final int DIRECTION_DOWN = 0x04;

	/**
	 * 0x08
	 */
	public static final byte DIRECTION_UP = 0x08;

	/**
	 * the start point of the contour line
	 */
	public Vertex from;

	/**
	 * the end point of the contour line
	 */
	public Vertex to;

	public Edge(Vertex from, Vertex to) {
		this.from = from;
		this.to = to;
	}

	public Edge(Edge other) {
		this.from = new Vertex(other.from);
		this.to = new Vertex(other.to);
	}

	/**
	 * Get the direction of the edge. It will be a bit. <br />
	 * <br />
	 * Possible states are
	 * <ul>
	 * <li>{@value #DIRECTION_DOWN}</li>
	 * <li>{@value #DIRECTION_UP}</li>
	 * <li>{@value #DIRECTION_RIGHT}</li>
	 * <li>{@value #DIRECTION_LEFT}</li>
	 * </ul>
	 * 
	 * @return
	 */
	public byte getDirection() {
		int x = to.x - from.x;
		if (x != 0) {
			if (x > 0) {
				return DIRECTION_RIGHT;
			} else {
				return DIRECTION_LEFT;
			}
		} else {
			int y = to.y - from.y;
			if (y > 0) {
				return DIRECTION_DOWN;
			} else {
				return DIRECTION_UP;
			}
		}
	}

	@Override
	public int compareTo(Edge o) {
		if (o.from == this.from && o.to == this.to) {
			return 0;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "E[" + from + "Ð " + to + "] ";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge line = (Edge) obj;
			return (line.from.equals(this.from) && line.to.equals(this.to));
		}

		return super.equals(obj);
	}
}
