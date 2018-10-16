package com.amebas.ref_u_store.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tom_roush.pdfbox.multipdf.PDFMergerUtility;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Class to handle PDF files.
 */
public class Pdf {

    private File location;
    private PDDocument pdf;

    /**
     * Creates a new pdf object.
     *
     * @param location  the location of the file.
     * @param pdf       the PDDocument representing the pdf file.
     */
    public Pdf(File location, PDDocument pdf)
    {
        this.location = location;
        this.pdf = pdf;
    }

    /**
     * Creates new pdf object from file.
     *
     * @param location  location of file.
     */
    public Pdf(File location)
    {
        this.location = location;
        try
        {
            this.pdf = PDDocument.load(location);
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "File not found");
            this.pdf = new PDDocument();
        }
    }

    /**
     * Appends pdf files together
     *
     * @param context  context method is being called in.
     * @param pdf1     pdf to append to.
     * @param pdf2     pdf to append.
     * @param dest     destination path to save new pdf.
     * @return merged pdf instance.
     */
    static public Pdf append(Context context, Pdf pdf1, Pdf pdf2, File dest)
    {
        PDFBoxResourceLoader.init(context);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(dest.getAbsolutePath());
        try
        {
            merger.addSource(pdf1.getLocation());
            merger.addSource(pdf2.getLocation());
            merger.mergeDocuments(true);
            return new Pdf(dest, PDDocument.load(dest));
        }
        catch (java.io.IOException e)
        {
            Log.d("ERROR", "Save dest does not exist");
        }
        return null;
    }

    /**
     * Creates a pdf instance from an image file.
     *
     * @param context  the context in app method is being called in.
     * @param img      the image file to read in.
     * @param dest     the destination path to save the pdf document.
     * @throws java.io.IOException if image file doesn't exist.
     * @return a pdf document instance.
     */
    public static Pdf fromImage(Context context, File img, File dest) throws java.io.IOException
    {
        PDFBoxResourceLoader.init(context);
        PDDocument document = new PDDocument();

        // Create page to place image in.
        InputStream in = new FileInputStream(img);
        Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
        File temp = File.createTempFile("temp", ".png", context.getCacheDir());
        FileOutputStream out = new FileOutputStream(temp);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); //100-best quality
        out.close();
        PDRectangle rect = new PDRectangle(bitmap.getWidth(), bitmap.getHeight());
        PDPage page = new PDPage(rect);
        document.addPage(page);

        // Populate page with image.
        PDImageXObject image = PDImageXObject.createFromFile(temp.getAbsolutePath(), document);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(image, 0, 0);
        contentStream.close();
        in.close();

        // Save changes
        document.save(dest);
        document.close();
        temp.delete();

        return new Pdf(dest, document);
    }

    /**
     * Gets file location.
     *
     * @return file location.
     */
    public File getLocation() {
        return location;
    }

    /**
     * Sets file location.
     *
     * @param location file location.
     */
    public void setLocation(File location) {
        this.location = location;
    }

    /**
     * Gets pdf instance.
     *
     * @return  pdf instance.
     */
    public PDDocument getPdf() {
        return pdf;
    }

    /**
     * Sets pdf instance.
     * @param pdf  pdf instance.
     */
    public void setPdf(PDDocument pdf) {
        this.pdf = pdf;
    }
}
