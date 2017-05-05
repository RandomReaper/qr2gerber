package org.pignat.app.qr2gerber;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.google.zxing.WriterException;

public class Cli 
{
	private static void usage()
	{
		System.out.println("qr2gerber string to encode size_in_mm [destination_file]");
		System.out.println("\t example : qr2gerber \"my message\" 3.72 test.gbr");
	}
	
	public static void main(String[] args) throws WriterException, FileNotFoundException
	{
		String string = null;
		double size = 10.0;
		String filename = null;
		
		if (args.length != 2 && args.length != 3)
		{
			usage();
			System.exit(-1);
		}
		
		try
		{
			string = args[0];
			size = Double.parseDouble(args[1]);
		}
		catch (Exception e)
		{
			usage();
			System.exit(-2);
		}

		if (args.length == 3)
		{
			filename = args[2];
		}
		
		QRPlusInfo qrcode = QRPlusInfo.encode(string).invert();
		QRtoGerber q2g = new QRtoGerber(qrcode, size);
		String s = q2g.toGerber();
		
		if (filename != null)
		{
			PrintWriter out = new PrintWriter(filename);		 
			out.println(s);
			out.close();
		}
		else
		{
			System.out.println(s);
		}
	}
}
