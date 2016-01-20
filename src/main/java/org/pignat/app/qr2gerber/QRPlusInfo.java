package org.pignat.app.qr2gerber;

import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * A simple object for storing a QR code and it's original string
 */
public class QRPlusInfo
{
	BitMatrix m_qrcode;
	String m_string;
	
	private QRPlusInfo(String s, BitMatrix q)
	{
		m_string = s;
		m_qrcode = q;
	}

	/**
	 * Encode a QR code
	 * @param string, the string to encode 
	 * @param encodingOptions, encoding options
	 * @return A new QRPlusInfo
	 * @throws WriterException
	 */
	public static QRPlusInfo encode(String string, Map<EncodeHintType, Object> encodingOptions) throws WriterException
	{
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix q = writer.encode(
				string,
				BarcodeFormat.QR_CODE, 0, 0, encodingOptions );
		return new QRPlusInfo(string, q);
	}
	
	/**
	 * Encode a QR code
	 * @param string, the string to encode 
	 * @param encodingOptions, encoding options
	 * @return A new QRPlusInfo
	 * @throws WriterException
	 */
	public static QRPlusInfo encode(String string) throws WriterException
	{
		Map<EncodeHintType, Object> encodingOptions = new HashMap <EncodeHintType, Object>();
		encodingOptions.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		return encode(string, encodingOptions);
	}
	
	/**
	 * Invert the QR code
	 * 
	 * Useful for drawing white lines
	 */
	public QRPlusInfo invert()
	{
		for (int y = 0 ; y < m_qrcode.getHeight(); y++)
		{
			for (int x = 0 ; x < m_qrcode.getWidth(); x++)
			{
				m_qrcode.flip(x, y);
			}
		}
		
		return this;
	}

	/**
	 * Get the pixel size of the QR code
	 * @return the size of the QR code (it is square)
	 */
	public int size()
	{
		return m_qrcode.getWidth();
	}

	/**
	 * Get the string used for the QR code generation
	 * @return
	 */
	public String string()
	{
		return m_string;
	}
	
	/**
	 * Get a point from the QR code
	 * @param x coordinate
	 * @param y coordinate
	 * @return true if the dot should be written
	 */
	public boolean get(int x, int y)
	{
		return m_qrcode.get(x, y);
	}
	
}
