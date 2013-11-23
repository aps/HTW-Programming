package potrace;

public class ContourLine implements Comparable<ContourLine> {

	/**
	 * the start point of the contour line
	 */
	public ContourPoint from;

	/**
	 * the end point of the contour line
	 */
	public ContourPoint to;

	public ContourLine(ContourPoint from, ContourPoint to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int compareTo(ContourLine o) {
		if (o.from == this.from && o.to == this.to) {
			return 0;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "from[" + from + "] Ð to[" + to + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContourLine) {
			ContourLine line = (ContourLine) obj;
			return (line.from.equals(this.from) && line.to.equals(this.to));
		}

		return super.equals(obj);
	}
}
