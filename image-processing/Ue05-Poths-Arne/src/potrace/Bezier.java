package potrace;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
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

		CenterPoint center = new CenterPoint(path.from.x + x, path.from.y + y);

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
	}

	public void draw(Graphics graphics, float zoom) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(Color.ORANGE);
		Path2D.Float path = new Path2D.Float();
		float alpha;
		CenterPoint start, end;
		Vertex a;

		path.moveTo((mCenterPoint.get(mCenterPoint.size() - 1).x) * zoom,
				(mCenterPoint.get(mCenterPoint.size() - 1).y * 1f) * zoom);

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

			} else {

				x1 = start.x + alpha * (a.x - start.x);
				y1 = start.y + alpha * (a.y - start.y);

				x2 = a.x + alpha * (end.x - a.x);
				y2 = a.y + alpha * (end.y - a.y);

				path.curveTo(x1 * zoom, y1 * zoom, x2 * zoom, y2 * zoom, end.x
						* zoom, end.y * zoom);

			}
		}
		g.draw(path);
	}
}
