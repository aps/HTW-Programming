package potrace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class Bezier {

	private float magicFactor = 4.0f / 3.0f;
	private float maximumAlpha = 1.0f;
	private float minimumAlpha = 0.55f;
	private Polygon mPolygon;

	private Vector<CenterPoint> mCenterPoint = new Vector<CenterPoint>(10, 5);
	private Vector<Float> mAlphas = new Vector<Float>(10, 5);

	public Bezier() {
	}

	public Bezier(Polygon polygon) {
		mPolygon = polygon;
	}

	public void run() {
		calculateCenterPoints();
		calculateAlphas();
	}

	public void setMagicFactor(float factor) {
		magicFactor = factor;
	}

	private void calculateAlphas() {
		Vertex currentCorner;
		CenterPoint start, end;
		int size = mCenterPoint.size();
		float cornerAlpha = 0f, d;

		for (int i = 0; i < size; i++) {
			currentCorner = mPolygon.get(i).from;
			start = mCenterPoint.get((size + i - 1) % size);
			end = mCenterPoint.get(i);

			d = calculateDistance(currentCorner, start, end);

			cornerAlpha = magicFactor * ((d - 0.5f) / d);

			if (cornerAlpha < minimumAlpha) {
				cornerAlpha = minimumAlpha;
			}

			mAlphas.add(cornerAlpha);
		}
	}

	private void calculateCenterPoints() {
		CenterPoint center;
		for (int i = 0; i < mPolygon.size(); i++) {
			center = calculateCenter(mPolygon.get(i));
			mCenterPoint.add(center);
		}
	}

	private CenterPoint calculateCenter(Path path) {
		float x = path.to.x - path.from.x;
		float y = path.to.y - path.from.y;

		CenterPoint center = new CenterPoint(path.from.x + x / 2.0f,
				path.from.y + y / 2.0f);

		D.ln("Center of: " + path + " is : " + center);

		return center;
	}

	private float calculateDistance(Vertex v, CenterPoint a, CenterPoint b) {
		float d = 0.0f;
		CenterPoint _s = new CenterPoint(b.y - a.y, -(b.x - a.x));
		float sum = sum(_s.x, _s.y);
		CenterPoint s = new CenterPoint(_s.x / sum, _s.y / sum);
		d = scalarProduct(s, new CenterPoint(v.x - a.x, v.y - a.y));
		return (float) Math.sqrt(d * d);
	}

	private float scalarProduct(CenterPoint a, CenterPoint b) {
		float scalar = 0f;

		scalar = a.x * b.x + a.y + b.y;
		scalar /= (sum(a.x, a.y) + sum(b.x, b.y));

		return scalar;
	}

	private float sum(float a, float b) {
		return (float) Math.sqrt(a * a + b * b);
	}

	private class CenterPoint {

		private float x;
		private float y;

		public CenterPoint(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "Center(" + x + ", " + y + ")";
		}
	}

	public void draw(Graphics graphics, float zoom) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.ORANGE);
		Path2D path = new Path2D.Float();
		float alpha;
		CenterPoint start, end;
		Vertex a;
		String pathString = "";

		path.moveTo(mCenterPoint.get(mCenterPoint.size() - 1).x * zoom,
				mCenterPoint.get(mCenterPoint.size() - 1).y * zoom);

		pathString += "M " + mCenterPoint.get(mCenterPoint.size() - 1).x + " "
				+ mCenterPoint.get(mCenterPoint.size() - 1).y + "\n";

		float x1, x2, y1, y2;

		int size = mCenterPoint.size();

		for (int i = 0; i < mAlphas.size(); i++) {
			alpha = mAlphas.get(i);
			start = mCenterPoint.get((mCenterPoint.size() + i - 1)
					% mCenterPoint.size());
			end = mCenterPoint.get(i);
			a = mPolygon.get(i).from;

			if (alpha > maximumAlpha) {

				path.lineTo(a.x * zoom, a.y * zoom);
				path.lineTo(end.x * zoom, end.y * zoom);

				pathString += "L " + a.x + " " + a.y + "\n";
				pathString += "L " + end.x + " " + end.y + "\n";

			} else {

				x1 = start.x + alpha * (a.x - start.x);
				y1 = start.y + alpha * (a.y - start.y);

				x2 = a.x + alpha * (end.x - a.x);
				y2 = a.y + alpha * (end.y - a.y);

				path.curveTo(x1 * zoom, y1 * zoom, x2 * zoom, y2 * zoom, end.x
						* zoom, end.y * zoom);

				pathString += "C " + x1 + " " + y1 + " " + x2 + " " + y2 + " "
						+ end.x + " " + end.y + "\n";

			}
		}
		// writeSVG(pathString);
		g.draw(path);
	}

	private void writeSVG(String path) {

		try {

			PrintWriter write = new PrintWriter("imageAs-" + Math.random()
					+ ".svg");
			write.append("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" version=\"1.1\" baseProfile=\"full\" width=\"600\" height=\"400\"> \n ");
			write.append("<path fill=\"black\" d=\"");

			write.append(path);

			write.append("\" /> ");
			write.append("</svg>");
			write.flush();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
