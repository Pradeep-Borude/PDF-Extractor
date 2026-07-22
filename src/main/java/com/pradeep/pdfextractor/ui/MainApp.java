package com.pradeep.pdfextractor.ui;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.pradeep.pdfextractor.excel.BinMasterReader;
import com.pradeep.pdfextractor.excel.ExcelExporter;
import com.pradeep.pdfextractor.model.ProcessingSummary;
import com.pradeep.pdfextractor.service.BatchProcessingService;
import com.pradeep.pdfextractor.service.DuplicateManager;
import com.pradeep.pdfextractor.service.InvoiceProcessingService;
import com.pradeep.pdfextractor.util.ConfigManager;


public class MainApp extends Application {


    private static final String PROCESSED_FILE_NAME =
            "processed-invoices.txt";

    private static final String CONFIG_FILE_NAME =
            "config.properties";

    /**
     * Resolves a file name to a per-user writable app-data directory
     * (%LOCALAPPDATA%\DFInvoiceExtractor on Windows) instead of the
     * app's install directory, which is read-only for normal users
     * once installed via jpackage into Program Files.
     */
    private static Path appDataFile(String fileName) {

        String localAppData = System.getenv("LOCALAPPDATA");

        Path dir = (localAppData != null)
                ? Path.of(localAppData, "DFInvoiceExtractor")
                : Path.of(System.getProperty("user.home"), ".dfinvoiceextractor");

        return dir.resolve(fileName);

    }


    private final TextField pdfInputField = new TextField();

    private final TextField binMasterField = new TextField();

    private final TextField outputExcelField = new TextField();


    private final Button processButton =
            new Button("Process");


    private final ProgressBar progressBar =
            new ProgressBar(0);


    private final Label statusLabel =
        new Label("Ready.");

{
    statusLabel.getStyleClass()
            .add("status");
}


    private final List<File> selectedPdfFiles =
            new ArrayList<>();


    private ConfigManager configManager;



    @Override
    public void start(Stage stage) {


        configManager =
                new ConfigManager(appDataFile(CONFIG_FILE_NAME));


        binMasterField.setText(
                configManager.getBinMaster());


        outputExcelField.setText(
                configManager.getOutputExcel());



        GridPane grid = new GridPane();

        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(12);



        addPdfRow(grid,0);

        addNormalRow(
                grid,
                1,
                "BinMaster.xlsx",
                binMasterField,
                false);


        addNormalRow(
                grid,
                2,
                "Output.xlsx",
                outputExcelField,
                false);



        processButton.setMaxWidth(Double.MAX_VALUE);

        processButton.setOnAction(e ->
                process());



        progressBar.setMaxWidth(Double.MAX_VALUE);

        statusLabel.setWrapText(true);



        VBox root =
                new VBox(
                        15,
                        grid,
                        processButton,
                        progressBar,
                        statusLabel);



        root.setPadding(
                new Insets(20));



    Scene scene =
        new Scene(
                root,
                700,
                350);


scene.getStylesheets()
        .add(
        getClass()
        .getResource( "/com/pradeep/pdfextractor/css/style.css")
        .toExternalForm());



        stage.setTitle(
                "DFPL Invoice PDF Extractor");


        stage.setScene(scene);

        stage.show();

    }





    private void addPdfRow(GridPane grid,int row){


        Label label =
                new Label("PDF Input");


        pdfInputField.setPromptText(
                "Drop PDFs here or select folder");


        enablePdfDrop();
        enablePdfPaste();


        Button browse =
                new Button("Browse");


        browse.setOnAction(e ->
                browsePdf());



        grid.add(label,0,row);

        grid.add(pdfInputField,1,row);

        grid.add(browse,2,row);



        GridPane.setHgrow(
                pdfInputField,
                Priority.ALWAYS);

    }






    private void addNormalRow(
            GridPane grid,
            int row,
            String text,
            TextField field,
            boolean folder){



        Label label =
                new Label(text);



        Button browse =
                new Button("Browse");



        browse.setOnAction(e -> {


            FileChooser chooser =
                    new FileChooser();


            chooser.getExtensionFilters()
                    .add(
                    new FileChooser.ExtensionFilter(
                    "Excel Files",
                    "*.xlsx"));



            File file =
                    chooser.showOpenDialog(
                    processButton.getScene()
                    .getWindow());



            if(file!=null){

                field.setText(
                        file.getAbsolutePath());

                if (field == binMasterField) {
                    configManager.setBinMaster(file.getAbsolutePath());
                } else if (field == outputExcelField) {
                    configManager.setOutputExcel(file.getAbsolutePath());
                }


            }


        });



        grid.add(label,0,row);

        grid.add(field,1,row);

        grid.add(browse,2,row);


        GridPane.setHgrow(
                field,
                Priority.ALWAYS);

    }

private void enablePdfPaste() {

    pdfInputField.setOnKeyPressed(event -> {

        if (event.isControlDown()
                && event.getCode().toString().equals("V")) {


            Clipboard clipboard =
                    Clipboard.getSystemClipboard();


            if (clipboard.hasFiles()) {


                selectedPdfFiles.clear();


                for (File file : clipboard.getFiles()) {


                    if (file.isFile()
                            && file.getName()
                            .toLowerCase()
                            .endsWith(".pdf")) {


                        selectedPdfFiles.add(file);

                    }

                }


                if (!selectedPdfFiles.isEmpty()) {


                    pdfInputField.setText(
                        selectedPdfFiles.size()
                        + " PDF files selected"
                    );

                }

            }

        }

    });

}





