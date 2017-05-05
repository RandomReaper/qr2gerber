package org.pignat.app.qr2gerber;

public class QRtoGerber
{
	private double size;
	private QRPlusInfo qrcode;
	
	private static final String VERSION = "0.1";
	
	private static final int INTEGER_PLACES = 4;
	private static final int DECIMAL_PLACES = 4;
	private static final int SCALE = (int) Math.pow(10, DECIMAL_PLACES);
	private static final int D_CODE = 999;

	/**
	 * Workaround for programs unhappy with a gerber lines whith zero in a
	 * coordinate. These software refuse to draw this kind of lines, because
	 * after adding the line with, the coordinate will be negative.
	 * 
	 * The workaround consist of adding an offset so the whole drawing fits has
	 * positive coordinates.
	 * 
	 * Known software with this bug include
	 * * PCAD 2006 sp2
	 */
	private static final boolean WORKAROUND_OFFSET = true;
	
	/**
	 * Some program import flashing (D3 gerber command) as pads, and prevents
	 * pad from beeing moved to the silk screen layer.
	 * 
	 * The workaround consists of drawing a minimal line instead of flashing.
	 * 
	 * Known software with this bug include
	 * * PCAD 2006 sp2
	 */
	private static final boolean WORKAROUND_FLASHING = true;
	
	/**
	 * @param sz The original QRCode 
	 * @param sz The final size
	 */
	public QRtoGerber(QRPlusInfo q, double sz)
	{
		qrcode = q;
		size = sz;
	}
	
	public double lineSize()
	{
		return (size/qrcode.size());
	}

	private String draw(QRPlusInfo matrix, int sx, int sy, int dx, int dy)
	{
		int f = (int)(SCALE*size/matrix.size());
		int x = f * sx;

		/* Invert Y for gerber */
		int y = f * (matrix.size()-1-sy);

		if (WORKAROUND_OFFSET)
		{
			x += SCALE;
			y += SCALE;
		}
		
		dx = f * dx;
		dy = f * dy;
		String format = "%06d";

		if (dx == 0 && dy == 0)
		{
			if (!WORKAROUND_FLASHING)
			{
				return "X" + String.format(format, x) + "Y" + String.format(format, y) + "D3*\n";
			}
			else
			{
				dx = 1;				
			}
		}
		return "X" + String.format(format, x) + "Y" + String.format(format, y) + "D2*\n"
		+ "X" + String.format(format, x+dx) + "Y" + String.format(format, y+dy) + "D1*\n";
	}

	public String toGerber()
	{
		double line_size = lineSize();
		StringBuilder s = new StringBuilder();
		String sizeString = String.format("%.4f", line_size);
		
		s.append("G04 File generated using https://github.com/RandomReaper/qr2gerber *\n");
		
		s.append("G04 encoded with QRtoGerber version " + VERSION + " *\n");
		s.append("G04 encoded string : '" + qrcode.string() + "' *\n");
		s.append("G04 target size : " + size + " mm*\n");

		s.append("G04 line width = " + (int)(lineSize()*1000) + " um" +  " *\n");
		s.append("%INQR2GERBER.GBR*%\n");
		
		/* Specify format */
		s.append("%ICAS*%\n");
		s.append("%MOMM*%\n");
		s.append("%ADD" + D_CODE +"R, "+ sizeString + "X" + sizeString + "*%\n");
		s.append("%FSLAX"+INTEGER_PLACES+DECIMAL_PLACES+"Y"+INTEGER_PLACES+DECIMAL_PLACES+"*%\n");
		s.append("%SFA1B1*%\n");
		s.append("%OFA0.000B0.000*%\n");
		s.append("G04*\n");
		s.append("G71*\n");
		s.append("G90*\n");
		s.append("G01*\n");
		s.append("D2*\n");
		s.append("%LNTop*%\n");
		s.append("D" + D_CODE + "*\n");

		/* Draw horizontal lines  */
		for (int y = 0; y < qrcode.size(); y++)
		{
			int x = 0;
			int len = 0;
			while (x < qrcode.size())
			{
				if (qrcode.get(x, y))
				{
					len++;
				}
				if (len > 0 && !qrcode.get(x, y))
				{
					s.append(draw(qrcode, x-len, y, len-1, 0));
					len = 0;
				}
				x++;
			}
			if (len > 0)
			{
				s.append(draw(qrcode, x-len, y, len-1, 0));
			}
		}
	
		s.append("D02M02*\n");
		
		return s.toString();
	}
}