package sample.vendor;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import sample.util.Util;
import sample.vendor.app.App;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Report {
    JasperPrint jasperPrint;

    JasperPrint jasperPrintSotuv;

    public void reportShtrix(Map<String, Object> map, JRDataSource source, int copies) {

        File file = new File("Report/shtrix.jrxml");
        if (file.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(file);
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                jasperPrint = JasperFillManager.fillReport(jasperReport, map, source);
                Properties properties = new Properties();
                properties.load(new FileInputStream("Report/settings.prop"));
                String printer = properties.getProperty("CHECK_PRINTER");
                System.out.println(printer);
                if (!printer.trim().isEmpty()) {
                    shtrix(jasperPrint, printer, copies);
                }

            } catch (Exception e) {
                Util.alert("EROR",e.getMessage());
            }
        }
    }

    public void reportSotuv(Map<String, Object> map, JRDataSource source) {
        File file = new File("Report/Printer_chek.jrxml");
        if (file.exists()) {
            try {
                JasperDesign jasperDesign = JRXmlLoader.load(file);
                JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                jasperPrintSotuv = JasperFillManager.fillReport(jasperReport, map, source);
                Properties properties = new Properties();
                properties.load(new FileInputStream("Report/settings.prop"));
                String printer = properties.getProperty("SOTUV_CHECK_PRINTER");
                System.out.println(printer);
                if (!printer.trim().isEmpty()) {
                    chek(jasperPrintSotuv, printer);
                }

            } catch (Exception e) {
                Util.alert("EROR",e.getMessage());
            }
        }
    }

    private void showReport(JasperPrint jasperPrint) {
        JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
        jasperViewer.setVisible(true);
        PauseTransition pt = new PauseTransition(Duration.seconds(10));
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                jasperViewer.setVisible(false);
            }
        });
        pt.play();
    }

    public void showReport() {
        showReport(jasperPrint);
    }

    public void showSotuvReport() {
        showReport(jasperPrintSotuv);
    }


//    -----------------------------------------------------------------

    private static byte[] bytes = {27, 100, 3};
    private static byte[] cutP = new byte[]{0x1d, 'V', 1};

    private static void shtrix(JasperPrint jasperPrint, String selectedPrinter, int copies) {

        if (selectedPrinter == null) {
            return;
        }
        if (selectedPrinter.trim().isEmpty()) {
            return;
        }

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService selectedService = null;
        //set the printing settings
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A9);
        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 50, 40, MediaPrintableArea.MM));
        printRequestAttributeSet.add(new Copies(copies));

        if (jasperPrint.getOrientationValue() == OrientationEnum.PORTRAIT) {
            printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
        } else {
            printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
        }


        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(selectedPrinter, null));
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setConfiguration(configuration);


        if (services != null && services.length != 0) {
            for (PrintService service : services) {
                String existingPrinter = service.getName();
                if (existingPrinter.equals(selectedPrinter)) {
                    selectedService = service;
                    break;
                }
            }
        }

        if (selectedService != null) {
            try {
                exporter.exportReport();
            } catch (Exception e) {
//                Util.alert("",e.getMessage());
            }
        } else {
            Util.alert("", "JasperReport Error: Printer not found!!!");
        }
    }

    private static void chek(JasperPrint jasperPrint, String selectedPrinter) {

        if (selectedPrinter == null) {
            return;
        }
        if (selectedPrinter.trim().isEmpty()) {
            return;
        }

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService selectedService = null;
        //set the printing settings
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A7);
        printRequestAttributeSet.add(new Copies(1));

        if (jasperPrint.getOrientationValue() == OrientationEnum.LANDSCAPE) {
            printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
        } else {
            printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
        }

        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(new PrinterName(selectedPrinter, null));
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
        configuration.setPrintServiceAttributeSet(printServiceAttributeSet);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);


        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setConfiguration(configuration);


        if (services != null && services.length != 0) {
            for (PrintService service : services) {
                String existingPrinter = service.getName();
                if (existingPrinter.equals(selectedPrinter)) {
                    selectedService = service;
                    break;
                }
            }
        }

        if (selectedService != null) {
            try {
                exporter.exportReport();
                if (App.has("AUTO_CUT_CHECK", "1")) {
                    printBytes(selectedService, selectedPrinter, bytes);
                    printBytes(selectedService, selectedPrinter, cutP);
                }
            } catch (Exception e) {
                System.out.println("JasperReport Error: " + e.getMessage());
            }
        } else {
            Util.alert("", "JasperReport Error: Printer not found!!!");
        }
    }

    public static void printBytes(PrintService service, String printerName, byte[] bytes) {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;

        DocPrintJob job = service.createPrintJob();

        try {

            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCheck() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("Report/settings.prop"));
        } catch (IOException e) {
        }
        String printer = properties.getProperty("CHECK_PRINTER");
        if (!printer.trim().isEmpty()) {
            return printer.trim();
        }
        return null;
    }

}
