open module org.openmarkov.full {
	requires org.openmarkov.core;
	requires org.openmarkov.gui;
	requires org.openmarkov.inference;
	requires org.openmarkov.io;
	requires org.openmarkov.io.database.elvira;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.poi.poi;
	requires org.jdom2;
	requires org.apache.poi.ooxml;
	
	exports org.openmarkov.full;
}
