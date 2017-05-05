package org.pignat.app.qr2gerber;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.zxing.WriterException;

public class Test
{
	private static final boolean OPTION_INVERT = true;
	private static final boolean OPTION_CONSOLE_PRINT = true;
	
	private static void consolePrint(QRPlusInfo qrcode)
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
			"https://github.com/RandomReaper"
		); 
		
		System.out.println("matrix size = " + qrcode.size() + "x" + qrcode.size());

		if (OPTION_INVERT)
		{
			qrcode.invert();
		}
		
		if (OPTION_CONSOLE_PRINT)
		{
			consolePrint(qrcode);
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
