module org.openmarkov.full {
	requires org.openmarkov.core;
	requires org.openmarkov.gui;
	requires org.openmarkov.inference.heuristic.canoandmoral;
	requires org.openmarkov.inference.heuristic.hybridelimination;
	requires org.openmarkov.inference.heuristic.minimalfillin;
	requires org.openmarkov.inference.heuristic.simpleelimination;
	requires org.openmarkov.inference.variableelimination;
	requires org.openmarkov.io.probmodelxml;
	requires org.openmarkov.io.database.elvira;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.poi.poi;
	requires org.jdom2;
	requires org.openmarkov.inference.decompositionintosymmetricdans;
	requires org.apache.poi.ooxml;
	requires org.openmarkov.inference.temporalevaluation;
	requires org.openmarkov.inference.huginpropagation;
}
