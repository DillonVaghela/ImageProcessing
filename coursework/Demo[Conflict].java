import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.*;

public class Demo extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
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

    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi, biFiltered, bi2;   // the input image saved as bi;//
    int w, h;

    public Demo() {
        String file = "BaboonRGB.bmp";
        try {
            bi = ImageIO.read(new File(file));
            bi2 = ImageIO.read(new File("BaboonRGB.bmp")); 
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            //System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (Exception e) {      // deal with the situation that th image has problem;/
            System.out.println("Image is raw");
            bi = displayRawImage(file);
            w = bi.getWidth(null);
            h = bi.getHeight(null);

            BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics big = bi2.getGraphics();
            big.drawImage(bi, 0, 0, null);
            biFiltered = bi = bi2;

        }
    }   

    public BufferedImage displayRawImage( String imageFilePath )
    {

        try
        {

            // get a reference to the .raw file
            File rawFile = new File( imageFilePath );

            // create a stream obj. to read in bytes from .raw file
            FileInputStream inputStream = new FileInputStream( imageFilePath );

            int i     = 0;
            int total = 0;
            int nRead = 0;

            /* initialise a new byte array to store each line ( 512 x 512 ) */
            byte[] buffer = new byte[ 512 ];

            /* initialise a new string array to store each line ( 512 x 512 ) */
            String[] stringImageData = new String[ 512 ];

            /* 512 x 512 image with 4 components --> alpha + RGB */
            int[][][] rawImgArray = new int[512][512][4];

            /* the buffered img to display */
            BufferedImage bufferedImage = new BufferedImage( 512 , 512 , BufferedImage.TYPE_INT_RGB );

            // the .read(buffer b) method fills buffer with data ( reads <= b.length of data into buffer )
            while( ( nRead = inputStream.read(buffer) ) != -1 )
            {

                //System.out.println( new String( buffer ) );

                stringImageData[i++] = Arrays.toString(buffer); 
                total = total + nRead;

            }

            return convertToBimage(( parseImageData( stringImageData )));

        }
        catch( Exception e )
        {

            e.printStackTrace();
            return null;
        }
    }
    //*********************************
    // this helper method takes a string[] (storing bytes represented as strings) 
    // and returns an int[][][] (storing integers those bytes represented from the argument's string[])
    //*********************************
    public static int[][][] parseImageData( String[] stringImageData )
    {

        int x = 0;
        int y = 0;
        int[][][] imgData = new int[512][512][4];

        // for-each line of string img data
        for( String strImageLine : stringImageData )
        {

            // convert the string[] --> int[]
            int[] intImageLine = parseStringArray( strImageLine );
            y =0;
            // for-each int in intImageLine
            for( int pixel : intImageLine )
            {

                //shit

                imgData [   y ] [x] [0] = 255;    //a
                // fuck you
                imgData[y][x][1] = pixel;  //r
                imgData[y][x][2] = pixel;  //g
                imgData[y][x][3] = pixel;  //b
                y = y + 1;
            }

            // increment x AND y to move a row down
            x = x + 1;

        }
        /*
        for(int a=0; a<512; a++)
        {
        for(int b=0; b<512; b++)
        {
        for(int c=0; c<4; c++)
        {

        System.out.println("at x:"+a+",y:"+b+",z:"+c+" --> "+imgData[a][b][c]);

        }
        }
        } */
        //         for (int[][] row:imgData)
        //         {
        //             for (int[] column:row)
        //             {
        //                 System.out.println(Arrays.toString(column));
        //             }
        //         }
        // return the img representation
        return imgData;

    }

    //*********************************
    // this helper method takes a string[] (storing integers represented as strings) 
    // and returns an int[] (storing integers those strings represented from the argument's string[])
    //*********************************
    /* for the 512 pixels (1 pixel == 1 byte) in row, return split line into 512 pixels stored in int[] */
    public static int[] parseStringArray( String line )
    {

        int[] toReturn = new int[ 512 ];

        // check if the line is in the correct format
        if( line.length() == 0 || line.charAt( 0 ) != '[' || line.charAt( line.length() - 1 ) != ']' )
        {

            return new int[]{ -1 };

        }

        // cut out the square brackets [] from the string
        String contents = line.substring( 1 , line.length() - 1 ).trim();

        // split the strings
        String[] nums = contents.split(", ");

        // for each integer, replace it with its ABSOLUTE integer counterpart
        for( int z = 0 ; z < nums.length ; z++ )
        {

            String temp = nums[z];
            toReturn[z] = Math.abs( Integer.parseInt( temp ) );

        }
        //printIntArray(toReturn);
        return toReturn;

    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
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
    public BufferedImage ImageScale(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int temp = 0;
        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = (int) (ImageArray[x][y][1] * 2.0f);
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
                temp = (int) (ImageArray[x][y][2] * 2.0f);
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
                temp = (int) (ImageArray[x][y][3] * 2.0f);

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

    public BufferedImage ImageShifting(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int temp = 0;
        // Image Negative Operation:
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

    public BufferedImage ImageAddRandomValue(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array
        int temp, randomValue = 0;
        // Image Negative Operation:
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

    public BufferedImage reScaleBetween0And255(int[][][] ImageArray){
        int width = ImageArray[1].length;
        int height = ImageArray[1].length;

        //  Convert the image to array
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int temp = ImageArray[x][y][1];
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
                int temp1 = ImageArray[x][y][2];

                if (temp1 <0)
                {
                    ImageArray[x][y][2] = 0;
                }
                else if (temp1 > 255)
                {
                    ImageArray[x][y][2] = 255;
                }
                else
                {
                    ImageArray[x][y][2] = temp1;
                }
                int temp2 = ImageArray[x][y][3];

                if (temp2 <0)
                {
                    ImageArray[x][y][3] = 0;
                }
                else if (temp2 > 255)
                {
                    ImageArray[x][y][3] = 255;
                }
                else
                {
                    ImageArray[x][y][3] = temp2;
                }
                if (temp > 256 || temp < 0 ){
                    System.out.println(temp + " " + temp1 + " " + temp2);
                    System.out.println(ImageArray[x][y][1] + " " + ImageArray[x][y][2] + " " + ImageArray[x][y][3]);
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

                try {
                    newImage[x][y][1] = ImageArray[x][y][1] / ImageArray2[x][y][1];
                    newImage[x][y][2] = ImageArray[x][y][2] / ImageArray2[x][y][2];
                    newImage[x][y][3] = ImageArray[x][y][3] / ImageArray2[x][y][3]; }
                catch (Exception e)
                {}

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
                newImage[x][y][1] = ImageArray[x][y][1] - ImageArray2[x][y][1];
                newImage[x][y][2] = ImageArray[x][y][2] - ImageArray2[x][y][2];
                newImage[x][y][3] = ImageArray[x][y][3] - ImageArray2[x][y][3];
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
        int[][][] ImageArray = convertToArray(timg); 
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
        double p = 0.75;
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
        int z = 3;
        int lengtth = 0;
        int[][][] ImageArray = convertToArray(timg); 
        //String rBinary = "";//  Convert the image to array
        // Image Negative Operation:

        int k = z;
        // /**
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

            }}//*/

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

        
        for(int j=0; j<256; j++) {
            
            redN[j] = ( red[j] / res);
            greenN[j] = ( green[j]/ res);
            blueN[j] = ( blue[j]/ res);
        }
        
        System.out.println("normHistogram" );
        System.out.println("red array ="+ Arrays.toString(redN)  );
        System.out.println("green array ="+ Arrays.toString(greenN)  );
        System.out.println("blue array ="+ Arrays.toString(blueN)  );


        double cumR = 0;
        double cumG = 0;
        double cumB = 0;
        
        double [] cumulativeHistogramR = new double[256];
        double [] cumulativeHistogramG = new double[256];
        double [] cumulativeHistogramB = new double[256];
        for (int i = 0; i < 256; i++) {
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

        //multiply cumulative by 255
        for (int i = 0; i < 256; i++) {
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
        //return convertToBimage(ImageArray);  
        return reScaleBetween0And255(ImageArray);
    }
    
    
   public void sort(java.util.List<Integer> pixel, java.util.List<Double> occurance)
   {
       int n = pixel.size();  
        int temp1 = -1;
        double temp2 = -1;
        
         for(int i=0; i < n; i++){  
                 for(int j=1; j <n-i; j++){  
                          if(pixel.get(j-1) > pixel.get(j)){  
                                 //swap elements  
                                 temp1 = pixel.get(j-1);  
                                 pixel.set(j-1, pixel.get(j));  
                                 pixel.set(j, temp1); 
                                 
                                 temp2 = occurance.get(j-1);  
                                 occurance.set(j-1, occurance.get(j));  
                                 occurance.set(j, temp2); 
                         }  
                          
                 }  
         }
   }
    
    public BufferedImage applyMask(double[][] Mask, BufferedImage timg)
    {
        int width = timg.getWidth();
        int height = timg.getHeight();
        int[][][] ImageArray = convertToArray(timg);
        int r = 0;
        int g = 0;
        int b = 0;
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
                    ImageArray[x][y][1] = r; //r 
                    ImageArray[x][y][2] = g; //g 
                    ImageArray[x][y][3] = b; //b
            }}
         return reScaleBetween0And255(ImageArray); 
    }

    public BufferedImage applyFilter(BufferedImage timg)
    {

        int opIndex = Integer.parseInt(JOptionPane.showInputDialog("pick filtering"));
        double [][]mask = new double [3][3];
        switch (opIndex) {
            case 0:
                for(int i=0;i<3;i++){
                    for(int j=0;j<3;j++){
                        mask[i][j]=(double)1/9;
                    }
                }
                return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
            return applyMask(mask, timg);
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
        for(int i=(int)(Math.random()*(width*height*0.05));i<width*height*0.05;i++){
            
    x =(int) (Math.abs(r.nextInt())%width);

    y =(int) (Math.abs(r.nextInt())%height);
    System.out.println(x + " " + y);
    ImageArray[x][y][1] = 255; //r 
    ImageArray[x][y][2] = 255; //g 
    ImageArray[x][y][3] = 255; //b

            }
        for(int i=(int)(Math.random()*(width*height*0.05));i<height*height*0.05;i++){
              
       x =(int) (Math.abs(r.nextInt())%width);

    y =(int) (Math.abs(r.nextInt())%height);
    ImageArray[x][y][1] = 0; //r 
    ImageArray[x][y][2] = 0; //g 
    ImageArray[x][y][3] = 0; //b
    
        }       
        return convertToBimage(ImageArray); 
    }
    
    public BufferedImage minFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
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
                    ImageArray[x][y][1] = rWindow[0]; //r 
                    ImageArray[x][y][2] = gWindow[0]; //g 
                    ImageArray[x][y][3] = bWindow[0]; //b
                }}
        return convertToBimage(ImageArray); 
    }
   
   
    public BufferedImage maxFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
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
                    ImageArray[x][y][1] = rWindow[8]; //r 
                    ImageArray[x][y][2] = gWindow[8]; //g 
                    ImageArray[x][y][3] = bWindow[8]; //b
                }}
        return convertToBimage(ImageArray); 
    }
    
    public BufferedImage midPointFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
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
                    ImageArray[x][y][1] = (rWindow[8] + rWindow[0])/2; //r 
                    ImageArray[x][y][2] = (gWindow[8] + gWindow[0])/2; //g 
                    ImageArray[x][y][3] = (bWindow[8] + bWindow[0])/2; //b
                }}
        return convertToBimage(ImageArray); 
    }
    
    public BufferedImage medianFiltering(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg);
        int width = timg.getWidth();
        int height = timg.getHeight();
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
                    ImageArray[x][y][1] = rWindow[4]; //r 
                    ImageArray[x][y][2] = gWindow[4]; //g 
                    ImageArray[x][y][3] = bWindow[4]; //b
                }}
        return convertToBimage(ImageArray); 
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
        int mean_r = 0;
        int mean_g = 0;
        int mean_b = 0;
        //find mean
	for (int i=0; i<256; i++){
		mean_r+= (i*redN[i]);
		mean_g+= (i*greenN[i]);
		mean_b+= (i*blueN[i]);	
	}
	System.out.println("Mean: red: "+ mean_r + " green: " +  mean_g + " blue: " + mean_b);
	//find standard deviation (sqrt of variance)
	int var_r = 0;
	int var_g = 0;
	int var_b = 0;
	int sd_r = 0;
	int sd_g = 0;
	int sd_b = 0;
	for (int i=0; i<256; i++){
		var_r+= Math.pow((i-mean_r),2)*redN[i];
		var_g+= Math.pow((i-mean_g),2)*greenN[i];
		var_b+= Math.pow((i-mean_b),2)*blueN[i];	
	}
	sd_r+= Math.sqrt(var_r);
	sd_g+= Math.sqrt(var_g) ;
	sd_b+= Math.sqrt(var_b) ;	
	System.out.println("Standard deviation is: red: "+ sd_r+ " green: " + sd_g + " blue: " + sd_b);
        
    }
    
    public BufferedImage simpleThresholding(BufferedImage timg)
    {
        int[][][] ImageArray = convertToArray(timg); 
        for( int i=startWidth;i<imgWidth;i++)
              			    for(int j=startHeight;j<imgHeight;j++){
					r = loadedImage->GetRed(i,j);
					g = loadedImage->GetGreen(i,j);
					b = loadedImage->GetBlue(i,j);
					if(r >= s_threshold){
						r = 255;
					}else if (r < s_threshold){
						r = 0;
					}
					if(g >= s_threshold){
						g = 255;
					}else if (g < s_threshold){
						g = 0;
					}
					if(b >= s_threshold){
						b = 255;
					}else if (b < s_threshold){
						b = 0;
					}
			
				 	loadedImage->SetRGB(i,j,r,g,b);
				    }
        return timg;
    }
    
    public BufferedImage automatedThresholding(BufferedImage timg)
    {
        return timg;
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
            return;
            case 2: biFiltered = ImageScale(bi); /* Image Scale by * value */
            return;
            case 3: biFiltered = ImageShifting(bi); /* Image Shifting */
            return;
            case 4: biFiltered = ImageAddRandomValue(bi);
            return;
            case 5: biFiltered = reScaleBetween0And255(addTwoImages(bi));
            return;
            case 6: biFiltered = reScaleBetween0And255(SubstractImage(bi));
            return;
            case 7: biFiltered = reScaleBetween0And255(divideImage(bi));
            return;
            case 8: biFiltered = reScaleBetween0And255(MultiplyImage(bi));
            return;
            case 9: biFiltered = notImage(convertToArray(bi));
            return;
            case 10: biFiltered = bitwiseAnd(bi);
            return;
            case 11: biFiltered = bitwiseOr((bi));
            return;
            case 12: biFiltered = bitwiseXor((bi));
            return;
            case 13: biFiltered = RegionOfInterestNegative((bitwiseAnd(RegionOfInterest(bi))));
            return;
            case 14: biFiltered = Logarithmic(bi);
            return;
            case 15: biFiltered = reScaleBetween0And255(PowerLaw(bi));
            return;
            case 16: biFiltered = LookUpTable(bi);
            return;
            case 17: biFiltered = BitPlaneSlicing(bi);
            return;
            case 18: biFiltered = findHistogram(bi);
            return;
            case 19: biFiltered = applyFilter(bi);
            return;
            case 20: biFiltered = saltAndPepper(bi);
            return;
            case 21: biFiltered = minFiltering(bi);
            return;
            case 22: biFiltered = maxFiltering(bi);
            return;
            case 23: biFiltered = midPointFiltering(bi);
            return;
            case 24: biFiltered = medianFiltering(bi);
            return;
            case 25: meanAndStandardDeviation(bi);
            return;
            case 26: biFiltered = simpleThresholding(bi);
            return;
            case 27: biFiltered = automatedThresholding(bi);
            return;
            

        }

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
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
