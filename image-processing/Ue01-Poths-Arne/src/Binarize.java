import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

public class Binarize extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 600;
	private static final int maxHeight = 600;
	private static final String title = "Binarisierung ";
	private static final String initalOpen = "tools1.png";
	private static final int THRESHOLD_INIT = 128;
	private static final int THRESHOLD_MAX = 256;
	private static final int THRESHOLD_MIN = 0;

	private static JFrame frame;

	private ImageView srcView; // source image view
	private ImageView dstView; // binarized image view

	private JComboBox methodList; // the selected binarization method
	private JLabel statusLine; // to print some status text
	private JSlider thresholdSlider; // the slider to adjust the threshold
	private JLabel thresholdLabel;
	private JCheckBox outlineCheckbox;

	public Binarize() {
		super(new BorderLayout(border, border));

		// load the default image
		File input = new File(initalOpen);

		if (!input.canRead())
			input = openFile(); // file not found, choose another image

		srcView = new ImageView(input);
		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// create an empty destination image
		dstView = new ImageView(srcView.getImgWidth(), srcView.getImgHeight());
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// load image button
		JButton load = new JButton("Bild šffnen");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File input = openFile();
				if (input != null) {
					srcView.loadImage(input);
					srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
					binarizeImage();
				}
			}
		});

		// selector for the binarization method
		JLabel methodText = new JLabel("Methode:");
		String[] methodNames = { "Schwellenwert Regler", "Iso-Data-Algorithmus" };

		methodList = new JComboBox(methodNames);
		methodList.setSelectedIndex(0); // set initial method
		methodList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				binarizeImage();
			}
		});

		// create threshold slider
		thresholdSlider = new JSlider(JSlider.HORIZONTAL, THRESHOLD_MIN,
				THRESHOLD_MAX, THRESHOLD_INIT);
		thresholdSlider.setMajorTickSpacing(64);
		thresholdSlider.setMinorTickSpacing(8);
		thresholdSlider.setPaintTicks(true);
		thresholdSlider.setPaintLabels(true);
		thresholdSlider.setVisible(false);
		thresholdSlider.addChangeListener(getThresholdChangeListener());

		// craete threshold value label
		thresholdLabel = new JLabel(" ");

		outlineCheckbox = new JCheckBox("outline");
		outlineCheckbox.addItemListener(getOutlineListener());

		// some status text
		statusLine = new JLabel(" ");

		// arrange all controls
		JPanel controls = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, border, 0, 0);
		controls.add(load, c);
		controls.add(methodText, c);
		controls.add(methodList, c);
		controls.add(thresholdSlider, c);
		controls.add(thresholdLabel, c);
		controls.add(outlineCheckbox, c);

		JPanel images = new JPanel(new FlowLayout());
		images.add(srcView);
		images.add(dstView);

		add(controls, BorderLayout.NORTH);
		add(images, BorderLayout.CENTER);
		add(statusLine, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(border, border, border,
				border));

		// perform the initial binarization
		binarizeImage();
	}

	private int[] outlineImage;
	private int[] normalImage;

	private ItemListener getOutlineListener() {
		return new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				int w = srcView.getImgWidth(), h = srcView.getImgHeight();

				if (outlineCheckbox.isSelected()) {
					normalImage = java.util.Arrays.copyOf(dstView.getPixels(),
							dstView.getPixels().length);
					outlineImage = java.util.Arrays.copyOf(dstView.getPixels(),
							dstView.getPixels().length);

					outline(outlineImage, w, h);
					dstView.setPixels(outlineImage, w, h);
				} else {
					dstView.setPixels(normalImage, w, h);
				}

				// dstView.saveImage("out.png");

				frame.pack();
			}
		};
	}

	private ChangeListener getThresholdChangeListener() {
		return new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					binarizeImage();
				}
			}
		};
	}

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

		JComponent newContentPane = new Binarize();
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

	protected void binarizeImage() {
		String methodName = (String) methodList.getSelectedItem();

		// image dimensions
		int width = srcView.getImgWidth();
		int height = srcView.getImgHeight();

		// get pixels arrays
		int srcPixels[] = srcView.getPixels();
		int dstPixels[] = java.util.Arrays.copyOf(srcPixels, srcPixels.length);

		String message = "Binarisieren mit \"" + methodName + "\"";

		statusLine.setText(message);

		long startTime = System.currentTimeMillis();

		boolean showSlider = false;
		switch (methodList.getSelectedIndex()) {
		case 0: // Schwellwert
			// binarize50(dstPixels);
			int threshold = thresholdSlider.getValue();
			binarizeSlider(dstPixels, threshold);
			showSlider = true;
			break;
		case 1: // ISO-Data-Algorithmus
			calulateISODataAlgorithm(dstPixels);
			break;
		}

		thresholdSlider.setVisible(showSlider);

		long time = System.currentTimeMillis() - startTime;

		dstView.setPixels(dstPixels, width, height);

		// dstView.saveImage("out.png");

		frame.pack();

		statusLine.setText(message + " in " + time + " ms");
	}

	/**
	 * get the Median for the gray scale
	 * 
	 * @param pixels
	 *            {@link Integer} array with the image pixels
	 * @return {@link Integer} with the calculated threshold value
	 */
	private int getThreshold(int[] pixels) {
		LinkedList<Integer> values = new LinkedList<Integer>();
		for (int pix : pixels) {
			int gray = ((pix & 0xff) + ((pix & 0xff00) >> 8) + ((pix & 0xff0000) >> 16)) / 3;

			if (!values.contains(gray))
				values.add(gray);
		}
		Collections.sort(values);
		int threshold = values.get(values.size() / 2);

		return threshold;

	}

	private void calulateISODataAlgorithm(int[] pixels) {
		calulateISODataAlgorithm(pixels, getThreshold(pixels), 0);
	}

	HashMap<Integer, Integer> mAppearancePa = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> mAppearancePb = new HashMap<Integer, Integer>();

	private void calulateISODataAlgorithm(int[] pixels, float threshold,
			int iteration) {
		float pa = 0.0f, pb = 0.0f, all = 0.0f;
		mAppearancePa.clear();
		mAppearancePb.clear();
		for (int i = 0; i < pixels.length; i++) {
			// calculate the gray value for the scala 0 - 255
			int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;

			if (gray < threshold) {
				addAppearance(gray, mAppearancePa);
			} else {
				addAppearance(gray, mAppearancePb);
			}
		}

		float ua = 0.0f, ub = 0.0f;
		for (Integer key : mAppearancePa.keySet()) {
			pa += mAppearancePa.get(key);
			ua += key * mAppearancePa.get(key);
		}
		for (Integer key : mAppearancePb.keySet()) {
			pb += mAppearancePb.get(key);
			ub += key * mAppearancePb.get(key);
		}

		ua = (1 / pa) * ua;
		ub = (1 / pb) * ub;

		float newThresold = (ua + ub) / 2.0f;
		if (newThresold != threshold && iteration < 100)
			calulateISODataAlgorithm(pixels, newThresold, ++iteration);
		else
			binarizeSlider(pixels, (int) (newThresold / 1));
	}

	private void addAppearance(int value, HashMap<Integer, Integer> appearance) {
		if (appearance.containsKey(value)) {
			appearance.put(value, appearance.get(value) + 1);
		} else {
			appearance.put(value, 1);
		}
	}

	private void binarizeSlider(int[] pixels, int threshold) {
		for (int i = 0; i < pixels.length; i++) {
			int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
			pixels[i] = gray < threshold ? black : white;
		}
		thresholdLabel.setText("Schwellenwert: " + threshold);
	}

	void binarize50(int pixels[]) {
		for (int i = 0; i < pixels.length; i++) {
			int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
			pixels[i] = gray < 128 ? black : white;
		}
	}

	int black = 0xff000000;
	int white = 0xffffffff;

	void outline(int pixels[], int imageWidth, int imageHeight) {
		int[] newPixels = new int[pixels.length];

		for (int i = 0; i < pixels.length; i++) {
			int pixelColor = pixels[i];
			newPixels[i] = white;

			if (pixelColor == black
					&& checkBoarder(pixels, i, imageWidth, imageHeight)) {

				if (pixels[i - imageWidth] == black
						&& pixels[i + imageWidth] == black
						&& pixels[i + 1] == black && pixels[i - 1] == black) {
					newPixels[i] = black;
				}
			}
		}

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (newPixels[i] == white && pixels[i] == black) ? black
					: white;
		}
	}

	public boolean checkBoarder(int[] pixels, int pos, int width, int height) {
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
}
