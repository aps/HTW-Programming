import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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

import potrace.Potrace;

public class PolygonExercise extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 600;
	private static final int maxHeight = 600;
	private static final String title = "Konturen ";
	private static final String initalOpen = "head.png";

	private static JFrame frame;

	private ImageView mImageView; // image view

	private JComboBox methodList; // select the flood filling method
	private JLabel statusLine; // to print some status text

	public PolygonExercise() {
		super(new BorderLayout(border, border));

		// load the default image
		mLoadedOriginalFile = new File(initalOpen);

		if (!mLoadedOriginalFile.canRead())
			mLoadedOriginalFile = openFile(); // file not found, choose another
												// image

		mImageView = new ImageView(mLoadedOriginalFile);
		mOriginalPixels = mImageView.getPixels().clone();
		mImageView.setMaxSize(new Dimension(maxWidth, maxHeight));

		// load image button
		JButton load = new JButton("Bild �ffnen");
		load.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mLoadedOriginalFile = openFile();
				if (mLoadedOriginalFile != null) {
					mImageView.loadImage(mLoadedOriginalFile);
					mImageView.setMaxSize(new Dimension(maxWidth, maxHeight));

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

		JSlider zoomSlider = new JSlider(110, 5000, 200);
		zoomSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setZoom(((JSlider) e.getSource()).getValue() / 100f);
			}
		});
		controls.add(zoomSlider);

		JPanel images = new JPanel(new GridLayout());
		images.add(mImageView);

		// bottom controls
		JPanel bottomControls = new JPanel(new GridBagLayout());
		bottomControls.add(statusLine, c);
		JCheckBox drawPixels = new JCheckBox("Pixels");
		drawPixels.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setDrawPixels(((JCheckBox) e.getSource())
						.isSelected());
			}
		});
		JCheckBox drawNeighbours = new JCheckBox("Neighbours");
		drawNeighbours.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setDrawNeighbours(((JCheckBox) e.getSource())
						.isSelected());
			}
		});
		JCheckBox drawImage = new JCheckBox("Image");
		drawImage.setSelected(true);
		drawImage.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setDrawImage(((JCheckBox) e.getSource())
						.isSelected());
			}
		});
		JCheckBox drawPolygon = new JCheckBox("Polygon");
		drawPolygon.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mImageView.setDrawPolygon(((JCheckBox) e.getSource())
						.isSelected());
			}
		});

		bottomControls.add(drawPixels, c);
		bottomControls.add(drawNeighbours, c);
		bottomControls.add(drawImage, c);
		bottomControls.add(drawPolygon, c);

		add(controls, BorderLayout.NORTH);
		add(images, BorderLayout.CENTER);
		// add(statusLine, BorderLayout.SOUTH);
		add(bottomControls, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(border, border, border,
				border));

		// perform the initial flooding
		drawImage();
		zoomSlider.setValue(5000);
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

		JComponent newContentPane = new PolygonExercise();
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

		// start potrace
		Potrace potrace = new Potrace(pixels, width, height);
		potrace.go();

		mImageView.setPixels(pixels);
		mImageView.setPotrace(potrace);
		long time = System.currentTimeMillis() - startTime;

		frame.pack();

		statusLine.setText(time + " ms");
	}
}
