package potrace;

import java.util.Vector;

public class Potrace {

	final boolean DEBUG = false;

	public static final int FROM_LEFT = 0;
	public static final int FROM_TOP = 1;
	public static final int FROM_RIGHT = 2;
	public static final int FROM_BOTTOM = 3;

	int[] pixels;
	int width;
	int height;

	Vector<PotraceContour> mContours = new Vector<PotraceContour>(2, 2);

	public Potrace(int[] pixels, int width, int height) {
		this.pixels = pixels.clone();
		this.width = width;
		this.height = height;
	}

	public void go() {
		int startPosition;
		ContourPoint startPoint;
		ContourLine start;
		Vector<ContourLine> lines;
		ContourLine currentLine;
		PotraceContour contour;

		int[] currentPixels = pixels.clone();

		do {
			// find start
			startPosition = findStart(currentPixels);
			if (startPosition != -1) {

				startPoint = new ContourPoint(getU(startPosition, width), getV(
						startPosition, width));
				start = new ContourLine(startPoint, new ContourPoint(
						startPoint.x, startPoint.y + 1));

				lines = new Vector<ContourLine>();
				currentLine = start;

				contour = new PotraceContour();
				if (pixels[startPosition] == forground) {
					contour.setType(PotraceContour.TYPE_OUTER);
				} else {
					contour.setType(PotraceContour.TYPE_INNER);
				}

				do {
					lines.add(currentLine);
					currentLine = getNextLine(currentLine, currentPixels,
							width, height);

				} while (!currentLine.equals(lines.get(0)));
				contour.addLines(lines);
				mContours.add(contour);
				currentPixels = invertPixels(currentPixels, lines, width);
			}
		} while (startPosition != -1);

	}

	void printPixels(int[] pixels) {
		int color = 0;
		String s = "";
		for (int i = 0; i < pixels.length; i++) {
			color = pixels[i] == forground ? 1 : 0;
			print("," + color);
			s += "," + i;
			if ((i + 1) % width == 0) {
				println("");
				s += "\n";
			}
		}
		println(s);
	}

	private ContourLine getNextLine(ContourLine currentLine, int[] image,
			int w, int h) {
		ContourLine newLine;

		switch (getDirection(currentLine)) {
		case FROM_TOP:
			newLine = getNextLineFromTop(currentLine, image, w, h);
			break;
		case FROM_LEFT:
			newLine = getNextLineFromLeft(currentLine, image, w, h);
			break;
		case FROM_RIGHT:
			newLine = getNextLineFromRight(currentLine, image, w, h);
			break;
		case FROM_BOTTOM:
			newLine = getNextLineFromBottom(currentLine, image, w, h);
			break;

		default:
			newLine = currentLine;
			break;
		}

		println(newLine.toString());
		return newLine;
	}

	private ContourLine getNextLineFromBottom(ContourLine currentLine,
			int[] image, int w, int h) {

		int nextX = -1, nextY = -1;

		if (isForground(currentLine.to.x, currentLine.to.y - 1, image, w, h)) {
			// right
			nextX = currentLine.to.x + 1;
			nextY = currentLine.to.y;
		} else if (isForground(currentLine.to.x - 1, currentLine.to.y - 1,
				image, w, h)) {
			// left
			nextX = currentLine.to.x;
			nextY = currentLine.to.y - 1;
		} else {
			// none
			nextX = currentLine.to.x - 1;
			nextY = currentLine.to.y;
		}

		return new ContourLine(new ContourPoint(currentLine.to.x,
				currentLine.to.y), new ContourPoint(nextX, nextY));
	}

	private ContourLine getNextLineFromRight(ContourLine currentLine,
			int[] image, int w, int h) {
		int nextX = -1, nextY = -1;

		if (isForground(currentLine.to.x - 1, currentLine.to.y - 1, image, w, h)) {
			// right
			nextX = currentLine.to.x;
			nextY = currentLine.to.y - 1;
		} else if (isForground(currentLine.to.x - 1, currentLine.to.y, image,
				w, h)) {
			// left
			nextX = currentLine.to.x - 1;
			nextY = currentLine.to.y;
		} else {
			// none
			nextX = currentLine.to.x;
			nextY = currentLine.to.y + 1;
		}

		return new ContourLine(new ContourPoint(currentLine.to.x,
				currentLine.to.y), new ContourPoint(nextX, nextY));
	}

