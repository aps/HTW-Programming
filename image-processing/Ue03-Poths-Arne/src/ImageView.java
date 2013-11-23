// Copyright (C) 2008 by Klaus Jung

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.text.ZoneView;

import potrace.PotraceContour;

import contour.Contour;

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
			borderX = maxSize.width - bi.getWidth();
		if (borderY < 0)
			borderY = maxSize.height - bi.getHeight();

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
	}

	public double getZoom() {
		return mZoom;
	}

	public Graphics getScrennGraphics() {
		return screen.getGraphics();
	}

	public void addContour(PotraceContour c) {
		screen.addContour(c);
		screen.revalidate();
	}

	public void addAllContour(Collection<PotraceContour> c) {
		screen.addAllContour(c);
		screen.revalidate();
	}

	class ImageScreen extends JComponent {

		private static final long serialVersionUID = 1L;

		private BufferedImage image;

		LinkedList<PotraceContour> contours = new LinkedList<PotraceContour>();

		public ImageScreen(BufferedImage bi) {
			super();
			image = bi;
		}

		public void addContour(PotraceContour c) {
			contours.add(c);
		}

		public void addAllContour(Collection<PotraceContour> all) {
			contours.addAll(all);
		}

		public void paintComponent(Graphics g) {
			Rectangle r = this.getBounds();
			if (image != null)
				g.drawImage(image, 0, 0, r.width, r.height, this);

			if (!contours.isEmpty()) {
				for (PotraceContour c : contours) {
					c.drawContour(g, mZoom);
				}
			}

		}

		public Dimension getPreferredSize() {
			if (image != null) {
				return new Dimension((int) (mZoom * image.getWidth()),
						(int) (mZoom * image.getHeight()));
			} else
				return new Dimension(800, 600);
		}
	}

}
