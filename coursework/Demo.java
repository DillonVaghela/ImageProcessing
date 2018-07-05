import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class Demo extends Component implements ActionListener {

    String descs[] = 
        {
            "Original", 
            "Negative",
            "ReScale / Pixel * value",
            "Shifting",
            "shift random value and rescale",
            "ADD IMAGE",
            "SUBTRACT IMAGE",
            "DIVIDE IMAGE",
            "MULITPLY IMAGE",
            "Bitwiese Boolean not operation",
            "Bitwise Boolean AND",
            "Bitwise Boolean OR",
            "Bitwise Boolean XOR",
            "ROI",
            "Logarithmic",
            "Powerlaw",
            "RandomLookUpTable",
            "BitPlaneSlicing",
            "Histogram",
            "ImageFiltering",
            "Salt and pepper",
            "Min filtering",
            "Max filtering",
            "Midpoint filtering",
            "Median filtering",
            "mean and SD",
            "simple thresholding",
            "automated thresholding ",
        };

    int opIndex; 
    int lastOp;

    private BufferedImage bi, biFiltered, bi2;  
    int w, h;
    ArrayList<BufferedImage> listUndo = new ArrayList<BufferedImage>();

    public Demo() {
        String file = "test.bmp";
        try {
            bi = ImageIO.read(new File(file));
            bi2 = ImageIO.read(new File("Lena.bmp")); 
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            //System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
                listUndo.add(biFiltered);
            }
        } catch (Exception e) {      
            try{
                System.out.println("Image is raw");
                bi = rawImage(file);
                w = bi.getWidth(null);
                h = bi.getHeight(null);
                //displayValues(bi);
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
                listUndo.add(biFiltered); }
            catch (Exception f)
            {
                System.out.println("cant read file");
            }
        }
    }   

    public void displayValues(BufferedImage bi)
    {
        int[][][] ImageArray = convertToArray(bi); 
        int[][][] ImageArray2 = convertToArray(bi2);
        int width =bi.getWidth();
        int height = bi.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.println(ImageArray[x][y][1] + " " + ImageArray2[x][y][1]);
                System.out.println(ImageArray[x][y][2] + " " + ImageArray2[x][y][2]);
                System.out.println(ImageArray[x][y][3] + " " + ImageArray2[x][y][3]);
            }}
    }

    public BufferedImage rawImage(String file)
    {

        try
        {
            File rawFile = new File(file);
            FileInputStream inputStream = new FileInputStream(file);
            int i = 0, readBuffer = 0;
            byte[] buffer = new byte[512];
            String[] ImageData = new String[512];
            while( ( readBuffer = inputStream.read(buffer) ) != -1 )
            {
                ImageData[i++] = Arrays.toString(buffer); 
            }
            return convertToBimage((convertImageData(ImageData)));
        }
        catch( Exception e )
        {
            System.out.println("error");
            e.printStackTrace();
            return null;
        }
    }

    public static int[][][] convertImageData(String[] ImageData)
    {
        int x = 0;
        int y = 0;
        int[][][] imageArray = new int[512][512][4];
        for( String lineImageData : ImageData )
        {
            int[] imageLine = convertIntoInt(lineImageData);
            y =0;
            for( int rgb : imageLine )
            {
                imageArray[y][x][0] = 255;    //a
                imageArray[y][x][1] = rgb;  //r
                imageArray[y][x][2] = rgb;  //g
                imageArray[y][x][3] = rgb;  //b
                y++;
            }
            x++;
        }
        return imageArray;
    }

    public static int[] convertIntoInt(String line)
    {
        int[] intImageData = new int[512];
        if( line.length() == 0 || line.charAt(0) != '[' || line.charAt(line.length() - 1 ) != ']')
        {
            return new int[]{-1};
        }
        String[] nums = (line.substring(1,line.length()-1).trim()).split(", ");
        for(int z = 0;z<nums.length;z++)
        {
            intImageData[z] = Math.abs(Integer.parseInt(nums[z]));
        }
        return intImageData;
    }

    public Dimension getPreferredSize() 
    {
        return new Dimension(w, h);
    }

    String[] getDescriptions() 
    {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() 
    {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) 
    {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();     

        g.drawImage(biFiltered, 0, 0, null);
    }

    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    //************************************
    //  Your turn now:  Add more function below
    //************************************
    public BufferedImage ImageScale(BufferedImage timg)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);        
        int temp = 0;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = (int) (ImageArray[x][y][1] * 0.4f);
                if (temp <0)
                {
                    ImageArray[x][y][1] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][1] = 255;
                }
                else
                {
                    ImageArray[x][y][1] = temp;
                }
                temp = (int) (ImageArray[x][y][2] * 0.4f);
                if (temp <0)
                {
                    ImageArray[x][y][2] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][2] = 255;
                }
                else
                {
                    ImageArray[x][y][2] = temp;
                }
                temp = (int) (ImageArray[x][y][3] * 0.4f);

                if (temp <0)
                {
                    ImageArray[x][y][3] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][3] = 255;
                }
                else
                {
                    ImageArray[x][y][3] = temp;
                }
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage ImageShifting(BufferedImage timg)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int temp = 0;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = ImageArray[x][y][1] + 50;
                if (temp <0)
                {
                    ImageArray[x][y][1] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][1] = 255;
                }
                else
                {
                    ImageArray[x][y][1] = temp;
                }
                temp = ImageArray[x][y][2] + 50;
                if (temp <0)
                {
                    ImageArray[x][y][2] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][2] = 255;
                }
                else
                {
                    ImageArray[x][y][2] = temp;
                }
                temp = ImageArray[x][y][3] + 50;
                if (temp <0)
                {
                    ImageArray[x][y][3] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][3] = 255;
                }
                else
                {
                    ImageArray[x][y][3] = temp;
                }
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage ImageAddRandomValue(BufferedImage timg)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int temp, randomValue = 0;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                randomValue = (int)(Math.random()*10);
                temp = ImageArray[x][y][1] + randomValue;
                if (temp <0)
                {
                    ImageArray[x][y][1] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][1] = 255;
                }
                else
                {
                    ImageArray[x][y][1] = temp;
                }
                randomValue = (int)(Math.random()*100);
                temp = ImageArray[x][y][2] + randomValue;
                if (temp <0)
                {
                    ImageArray[x][y][2] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][2] = 255;
                }
                else
                {
                    ImageArray[x][y][2] = temp;
                }
                randomValue = (int)(Math.random()*100);
                temp = ImageArray[x][y][3] + randomValue;
                if (temp <0)
                {
                    ImageArray[x][y][3] = 0;
                }
                else if (temp > 255)
                {
                    ImageArray[x][y][3] = 255;
                }
                else
                {
                    ImageArray[x][y][3] = temp;
                }
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage reScaleBetween0And255(int[][][] ImageArray)
    {
        int width = ImageArray[1].length;
        int height = ImageArray[1].length;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                if (ImageArray[x][y][1] <0)
                {
                    ImageArray[x][y][1] = 0;
                }
                else if (ImageArray[x][y][1] > 255)
                {
                    ImageArray[x][y][1] = 255;
                }

                if (ImageArray[x][y][2] <0)
                {
                    ImageArray[x][y][2] = 0;
                }
                else if (ImageArray[x][y][2] > 255)
                {
                    ImageArray[x][y][2] = 255;
                }

                if (ImageArray[x][y][3] <0)
                {
                    ImageArray[x][y][3] = 0;
                }
                else if (ImageArray[x][y][3] > 255)
                {
                    ImageArray[x][y][3] = 255;
                }

            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public int[][][] addTwoImages(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2); 
        int[][][] newImage = new int[width][height][4];
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                newImage[x][y][0] = 255;
                newImage[x][y][1] = ImageArray[x][y][1] + ImageArray2[x][y][1];
                newImage[x][y][2] = ImageArray[x][y][2] + ImageArray2[x][y][2];
                newImage[x][y][3] = ImageArray[x][y][3] + ImageArray2[x][y][3];
            }
        }

        return (newImage);
    }

    public int[][][] divideImage(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2);
        int[][][] newImage = new int[width][height][4];
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                newImage[x][y][0] = 255;
                if (ImageArray[x][y][1] != 0)
                {
                    newImage[x][y][1] = ImageArray2[x][y][1] / ImageArray[x][y][1];
                }
                if (ImageArray[x][y][2] != 0)
                {
                    newImage[x][y][2] = ImageArray2[x][y][2] / ImageArray[x][y][2];
                }
                if (ImageArray[x][y][3] != 0)
                {
                    newImage[x][y][3] = ImageArray2[x][y][3] / ImageArray[x][y][3];
                }

            }
        }

        return (newImage);
    }

    public int[][][] SubstractImage(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2);
        int[][][] newImage = new int[width][height][4];
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                newImage[x][y][0] = 255;
                newImage[x][y][1] = ImageArray2[x][y][1] - ImageArray[x][y][1];
                newImage[x][y][2] = ImageArray2[x][y][2] - ImageArray[x][y][2];
                newImage[x][y][3] = ImageArray2[x][y][3] - ImageArray[x][y][3];
            }
        }

        return (newImage);
    }

    public int[][][] MultiplyImage(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi);
        int[][][] newImage = new int[width][height][4];
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                newImage[x][y][0] = 255;
                newImage[x][y][1] = ImageArray[x][y][1] * ImageArray2[x][y][1];
                newImage[x][y][2] = ImageArray[x][y][2] * ImageArray2[x][y][2];
                newImage[x][y][3] = ImageArray[x][y][3] * ImageArray2[x][y][3];
            }
        }

        return (newImage);
    }

    public BufferedImage notImage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                p = ~p;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    public BufferedImage bitwiseAnd(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                ImageArray[x][y][1] = (ImageArray[x][y][1] & ImageArray2[x][y][1]);
                ImageArray[x][y][2] = (ImageArray[x][y][2] & ImageArray2[x][y][2]);
                ImageArray[x][y][3] = (ImageArray[x][y][3] & ImageArray2[x][y][3]);
            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage bitwiseOr(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                ImageArray[x][y][1] = (ImageArray[x][y][1] | ImageArray2[x][y][1]) &0xff;
                ImageArray[x][y][2] = (ImageArray[x][y][2] | ImageArray2[x][y][2])&0xff;
                ImageArray[x][y][3] = (ImageArray[x][y][3] | ImageArray2[x][y][3])&0xff;
            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage bitwiseXor(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        int[][][] ImageArray2 = convertToArray(bi2); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                ImageArray[x][y][1] = (ImageArray[x][y][1] ^ ImageArray2[x][y][1]) &0xff;
                ImageArray[x][y][2] = (ImageArray[x][y][2] ^ ImageArray2[x][y][2])&0xff;
                ImageArray[x][y][3] = (ImageArray[x][y][3] ^ ImageArray2[x][y][3])&0xff;
            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage RegionOfInterest(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = new int[height][width][4]; 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (x > 150 && x < 200 && y > 30 && y < 80)
                {

                    ImageArray[x][y][0] = 255;
                    ImageArray[x][y][1] = 255;
                    ImageArray[x][y][2] = 255;
                    ImageArray[x][y][3]= 255;

                }

                else
                {
                    ImageArray[x][y][0] = 0;
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3]= 0;
                }

            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage RegionOfInterestNegative(BufferedImage timg) 
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (x > 150 && x < 200 && y > 30 && y < 80)
                {

                    ImageArray[x][y][0] = 255 - ImageArray[x][y][0];
                    ImageArray[x][y][1] = 255 - ImageArray[x][y][1];
                    ImageArray[x][y][2] = 255 - ImageArray[x][y][2];
                    ImageArray[x][y][3]= 255 -  ImageArray[x][y][3];

                }

            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage Logarithmic(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int z = (int)(255 / Math.log(256));
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] =(int)( z * Math.log(1+ImageArray[x][y][1]));  //r
                ImageArray[x][y][2] =(int)( z * Math.log(1+ImageArray[x][y][2]));  //g
                ImageArray[x][y][3] =(int)( z * Math.log(1+ImageArray[x][y][3]));  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public int[][][] PowerLaw(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg); 
        double p = 2;
        int z = (int)(255/Math.pow(255,1-p));
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] =(int)( z * Math.pow(ImageArray[x][y][1], p));  //r
                ImageArray[x][y][2] =(int)( z * Math.pow(ImageArray[x][y][2],p));  //g
                ImageArray[x][y][3] =(int)( z * Math.pow(ImageArray[x][y][3],p));  //b

            }
        }

        return ImageArray;  // Convert the array to BufferedImage
    }

    public BufferedImage LookUpTable(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int lookup_table [] = new int[256];;
        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        for (int i=0;i<256;i++){
            lookup_table[i] = (int)( Math.random() * 256); //assign random numbers between 0 and 255, inclusively           
        }
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] =lookup_table[ImageArray[x][y][1]];  //r
                ImageArray[x][y][2] =lookup_table[ImageArray[x][y][2]];  //g
                ImageArray[x][y][3] =lookup_table[ImageArray[x][y][3]];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage BitPlaneSlicing(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();
        int lengtth = 0;
        int[][][] ImageArray = convertToArray(timg); 
        int k = 7;
        for(int y=0; y<height; y++){ 
            for(int x=0; x<width; x++){
                int r = ImageArray[x][y][1]; //r
                int g = ImageArray[x][y][2]; //g
                int b = ImageArray[x][y][3]; //b 
                ImageArray[x][y][1] = (r>>k)&1; //r 
                ImageArray[x][y][2] = (g>>k)&1; //g 
                ImageArray[x][y][3] = (b>>k)&1; //b

                if (ImageArray[x][y][1] == 1) {
                    ImageArray[x][y][1] = 255;
                }

                if (ImageArray[x][y][2] == 1) {
                    ImageArray[x][y][2] = 255;
                }

                if (ImageArray[x][y][3] == 1) {
                    ImageArray[x][y][3] = 255;
                }

            }}

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage findHistogram(BufferedImage timg)
    {
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        double[] redN = new double[256];
        double[] greenN = new double[256];
        double[] blueN = new double[256];
        int[][][] ImageArray = convertToArray(timg); 
        float res = 0;
        for(int i=0; i<timg.getWidth(); i++) {
            for(int j=0; j<timg.getHeight(); j++) {

                int r = new Color(timg.getRGB (i, j)).getRed();
                int g = new Color(timg.getRGB (i, j)).getGreen();
                int b = new Color(timg.getRGB (i, j)).getBlue();

                // Increase the values of colors
                red[r]++; 
                green[g]++; 
                blue[b]++;
                res++;

            }
        }
        System.out.println("getHistogram" );
        System.out.println("red array ="+ Arrays.toString(red)  );
        System.out.println("green array ="+ Arrays.toString(green)  );
        System.out.println("blue array ="+ Arrays.toString(blue)  );

        for(int j=0; j<256; j++) 
        {

            redN[j] = ( red[j] / res);
            greenN[j] = ( green[j]/ res);
            blueN[j] = ( blue[j]/ res);
        }

        System.out.println("normHistogram" );
        System.out.println("red array ="+ Arrays.toString(redN));
        System.out.println("green array ="+ Arrays.toString(greenN));
        System.out.println("blue array ="+ Arrays.toString(blueN));

        double cumR = 0;
        double cumG = 0;
        double cumB = 0;

        double [] cumulativeHistogramR = new double[256];
        double [] cumulativeHistogramG = new double[256];
        double [] cumulativeHistogramB = new double[256];
        for (int i = 0; i < 256; i++) 
        {
            cumR += redN[i];
            cumG += greenN[i];
            cumB += blueN[i];
            cumulativeHistogramR[i] = cumR;
            cumulativeHistogramG[i] = cumG;
            cumulativeHistogramB[i] = cumB;
        }
        System.out.println("cumHistogram" );
        System.out.println("red array ="+ Arrays.toString(cumulativeHistogramR)  );
        System.out.println("green array ="+ Arrays.toString(cumulativeHistogramG)  );
        System.out.println("blue array ="+ Arrays.toString(cumulativeHistogramB)  );

        for (int i = 0; i < 256; i++) 
        {
            red[i] = (int)Math.round(cumulativeHistogramR[i] * 255);
            green[i] = (int)Math.round(cumulativeHistogramG[i] * 255);
            blue[i] = (int)Math.round(cumulativeHistogramB[i] * 255);
        }
        System.out.println("roundHistogram" );
        System.out.println("red array ="+ Arrays.toString(red)  );
        System.out.println("green array ="+ Arrays.toString(green)  );
        System.out.println("blue array ="+ Arrays.toString(blue)  );
        //apply to image
        int count =0;
        for (int y = 0; y < 512; y++) {
            for (int x = 0; x < 512; x++) {
                ImageArray[x][y][1] = red[ImageArray[x][y][1]];
                ImageArray[x][y][2] =  green[ImageArray[x][y][2]];
                ImageArray[x][y][3] =  blue[ImageArray[x][y][3]];

            }
        }
        return convertToBimage(ImageArray);  
        //return reScaleBetween0And255(ImageArray);
    }


    public BufferedImage applyMask(double[][] Mask, BufferedImage timg, int Case)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        int[][][] ImageArray2 = new int[width][height][4];
        int r = 0;
        int g = 0;
        int b = 0;
        System.out.println(Arrays.deepToString(Mask));
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
                r = 0;
                g = 0;
                b = 0;

                for(int s=-1; s<=1; s++){
                    for(int t=-1; t<=1; t++){
                        r = (int) (r +  Mask[1-s][1-t]*ImageArray[x+s][y+t][1]); //r
                        g =(int)( g +  Mask[1-s][1-t]*ImageArray[x+s][y+t][2]); //g
                        b =(int)( b +  Mask[1-s][1-t]*ImageArray[x+s][y+t][3]); //b

                    }}
                if (Case >= 6)
                {
                    r = Math.abs(r);
                    g = Math.abs(g);
                    b = Math.abs(b);
                }
                ImageArray2[x][y][0] = ImageArray[x][y][0];
                ImageArray2[x][y][1] = rescaleValue(r); //r 
                ImageArray2[x][y][2] = rescaleValue(g); //g 
                ImageArray2[x][y][3] = rescaleValue(b);  //b
            }}
        return convertToBimage(ImageArray2); 
    }

    public BufferedImage applyFilter(BufferedImage timg)
    {

        int opIndex = Integer.parseInt(JOptionPane.showInputDialog("pick filtering"));
        double [][]mask = new double [3][3];
        int[][][] ImageArray = convertToArray(timg);
        switch (opIndex) {
            case 0:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    mask[i][j]=(double)1/9;
                }
            }
            return applyMask(mask, timg, opIndex);
            case 1:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=(double)4/16;
                    }
                    else if(j==1||i==1){
                        mask[i][j]=(double)2/16;
                    }
                    else{
                        mask[i][j]=(double)1/16;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 2:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=4;
                    }
                    else if(j==1||i==1){
                        mask[i][j]=-1;
                    }

                }
            }
            return applyMask(mask, timg, opIndex);
            case 3:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=8;
                    }
                    else {
                        mask[i][j]=-1;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 4:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=5;
                    }
                    else if(j==1||i==1){
                        mask[i][j]=-1;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 5:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=9;
                    }
                    else {
                        mask[i][j]=-1;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 6: 
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==2&&j==1){
                        mask[i][j]=-1;
                    }
                    else if (i==1&&j==2) {
                        mask[i][j]=1;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 7: 
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==1&&j==1){
                        mask[i][j]=-1;
                    }
                    else if (i==2&&j==2) {
                        mask[i][j]=1;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 8:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i==0&&j!=1){
                        mask[i][j]=-1;
                    }
                    else if (i==0&&j==1) {
                        mask[i][j]=-2;
                    }
                    else if (i==2&&j!=1) {
                        mask[i][j]=1;
                    }
                    else if (i==2&&j==1) {
                        mask[i][j]=2;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
            case 9:
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(i!=1&&j==0){
                        mask[i][j]=-1;
                    }
                    else if (i==1&&j==0) {
                        mask[i][j]=-2;
                    }
                    else if (i!=1&&j==2) {
                        mask[i][j]=1;
                    }
                    else if (i==1&&j==2) {
                        mask[i][j]=2;
                    }
                }
            }
            return applyMask(mask, timg, opIndex);
        }
        return timg;
    }

    public BufferedImage saltAndPepper(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int x,y = 0;
        Random r = new Random();
        for(int i=0;i<25000;i++){

            x =(int) (Math.abs(r.nextInt())%width);

            y =(int) (Math.abs(r.nextInt())%height);
            if (i> 12500){
            ImageArray[x][y][1] = 255; //r 
            ImageArray[x][y][2] = 255; //g 
            ImageArray[x][y][3] = 255; //b
        }
        else{

            ImageArray[x][y][1] = 0; //r 
            ImageArray[x][y][2] = 0; //g 
            ImageArray[x][y][3] = 0; //b
            

        }
    }
        return convertToBimage(ImageArray); 
    }

    public BufferedImage minFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray2 = new int[width][height][4];

        int k = 0;
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){ 
                k = 0;
                for(int s=-1; s<=1; s++){ 
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; 
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g 
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    } 
                }
                Arrays.sort(rWindow); 
                Arrays.sort(gWindow); 
                Arrays.sort(bWindow); 

                ImageArray2[x][y][0] = ImageArray[x][y][0]; //r 
                ImageArray2[x][y][1] = rWindow[0]; //r 
                ImageArray2[x][y][2] = gWindow[0]; //g 
                ImageArray2[x][y][3] = bWindow[0]; //b
            }}
        return convertToBimage(ImageArray2); 
    }

    public BufferedImage maxFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray2 = new int[width][height][4];

        int k = 0;
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){ 
                k = 0;
                for(int s=-1; s<=1; s++){ 
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; 
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g 
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    } 
                }
                Arrays.sort(rWindow); 
                Arrays.sort(gWindow); 
                Arrays.sort(bWindow); 
                ImageArray2[x][y][0] = ImageArray[x][y][0]; //r 
                ImageArray2[x][y][1] = rWindow[8]; //r 
                ImageArray2[x][y][2] = gWindow[8]; //g 
                ImageArray2[x][y][3] = bWindow[8]; //b
            }}
        return convertToBimage(ImageArray2); 
    }

    public BufferedImage midPointFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray2 = new int[width][height][4];
        int k = 0;
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){ 
                k = 0;
                for(int s=-1; s<=1; s++){ 
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; 
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g 
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    } 
                }
                Arrays.sort(rWindow); 
                Arrays.sort(gWindow); 
                Arrays.sort(bWindow); 
                ImageArray2[x][y][0] = ImageArray[x][y][0]; //r 
                ImageArray2[x][y][1] = (rWindow[8] + rWindow[0])/2; //r 
                ImageArray2[x][y][2] = (gWindow[8] + gWindow[0])/2; //g 
                ImageArray2[x][y][3] = (bWindow[8] + bWindow[0])/2; //b
            }}
        return convertToBimage(ImageArray2); 
    }

    public BufferedImage medianFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray2 = new int[width][height][4];
        int k = 0;
        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){ 
                k = 0;
                for(int s=-1; s<=1; s++){ 
                    for(int t=-1; t<=1; t++){
                        rWindow[k] = ImageArray[x+s][y+t][1]; 
                        gWindow[k] = ImageArray[x+s][y+t][2]; //g 
                        bWindow[k] = ImageArray[x+s][y+t][3]; //b
                        k++;
                    } 
                }
                Arrays.sort(rWindow); 
                Arrays.sort(gWindow); 
                Arrays.sort(bWindow); 
                ImageArray2[x][y][0] = ImageArray[x][y][0]; //r 
                ImageArray2[x][y][1] = rWindow[4]; //r 
                ImageArray2[x][y][2] = gWindow[4]; //g 
                ImageArray2[x][y][3] = bWindow[4]; //b
            }}
        return convertToBimage(ImageArray2); 
    }

    public void meanAndStandardDeviation(BufferedImage timg)
    {
        int[] red = new int[256];
        int[] green = new int[256];
        int[] blue = new int[256];
        double[] redN = new double[256];
        double[] greenN = new double[256];
        double[] blueN = new double[256];
        int[][][] ImageArray = convertToArray(timg); 
        float res = 0;
        for(int i=0; i<timg.getWidth(); i++) {
            for(int j=0; j<timg.getHeight(); j++) {

                int r = new Color(timg.getRGB (i, j)).getRed();
                int g = new Color(timg.getRGB (i, j)).getGreen();
                int b = new Color(timg.getRGB (i, j)).getBlue();

                // Increase the values of colors
                red[r]++; 
                green[g]++; 
                blue[b]++;
                res++;

            }
        }

        //normalise
        for(int j=0; j<256; j++) {

            redN[j] = ( red[j] / res);
            greenN[j] = ( green[j]/ res);
            blueN[j] = ( blue[j]/ res);
        }
        double redMean = 0;
        double greenMean = 0;
        double blueMean = 0;
        //find mean
        for (int i=0; i<256; i++){
            redMean+= (i*redN[i]);
            greenMean+= (i*greenN[i]);
            blueMean+= (i*blueN[i]);  
        }
        System.out.println("Mean: red: "+ redMean + " green: " +  greenMean + " blue: " + blueMean);
        //find standard deviation (sqrt of variance)
        double redVariance = 0;
        double greenVariance = 0;
        double blueVariance = 0;
        for (int i=0; i<256; i++){
            redVariance+= Math.pow((redMean),2)*redN[i];
            greenVariance+= Math.pow((greenMean),2)*greenN[i];
            blueVariance+= Math.pow((blueMean),2)*blueN[i];    
        }
        System.out.println("Standard deviation is: red: "+ Math.sqrt(redVariance)+ " green: " + Math.sqrt(greenVariance) + " blue: " + Math.sqrt(blueVariance));

    }

    public BufferedImage simpleThresholding(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg); 
        int width = timg.getWidth();
        int height = timg.getHeight();
        int thresholdingValue = (int) 70;
        int r = 0;
        int g = 0;
        int b = 0;
        for( int i=0;i<width;i++)
            for(int j=0;j<height;j++){
                r = ImageArray[i][j][1];
                g = ImageArray[i][j][2];
                b = ImageArray[i][j][3];
                if(r >= thresholdingValue){
                    r = 255;
                }else if (r < thresholdingValue){
                    r = 0;
                }
                if(g >= thresholdingValue){
                    g = 255;
                }else if (g < thresholdingValue){
                    g = 0;
                }
                if(b >= thresholdingValue){
                    b = 255;
                }else if (b < thresholdingValue){
                    b = 0;
                }

                ImageArray[i][j][1] = r;
                ImageArray[i][j][2] = g;
                ImageArray[i][j][3] = b;
            }
        return convertToBimage(ImageArray); 
    }

    public BufferedImage automatedThresholding(BufferedImage img)
    {
        int backgroundMeanRed = 0, backgroundMeanGreen = 0, backgroundMeanBlue = 0;
        int imageMeanRed = 0, imageMeanGreen = 0, imageMeanBlue = 0;
        int[][][] ImageArray = convertToArray(img); 
        int width = img.getWidth();
        int height = img.getHeight(); 
        int r = 0, g = 0, b = 0;
        for( int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                r =  ImageArray[i][j][1];
                g = ImageArray[i][j][2];
                b = ImageArray[i][j][3];
                if((i == 0 && j == 0) || (i == width-1 && j == 0) || (i == 0 && j == height-1) || (i ==width-1 && j == height-1))
                {
                    backgroundMeanRed += r;
                    backgroundMeanGreen += g;
                    backgroundMeanBlue += b;
                }else{
                    imageMeanRed += r;
                    imageMeanGreen += g;
                    imageMeanBlue += b;
                }
            }
        }
        backgroundMeanRed = backgroundMeanRed/4;
        backgroundMeanGreen = backgroundMeanGreen/4;
        backgroundMeanBlue = backgroundMeanBlue/4;
        imageMeanRed = imageMeanRed/((width*height)-4);
        imageMeanGreen = imageMeanGreen/((width*height)-4);
        imageMeanBlue = imageMeanBlue/((width*height)-4);
        int totalMeanRed = (backgroundMeanRed + imageMeanRed)/2;
        int totalMeanGreen = (backgroundMeanGreen + imageMeanGreen)/2;
        int totalMeanBlue = (backgroundMeanBlue + imageMeanBlue)/2;
        boolean redFlag = false, greenFlag = false, blueFlag = false;
        //Iteration to find proper threshold for red
        while(true){
            imageMeanRed = 0;
            backgroundMeanRed = 0;
            int imageCounterRed = 0;
            int backgroundCounterRed = 0;
            int tempTotalRed = totalMeanRed;
            imageMeanGreen = 0;
            backgroundMeanGreen = 0;
            int imageCounterGreen = 0;
            int backgroundCounterGreen = 0;
            int tempTotalGreen = totalMeanGreen;
            imageMeanBlue = 0;
            backgroundMeanBlue = 0;
            int imageCounterBlue = 0;
            int backgroundCounterBlue = 0;
            int tempTotalBlue = totalMeanBlue;
            for( int i=0;i<width;i++)
                for(int j=0;j<height;j++){
                    r = ImageArray[i][j][1];
                    g = ImageArray[i][j][2];
                    b = ImageArray[i][j][3];
                    if (!redFlag)
                    {
                        if(r >= tempTotalRed){
                            imageMeanRed += r;
                            imageCounterRed += 1;
                        }else if (r < tempTotalRed){
                            backgroundMeanRed += r;
                            backgroundCounterRed += 1;
                        }}
                    if (!greenFlag)
                    {
                        if(r >= tempTotalGreen){
                            imageMeanGreen += g;
                            imageCounterGreen += 1;
                        }else if (r < tempTotalGreen){
                            backgroundMeanGreen += g;
                            backgroundCounterGreen += 1;
                        }}
                    if (!blueFlag)
                    {
                        if(r >= tempTotalBlue){
                            imageMeanBlue += b;
                            imageCounterBlue += 1;
                        }else if (r < tempTotalBlue){
                            backgroundMeanBlue += b;
                            backgroundCounterBlue += 1;
                        }}
                }

            if (!redFlag)
            {
                if(backgroundMeanRed>0){backgroundMeanRed = backgroundMeanRed/backgroundCounterRed;}
                if(imageMeanRed>0){imageMeanRed = imageMeanRed/imageCounterRed;} 

                totalMeanRed = (backgroundMeanRed + imageMeanRed)/2; }

            if (!greenFlag)
            {
                if(backgroundMeanGreen>0){backgroundMeanGreen = backgroundMeanGreen/backgroundCounterGreen;}
                if(imageMeanGreen>0){imageMeanGreen = imageMeanGreen/imageCounterGreen;} 

                totalMeanGreen = (backgroundMeanGreen + imageMeanGreen)/2; }

            if (!blueFlag)
            {
                if(backgroundMeanBlue>0){backgroundMeanBlue = backgroundMeanBlue/backgroundCounterBlue;}
                if(imageMeanBlue>0){imageMeanBlue = imageMeanBlue/imageCounterBlue;} 

                totalMeanBlue = (backgroundMeanBlue + imageMeanBlue)/2; }
            if(Math.abs(totalMeanRed - tempTotalRed)<1){
                redFlag = true;        
            }     
            if(Math.abs(totalMeanRed - tempTotalRed)<1){
                greenFlag = true;        
            } 
            if(Math.abs(totalMeanRed - tempTotalRed)<1){
                blueFlag = true;        
            } 
            if (redFlag && greenFlag && blueFlag)
            {
                break;
            }
        }

        for( int i=0;i<width;i++)
            for(int j=0;j<height;j++){
                    r = ImageArray[i][j][1];
                g = ImageArray[i][j][2];
                b = ImageArray[i][j][3];
                if(r >= totalMeanRed){
                    r = 255;
                }else if (r < totalMeanRed){
                    r = 0;
                }
                if(g >= totalMeanGreen){
                    g = 255;
                }else if (g < totalMeanGreen){
                    g = 0;
                }
                if(b >= totalMeanBlue){
                    b = 255;
                }else if (b < totalMeanBlue){
                    b = 0;
                }

                ImageArray[i][j][1] = r;
                ImageArray[i][j][2] = g;
                ImageArray[i][j][3] = b;
            }
        return convertToBimage(ImageArray); 
    }

    private int rescaleValue( int value )
    {
        if (value > 255)
        {
            return 255;
        }
        else if (value< 0)
        {
            return 0;
        }
        return value;

    }

    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {

        // the list used to store histogram data
        java.util.List<Map<Integer,Integer>> histogram = new ArrayList<Map<Integer,Integer>>();

        // the list used to store normalised histogram data
        java.util.List<Map<Integer,Double>> normalisedHistogram = new ArrayList<Map<Integer,Double>>();
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
            return; 
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
            listUndo.add(biFiltered);
            return;
            case 2: biFiltered = ImageScale(bi); /* Image Scale by * value */
            listUndo.add(biFiltered);
            return;
            case 3: biFiltered = ImageShifting(bi); /* Image Shifting */
            listUndo.add(biFiltered);
            return;
            case 4: biFiltered = ImageAddRandomValue(bi);
            listUndo.add(biFiltered);
            return;
            case 5: biFiltered = reScaleBetween0And255(addTwoImages(bi));
            listUndo.add(biFiltered);
            return;
            case 6: biFiltered = reScaleBetween0And255(SubstractImage(bi));
            listUndo.add(biFiltered);
            return;
            case 7: biFiltered = reScaleBetween0And255(divideImage(bi));
            listUndo.add(biFiltered);
            return;
            case 8: biFiltered = reScaleBetween0And255(MultiplyImage(bi));
            listUndo.add(biFiltered);
            return;
            case 9: biFiltered = notImage(convertToArray(bi));
            listUndo.add(biFiltered);
            return;
            case 10: biFiltered = bitwiseAnd(bi);
            listUndo.add(biFiltered);
            return;
            case 11: biFiltered = bitwiseOr((bi));
            listUndo.add(biFiltered);
            return;
            case 12: biFiltered = bitwiseXor((bi));
            listUndo.add(biFiltered);
            return;
            case 13: biFiltered = RegionOfInterestNegative((regionOfInterestBitwiseAnd(RegionOfInterest(bi), bi)));
            listUndo.add(biFiltered);
            return;
            case 14: biFiltered = Logarithmic(bi);
            listUndo.add(biFiltered);
            return;
            case 15: biFiltered = reScaleBetween0And255(PowerLaw(bi));
            listUndo.add(biFiltered);
            return;
            case 16: biFiltered = LookUpTable(bi);
            listUndo.add(biFiltered);
            return;
            case 17: biFiltered = BitPlaneSlicing(bi);
            listUndo.add(biFiltered);
            return;
            case 18: biFiltered = findHistogram(bi);
            listUndo.add(biFiltered);
            return;
            case 19: biFiltered = (applyFilter(bi));
            listUndo.add(biFiltered);
            return;
            case 20: biFiltered = saltAndPepper(bi);
            listUndo.add(biFiltered);
            return;
            case 21: biFiltered = minFiltering(bi);
            listUndo.add(biFiltered);
            return;
            case 22: biFiltered = maxFiltering(bi);
            listUndo.add(biFiltered);
            return;
            case 23: biFiltered = midPointFiltering(bi);
            listUndo.add(biFiltered);
            return;
            case 24: biFiltered = medianFiltering(bi);
            listUndo.add(biFiltered);
            return;
            case 25: meanAndStandardDeviation(bi);
            return;
            case 26: biFiltered = simpleThresholding(bi);
            listUndo.add(biFiltered);
            return;
            case 27: biFiltered = automatedThresholding(bi);
            listUndo.add(biFiltered);
            return;

        }
    }

    public BufferedImage regionOfInterestBitwiseAnd(BufferedImage img, BufferedImage bi)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] ImageArray = convertToArray(img); 
        int[][][] ImageArray2 = convertToArray(bi); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {

                if (x > 150 && x < 200 && y > 30 && y < 80)
                {
                    //ImageArray2[x][y][0] = ImageArray[x][y][1];
                    ImageArray2[x][y][1] = (ImageArray[x][y][1] & ImageArray2[x][y][1]);
                    ImageArray2[x][y][2] = (ImageArray[x][y][2] & ImageArray2[x][y][2]);
                    ImageArray2[x][y][3] = (ImageArray[x][y][3] & ImageArray2[x][y][3]);

                }

            }
        }

        return convertToBimage(ImageArray2); 
    }

    public BufferedImage regionOfInterestCombination(BufferedImage img)
    {
        BufferedImage mask  = RegionOfInterest(bi);
        int width = img.getWidth();
        int height = img.getHeight();
        int[][][] ImageArray = convertToArray(mask); 
        int[][][] ImageArray2 = convertToArray(img); 
        int[][][] ImageArray3 = convertToArray(bi); 
        for(int y=0; y<height; y++)
        {
            for(int x =0; x<width; x++)
            {
                if (x > 150 && x < 200 && y > 30 && y < 80)
                {
                    //ImageArray2[x][y][0] = ImageArray[x][y][1];
                    ImageArray2[x][y][1] = (ImageArray[x][y][1] & ImageArray2[x][y][1]);
                    ImageArray2[x][y][2] = (ImageArray[x][y][2] & ImageArray2[x][y][2]);
                    ImageArray2[x][y][3] = (ImageArray[x][y][3] & ImageArray2[x][y][3]);

                }
                else
                {
                    ImageArray2[x][y][1] = ImageArray3[x][y][1];
                    ImageArray2[x][y][2] = ImageArray3[x][y][2];
                    ImageArray2[x][y][3] = ImageArray3[x][y][3];
                }
            }
        }

        return convertToBimage(ImageArray2); 
    }

    public void combineAction()
    {
        biFiltered = regionOfInterestCombination(listUndo.get(listUndo.size()-1));
        repaint();
    }

    public void undoAction()
    {

        if (listUndo.size()-1>0)
        {
            listUndo.remove(listUndo.size()-1);
            biFiltered = listUndo.get(listUndo.size()-1);

        }
        else
        {
            biFiltered = listUndo.get(0);
        }

        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        if (cb.getActionCommand().equals("SetFilter")) {
            setOpIndex(cb.getSelectedIndex());
            repaint();
        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }
    };

    public static void main() {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
        Demo de = new Demo();
        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        JButton combineButton = new JButton("Combine");
        combineButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    de.combineAction();
                }
            });
        panel.add(combineButton);
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    de.undoAction();
                }
            });
        panel.add(undoButton);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
