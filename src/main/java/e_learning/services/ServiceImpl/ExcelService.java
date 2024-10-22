package e_learning.services.ServiceImpl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ExcelService {

    public ResponseEntity<byte[]> generateExcelTemplate() throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        System.out.println("Workbook and sheet created successfully.");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "First Name", "Last Name", "Email", "Matricule", "Password", "Phone Number",
                "Gender", // Added Gender column
                "Age", "CIN", "Role",
                "Type Formateur (for Formateurs)", "Cabinet Name (for Formateurs)", "Cabinet Num (for Formateurs)",
                "Unit Name (for Participants)", "Entreprise Name"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }
        System.out.println("Header row created successfully.");

        // Add validations
        System.out.println("Applying validations.");
        applyTextOnlyValidation(sheet, 1, 100, 0, 1); // First Name and Last Name
        applyEmailValidation(sheet, 1, 100, 2, 2); // Email
        applyNumericValidation(sheet, 1, 100, 3, 3); // Matricule
        applyPasswordValidation(sheet, 1, 100, 4, 4); // Password
        applyPhoneNumberValidation(sheet, 1, 100, 5, 5); // Phone Number
        applyGenderValidation(sheet, 1, 100, 6, 6); // Gender
        applyAgeValidation(sheet, 1, 100, 7, 7); // Age
        applyCINValidation(sheet, 1, 100, 8, 8); // CIN
        System.out.println("Basic validations applied successfully.");

        // Validation for Role (dropdown)
        String[] roles = new String[]{"FORMATEUR", "PARTICIPANT", "RESPONSABLE"};
        applyDropdownValidation(sheet, roles, 1, 100, 9, 9); // Role
        System.out.println("Role validation applied successfully.");

        // Additional validations
        applyDropdownValidation(sheet, new String[]{"INTERN", "EXTERN"}, 1, 100, 10, 10); // Type Formateur
        applyTextOnlyValidation(sheet, 1, 100, 11, 12); // Cabinet Name, Cabinet Num
        applyTextOnlyValidation(sheet, 1, 100, 13, 14); // Unit Name, EntrepriseName
        System.out.println("Additional validations applied successfully.");

        // Autosize columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        System.out.println("Columns auto-sized successfully.");

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            workbook.close();
            System.out.println("Workbook written to ByteArrayOutputStream successfully.");
        } catch (IOException e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // Log the stack trace
            throw e;
        }

        HttpHeaders headersResponse = new HttpHeaders();
        headersResponse.add("Content-Disposition", "attachment; filename=User_Template.xlsx");
        System.out.println("Returning generated Excel template as response.");
        return new ResponseEntity<>(out.toByteArray(), headersResponse, HttpStatus.OK);
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void applyDropdownValidation(Sheet sheet, String[] options, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Input", "Please select a valid option from the dropdown list.");
        sheet.addValidationData(validation);
    }

    private void applyTextOnlyValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createCustomConstraint("ISTEXT(INDIRECT(ADDRESS(ROW(),COLUMN())))");
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Input", "This field must contain text only.");
        sheet.addValidationData(validation);
    }

    private void applyEmailValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createCustomConstraint("ISNUMBER(FIND(\"@\",INDIRECT(ADDRESS(ROW(),COLUMN()))))");
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Email", "Please enter a valid email address in the format example@example.com.");
        sheet.addValidationData(validation);
    }

    private void applyNumericValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createNumericConstraint(
                DataValidationConstraint.ValidationType.INTEGER,
                DataValidationConstraint.OperatorType.BETWEEN,
                "1",
                "9999999999"
        );
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Number", "This field must contain numeric values only.");
        sheet.addValidationData(validation);
    }

    private void applyPasswordValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // Basic password length check (minimum 8 characters)
        String passwordLengthConstraint =
                "LEN(INDIRECT(ADDRESS(ROW(),COLUMN())))>=8";

        DataValidationConstraint constraint = validationHelper.createCustomConstraint(passwordLengthConstraint);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Password",
                "Password must be at least 8 characters long."
        );
        sheet.addValidationData(validation);
    }

    private void applyPhoneNumberValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        // Regular expression for phone numbers, allowing optional + sign and digits only
        String phoneNumberRegex =
                "AND(LEN(INDIRECT(ADDRESS(ROW(),COLUMN())))>=6, " +
                        "LEN(INDIRECT(ADDRESS(ROW(),COLUMN())))<=15, " +
                        "ISNUMBER(VALUE(SUBSTITUTE(INDIRECT(ADDRESS(ROW(),COLUMN())), \"+\", \"\"))) " +
                        ")";

        DataValidationConstraint constraint = validationHelper.createCustomConstraint(phoneNumberRegex);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Phone Number",
                "Please enter a valid phone number in E.164 format (e.g., +212603879460)."
        );
        sheet.addValidationData(validation);
    }

    private void applyAgeValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createNumericConstraint(
                DataValidationConstraint.ValidationType.INTEGER,
                DataValidationConstraint.OperatorType.BETWEEN,
                "1",
                "120"
        );
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Age", "Age must be a number between 1 and 120.");
        sheet.addValidationData(validation);
    }

    private void applyCINValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createCustomConstraint(
                "AND(LEN(INDIRECT(ADDRESS(ROW(),COLUMN())))>=4, LEN(INDIRECT(ADDRESS(ROW(),COLUMN())))<=9)"
        );
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);

        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid CIN", "CIN must be between 4 and 9 characters long.");

        sheet.addValidationData(validation);
    }

    /**
     * New method to apply Gender validation with options "Male" and "Female".
     */
    private void applyGenderValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        String[] genders = new String[]{"Male", "Female"};
        applyDropdownValidation(sheet, genders, firstRow, lastRow, firstCol, lastCol);
    }

    private void applyTextValidation(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = validationHelper.createTextLengthConstraint(
                DataValidationConstraint.OperatorType.BETWEEN, "1", "255"
        );
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.createErrorBox("Invalid Input", "This field must contain text only.");
        sheet.addValidationData(validation);
    }
}
