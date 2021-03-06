// Copyright (C) 2008 by Klaus Jung

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import potrace.Potrace;

/**
 * Edited version
 * 
 * @author Arne Poths
 * 
 */
public class ImageView extends JScrollPane {

	private static final long serialVersionUID = 1L;

	ImageScreen screen;
	Dimension maxSize;
	int borderX = -1;
	int borderY = -1;
	float mZoom = 2.0f;

	int pixels[] = null; // pixel array in ARGB format

	private boolean mDrawPixels;

	private boolean mDrawNeighbours;

	private boolean mDrawImage = false;

	private boolean mDrawPolygon = false;

	private boolean mDrawBezier = true;

	private boolean mDrawBezierCurve = false;

	public ImageView(int width, int height) {
		// construct empty image of given size
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		init(bi, true);
	}

	public ImageView(File file) {
		// construct image from file
		loadImage(file);
	}

	public void setMaxSize(Dimension dim) {
		// limit the size of the image view
		maxSize = new Dimension(dim);

		Dimension size = new Dimension(maxSize);
		if (size.width - borderX > screen.image.getWidth())
			size.width = screen.image.getWidth() + borderX;
		if (size.height - borderY > screen.image.getHeight())
			size.height = screen.image.getHeight() + borderY;
		setPreferredSize(size);
	}

	public int getImgWidth() {
		return screen.image.getWidth();
	}

	public int getImgHeight() {
		return screen.image.getHeight();
	}

