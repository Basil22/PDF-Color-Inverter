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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/invert")
@CrossOrigin("http://localhost:4200")
public class PdfController {

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> invertPdf(@RequestParam MultipartFile file, @RequestParam(defaultValue = "150") int dpi) {
		if (!file.getContentType().equals("application/pdf")) {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only PDF files are supported.");
		}

		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page, dpi);
				BufferedImage invertedImage = invertColors(image);

				PDPage pdPage = document.getPage(page);
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					ImageIO.write(invertedImage, "jpg", baos);
					PDImageXObject pdImage = JPEGFactory.createFromByteArray(document, baos.toByteArray());

					try (PDPageContentStream contentStream = new PDPageContentStream(document, pdPage,
							PDPageContentStream.AppendMode.OVERWRITE, true)) {
						contentStream.drawImage(pdImage, 0, 0, pdPage.getMediaBox().getWidth(),
								pdPage.getMediaBox().getHeight());
					}
				}
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			document.save(outputStream);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_PDF);
			String originalFilename = file.getOriginalFilename();
			String outputFilename = originalFilename != null ? originalFilename.replace(".pdf", "-inverted.pdf")
					: "inverted.pdf";
			headers.setContentDispositionFormData("attachment", outputFilename);

			return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing PDF: " + e.getMessage());
		}
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
				Color invertedColor = new Color(255 - originalColor.getRed(), 255 - originalColor.getGreen(),
						255 - originalColor.getBlue());

				// Set the inverted color to the new image
				invertedImage.setRGB(x, y, invertedColor.getRGB());
			}
		}

		return invertedImage;
	}
}
