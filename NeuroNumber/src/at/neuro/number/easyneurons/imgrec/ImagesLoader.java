package at.neuro.number.easyneurons.imgrec;
/***
 * Neuroph  http://neuroph.sourceforge.net
 * Copyright by Neuroph Project (C) 2008
 *
 * This file is part of Neuroph framework.
 *
 * Neuroph is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Neuroph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.neuroph.contrib.imgrec.FractionRgbData;
import org.neuroph.contrib.imgrec.ImageSampler;
import org.neuroph.contrib.imgrec.image.Dimension;
import org.neuroph.contrib.imgrec.image.Image;
import org.neuroph.contrib.imgrec.image.ImageFactory;

public class ImagesLoader
{
    /**
     * Loads images from the specified dir, scales to specified resolution and creates RGB data for each image
     * Puts RGB data in a Map using filenames as keys, and rerurns that amp
     * @param imgDir
     * @param samplingResolution
     * @return
     * @throws java.io.IOException
     */
  public static Map<String, FractionRgbData> getFractionRgbDataForDirectory(File imgDir, Dimension samplingResolution) throws IOException
  {
    if(!imgDir.isDirectory()) {
      throw new IOException("The given file must be a directory.  Argument is: " + imgDir);
    }
    
    Map<String, FractionRgbData> rgbDataMap = new HashMap<String, FractionRgbData>();
    
    ImagesIterator imagesIterator = new ImagesIterator(imgDir);
    while (imagesIterator.hasNext()) {
      Image img = imagesIterator.next();
      img = ImageSampler.downSampleImage(samplingResolution, img);
      String filenameOfCurrentImage = imagesIterator.getFilenameOfCurrentImage();
//      System.out.println(filenameOfCurrentImage + " is: " + img.getWidth() + "x" + img.getHeight());
      StringTokenizer st = new StringTokenizer(filenameOfCurrentImage, ".");
      rgbDataMap.put(st.nextToken(), new FractionRgbData(img));
    }
    return rgbDataMap;
  }

    /**
     * Loads image from file into BufferedImage object and scales image to sampling resolution
     * @param imgFile
     * @param samplingResolution
     * @return
     * @throws java.io.IOException
     */
  public static Image loadImage(File imgFile, Dimension samplingResolution) 
    throws IOException
  {
	Image img = ImageFactory.getImage(imgFile);
    img = ImageSampler.downSampleImage(samplingResolution, img);
    return img;
  }
}