package com.cafe.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.JWT.JwtFilter;
import com.cafe.dao.BillRepo;
import com.cafe.entity.Bill;
import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;
import com.cafe.service.BillService;
import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import antlr.CharQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

	@Autowired
	private JwtFilter jwtFilter;
	@Autowired
	private BillRepo billRepo;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> reqMap) {

		try {
			String fileName;
			if (validateRequestMap(reqMap)) {
				if (reqMap.containsKey("isGenerate") && !(Boolean) reqMap.get("isGenerate")) {
					fileName = (String) reqMap.get("uuid");
				} else {

					fileName = CafeUtils.getUUID();
					reqMap.put("uuid", fileName);
					insertBill(reqMap);
				}

				String data = "Name: " + reqMap.get("name") + "\n" + "Contact Number: " + reqMap.get("mobile") + "\n"
						+ "Email: " + reqMap.get("email") + "\n" + "Payment Method: " + reqMap.get("paymentMethod");

				Document document = new Document();
				PdfWriter.getInstance(document,
						new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));
				document.open();
				setRectangleInPdf(document);

				Paragraph chunk = new Paragraph("Cafe By WD", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);

				Paragraph para = new Paragraph(data + "\n \n", getFont("Data"));
				document.add(para);

				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);

				JSONArray jsonArray = CafeUtils.getJSONArrayFromString((String) reqMap.get("productDetails"));
				for (int i = 0; i < jsonArray.length(); i++) {
					addRows(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
				}
				document.add(table);

				Paragraph footer = new Paragraph(
						"Total : " + reqMap.get("total") + "\n" + "Thank you ...Please Visit again..", getFont("Data"));
				document.add(footer);
				document.close();
				return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);

			}
			return CafeUtils.getResponseEntity("Required Data not found", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		log.info("inside addRows");

		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("catagory"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("total")));

	}

	private void addTableHeader(PdfPTable table) {
		log.info("inside table header");
		Stream.of("Name", "Catgory", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(BaseColor.YELLOW);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);

		});

	}

	private Font getFont(String type) {
		log.info("inside getfont");
		switch (type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
			headerFont.setStyle(Font.BOLD);
			return headerFont;

		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.BLACK);
			dataFont.setStyle(Font.BOLD);
			return dataFont;

		default:
			return new Font();
		}

	}

	private void setRectangleInPdf(Document document) throws DocumentException {
		log.info("Inside set reactangle in pdf");
		Rectangle rect = new Rectangle(577, 825, 18, 15);

	}

	private boolean validateRequestMap(Map<String, Object> reqMap) {
		log.info("nside validate....");
		return reqMap.containsKey("name") && reqMap.containsKey("mobile") && reqMap.containsKey("email")
				&& reqMap.containsKey("paymentMethod") && reqMap.containsKey("productDetails")
				&& reqMap.containsKey("total");
	}

	private void insertBill(Map<String, Object> reqMap) {

		System.out.println("inside insert bill....");
		try {
			Bill bill = new Bill();
			bill.setUuid((String) reqMap.get("uuid"));
			bill.setName((String) reqMap.get("name"));
			bill.setEmail((String) reqMap.get("email"));
			bill.setMobile((String) reqMap.get("mobile"));
			bill.setPaymentMethod((String) reqMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String) reqMap.get("total")));
			bill.setProductDetails((String) reqMap.get("productDetils"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());

			billRepo.save(bill);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public ResponseEntity<List<Bill>> getBills() {

		List<Bill> list = new ArrayList<>();
		if (jwtFilter.isAdmin()) {
			list = billRepo.getAllBills();
		} else {
			list = billRepo.getBillByUsername(jwtFilter.getCurrentUser());
		}

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> reqMap) {
		log.info("inside get pdf : requestMap {}", reqMap);

		try {
			byte[] byteArray = new byte[0];
			if (!reqMap.containsKey("uuid") && validateRequestMap(reqMap))
				return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
			String filePath = CafeConstants.STORE_LOCATION + "\\" + (String) reqMap.get("uuid") + ".pdf";

			if (CafeUtils.isFileExist(filePath)) {

				byteArray = getByteArray(filePath);

				return new ResponseEntity<>(byteArray, HttpStatus.OK);

			} else {

				reqMap.put("isGenerate", false);
				generateReport(reqMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] getByteArray(String filePath) throws Exception {
		File intialFile = new File(filePath);
		InputStream targetStream = new FileInputStream(intialFile);
		byte[] byteArray = IOUtils.toByteArray(targetStream);
		targetStream.close();
		return byteArray;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional<Bill> bill = billRepo.findById(id);
			if (!bill.isEmpty()) {
				billRepo.deleteById(id);
				return CafeUtils.getResponseEntity("Bill deleted successfully.", HttpStatus.OK);
			}
			return CafeUtils.getResponseEntity("Bill id deos not exist.", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
