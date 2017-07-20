package PoiClasses;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import StructureClasses.Etudiant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


public class XSSFileExchanger {


    public XSSFileExchanger() {

    }

    public HashMap<String, HashMap<String, Object>> excelRead(String file) throws IOException {

        if(file!=null){
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = workbook.getSheetAt(0);

            HashMap<String, HashMap<String, Object>> returnHashMap = new HashMap<>();

            Iterator<Row> rowIterator = sheet.iterator();
            Row row;

            int i =0;
            while (rowIterator.hasNext()){
                row = rowIterator.next();
                HashMap<String, Object> atrebuts =new HashMap<>();

                Cell cell = row.getCell(0);
                atrebuts.put("nom", cell.getStringCellValue());
                cell = row.getCell(1);
                atrebuts.put("prenom", cell.getStringCellValue());
                cell = row.getCell(2);
                atrebuts.put("sexe", cell.getStringCellValue());

                atrebuts.put("email", row.getCell(0).getStringCellValue().charAt(0)+"."+row.getCell(1).getStringCellValue()+"@esi-sba.dz");

                returnHashMap.put("etudiant"+i, atrebuts);
                i++;

            }
            Alert dialogE = new Alert(Alert.AlertType.INFORMATION);
            dialogE.setTitle("Succés");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Importation de la list d'éudiants avec succés ");
            dialogE.showAndWait();

            return returnHashMap;

        }
        else
            throw new NullPointerException();


    }

    public void excelCreate(String directory, String fileName, TableView<Etudiant> tableView) throws IOException, NullPointerException {

        if(directory==null)
            throw new NullPointerException();
        else{

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Etudiants");

            ObservableList<Etudiant> etudiants = tableView.getItems();

            Row row = sheet.createRow(0);

            row.createCell(0).setCellValue("ID");
            row.createCell(1).setCellValue("NOM");
            row.createCell(2).setCellValue("PRENOM");
            row.createCell(3).setCellValue("SEXE");
            row.createCell(4).setCellValue("EMAIL");
            row.createCell(5).setCellValue("NB-ABSENCES");

            for (int i=1; i<etudiants.size(); i++) {

                row= sheet.createRow(i);
                row.createCell(0).setCellValue(etudiants.get(i-1).getId());
                row.createCell(1).setCellValue(etudiants.get(i-1).getNom());
                row.createCell(2).setCellValue(etudiants.get(i-1).getPrenom());
                if(etudiants.get(i).getSexe()=='H')
                    row.createCell(3).setCellValue("H");
                else
                    row.createCell(3).setCellValue("F");
                row.createCell(4).setCellValue(etudiants.get(i-1).getEmail());
                row.createCell(5).setCellValue(etudiants.get(i-1).getNbAbsences());

            }

            String title = ""+directory+"\\Etudiants "+fileName+".xlsx";
            FileOutputStream fos = new FileOutputStream(title);
            workbook.write(fos);
            fos.close();

            Alert dialogE = new Alert(Alert.AlertType.INFORMATION);
            dialogE.setTitle("Succés");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Exportation de la list d'éudiants avec succés sous un fichier \"xlsx\" du nom de EtudiantsExports ");
            dialogE.showAndWait();


        }
    }

    public void exportAbsences(String directory, String fileName, TableView<Etudiant> tableView) throws IOException {

        if(directory==null)
            throw new NullPointerException();
        else{

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Absences");

            ObservableList<Etudiant> etudiants = tableView.getItems();

            Row row = sheet.createRow(0);


            row.createCell(0).setCellValue("NOM");
            row.createCell(1).setCellValue("PRENOM");

            for (int i = 2; i <=tableView.getColumns().size() ; i++) {

                row.createCell(i).setCellValue(tableView.getColumns().get(i-1).getText());
            }

            for (int i=1; i<=etudiants.size(); i++) {

                row= sheet.createRow(i);
                row.createCell(0).setCellValue(etudiants.get(i-1).getNom());
                row.createCell(1).setCellValue(etudiants.get(i-1).getPrenom());
                for (int j = 2; j <= tableView.getColumns().size() ; j++) {

                    row.createCell(j).setCellValue(tableView.getColumns().get(j-1).getCellObservableValue(etudiants.get(i-1))
                            .getValue().toString());
                }

            }

            FileOutputStream fos = new FileOutputStream(directory+"\\Absences "+fileName+".xlsx");
            workbook.write(fos);
            fos.close();

            Alert dialogE = new Alert(Alert.AlertType.INFORMATION);
            dialogE.setTitle("Succés");
            dialogE.setHeaderText(null);
            dialogE.setContentText("Exportation de la fiche d'absences avec succés sous un fichier \"xlsx\" du nom de AbsencesExports ");
            dialogE.showAndWait();

        }
    }//TODO remouve pics and try
     /* public static Object getValueAt(TableView table, int column, int row) {
    return table.getColumns().get(column).getCellObservableValue(row).getValue();
}*/
}

