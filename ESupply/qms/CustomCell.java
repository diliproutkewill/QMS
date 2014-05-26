import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;


public class CustomCell implements PdfPCellEvent {  
		public void cellLayout(PdfPCell cell, Rectangle rect,PdfContentByte[] canvas) {
		PdfContentByte cb = canvas[PdfPTable.BASECANVAS];//canvas[PdfPTable.LINECANVAS];
		cb.setLineDash(0.5f,2.5f,0);                              
		cb.stroke();         
		}

		
	}
