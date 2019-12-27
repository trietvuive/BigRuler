import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

enum Unit {
	Inches(1), Centimeteres(2.54), Milimeters(25.4);
	private double value;

	Unit(double value) {
		this.value = value;
	}

	double getValue() {
		return value;
	}

	public String toString() {
		return this.name();
	}
}

public class Runner {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				GUI.createAndShowGUI();
			}
		});

	}

	@SuppressWarnings("unused")
	private static boolean isInteger(String s) {
		int radix = 10;
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	private static class GUI extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel unitLabel, resLabel, convertLabel;
		private static final String chooseString = "Choose unit: ";
		private static final String resString = "Result: ";
		private JTextField textField = input();

		private Unit[] arr = new Unit[] { Unit.Inches, Unit.Centimeteres, Unit.Milimeters };
		private JComboBox<Unit> input = new JComboBox<Unit>(arr);
		private JComboBox<Unit> convert = new JComboBox<Unit>(arr);
		private JTextField resField;
		private JMenuItem i1, i2, i3;
		private static ButtonGroup demGroup,gradGroup,showMarking;
		private static JRadioButtonMenuItem i18, i116, i132;
		private static JRadioButtonMenuItem i64,i32,i16;
		private JRadioButtonMenuItem yes,no;

		static JFrame frame;

		private static DrawCanvas canvas;

		public GUI() {
			super(new BorderLayout());

			resLabel = new JLabel(resString);
			unitLabel = new JLabel(chooseString);
			convertLabel = new JLabel("Convert to: ");

			JPanel labelPane = new JPanel(new GridLayout(3, 3));
			labelPane.add(unitLabel);
			labelPane.add(convertLabel);
			labelPane.add(resLabel);

			resField = new JTextField();
			resField.setEditable(false);
			resField.setText("0");

			JPanel fieldPane = new JPanel(new GridLayout(3, 3));
			input.addActionListener(this);
			convert.addActionListener(this);
			fieldPane.add(input);
			fieldPane.add(convert);
			fieldPane.add(resField);
			setBorder(BorderFactory.createEmptyBorder(20 + 5 * 2, 20 + 5 * 2, 20 + 5 * 2, 20 + 5 * 2));

			JLabel instruction = new JLabel("A tool to calculate number in format of /64, /32, /16,...");

			JPanel buttonPane = new JPanel(new GridLayout(0, 1));
			add(buttonPane, BorderLayout.CENTER);
			add(labelPane, BorderLayout.WEST);
			add(fieldPane, BorderLayout.EAST);
			add(instruction, BorderLayout.NORTH);
			add(textField, BorderLayout.NORTH);

			// menu part

			JMenuBar mb = new JMenuBar();
			i1 = new JMenu("Denominator");
			i2 = new JMenu("Inch Graduation");
			i3 = new JMenu("Show 1/8 Marking");

			demGroup = new ButtonGroup();

			i64 = new JRadioButtonMenuItem("64");
			i64.addActionListener(this);
			i32 = new JRadioButtonMenuItem("32");
			i32.addActionListener(this);
			i16 = new JRadioButtonMenuItem("16");
			i16.addActionListener(this);
			i64.setSelected(true);

			demGroup.add(i64);
			demGroup.add(i32);
			demGroup.add(i16);

			i1.add(i64);
			i1.add(i32);
			i1.add(i16);

			gradGroup = new ButtonGroup();

			i18 = new JRadioButtonMenuItem("1/8");
			i18.addActionListener(this);
			i116 = new JRadioButtonMenuItem("1/16");
			i116.addActionListener(this);
			i132 = new JRadioButtonMenuItem("1/32");
			i132.addActionListener(this);
			i18.setSelected(true);

			gradGroup.add(i18);
			gradGroup.add(i116);
			gradGroup.add(i132);

			i2.add(i18);
			i2.add(i116);
			i2.add(i132);

			ButtonGroup showMarking = new ButtonGroup();

			yes = new JRadioButtonMenuItem("Yes");
			yes.addActionListener(this);
			no = new JRadioButtonMenuItem("No");
			no.addActionListener(this);
			no.setSelected(true);

			showMarking.add(yes);
			showMarking.add(no);
			i3.add(yes);
			i3.add(no);
			mb.add(i1);
			mb.add(i2);
			mb.add(i3);
			frame.setJMenuBar(mb);
			System.out.println(getSelectedButtonText(showMarking));
			System.out.println(showMarking.getButtonCount());

			// canvas part
			canvas = new DrawCanvas();
			frame.pack();
			canvas.setPreferredSize(new Dimension(720, 180));
			canvas.addComponentListener(new ComponentListener() {

				@Override
				public void componentHidden(ComponentEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void componentMoved(ComponentEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void componentResized(ComponentEvent e) {
					canvas.repaint();
				}

				@Override
				public void componentShown(ComponentEvent e) {
					// TODO Auto-generated method stub

				}

			});
			add(canvas, BorderLayout.SOUTH);
		}

		public static String getSelectedButtonText(ButtonGroup buttonGroup) {
			for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
				AbstractButton button = buttons.nextElement();

				if (button.isSelected()) {
					return button.getText();
				}
			}

			return null;
		}

		public void start(String str) {
			ArrayList<Double> doub = new ArrayList<Double>();
			char sign = '+';
			int pointer = 0;
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == '+' || str.charAt(i) == '-') {
					doub.add(convertFraction(str.substring(pointer, i).trim(), sign));
					pointer = i + 1;
					sign = str.charAt(i);

				}
				if (i == str.length() - 1) {
					doub.add(convertFraction(str.substring(pointer, i + 1).trim(), sign));
				}

			}
			String result = computeResult(doub, (Unit) input.getSelectedItem(), (Unit) convert.getSelectedItem());
			resField.setText(result);
		}

		public JTextField input() {
			// c.setLayout(new BorderLayout());
			final JTextField t = new JTextField(100);
			Font myFontSize = t.getFont().deriveFont(Font.BOLD, 50f);
			t.setFont(myFontSize);
			t.addKeyListener(new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						// String num1 = n15.getText();
						// global = global.concat(num1);
						try {
							start(textField.getText());
						} catch (Exception error) {
							textField.setText("Error");
						}
						canvas.repaint();
					}
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
			return t;

		}

		private static void createAndShowGUI() {
			frame = new JFrame("Conversion Calculator");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(new GUI());
			frame.pack();
			frame.setSize(new Dimension(720, 720));
			frame.setVisible(true);
		}

		String computeResult(ArrayList<Double> number, Unit input, Unit convert) {
			return convertUnit(sum(number), input, convert);
		}

		String convertUnit(double number, Unit input, Unit convert) {
			double result = number * convert.getValue() / input.getValue();
			if (convert.equals(Unit.Inches)) {
				return ToFraction64(result);
			}
			return Double.toString(result);
		}

		private static String ToFraction64(double value) {
			// denominator is fixed
			System.out.println(getSelectedButtonText(demGroup));
			int denominator = Integer.parseInt(getSelectedButtonText(demGroup));
			// integer part, can be signed: 1, 0, -3,...
			int integer = (int) value;
			// numerator: always unsigned (the sign belongs to the integer part)
			// + 0.5 - rounding, nearest one: 37.9 / 64 -> 38 / 64; 38.01 / 64 -> 38 / 64
			int numerator = (int) ((Math.abs(value) - Math.abs(integer)) * denominator + 0.5);

			// some fractions, e.g. 24 / 64 can be simplified:
			// both numerator and denominator can be divided by the same number
			// since 64 = 2 ** 6 we can try 2 powers only
			// 24/64 -> 12/32 -> 6/16 -> 3/8
			// In general case (arbitrary denominator) use gcd (Greatest Common Divisor):
			// double factor = gcd(denominator, numerator);
			// denominator /= factor;
			// numerator /= factor;
			while ((numerator % 2 == 0) && (denominator % 2 == 0)) {
				numerator /= 2;
				denominator /= 2;
			}
			if (denominator > 1)
				if (integer != 0) // all three: integer + numerator + denominator
					return String.format("%s %s/%s", integer, numerator, denominator);
				else if (value < 0) // negative numerator/denominator, e.g. -1/4
					return String.format("-%s/%s", numerator, denominator);
				else // positive numerator/denominator, e.g. 3/8
					return String.format("%s/%s", numerator, denominator);
			else
				return Integer.toString(integer); // just an integer value, e.g. 0, -3, 12...
		}

		double sum(ArrayList<Double> a) {
			double b = 0;
			for (Double c : a) {
				b += c;
			}
			return b;
		}

		private static double convertFraction(String number, char buttonselected) {
			if (number.length() == 0)
				return 0;
			if (number.charAt(0) == '-')
				buttonselected = '-';
			String[] a = number.split(" ");
			if (a.length == 2) {
				double b = 0;
				if (isDouble(a[0])) {
					b = Double.parseDouble(a[0]);
				}
				String[] c = a[1].split("/");
				double d = Double.parseDouble(c[0]) / Double.parseDouble(c[1]);
				if (buttonselected == '+')
					return b + d;
				if (buttonselected == '-')
					return -1 * (b + d);
			} else if (number.contains("/")) {
				String[] c = number.split("/");
				double d = Double.parseDouble(c[0]) / Double.parseDouble(c[1]);
				if (buttonselected == '+')
					return d;
				if (buttonselected == '-')
					return -1 * d;
			}
			if (isDouble(number))
				if (buttonselected == '+')
					return Double.parseDouble(number);
			if (buttonselected == '-')
				return -1 * Double.parseDouble(number);
			return 0;
		}

		private static boolean isDouble(String s) {
			String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
			return s.matches(regExp);
		}

		private int inches = 12;
		private int width, height;

		public void actionPerformed(ActionEvent arg0) {
			start(textField.getText());
			canvas.repaint();
		}

		private class DrawCanvas extends JPanel {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			ArrayList<Double> lineinchwhole, lineinchhalf, lineinchquarter, lineincheighth, lineinchsixteen,
					lineinchthirtytwo;
			ArrayList<Double> linecentwhole, linecenthalf,linecentdime;
			HashMap<String, Integer> inchwholetext, centtext;
			ArrayList<Double> inchhalftext, inchquartertext, incheighthtext;

			public void paintComponent(Graphics g) {
				String inputinches = convertUnit(convertFraction(resField.getText(), '+'),
						(Unit) convert.getSelectedItem(), Unit.Inches);
				initiatelist(inputinches);
				Dimension d = this.getSize();
				width = (int) d.getWidth() - 5;
				height = (int) d.getHeight();
				int rectheight = height;
				super.paintComponent(g);
				// draw Rect
				g.setColor(new Color(255, 255, 255));
				Rectangle r = new Rectangle(0, 0, width + 5, rectheight);
				g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
				g.setColor(new Color(0, 0, 0));
				((Graphics2D) g).setStroke(new BasicStroke((float) 2.0));
				// draw inch line
				for (Double i : lineinchwhole) {
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 4);
				}
				for (Double i : lineinchhalf)
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 5);
				for (Double i : lineinchquarter)
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 6);
				for (Double i : lineincheighth)
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 7);
				if(i116.isSelected()||i132.isSelected())
				for (Double i : lineinchsixteen)
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 10);
				if(i132.isSelected())
				for (Double i : lineinchthirtytwo)
					g.drawLine(i.intValue(), 0, i.intValue(), rectheight / 16);
				// draw centimeter line
				((Graphics2D) g).setStroke(new BasicStroke((float) 2.0));
				int translatevalue = centitranslate(inputinches);
				g.translate(-translatevalue, 0);
				for (Double i : linecentwhole) {
					g.drawLine(i.intValue(), rectheight * 3 / 4, i.intValue(), rectheight);
				}
				for (Double i : linecenthalf)
					g.drawLine(i.intValue(), rectheight * 4 / 5, i.intValue(), rectheight);
				for(Double i:linecentdime)
					g.drawLine(i.intValue(), rectheight * 7 / 8, i.intValue(), rectheight);
				g.translate(translatevalue, 0);
				// draw text

				// draw red line
				((Graphics2D) g).setStroke(new BasicStroke((float) 2.0));
				g.setColor(new Color(255, 0, 0));
				((Graphics2D) g).setStroke(new BasicStroke((float) 2.0));
				int xposition = xposition(inputinches, width);
				g.drawLine(xposition, 0, xposition, height);
				g.setColor(new Color(0, 0, 0));
				if (g instanceof Graphics2D) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g.setFont(new Font("default", Font.BOLD, 16));
					for (Map.Entry<String, Integer> entry : inchwholetext.entrySet()) {
						g2.drawString(entry.getKey(), entry.getValue(), height / 3 + 10);
					}

					g.setFont(new Font("default", Font.BOLD, 12));
					for (Double i : inchhalftext) {
						g2.drawString("1/2", i.intValue() - 8, height / 4 + 10);
					}

					g.setFont(new Font("default", Font.BOLD, 10));
					for (Double i : inchquartertext) {
						g2.drawString("1/4", i.intValue() - 10, height / 5 + 10);
					}
					if (!no.isSelected())
						for (Double i : incheighthtext) {
							g2.drawString("1/8", i.intValue() - 8, height / 6 + 6);
						}
					g.setFont(new Font("default", Font.BOLD, 13));
					g.translate(-translatevalue, 0);
					for (Map.Entry<String, Integer> entry : centtext.entrySet()) {
						g2.drawString(entry.getKey(), entry.getValue(), height * 2 / 3);
					}
					g.translate(translatevalue, 0);
				}
			}

			int xposition(String inputinches, int width) {
				double convert = convertFraction(inputinches, '+');
				double position = convert % inches;
				return (int) (width * position / inches);
			}

			int centitranslate(String input) {
				double convert = convertFraction(input, '+');
				int dividend = (int) convert / inches;
				int closest12 = dividend * inches;
				double centimeter = closest12 * 2.54;
				int displayed = (int) centimeter;
				double difference = centimeter - displayed;
				return (int) (difference / (inches * 2.54) * width);
			}

			void initiatelist(String input) {
				// ruler
				lineinchwhole = new ArrayList<Double>();
				lineinchhalf = new ArrayList<Double>();
				lineinchquarter = new ArrayList<Double>();
				lineincheighth = new ArrayList<Double>();
				lineinchsixteen = new ArrayList<Double>();
				lineinchthirtytwo = new ArrayList<Double>();
				

				linecentwhole = new ArrayList<Double>();
				linecenthalf = new ArrayList<Double>();
				linecentdime = new ArrayList<Double>();

				inchhalftext = new ArrayList<Double>();
				inchquartertext = new ArrayList<Double>();
				incheighthtext = new ArrayList<Double>();

				inchwholetext = new HashMap<String, Integer>();
				centtext = new HashMap<String, Integer>();
				double convert = convertFraction(input, '+');
				if (convert < 1000)
					inches = 6;
				if (convert >= 1000)
					inches = 5;
				if (convert >= 10000)
					inches = 4;
				if (convert >= 100000)
					inches = 3;
				if (convert >= 10000000)
					inches = 2;
				int dividend = (int) convert / inches;
				int closest12 = dividend * inches;
				for (int i = 0; i <= inches * 100000; i += 625) {
					double value = (double) i / 100000;
					if (i % 100000 == 0) {
						lineinchwhole.add(value * width / inches + 2);
						inchwholetext.put(Integer.toString((int) value + closest12), (int) value * width / inches + 2);
					} else if (i % 50000 == 0) {
						lineinchhalf.add(value * width / inches + 2);
						inchhalftext.add(value * width / inches + 2);
					} else if (i % 25000 == 0) {
						lineinchquarter.add(value * width / inches + 2);
						inchquartertext.add(value * width / inches + 2);
					} else if (i % 12500 == 0) {
						lineincheighth.add(value * width / inches + 2);
						incheighthtext.add(value * width / inches + 2);
					} else if (i % 6250 == 0)
						lineinchsixteen.add(value * width / inches + 2);
					else if (i % 3125 == 0)
						lineinchthirtytwo.add(value * width / inches + 2);
				}
				for (int i = 0; i <= (inches * 254 + 500); i += 10) {
					double value = (double) i / 100;
					System.out.println(value);
					System.out.println(value%.1);
					if (i % 100 == 0) {
						linecentwhole.add(value * width / (inches * 2.54) + 2);
						centtext.put(Integer.toString((int) (value + closest12 * 2.54)),
								(int) (value * width / (inches * 2.54) + 2));
					} else if (i % 50 == 0)
						linecenthalf.add(value * width / (inches * 2.54) + 2);
					else if(i%10==0)
						linecentdime.add(value * width / (inches * 2.54) + 2);
				}
			}
		}
	}
}