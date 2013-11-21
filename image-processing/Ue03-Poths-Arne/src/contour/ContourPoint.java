package contour;

/**
 * 
 * @author Arne Poths
 * 
 */
public class ContourPoint {

	/**
	 * the x position of the contour point
	 */
	public int x;

	/**
	 * the y position of the contour point
	 */
	public int y;

	public ContourPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}