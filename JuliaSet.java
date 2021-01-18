import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.awt.event.*;

import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.Timer;



public class JuliaSet extends JPanel implements AdjustmentListener, ActionListener{
	Timer timer;
	boolean anim;
	int name = 0;

	JFileChooser fileChooser;

	JFrame frame;
	int w = 960;
	int h = 540;
	int screenH = h+217;
	JScrollBar aBar, bBar, backColorBar, satBar, brightBar, eyeColorBar, zoomBar, resBar, xBar, yBar;
	JLabel aLabel, bLabel, backColorLabel, satLabel, brightLabel, eyeColorLabel, zoomLabel, resLabel, xLabel, yLabel;
	JButton clearButton, saveButton, animButton;

	float A = 0;
	float B = 0;
	float backColor = 1;
	float sat = 1;
	float bright = 1;
	float eyeColor = .333f;
	float zoom = 1;
	float maxIter = 300.0f;
	int shiftX = 0;
	int shiftY = 0;
	JPanel panelBars, panelLabels, panel, buttonPanel;
	BufferedImage image;
	public JuliaSet(){
		String currDir=System.getProperty("user.dir");
		fileChooser=new JFileChooser(currDir);

		frame = new JFrame("Julia Set App");
		frame.setSize(w, screenH);
		frame.add(this);

		clearButton = new JButton("Clear");
		saveButton = new JButton("Save");
		animButton = new JButton("Start/Stop A&B Anim");
		clearButton.addActionListener(this);
		saveButton.addActionListener(this);
		animButton.addActionListener(this);

		aBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
		bBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -2000, 2000);
		backColorBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 1000);
		eyeColorBar = new JScrollBar(JScrollBar.HORIZONTAL, 333, 0, 0, 1000);
		satBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 1000);
		brightBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 1000);
		zoomBar = new JScrollBar(JScrollBar.HORIZONTAL, 1, 0, 1, 300);
		resBar = new JScrollBar(JScrollBar.HORIZONTAL, 300, 0, 1, 10000);
		xBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -10000, 10000);
		yBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, -10000, 10000);

		aBar.addAdjustmentListener(this);
		bBar.addAdjustmentListener(this);
		backColorBar.addAdjustmentListener(this);
		eyeColorBar.addAdjustmentListener(this);
		satBar.addAdjustmentListener(this);
		brightBar.addAdjustmentListener(this);
		zoomBar.addAdjustmentListener(this);
		resBar.addAdjustmentListener(this);
		xBar.addAdjustmentListener(this);
		yBar.addAdjustmentListener(this);

		aLabel = new JLabel("A: "+A);
		bLabel = new JLabel("B: "+B);
		backColorLabel = new JLabel("Back Hue: "+backColor);
		eyeColorLabel = new JLabel("Eye Hue: "+eyeColor);
		satLabel = new JLabel("Sat: "+sat);
		brightLabel = new JLabel("Bri: "+bright);
		zoomLabel = new JLabel("Zoom: "+zoom);
		resLabel = new JLabel("Res: "+maxIter);
		xLabel = new JLabel("X: "+shiftX);
		yLabel = new JLabel("Y: "+shiftY);

		GridLayout grid = new GridLayout(10, 1);

		panelBars = new JPanel();
		panelBars.setLayout(grid);
		panelBars.add(aBar);
		panelBars.add(bBar);
		panelBars.add(backColorBar);
		panelBars.add(eyeColorBar);
		panelBars.add(satBar);
		panelBars.add(brightBar);
		panelBars.add(zoomBar);
		panelBars.add(resBar);
		panelBars.add(xBar);
		panelBars.add(yBar);

		panelLabels = new JPanel();
		panelLabels.setLayout(grid);
		panelLabels.add(aLabel);
		panelLabels.add(bLabel);
		panelLabels.add(backColorLabel);
		panelLabels.add(eyeColorLabel);
		panelLabels.add(satLabel);
		panelLabels.add(brightLabel);
		panelLabels.add(zoomLabel);
		panelLabels.add(resLabel);
		panelLabels.add(xLabel);
		panelLabels.add(yLabel);

		buttonPanel = new JPanel(new GridLayout(3, 1));
		buttonPanel.add(clearButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(animButton);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(panelBars, BorderLayout.CENTER);
		panel.add(panelLabels, BorderLayout.WEST);
		panel.add(buttonPanel, BorderLayout.EAST);
		frame.add(panel, BorderLayout.SOUTH);

		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		timer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				A+=.005;
				aBar.setValue((int)(A*1000f));
				B+=.01;
				bBar.setValue((int)(B*1000f));
				drawJuliaImage();
				repaint();
			}
		});
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(!anim){
			drawJuliaImage();
			g.drawImage(image,0,0,null);
		}
		else{
			g.drawImage(image,0,0,null);
		}
	}
	public void drawJuliaImage(){
		for(int i = shiftX; i < w+shiftX; i++){
			for(int j = shiftY; j < h+shiftY; j++){
				float iter = maxIter;
				double zx = 1.5*(((i)-(w/2.0))/(zoom/2.0*w));
				double zy = ((j)-(h/2.0))/(zoom/2.0*h);

				while(zx*zx+zy*zy < 6 && iter > 0){
					double diff = zx*zx-zy*zy + A;
					zy = 2*zx*zy+B;
					zx = diff;
					iter--;
				}
				int c = 0;
				if(iter > 0)//bounded
					c = Color.HSBtoRGB(backColor*(iter / maxIter)%1, sat, bright);
				else{//escape to infinity (at least past maxIter)
					c = Color.HSBtoRGB(eyeColor, 1, 1);
				}
				image.setRGB(i-shiftX,j-shiftY,c);
			}
		}
	}
	public void saveImage(){
		if(image!=null){
			FileFilter filter=new FileNameExtensionFilter("*.png","png");
		    fileChooser.setFileFilter(filter);
			if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				try{
					String st=file.getAbsolutePath();
					if(st.indexOf(".png")>=0)
						st=st.substring(0,st.length()-4);
					ImageIO.write(image,"png",new File(st+".png"));
				}catch(IOException e){
					System.out.println("Error");
				}
			}
		}
		repaint();
	}
	public void autoSaveImages(){
		try{
			ImageIO.write(image,"png",new File("Seq\\"+name+".png"));
			name++;
		}
		catch(IOException e){
			System.out.println("Error");
		}
	}
	public void clearImage(){
		aBar.setValue(0);
		bBar.setValue(0);
		backColorBar.setValue(1000);
		eyeColorBar.setValue(333);
		satBar.setValue(1000);
		brightBar.setValue(1000);
		satBar.setValue(1);
		brightBar.setValue(300);

		A = 0;
		B = 0;
		backColor = 1;
		sat = 1;
		bright = 1;
		eyeColor = .333f;
		zoom = 1;
		maxIter = 300f;

		anim = false;
		timer.stop();
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == clearButton){
			clearImage();
		}
		else if(e.getSource() == saveButton){
			saveImage();
		}
		else if(e.getSource() == animButton){
			anim = !anim;
			if (anim) {
				timer.start();
			} else {
				timer.stop();
			}
		}
		repaint();
	}
	public void adjustmentValueChanged(AdjustmentEvent e){
		if(e.getSource() == aBar){
			A = aBar.getValue()/1000.0f;
			aLabel.setText("A: "+A);
			if(A >= 1){
				anim = false;
				timer.stop();
			}
		}
		else if(e.getSource() == bBar){
			B = bBar.getValue()/1000.0f;
			bLabel.setText("B: "+B);
			if(A >= 1){
				anim = false;
				timer.stop();
			}
		}
		else if(e.getSource() == backColorBar){
			backColor = backColorBar.getValue()/1000.0f;
			backColorLabel.setText("Back Hue: "+String.format("%.3g%n", backColor));
		}
		else if(e.getSource() == satBar){
			sat = satBar.getValue()/1000.0f;
			satLabel.setText("Sat: "+sat);
		}
		else if(e.getSource() == brightBar){
			bright = brightBar.getValue()/1000.0f;
			brightLabel.setText("Bri: "+bright);
		}
		else if(e.getSource() == eyeColorBar){
			eyeColor = eyeColorBar.getValue()/1000.0f;
			eyeColorLabel.setText("Eye Hue: "+String.format("%.3g%n", eyeColor));
		}
		else if(e.getSource() == zoomBar){
			zoom = zoomBar.getValue();
			zoomLabel.setText("Zoom: "+zoom);
		}
		else if(e.getSource() == resBar){
			maxIter = resBar.getValue();
			resLabel.setText("Res: "+maxIter);
		}
		else if(e.getSource() == xBar){
			shiftX = xBar.getValue();
			xLabel.setText("X: "+shiftX);
		}
		else if(e.getSource() == yBar){
			shiftY = yBar.getValue();
			yLabel.setText("Y: "+shiftY);
		}
		repaint();
	}
	public static void main(String[] args){
		JuliaSet app = new JuliaSet();
	}
}