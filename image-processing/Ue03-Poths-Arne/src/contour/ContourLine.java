package contour;

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

}
