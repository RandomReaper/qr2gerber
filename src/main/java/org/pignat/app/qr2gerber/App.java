package org.pignat.app.qr2gerber;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class App extends JFrame implements ActionListener {
	class CustomDraw extends JPanel {
		int lx;
		int ly;

		transient BufferedImage img = null;

		CustomDraw(int x, int y) {
			lx = x;
			ly = y;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img != null) {
				g.drawImage(img, 0, 0, this);
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(lx, ly);
		}

	    @Override
		public Dimension getMinimumSize() {
			return new Dimension(lx, ly);
	    }

		public void setImage(BufferedImage i)
		{
			img = i;
			repaint();
		}
	}

	private JButton gobutton;
	private JButton browseButton;
	private JTextField stringField;
	private JTextField sizeField;
	private JTextField destField;
	private CustomDraw preview;

	public App() {
		setLayout(new MigLayout());
		stringField = new JTextField("https://github.com/RandomReaper/qr2gerber", 35);
		sizeField = new JTextField("10.0", 35);
		gobutton = new JButton("Generate");
		gobutton.addActionListener(this);
		browseButton = new JButton("browse");
		browseButton.addActionListener(this);
		destField = new JTextField("/tmp/toto.txt");
		preview = new CustomDraw(177, 177);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("qr2gerber");
		setSize(400, 300);
		setLocationRelativeTo(null);
		add(new JLabel("String"));
		add(stringField					, "wrap");
		add(new JLabel("Size (mm): ")	, "gap unrelated");
		add(sizeField					, "wrap");
		add(new JLabel("Preview"));
		add(preview						, "wrap, grow");
		add(new JLabel("Save to"));
		add(destField					, "grow");
		add(browseButton				, "wrap");
		add(gobutton					, "wrap");
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == gobutton)
		{
			try {
				double size = Double.parseDouble(sizeField.getText());
				QRPlusInfo qrcode = null;
				
				Map<EncodeHintType, Object> encodingOptions = new HashMap<EncodeHintType, Object>();
				encodingOptions.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
				qrcode = QRPlusInfo.encode(stringField.getText(), encodingOptions).invert();
				QRPlusInfo.encode(stringField.getText());
				preview.setImage(MatrixToImageWriter.toBufferedImage(QRPlusInfo.encode(stringField.getText()).m_qrcode));
				preview.setSize(qrcode.size(), qrcode.size());
		
				QRtoGerber q2g = new QRtoGerber(qrcode, size);
				PrintWriter out = new PrintWriter(destField.getText());
				out.println(q2g.toGerber());
				out.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WriterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		 
		}
		if (e.getSource() == browseButton)
		{
			final JFileChooser fc = new JFileChooser();
	        int returnVal = fc.showOpenDialog(this);
	        if (returnVal == JFileChooser.APPROVE_OPTION)
	        {
	        	destField.setText(fc.getSelectedFile().getAbsolutePath());
	        }

		}
	}

	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				App ex = new App();
				ex.setVisible(true);
			}
		});
	}
}