	private ContourLine getNextLineFromLeft(ContourLine currentLine,
			int[] image, int w, int h) {
		int nextX = -1, nextY = -1;

		if (isForground(currentLine.to.x, currentLine.to.y, image, w, h)) {
			// right
			nextX = currentLine.to.x;
			nextY = currentLine.to.y + 1;
		} else if (isForground(currentLine.to.x, currentLine.to.y - 1, image,
				w, h)) {
			// left
			nextX = currentLine.to.x + 1;
			nextY = currentLine.to.y;
		} else {
			// none
			nextX = currentLine.to.x;
			nextY = currentLine.to.y - 1;
		}
		return new ContourLine(new ContourPoint(currentLine.to.x,
				currentLine.to.y), new ContourPoint(nextX, nextY));
	}

	private ContourLine getNextLineFromTop(ContourLine currentLine,
			int[] image, int w, int h) {
		int nextX = -1, nextY = -1;

		if (isForground(currentLine.to.x - 1, currentLine.to.y, image, w, h)) {
			// right
			nextX = currentLine.to.x - 1;
			nextY = currentLine.to.y;
		} else if (isForground(currentLine.to.x, currentLine.to.y, image, w, h)) {
			// left
			nextX = currentLine.to.x;
			nextY = currentLine.to.y + 1;
		} else {
			// none
			nextX = currentLine.to.x + 1;
			nextY = currentLine.to.y;
		}
		return new ContourLine(new ContourPoint(currentLine.to.x,
				currentLine.to.y), new ContourPoint(nextX, nextY));
	}

	/**
	 * 
	 * @param currentLine
	 * @return
	 */
	private int getDirection(ContourLine currentLine) {
		int xDiff = currentLine.from.x - currentLine.to.x;
		int yDiff = currentLine.from.y - currentLine.to.y;

		if (xDiff == -1 && yDiff == 0) {
			return FROM_LEFT;
		} else if (xDiff == 0 && yDiff == -1) {
			return FROM_TOP;
		} else if (xDiff == 1 && yDiff == 0) {
			return FROM_RIGHT;
		} else if (xDiff == 0 && yDiff == 1) {
			return FROM_BOTTOM;
		} else {
			return -1;
		}
	}

	private boolean isForground(int x, int y, int[] pixels, int width,
			int height) {

		if (x > -1 && x < width && y > -1 && y < height) {
			println("isForground: x=" + x + ",y=" + y + " pixels["
					+ I(x, y, width) + "]="
					+ (pixels[I(x, y, width)] == forground ? "black" : "white"));
		}

		if (x > -1 && x < width && y > -1 && y < height
				&& pixels[I(x, y, width)] == forground) {
			return true;
		}

		return false;
	}

	private int[] invertPixels(int[] currentPixels, Vector<ContourLine> lines,
			int width) {

		int direction;
		int x, y;

		for (ContourLine line : lines) {
			direction = getDirection(line);
			if (direction == FROM_BOTTOM) {
				x = line.to.x;
				y = line.to.y;
				currentPixels = invert(x, y, currentPixels, width);
			} else if (direction == FROM_TOP) {
				x = line.from.x;
				y = line.from.y;
				currentPixels = invert(x, y, currentPixels, width);
			}
		}
		return currentPixels;
	}

	private int[] invert(int x, int y, int[] currentPixels, int w) {
		for (int u = x; u < w; u++) {
			print(" " + I(u, y, w));
			currentPixels[I(u, y, w)] = currentPixels[I(u, y, w)] == background ? forground
					: background;
		}
		println("");
		return currentPixels;
	}

	int forground = 0xff000000;
	int background = 0xffffffff;

	public int findStart(int[] currentPixels) {
		int start = -1;

		int counter = 0;
		int prev = background;
		while (start == -1 && counter < currentPixels.length) {
			if (prev == background && currentPixels[counter] == forground) {
				start = counter;
			}
			prev = currentPixels[counter];
			counter++;
		}

		return start;
	}

	public PotraceContour get(int pos) {
		return mContours.get(pos);
	}

	public Vector<PotraceContour> getAll() {
		return mContours;
	}

	private int I(int u, int v, int width) {
		return u + (v * width);
	}

	private int getU(int position, int width) {
		return position % width;
	}

	private int getV(int position, int width) {
		return position / width;
	}

	private void println(String s) {
		if (DEBUG)
			System.out.println(s);
	}

	private void print(String s) {
		if (DEBUG)
			System.out.print(s);
	}

}