	public int[] getPixels() {
		// get reference to internal pixels array
		if (pixels == null) {
			pixels = new int[getImgWidth() * getImgHeight()];
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0,
					getImgWidth());
		}
		return pixels;
	}

	public void applyChanges() {
		// if the pixels array obtained by getPixels() has been modified,
		// call this method to make your changes visible
		if (pixels != null)
			setPixels(pixels);
	}

	public void setPixels(int[] pix) {
		// set pixels with same dimension
		setPixels(pix, getImgWidth(), getImgHeight());
	}

	public void setPixels(int[] pix, int width, int height) {
		// set pixels with arbitrary dimension
		if (pix == null || pix.length != width * height)
			throw new IndexOutOfBoundsException();

		if (width != getImgWidth() || height != getImgHeight()) {
			// image dimension changed
			screen.image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			pixels = null;
		}

		screen.image.setRGB(0, 0, width, height, pix, 0, width);

		if (pixels != null && pix != pixels) {
			// update internal pixels array
			System.arraycopy(pix, 0, pixels, 0,
					Math.min(pix.length, pixels.length));
		}

		Dimension size = new Dimension(maxSize);
		if (size.width - borderX > width)
			size.width = width + borderX;
		if (size.height - borderY > height)
			size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}

	public void printText(int x, int y, String text) {
		Graphics2D g = screen.image.createGraphics();

		Font font = new Font("TimesRoman", Font.BOLD, 12);
		g.setFont(font);
		g.setPaint(Color.black);
		g.drawString(text, x, y);
		g.dispose();

		updatePixels(); // update the internal pixels array
	}

	public void clearImage() {
		Graphics2D g = screen.image.createGraphics();

		g.setColor(Color.white);
		g.fillRect(0, 0, getImgWidth(), getImgHeight());
		g.dispose();

		updatePixels(); // update the internal pixels array
	}

	public void loadImage(File file) {
		// load image from file
		BufferedImage bi = null;
		boolean success = false;

		try {
			bi = ImageIO.read(file);
			success = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Bild konnte nicht geladen werden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);
			bi = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
		}

		// MediaTracker tracker = new MediaTracker(this);
		// tracker.addImage(srcImage, 0);
		// try {
		// tracker.waitForID(0);
		// }
		// catch (InterruptedException e) {}

		init(bi, !success);

		if (!success)
			printText(5, getImgHeight() / 2,
					"Bild konnte nicht geladen werden.");
	}

	public void saveImage(String fileName) {
		try {
			File file = new File(fileName);
			String ext = (fileName.lastIndexOf(".") == -1) ? ""
					: fileName.substring(fileName.lastIndexOf(".") + 1,
							fileName.length());
			if (!ImageIO.write(screen.image, ext, file))
				throw new Exception("Image save failed");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
					"Bild konnte nicht geschrieben werden.", "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void init(BufferedImage bi, boolean clear) {
		screen = new ImageScreen(bi);
		setViewportView(screen);

		maxSize = new Dimension(getPreferredSize());

		if (borderX < 0)
			borderX = Math.round((maxSize.width - bi.getWidth()) * mZoom);
		if (borderY < 0)
			borderY = Math.round((maxSize.height - bi.getHeight()) * mZoom);

		if (clear)
			clearImage();
		pixels = null;
	}

	private void updatePixels() {
		if (pixels != null)
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0,
					getImgWidth());
	}

	public void setZoom(float zoom) {
		mZoom = zoom;
		screen.revalidate();
		screen.repaint();
	}

	public float getZoom() {
		return mZoom;
	}

	public Graphics getScrennGraphics() {
		return screen.getGraphics();
	}

	public void setPotrace(Potrace potrace) {
		screen.setPotrace(potrace);
		this.revalidate();
	}

	class ImageScreen extends JComponent {

		private static final long serialVersionUID = 1L;

		private BufferedImage image;

		private Potrace potrace;

		public ImageScreen(BufferedImage bi) {
			super();
			image = bi;
		}

		public void setPotrace(Potrace potrace) {
			this.potrace = potrace;
		}

		@Override
		public void paintComponent(Graphics g) {
			if (image != null && mDrawImage)
				g.drawImage(image, 0, 0, (int) (image.getWidth() * mZoom),
						(int) (image.getHeight() * mZoom), this);

			if (mDrawPixels && mZoom > 5.0f) {
				drawPixels(g, mZoom, image.getWidth(), image.getHeight());
			}
			if (mDrawNeighbours) {
				drawNeighbours(g, mZoom, image.getWidth(), image.getHeight());
			}
			if (potrace != null) {
				HashMap<String, Boolean> draw = new LinkedHashMap<String, Boolean>();
				draw.put("image", mDrawImage);
				draw.put("polygon", mDrawPolygon);
				draw.put("bezier", mDrawBezier);
				draw.put("bezierCurve", mDrawBezierCurve);
				potrace.setSettings(draw);

				potrace.draw(g, mZoom);
			}

		}

		private void drawPixels(Graphics g, float zoom, int width, int height) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(Color.LIGHT_GRAY);

			int posX;
			int posY;

			for (int w = 1; w < width; w++) {
				posX = Math.round(zoom * w);
				g2.setColor(Color.DARK_GRAY);
				g2.drawLine(posX, 0, posX, Math.round(zoom * (height)));
			}

			for (int h = 1; h < height; h++) {
				g2.setColor(Color.DARK_GRAY);
				posY = Math.round(zoom * h);
				g2.drawLine(0, posY, Math.round(zoom * (width - 1)), posY);
			}
		}

		private void drawNeighbours(Graphics g, float zoom, int width,
				int height) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(Color.LIGHT_GRAY);

			int posX;
			int posY;

			for (int w = 1; w < width; w++) {
				posX = Math.round(zoom * w);
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawLine(posX - Math.round(zoom / 2), 0,
						posX - Math.round(zoom / 2),
						Math.round(zoom * (height)));
			}

			for (int h = 1; h < height; h++) {
				posY = Math.round(zoom * h);
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawLine(0, posY - Math.round(zoom / 2),
						Math.round(zoom * (width - 1)),
						posY - Math.round(zoom / 2));

			}
		}

		@Override
		public Dimension getPreferredSize() {
			if (image != null) {
				return new Dimension((int) (mZoom * image.getWidth()),
						(int) (mZoom * image.getHeight()));
			} else
				return new Dimension(800, 600);
		}
	}

	public void setDrawPixels(boolean selected) {
		mDrawPixels = selected;
		screen.revalidate();
		this.repaint();
	}

	public void setDrawNeighbours(boolean selected) {
		mDrawNeighbours = selected;
		screen.revalidate();
		this.repaint();
	}

	public void setDrawImage(boolean selected) {
		mDrawImage = selected;
		screen.revalidate();
		this.repaint();
	}

	public void setDrawPolygon(boolean selected) {
		mDrawPolygon = selected;
		screen.revalidate();
		this.repaint();
	}

	public void setDrawBezier(boolean selected) {
		mDrawBezier = selected;
		screen.revalidate();
		this.repaint();
	}

	public void setMinimumAlpha(float f) {
		screen.potrace.setMinimumAlpha(f);
		this.repaint();
	}

	public void setMaximumAlpha(float f) {
		screen.potrace.setMinimumAlpha(f);
		this.repaint();
	}

	public void setMagicFactor(float f) {
		screen.potrace.setMagicFactor(f);
		this.repaint();
	}

	public void setDrawBezierCurve(boolean selected) {
		mDrawBezierCurve = selected;
		screen.revalidate();
		this.repaint();
	}

}
