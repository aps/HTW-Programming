package contour;

public class ContourLine {

	/**
	 * the start point of the contour line
	 */
	ContourPoint from;

	/**
	 * the end point of the contour line
	 */
	ContourPoint to;

	public ContourLine(ContourPoint from, ContourPoint to) {
		this.from = from;
		this.to = to;
	}

}
