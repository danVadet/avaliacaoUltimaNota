package com.sayit.data;

import java.nio.ByteBuffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageProfile {
	
    public byte[] readImageBytes(Image image) {
        ByteBuffer pixelBuffer = ByteBuffer.allocate((int) (image.getWidth() * image.getHeight()) * 4);
        PixelReader pixelReader = image.getPixelReader();

        for (int h = 0; h < image.getHeight(); h++) {
            for (int w = 0; w < image.getWidth(); w++) {
                pixelBuffer.putInt(pixelReader.getArgb(w, h));
            }
        }

        return pixelBuffer.array();
    }

	
	public  Image writeImageBytes(final byte[] buffer, final int width, final int height) {
        WritableImage writableImage = new WritableImage(width, height);
        ByteBuffer pixelBuffer = ByteBuffer.wrap(buffer);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                pixelWriter.setArgb(w, h, pixelBuffer.getInt());
            }
        }

        return writableImage;
    }


}
