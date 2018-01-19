package enigma.backend;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class SteganographySystem {
	// This class should be used to hide data inside
	// of mutable data.
	//
	// Compatible File formats:
	// .bmp
	// .png
	// .wav
	//
	// steganography technique:
	// LSB - Least Significat Bit
	//
	// -------------------------------------------------------- 
	// Implementation:
	// Hide the size of the encrypted data in the first four bytes
	// that are being encrypted.
	//
	//
	
	public static byte[] intToBytes(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((num >> 24)&0xff);
		bytes[1] = (byte) ((num >> 16)&0xff);
		bytes[2] = (byte) ((num >> 8 )&0xff);
		bytes[3] = (byte) (num & 0xff);
		return bytes;
	}
	
	public static int bytesToInt(byte[] bytes) {
		if(bytes == null || bytes.length < 4) return 0;
		int num = ((bytes[0]&0xff) << 24) +
				  ((bytes[1]&0xff) << 16) +
				  ((bytes[2]&0xff) << 8) +
				  ((bytes[3]&0xff));
		return num;
	}
	
	// for every byte of image data, there is only
	// 1 bit available for hiding data
	public static byte[] extractLSBData(byte[] hostData) {
		if(hostData == null || hostData.length < 4) return null;
		byte[] data = new byte[hostData.length / 8];
		
		for(int i = 0; i < data.length; i++) {
			data[i] = (byte)
					  ((((hostData[i*8+0]&0x1) << 7) +
					    ((hostData[i*8+1]&0x1) << 6) +
					    ((hostData[i*8+2]&0x1) << 5) +
					    ((hostData[i*8+3]&0x1) << 4) +
					    ((hostData[i*8+4]&0x1) << 3) +
					    ((hostData[i*8+5]&0x1) << 2) +
					    ((hostData[i*8+6]&0x1) << 1) +
					    ((hostData[i*8+7]&0x1)))
							  & 0xff);
		}
		
		return data;
	}
	
	public static byte[] embedLSBData(byte[] hostData, byte[] data) {
		//byte[] embeddedData = new byte[hostData.length];
		//System.arraycopy(hostData, 0, embeddedData, 0, embeddedData.length);
		byte[] embeddedData = hostData;
		
		// hide the data inside the hostData
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < 8; j++) {
				embeddedData[i*8+j] = (byte) 
						((embeddedData[i*8+j]&0xfe) + ((data[i] >> (7-j))&0x1));
			}
		}
		
		return embeddedData;
	}
	
	public static byte[] prependSize(byte[] data) {
		byte[] prependedData = new byte[data.length + 4];
		byte[] size = intToBytes(data.length);
		System.arraycopy(size, 0, prependedData, 0, size.length);
		System.arraycopy(data, 0, prependedData, size.length, data.length);
		return prependedData;
	}
	
	public static byte[] decryptImage(BufferedImage image) {
		
		WritableRaster raster = image.getRaster();
		DataBufferByte rasterBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] rasterData = rasterBuffer.getData();
		byte[] lsbData = extractLSBData(rasterData);
		
		byte[] sizeBytes = new byte[4];
		System.arraycopy(lsbData, 0, sizeBytes, 0, sizeBytes.length);
		int size = bytesToInt(sizeBytes);
		
		if(size <= 0) return null;
		byte[] data = new byte[size];
		System.arraycopy(lsbData, sizeBytes.length, data, 0, size);
		
		return data;
	}
	
	public static int spaceForLSB(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte rasterBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] rasterData = rasterBuffer.getData();
		return rasterData.length;
	}
	
	public static BufferedImage encryptImage(BufferedImage image, byte[] data) {

		// take out the image raster as bytes
		WritableRaster raster = image.getRaster();
		DataBufferByte rasterBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] rasterData = rasterBuffer.getData();
		
		// determine if the data will fit in the chosen image data
		if(rasterData.length / 8 < data.length) {
			return null;
		}
		
		// prepend the size to the data
		byte[] prependedData = prependSize(data);
		
		// embed the data inside the raster bytes
		byte[] embeddedData = embedLSBData(rasterData, prependedData);
		
		/*BufferedImage encryptedImage = new BufferedImage(
				image.getWidth(), image.getHeight(), image.getType());
		encryptedImage.getRaster().setDataElements(
				0, 0, image.getWidth(), image.getHeight(), embeddedData);*/
		
		return image;
	}
	
	
}

