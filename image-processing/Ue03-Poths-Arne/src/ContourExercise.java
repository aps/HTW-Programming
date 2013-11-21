import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import potrace.PotraceContour;

import contour.Contour;
import contour.ContourLine;
import contour.ContourPoint;

public class ContourExercise extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 600;
	private static final int maxHeight = 600;
	private static final String title = "Konturen ";
	private static final String initalOpen = "test3.png";

	private static JFrame frame;

	private ImageView mImageView; // image view

	private JComboBox methodList; // select the flood filling method
	private JLabel statusLine; // to print some status text

	private LinkedList<Integer> stack = new LinkedList<Integer>();

	public ContourExercise() {
		super(new BorderLayout(border, border));

		// load the default image
		mLoadedOriginalFile = new File(initalOpen);

		if (!mLoadedOriginalFile.canRead())
			mLoadedOriginalFile = openFile(); // file not found, choose another
												// image

		mImageView = new ImageView(mLoadedOriginalFile);
		mImageView.setMinimumSize(new Dimension(maxWidth, maxHeight));
		mOriginalPixels = mImageView.getPixels().clone();
		// mImageView.setMaxSize(new Dimension(maxWidth, maxHeight));
		// mImageView.setMaxSize(new Dimension(mImageView.getImgWidth(),
		// mImageView.getImgHeight()));

		// load image button
		JButton load = new JButton("Bild šffnen");
		load.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mLoadedOriginalFile = openFile();
				if (mLoadedOriginalFile != null) {
					mImageView.loadImage(mLoadedOriginalFile);
					mImageView.setMaxSize(new Dimension(mImageView
							.getImgWidth(), mImageView.getImgWidth()));

					mOriginalPixels = mImageView.getPixels().clone();
					drawImage();
				}
			}
		});

		// selector for the binarization method
		JLabel methodText = new JLabel("Methode:");
		String[] methodNames = { "kontur" };

		methodList = new JComboBox(methodNames);
		methodList.setSelectedIndex(0); // set initial method
		methodList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawImage();
			}
		});

		// hide unused controlls
		methodText.setVisible(false);
		methodList.setVisible(false);

		// some status text
		statusLine = new JLabel(" ");

		// arrange all controls
		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, border, 0, 0);
		controls.add(load, c);
		controls.add(methodText, c);
		controls.add(methodList, c);

		JSlider zoomSlider = new JSlider(0, 1000, 100);
		zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setZoom(((JSlider) e.getSource()).getValue() / 100.0d);
			}
		});
		controls.add(zoomSlider);

		JPanel images = new JPanel(new FlowLayout());
		images.add(mImageView);

		add(controls, BorderLayout.NORTH);
		add(images, BorderLayout.CENTER);
		add(statusLine, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(border, border, border,
				border));

		// perform the initial flooding
		drawImage();
	}

	private File mLoadedOriginalFile;

	private File openFile() {
		JFileChooser chooser = new JFileChooser(new File("./"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
		chooser.setFileFilter(filter);
		int ret = chooser.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			frame.setTitle(title + chooser.getSelectedFile().getName());
			return chooser.getSelectedFile();
		}
		return null;
	}

	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame(title + initalOpen);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JComponent newContentPane = new ContourExercise();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// display the window.
		frame.pack();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		frame.setLocation((screenSize.width - frame.getWidth()) / 2,
				(screenSize.height - frame.getHeight()) / 2);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	int mOriginalPixels[];

	protected void drawImage() {
		if (mOriginalPixels != null) {
			mImageView.setPixels(mOriginalPixels.clone());
		}

		String methodName = (String) methodList.getSelectedItem();

		String message = "Kontur mit \"" + methodName + "\"";
		long startTime = System.currentTimeMillis();

		int width = mImageView.getImgWidth();
		int height = mImageView.getImgHeight();

		statusLine.setText(message);

		int pixels[] = mImageView.getPixels().clone();

		// int color = 0;
		// String s = "";
		// for (int i = 0; i < pixels.length; i++) {
		// color = pixels[i] == forground ? 1 : 0;
		// System.out.print("," + color);
		// s += "," + i;
		// if ((i + 1) % width == 0) {
		// printMe("");
		// s += "\n";
		// }
		// }
		// printMe(s);

		LinkedList<PotraceContour> c = potrace(pixels, width, height);

		LinkedHashSet<Contour> contours = combinedContourLabeling(pixels,
				width, height);

		if (contours != null && !contours.isEmpty()) {

			for (Iterator iterator = contours.iterator(); iterator.hasNext();) {
				Contour contour = (Contour) iterator.next();
				printMe("lalal");
				contour.printCoordinates();
				printMe("");
				mImageView.addContour(contour);
			}

		}

		mImageView.setPixels(pixels);
		long time = System.currentTimeMillis() - startTime;

		frame.pack();

		statusLine.setText(message + " in " + time + " ms");
	}

	private LinkedList<PotraceContour> potrace(int[] pixels, int width,
			int height) {
		int[] newPixels = pixels.clone();

		PotraceContour potrace = new PotraceContour();
		potrace.setType(PotraceContour.TYPE_OUTER);

		int start = 0;
		ContourPoint startPoint;
		ContourPoint secondPoint;

		while (pixels[start] != forground) {
			start++;
		}

		startPoint = new ContourPoint(getU(start, width), getV(start, width));
		secondPoint = new ContourPoint(startPoint.x, startPoint.y + 1);
		LinkedList<ContourLine> contour = new LinkedList<ContourLine>();
		contour.add(new ContourLine(startPoint, secondPoint));

		ContourLine currentLine = contour.get(0);

		printMe(currentLine.toString());

		while ((contour.size() < 4 || contour.get(0).equals(currentLine))) {
			currentLine = findNextLine(currentLine, pixels, width, height);
			contour.addLast(currentLine);
		}

		potrace.addLines(contour);

		potrace.printCoordinates();

		LinkedList<PotraceContour> contours = new LinkedList<PotraceContour>();
		contours.add(potrace);

		return contours;
	}

	private ContourLine findNextLine(ContourLine currentLine, int[] pixels,
			int width, int height) {

		boolean left = false, right = false;
		if (isForground(currentLine.to.x - 1, currentLine.to.y + 1, pixels,
				width, height)) {
			left = true;
		}
		if (isForground(currentLine.to.x, currentLine.to.y + 1, pixels, width,
				height)) {
			right = true;
		}

		if (!left && !right) {
			return new ContourLine(currentLine.to, new ContourPoint(
					currentLine.to.x + 1, currentLine.to.y));
		} else if (!left && right) {
			return new ContourLine(currentLine.to, new ContourPoint(
					currentLine.to.x, currentLine.to.y + 1));
		} else {
			// left && not right || left && right
			return new ContourLine(currentLine.to, new ContourPoint(
					currentLine.to.x - 1, currentLine.to.y));
		}

	}

	private boolean isForground(int x, int y, int[] pixels, int width,
			int height) {

		int u = getU(x, width);
		int v = getV(y, width);

		if (u > -1 && u < width && v > -1 && v < height
				&& pixels[I(u, v, width)] == forground) {
			return true;
		}

		return false;
	}

	final int forground = 0xff000000;
	final int background = 0xffffffff;

	private LinkedHashSet<Contour> combinedContourLabeling(
			int[] originalPixels, int width, int height) {
		int[] picture = new int[originalPixels.length]; // the pixel to work on

		LinkedHashSet<Contour> contours = new LinkedHashSet<Contour>();

		for (int i = 0; i < originalPixels.length; i++) {
			picture[i] = 0;
		}

		int regionCounter = 0;
		int currentLabel = 0;
		int contourStart = 0;
		Contour contour = null;

		for (int v = 0; v < height; v++) {
			currentLabel = 0;
			for (int u = 0; u < width; u++) {
				if (originalPixels[I(u, v, width)] == forground) {
					if (currentLabel != 0) {
						picture[I(u, v, width)] = currentLabel;
					} else {
						currentLabel = picture[I(u, v, width)];

						if (currentLabel == 0) {
							currentLabel = ++regionCounter;
							contourStart = I(u, v, width);
							contour = traceContour(contourStart, 0,
									currentLabel, originalPixels, picture,
									width, height);
							contours.add(contour);
							picture[I(u, v, width)] = currentLabel;
						}
					} // end else

				} // end if foreground
					// if current pixel is a forground pixel
				else if (currentLabel != 0) {
					if (picture[I(u, v, width)] == 0) {
						contourStart = I(u - 1, v, width);
						contour = traceContour(contourStart, 1, currentLabel,
								originalPixels, picture, width, height);
						contours.add(contour);
					}
					currentLabel = 0;
				} // end else
			} // end u-for
		} // end v-for
		return contours;
	}

	private Contour traceContour(int start, int type, int currentLabel,
			int[] originalPixels, int[] newPixels, int width, int height) {

		// printMe("traceContour: " + start + " type=" + type + " label: "
		// + currentLabel);

		Contour contour = new Contour(type);
		int[] currentNode = findNextNode(start, type, originalPixels,
				newPixels, width, height);
		int d = currentNode[1];
		int xT = currentNode[0];
		int previousPosition = start;
		int currentPosition = currentNode[0];

		// add the current position to the contour
		contour.addCoorodinates(getU(currentPosition, width),
				getV(currentPosition, width));

		// printMe("xT: " + xT + " start: " + start);

		boolean done = (previousPosition == currentPosition);
		int counter = 0;
		while (!done) {
			newPixels[currentPosition] = currentLabel;
			currentNode = findNextNode(currentPosition, (d + 6) % 8,
					originalPixels, newPixels, width, height);
			previousPosition = currentPosition;
			currentPosition = currentNode[0];
			d = currentNode[1];
			done = (previousPosition == start && currentPosition == xT);

			// printMe("done: " + done + " prev: " + previousPosition + " cur: "
			// + currentPosition);

			// if (previousPosition == 88)
			// done = true;

			if (!done) {
				contour.addCoorodinates(getU(currentPosition, width),
						getV(currentPosition, width));
			}

			counter++;
		}

		return contour;
	}

	private int[] findNextNode(int start, int d, int[] originalPixels,
			int[] newPixels, int width, int height) {

		int currentPosition = start;

		// printMe("pos: " + currentPosition + " start: " + start + " d: " +
		// d);
		// printMe("i=" + i + " pos: " + currentPosition + " start: "
		// + start + " d: " + d);

		for (int i = 0; i <= 6; i++) {
			currentPosition = nextPosition(start, d, width, height);
			if (currentPosition != -1) {

				if (originalPixels[currentPosition] == background) {
					newPixels[currentPosition] = -1;
					d = (d + 1) % 8;
				} else {
					// printMe("returning: " + currentPosition + " d=" + d);
					return new int[] { currentPosition, d };
				}
			} // end if currentPos
			else {
				d = (d + 1) % 8;
			}
		} // end for

		return new int[] { start, d };
	}

	private int nextPosition(int position, int d, int width, int height) {

		int u = getU(position, width);
		int v = getV(position, width);

		switch (d) {
		case 0:
			if (++u < width) {
				return I(u, v, width);
			}
			break;
		case 7:
			if (++u < width && --v > -1) {
				return I(u, v, width);
			}
			break;
		case 6:
			if (--v > -1) {
				return I(u, v, width);
			}
			break;
		case 5:
			if (--u > -1 && --v > -1) {
				return I(u, v, width);
			}
			break;
		case 4:
			if (--u > -1) {
				return I(u, v, width);
			}
			break;
		case 3:
			if (--u > -1 && ++v < height) {
				return I(u, v, width);
			}
			break;
		case 2:
			if (++v < height) {
				return I(u, v, width);
			}
			break;
		case 1:
			if (++u < width && ++v < height) {
				return I(u, v, width);
			}
			break;
		}

		return -1;
	}

	/**
	 * calculate the pixel position
	 * 
	 * @param u
	 * @param v
	 * @param width
	 * @return
	 */
	private int I(int u, int v, int width) {
		return u + (v * width);
	}

	private int getU(int position, int width) {
		return position % width;
	}

	private int getV(int position, int width) {
		return position / width;
	}

	public boolean valid(int[] pixels, int pos, int width, int height) {
		// --- generell boarders

		if (pos == 0) { // is first pos
			return false;
		}

		if (pos + 1 == pixels.length) { // last pos
			return false;
		}

		if (pos + width > pixels.length) { // check position for next row
			return false;
		}

		if (pos - width < 0) { // check position for prev row
			return false;
		}

		// --- row specific boarders
		int row = pos / width;

		if (row != ((pos - 1) / width)) {
			return false;
		}
		if (row != ((pos + 1) / width)) {
			return false;
		}

		return true;
	}

	private void printMe(String s) {
		System.out.println(s);
	}
}
