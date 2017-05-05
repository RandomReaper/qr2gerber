package org.pignat.app.qr2gerber;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.zxing.WriterException;

public class Test
{
	private static final boolean invert = true;
	private static final boolean console_print = false;
	
	private static void console_print(QRPlusInfo qrcode)
	{
		for (int y = 0; y < qrcode.size(); y++)
		{
			for (int x = 0; x < qrcode.size(); x++)
			{
				if (qrcode.get(x, y))
				{
					System.out.print("**");
				} else
				{
					System.out.print("  ");
				}
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws WriterException, IOException
	{
		QRPlusInfo qrcode = QRPlusInfo.encode
		(
			"http://wiki.hevs.ch/uit/index.php5/Hardware/ARMEBS/4/Welcome"
		); 
		
		System.out.println("matrix size = " + qrcode.size() + "x" + qrcode.size());

		if (invert)
		{
			qrcode.invert();
		}
		
		if (console_print)
		{
			console_print(qrcode);
		}
		
		
		QRtoGerber q2g = new QRtoGerber(qrcode, 10.0);
		System.out.println("line width = " + (int)(q2g.line_size_mm()*1000) + " um");
		PrintWriter out = new PrintWriter("test.gbr");
		long before = System.currentTimeMillis();
		String s = q2g.toGerber(); 
		System.out.println("done in " + (System.currentTimeMillis()-before) + " ms");
		out.println(s);
		out.close();

	}
}