    private void enablePdfDrop(){


        pdfInputField.setOnDragOver(event->{


            Dragboard db =
                    event.getDragboard();


            if(db.hasFiles()){

                event.acceptTransferModes(
                        TransferMode.COPY);

            }


            event.consume();


        });



        pdfInputField.setOnDragDropped(event->{


            Dragboard db =
                    event.getDragboard();



            selectedPdfFiles.clear();



            if(db.hasFiles()){


                for(File file:db.getFiles()){


                    if(file.isFile()
                    &&
                    file.getName()
                    .toLowerCase()
                    .endsWith(".pdf")){


                        selectedPdfFiles.add(file);

                    }

                }


            }



            if(!selectedPdfFiles.isEmpty()){


                pdfInputField.setText(
                    selectedPdfFiles.size()
                    +" PDF files selected");


            }



            event.setDropCompleted(
                    !selectedPdfFiles.isEmpty());


            event.consume();


        });

    }







    private void browsePdf(){



        FileChooser chooser =
                new FileChooser();



        chooser.setTitle(
                "Select PDF Files");


        chooser.getExtensionFilters()
                .add(
                new FileChooser.ExtensionFilter(
                "PDF Files",
                "*.pdf"));



        List<File> files =
                chooser.showOpenMultipleDialog(
                processButton.getScene()
                .getWindow());



        if(files!=null
        &&
        !files.isEmpty()){


            selectedPdfFiles.clear();

            selectedPdfFiles.addAll(files);


            pdfInputField.setText(
                    files.size()
                    +" PDF files selected");

        }

    }







    private void process(){



        if(binMasterField.getText().isBlank()
        ||
        outputExcelField.getText().isBlank()){


            alert(
            AlertType.WARNING,
            "Missing",
            "Select BinMaster and Output Excel");


            return;

        }





        File binFile =
                new File(
                binMasterField.getText());


        File output =
                new File(
                outputExcelField.getText());



        if(selectedPdfFiles.isEmpty()){


            alert(
            AlertType.WARNING,
            "No PDFs",
            "Drop PDF files first");


            return;

        }





        processButton.setDisable(true);

        progressBar.setProgress(
                ProgressBar.INDETERMINATE_PROGRESS);



        Task<ProcessingSummary> task =
                new Task<>(){


            @Override
            protected ProcessingSummary call()
                    throws Exception {



                BinMasterReader reader =
                        new BinMasterReader(binFile);



                InvoiceProcessingService invoiceService =
                        new InvoiceProcessingService(reader);



                DuplicateManager duplicate =
                        new DuplicateManager(
                        appDataFile(PROCESSED_FILE_NAME));



                ExcelExporter exporter =
                        new ExcelExporter();



                BatchProcessingService service =
                        new BatchProcessingService(
                        invoiceService,
                        duplicate,
                        exporter);



                return service.processFiles(
                        selectedPdfFiles,
                        output);


            }

        };





        task.setOnSucceeded(e->{


            processButton.setDisable(false);

            progressBar.setProgress(1);


            ProcessingSummary s =
                    task.getValue();


            statusLabel.setText(
                    "Completed : "
                    +s.getProcessed());


            showSummary(s);


        });




        task.setOnFailed(e->{


            processButton.setDisable(false);


            Throwable ex =
                    task.getException();


            alert(
            AlertType.ERROR,
            "Failed",
            ex.getMessage());


        });




        new Thread(task).start();


    }






    private void showSummary(
            ProcessingSummary s){


        Alert alert =
                new Alert(
                AlertType.INFORMATION);



        alert.setTitle(
                "Completed");


        alert.setContentText(
                "Total : "
                +s.getTotalPdfs()
                +"\nProcessed : "
                +s.getProcessed()
                +"\nDuplicates : "
                +s.getDuplicates()
                +"\nFailed : "
                +s.getFailed());



        alert.showAndWait();

    }






    private void alert(
            AlertType type,
            String title,
            String msg){


        Alert a =
                new Alert(type);


        a.setTitle(title);

        a.setHeaderText(null);

        a.setContentText(msg);

        a.showAndWait();

    }





    public static void main(String[] args){

        launch(args);

    }

}