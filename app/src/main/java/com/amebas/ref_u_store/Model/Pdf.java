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
import java.io.FileOutputStream;

/**
 * Class to handle PDF files.
 */
public class Pdf
{
    /**
     * Closes a PDDocument.
     *
     * @param pdf  the document to close.
     */
    public static void close(PDDocument pdf)
    {
        try
        {
            pdf.close();
        }
        catch (java.io.IOException e)
        {
            Log.e("ERROR", e.getMessage());
        }
    }

    private File location;

    /**
     * Creates new pdf object from file.
     *
     * @param location  location of file.
     */
    public Pdf(File location)
    {
        this.location = location;
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
            return new Pdf(dest);
        }
        catch (java.io.IOException e)
        {
            Log.e("ERROR", "Save dest does not exist");
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
        Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
        PDRectangle rect = new PDRectangle(bitmap.getWidth(), bitmap.getHeight());
        PDPage page = new PDPage(rect);
        document.addPage(page);

        // Populate page with image.
        PDImageXObject image = PDImageXObject.createFromFile(img.getAbsolutePath(), document);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(image, 0, 0);
        contentStream.close();

        // Save changes
        document.save(dest);
        document.close();

        return new Pdf(dest);
    }

    /**
     * Removes a page from the document.
     *
     * @param context   the app context the method is being called in.
     * @param page_num  the page number of the page to delete, starting from 1.
     * @throws java.io.IOException if file path cannot be found.
     */
    public void removePage(Context context, int page_num) throws java.io.IOException
    {
        PDFBoxResourceLoader.init(context);
        PDDocument pdf = getPdf();
        pdf.removePage(page_num - 1);
        pdf.save(this.location);
        pdf.close();
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
        try
        {
            return PDDocument.load(location);
        }
        catch (java.io.IOException e)
        {
            Log.e("ERROR", e.getMessage());
            return new PDDocument();
        }
    }
}
