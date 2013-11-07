import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import javax.management.loading.MLet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FloodFilling extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 600;
	private static final int maxHeight = 600;
	private static final String title = "Flood Filling ";
	private static final String initalOpen = "klein.png";

	private static JFrame frame;

	private ImageView mImageView; // image view

	private JComboBox methodList; // select the flood filling method
	private JLabel statusLine; // to print some status text

	private LinkedList<Integer> stack = new LinkedList<Integer>();

	public FloodFilling() {
		super(new BorderLayout(border, border));

		// load the default image
		mLoadedOriginalFile = new File(initalOpen);

		if (!mLoadedOriginalFile.canRead())
			mLoadedOriginalFile = openFile(); // file not found, choose another
												// image

		mImageView = new ImageView(mLoadedOriginalFile);
		originalPixels = mImageView.getPixels().clone();
		mImageView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// load image button
		JButton load = new JButton("Bild šffnen");
		load.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mLoadedOriginalFile = openFile();
				if (mLoadedOriginalFile != null) {
					mImageView.loadImage(mLoadedOriginalFile);
					mImageView.setMaxSize(new Dimension(mImageView
							.getImgWidth(), mImageView.getImgWidth()));
					originalPixels = mImageView.getPixels().clone();
					floodImage();
				}
			}
		});

		// selector for the binarization method
		JLabel methodText = new JLabel("Methode:");
		String[] methodNames = { "depth first", "depth frist 8",
				"breadth first", "breadth first 8", "region marking",
				"original" };

		methodList = new JComboBox(methodNames);
		methodList.setSelectedIndex(0); // set initial method
		methodList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				floodImage();
			}
		});

		// some status text
		statusLine = new JLabel(" ");

		// arrange all controls
		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, border, 0, 0);
		controls.add(load, c);
		controls.add(methodText, c);
		controls.add(methodList, c);

		JPanel images = new JPanel(new FlowLayout());
		images.add(mImageView);

		add(controls, BorderLayout.NORTH);
		add(images, BorderLayout.CENTER);
		add(statusLine, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(border, border, border,
				border));

		// perform the initial flooding
		floodImage();
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

		JComponent newContentPane = new FloodFilling();
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

	int originalPixels[];

	protected void floodImage() {
		if (originalPixels != null) {
			mImageView.setPixels(originalPixels.clone());
		}

		String methodName = (String) methodList.getSelectedItem();

		String message = "Flood Filling mit \"" + methodName + "\"";

		int width = mImageView.getImgWidth();
		int height = mImageView.getImgHeight();

		statusLine.setText(message);

		long startTime = System.currentTimeMillis();

		int pixels[] = mImageView.getPixels().clone();
		mLabel = 2;
		mStackSize = 0;

		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		switch (methodList.getSelectedIndex()) {
		case 0: // depth first
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == black) {
					depthFirst(pixels, i, width, height);
					mLabel++;
				}
			}
			break;
		case 1: // depth first 8
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == black) {
					depthFirst8(pixels, i, width, height);
					mLabel++;
				}
			}
			break;
		case 2: // breadth first
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == black) {
					breadthFirst(pixels, i, width, height);
					mLabel++;
				}
			}
			break;
		case 3: // breadth first 8
			for (int i = 0; i < pixels.length; i++) {
				if (pixels[i] == black) {
					breadthFirst8(pixels, i, width, height);
					mLabel++;
				}
			}
			break;
		case 4: // sequentially marking
			sequentielMarking(pixels, width, height);
			break;
		default:
			printMe("done nothing...");
			break;
		}

		mImageView.setPixels(pixels);
		long time = System.currentTimeMillis() - startTime;

		frame.pack();

		statusLine.setText(message + " in " + time + " ms Ð stack depth "
				+ mStackSize);
	}

	LinkedHashSet<LinkedHashSet<Integer>> kolisionen = new LinkedHashSet<LinkedHashSet<Integer>>();

	private void sequentielMarking(int[] pixels, int width, int height) {
		collisions.clear();
		kolisionen.clear();

		LinkedList<Integer> menge;
		LinkedHashSet<Integer> temp;

		for (int v = 0; v < height - 1; v++) {
			for (int u = 0; u < width - 1; u++) {
				int pos = I(u, v, width);
				if (pixels[pos] == black) {
					// printMe("pos(" + pos + ") is black");
					int[] labels = checkMask(pixels, u, v, width);
					if (labels.length == 0) {
						pixels[pos] = mLabel++;
					} else if ((labels.length == 1)) {
						pixels[pos] = labels[0];
					} else {
						Arrays.sort(labels);

						temp = new LinkedHashSet<Integer>();
						for (int labelPos = 0; labelPos < labels.length; labelPos++) {
							temp.add(labels[labelPos]);
						}
						pixels[pos] = labels[0];
						kolisionen.add(temp);

						// menge = new LinkedList<Integer>();
						// for (int labelPos = 0; labelPos < labels.length;
						// labelPos++) {
						// menge.add(labels[labelPos]);
						// }
						// Collections.sort(menge);
						// if (collisions.containsKey(menge.get(0))) {
						// menge.addAll(collisions.get(menge.get(0)));
						// }
						// collisions.put(menge.get(0), menge);
						// pixels[pos] = menge.get(0);
						// menge = null;
					}
				}
				// System.out.print("," + pixels[pos]);
			}
			// System.out.println("");
		}

		resolveColisions(pixels);
	}

	private void resolveColisions(int[] pixels) {
		// printMe("Colisions: " + collisions.size());

		int firstItem = -1;
		for (Iterator<LinkedHashSet<Integer>> outer = kolisionen.iterator(); outer
				.hasNext();) {
			LinkedHashSet<Integer> item = outer.next();
			firstItem = -1;
			for (Iterator<Integer> it = item.iterator(); it.hasNext();) {
				int value = it.next();
				if (firstItem == -1) {
					firstItem = value;
				} else {
					for (int i = 0; i < pixels.length; i++) {
						if (pixels[i] == value) {
							pixels[i] = firstItem;
						}
					}
				}
			}
		}

		// printMe("coll: " + collisions.size());
		// for (Integer key : collisions.keySet()) {
		// LinkedList<Integer> col = collisions.get(key);
		// for (int item : col) {
		// for (int i = 0; i < pixels.length; i++) {
		// if (pixels[i] == item)
		// pixels[i] = key;
		// }
		// }
		// }

		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] != white) {
				pixels[i] = getColor(pixels[i]);
			}
			// System.out.print(" " + pixels[i]);
		}

	}

	private LinkedHashMap<Integer, LinkedList<Integer>> collisions = new LinkedHashMap<Integer, LinkedList<Integer>>();

	private int[] checkMask(int[] pixels, int u, int v, int width) {
		LinkedHashSet<Integer> items = new LinkedHashSet<Integer>();

		// check pos - 1 same row
		if ((u - 1) >= 0 && pixels[u - 1 + v * width] != white) {
			items.add(pixels[u - 1 + v * width]);
		}

		// prev row pos - 1
		if ((u - 1) >= 0 && v - 1 >= 0
				&& pixels[u - 1 + (v - 1) * width] != white) {
			items.add(pixels[u - 1 + (v - 1) * width]);
		}

		// prev row same pos
		if ((v - 1) >= 0 && pixels[u + (v - 1) * width] != white) {
			items.add(pixels[u + (v - 1) * width]);
		}

		// prev row pos + 1
		if (u < width && v - 1 >= 0 && pixels[u + 1 + (v - 1) * width] != white) {
			items.add(pixels[u + 1 + (v - 1) * width]);
		}

		int[] res = new int[items.size()];
		int couter = 0;
		for (Iterator<Integer> it = items.iterator(); it.hasNext();) {
			Integer value = it.next();
			res[couter++] = value;
		}

		return res;
	}

	private int I(int u, int v, int w) {
		return u + (v * w);
	}

	private void breadthFirst8(int[] pixels, int initialPos, int width,
			int height) {
		stack.addFirst(initialPos);
		int currentPos;
		do {
			currentPos = stack.removeLast();
			if (checkBorder(pixels, currentPos, width, height)
					&& pixels[currentPos] == black) {
				pixels[currentPos] = getColor(this.mLabel);
				stack.addFirst(currentPos + 1);
				stack.addFirst(currentPos + width);
				stack.addFirst(currentPos + 1 + width);
				stack.addFirst(currentPos - 1 + width);
				stack.addFirst(currentPos - width);
				stack.addFirst(currentPos - 1 - width);
				stack.addFirst(currentPos + 1 - width);
				stack.addFirst(currentPos - 1);

				if (mStackSize < stack.size()) {
					mStackSize = stack.size();
				}
			}
		} while (!stack.isEmpty());
	}

	private void breadthFirst(int[] pixels, int initialPos, int width,
			int height) {
		stack.addFirst(initialPos);
		int currentPos;
		do {
			currentPos = stack.removeLast();
			if (checkBorder(pixels, currentPos, width, height)
					&& pixels[currentPos] == black) {
				pixels[currentPos] = getColor(this.mLabel);
				stack.addFirst(currentPos + 1);
				stack.addFirst(currentPos + width);
				stack.addFirst(currentPos - width);
				stack.addFirst(currentPos - 1);

				if (mStackSize < stack.size()) {
					mStackSize = stack.size();
				}
			}
		} while (!stack.isEmpty());
	}

	int mLabel = 100;

	/**
	 * 0xff000000
	 */
	int black = 0xff000000;
	/**
	 * 0xffffffff
	 */
	int white = 0xffffffff;

	LinkedHashMap<Integer, Integer> colors = new LinkedHashMap<Integer, Integer>();

	private int getColor(int label) {
		if (colors.containsKey(label)) {
			return colors.get(label);
		} else {
			return generateColor(label);
		}
	}

	private int generateColor(int label) {
		Random rnd = new Random();
		float r, g, b;
		Color c;

		do {
			r = rnd.nextFloat();
			g = rnd.nextFloat();
			b = rnd.nextFloat();
			c = new Color(r, g, b);
		} while (colors.containsValue(c.getRGB()));

		colors.put(label, c.getRGB());
		return c.getRGB();
	}

	int mStackSize = 0;

	private void depthFirst(int[] pixels, int initialPos, int width, int height) {
		stack.addFirst(initialPos);
		int currentPos;
		do {
			currentPos = stack.removeFirst();
			if (checkBorder(pixels, currentPos, width, height)
					&& pixels[currentPos] == black) {
				pixels[currentPos] = getColor(this.mLabel);
				stack.addFirst(currentPos + 1);
				stack.addFirst(currentPos + width);
				stack.addFirst(currentPos - width);
				stack.addFirst(currentPos - 1);

				if (mStackSize < stack.size()) {
					mStackSize = stack.size();
				}
			}
		} while (!stack.isEmpty());
	}

	private void depthFirst8(int[] pixels, int initialPos, int width, int height) {
		stack.addFirst(initialPos);
		int currentPos;
		do {
			currentPos = stack.removeFirst();
			if (checkBorder(pixels, currentPos, width, height)
					&& pixels[currentPos] == black) {
				pixels[currentPos] = getColor(this.mLabel);
				stack.addFirst(currentPos + 1);
				stack.addFirst(currentPos + width);
				stack.addFirst(currentPos + 1 + width);
				stack.addFirst(currentPos - 1 + width);
				stack.addFirst(currentPos - width);
				stack.addFirst(currentPos - 1 - width);
				stack.addFirst(currentPos + 1 - width);
				stack.addFirst(currentPos - 1);

				if (mStackSize < stack.size()) {
					mStackSize = stack.size();
				}
			}
		} while (!stack.isEmpty());
	}

	/**
	 * Checking if the position is within the border of the image
	 * 
	 * @param pixels
	 *            the image
	 * @param pos
	 *            position to check
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 * @return true if is within the border of the image or false if not
	 */
	public boolean checkBorder(int[] pixels, int pos, int width, int height) {
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
