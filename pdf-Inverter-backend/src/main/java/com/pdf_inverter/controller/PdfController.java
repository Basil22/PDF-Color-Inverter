package com.pdf_inverter.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/invert")
public class PdfController {

    @PostMapping
    public ResponseEntity<byte[]> invertPdf(@RequestParam MultipartFile file) throws IOException {
        // Load the PDF file
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Process each page
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            // Render the page to an image
            BufferedImage image = pdfRenderer.renderImageWithDPI(page, 150); //72, 150, 300, 600

            // Invert the image
            BufferedImage invertedImage = invertColors(image);

            // Compress the inverted image and replace it in PDF
            PDPage pdPage = document.getPage(page);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(invertedImage, "jpg", baos); // Compress the image to JPEG
            PDImageXObject pdImage = JPEGFactory.createFromByteArray(document, baos.toByteArray());

            // Replace the page content with the compressed image
            PDPageContentStream contentStream = new PDPageContentStream(document, pdPage, PDPageContentStream.AppendMode.OVERWRITE, true);
            contentStream.drawImage(pdImage, 0, 0, pdPage.getMediaBox().getWidth(), pdPage.getMediaBox().getHeight());
            contentStream.close();
        }

        // Save the modified PDF to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        // Set headers and return the compressed PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            headers.setContentDispositionFormData("attachment", originalFilename.replace(".pdf", "-inverted.pdf"));
        } else {
            headers.setContentDispositionFormData("attachment", "inverted.pdf");
        }

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    // Utility function to invert the colors of an image
    private BufferedImage invertColors(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage invertedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the original color of the pixel
                Color originalColor = new Color(image.getRGB(x, y));

                // Invert the color
                Color invertedColor = new Color(
                    255 - originalColor.getRed(),
                    255 - originalColor.getGreen(),
                    255 - originalColor.getBlue());

                // Set the inverted color to the new image
                invertedImage.setRGB(x, y, invertedColor.getRGB());
            }
        }

        return invertedImage;
    }
}
