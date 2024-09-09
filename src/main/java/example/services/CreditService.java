package example.services;
import example.models.Payment;
import example.models.SourceData;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
// import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.Color;
@Service
@SessionScope
public class CreditService {
  private final SourceData sourceData;
  private List<Payment> pay;
  public CreditService(SourceData sourceData) {
    this.sourceData = sourceData;
    pay = new ArrayList<>();
  }
  public boolean exam(SourceData sourceData) {
    if (sourceData.getTotalSum() == null) {
      this.sourceData.setMessage("Отсутствует общая сумма кредитования");
      return false;
    } else if (sourceData.getInterestRate() == null) {
      this.sourceData.setMessage("Отсутствует процентная ставка в год");
      return false;
    } else if (sourceData.getLoanTerm() == null) {
      this.sourceData.setMessage("Отсутствует сроки кредитования");
      return false;
    } else if (sourceData.getInDate() == null) {
      this.sourceData.setMessage("Вы не выбрали дату одобрения кредита");
      return false;
    }
    return true;
  }
  public SourceData getSourceData() {
    return sourceData;
  }
  public SourceData setSource(SourceData source) {
    sourceData.setTotalSum(source.getTotalSum());
    sourceData.setInterestRate(source.getInterestRate());
    sourceData.setLoanTerm(source.getLoanTerm());
    // Присвоить текущую дату
    // sourceData.setInDate(LocalDate.now());
    sourceData.setInDate(source.getInDate());
    sourceData.setMessage("");
    return sourceData;
  }
  public List<Payment> resultData() {
    final String[] monthSelect = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь",
       "Октябрь", "Ноябрь", "Декабрь"};
    int month = sourceData.getInDate().getMonthValue();
    // Общая сумма кредита
    double totalSum = (double) sourceData.getTotalSum();
    // Используем формулу для расчета аннуитетного платежа.
    // Формула выглядит следующим образом:
    // [ M = \frac{P \cdot r \cdot (1 + r)^n}{(1 + r)^n - 1} ]
    // где:
    // ( M ) — ежемесячный платеж,
    // ( P ) — сумма кредита (3000 руб.),
    // ( r ) — месячная процентная ставка,
    // ( n ) — количество платежей (в месяцах, в данном случае 12).
    // Процентная ставка в годовой форме (15%) должна быть преобразована в месячную.
    // Для этого нужно разделить её на 12 и перевести в десятичную
    // Вычисляем месячную процентную ставку
    double monthlyInterestRate = ((double) sourceData.getInterestRate() / 100.0) / 12.0;
    // вычисляем минимальный ежемесячный платеж
    double monthlyPayment = (totalSum * monthlyInterestRate *
       Math.pow(1 + monthlyInterestRate, (double) sourceData.getLoanTerm())) /
       (Math.pow(1 + monthlyInterestRate, (double) sourceData.getLoanTerm()) - 1.0);
    /*
      // Добавляем месяцы к inDate
      LocalDate datePlusMonths = sourceData.getInDate().plusMonths(sourceData.getLoanTerm());
      // Вычисляем количество дней между inDate и datePlusMonths
      double daysBetween = ChronoUnit.DAYS.between(sourceData.getInDate(), datePlusMonths);
    */

    double[] principalRepayment = new double[sourceData.getLoanTerm()];
    double[] interestPayment = new double[sourceData.getLoanTerm()];
    double[] remainingBalance = new double[sourceData.getLoanTerm()];

    /*
       // Высчитываем сумму по процентам за первый месяц
       int month = sourceData.getInDate().getMonthValue();
       int year = sourceData.getInDate().getYear();
       int curDays = sourceData.getInDate().getDayOfMonth();
       YearMonth curMonth = YearMonth.of(year, month);
       // Остаток в текущем месяце
       int remDays = curMonth.lengthOfMonth() - curDays;
    */
    for (int i = 0; i < sourceData.getLoanTerm(); i++) {
      // Вычисляем сколько необходимо платить денег за проценты в текущем месяце
      interestPayment[i] = monthlyInterestRate * totalSum;
      // Вычисляем сколько приходится на основной долг
      principalRepayment[i] = monthlyPayment - interestPayment[i];
      // Вычисляем остаток долга на конец месяца
      totalSum -= principalRepayment[i];
      remainingBalance[i] = totalSum;
    }
    for (int i = 0; i < sourceData.getLoanTerm(); i++) {
      //  private Integer number;                  // Номер значения
      //  private String month;                    // Месяц
      //  private Integer monthlyContribution;     // Ежемесячный взнос
      //  private Integer principalRepayment;      // Погашение основного долга
      //  private Integer interestPayment;         // Погашение процентов по кредиту
      //  private Double remainingBalance;         // Остаток кредита
      Payment payment = new Payment();
      payment.setNumber(i + 1);
      payment.setMonth(monthSelect[(month + i) % 12]);
      payment.setPrincipalRepayment((int) principalRepayment[i]);
      payment.setInterestPayment((int) interestPayment[i]);

      if (i < sourceData.getLoanTerm() - 1)
        payment.setMonthlyContribution((int) monthlyPayment);
      else payment.setMonthlyContribution(payment.getPrincipalRepayment() +
         payment.getInterestPayment());

      payment.setRemainingBalance((int) remainingBalance[i]);
      pay.add(payment);
    }
    Payment payment = new Payment();
    payment.setMonth("Всего");
    int principal = Arrays.stream(principalRepayment).mapToInt(d -> (int) Math.round(d)).sum();
    payment.setPrincipalRepayment(principal);
    int interest = Arrays.stream(interestPayment).mapToInt(d -> (int) Math.round(d)).sum();
    payment.setInterestPayment(interest);
    int remaining = principal + interest;
    payment.setRemainingBalance(remaining);
    pay.add(payment);
    return pay;
  }
  public ResponseEntity<byte[]> exportXls() {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("CreditData");
    xlsData(workbook, sheet);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      workbook.write(outputStream);
      workbook.close();
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    byte[] bytes = outputStream.toByteArray();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=example.xlsx");
    return ResponseEntity.ok()
       .headers(headers)
       .contentType(MediaType.APPLICATION_OCTET_STREAM)
       .body(bytes);
  }
  private void xlsData(Workbook workbook, Sheet sheet) {
    CellStyle styleBold = setStyle(workbook, sheet,true);

    // Рисуем шапку
    tableHeader(sheet, styleBold);

    CellStyle styleNormal = setStyle(workbook, sheet,false);
    // Выводим данные
    for (int i = 0; i < pay.size() - 1; i++) {
      addRow(sheet, styleNormal, i+2, pay.get(i));
    }
    // Добавляем жирный шрифт
    sheet.addMergedRegion(new CellRangeAddress(2+(pay.size()-1), 2+(pay.size()-1),
       0, 2));
    addResult(sheet, styleBold, 2+pay.size()-1,pay.get(pay.size()-1));
  }
  // Задаем стиль ячейки
  private CellStyle setStyle(Workbook workbook, Sheet sheet,boolean status) {
    CellStyle style = workbook.createCellStyle();
    // В стиле рисуем рамку
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    // Включаем перенос текста
    style.setWrapText(true);
    // Выравнивание по горизонтали (по центру)
    style.setAlignment(HorizontalAlignment.CENTER);
    // Выравнивание по вертикали (по центру)
    style.setVerticalAlignment(VerticalAlignment.CENTER);

    // Установка цвета фона
    // Белый цвет
    Color rgb = new Color(255, 255, 255);
    if (status)
      rgb = new Color(98, 204, 255);
    XSSFColor color = new XSSFColor(rgb, null);
    style.setFillForegroundColor(color);
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    // Добавляем жирный шрифт
    Font font = workbook.createFont();
    font.setFontHeightInPoints((short) 14);
    font.setBold(status);
    // Применение шрифта к стилю ячейки
    style.setFont(font);
    return style;
  }
  private void tableHeader(Sheet sheet, CellStyle style) {
    // Объединение ячеек
    // Здесь `CellRangeAddress` принимает четыре параметра:
    // начальный индекс строки, конечный индекс строки, начальный индекс столбца и конечный индекс столбца.
    // В данном случае это будет объединение ячеек A1 (индекс 0,0) и B1 (индекс 0,1).
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 4));
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
    sheet.addMergedRegion(new CellRangeAddress(0, 1, 5, 5));

    // Устанавливаем ширину первого столбца (индекс 0) в 20 символов.
    // Обратите внимание, что ширина задается в единицах 1/256 от ширины символа.
    sheet.setColumnWidth(0, 6 * 256);
    sheet.setColumnWidth(1, 16 * 256);
    sheet.setColumnWidth(2, 22 * 256);
    sheet.setColumnWidth(3, 18 * 256);
    sheet.setColumnWidth(4, 24 * 256);
    sheet.setColumnWidth(5, 20 * 256);

    Row row = sheet.createRow(0);  // Создаёт первую строку (индекс 0)
    Cell cell = row.createCell(0); // Создаёт первую ячейку в первой строке
    cell.setCellStyle(style);         // Присваиваем стиль ячейке
    cell.setCellValue("№");           // Присваивает значение ячейке A1.
    cell = row.createCell(1);
    cell.setCellStyle(style);
    cell.setCellValue("Месяц");
    cell = row.createCell(2);
    cell.setCellStyle(style);
    cell.setCellValue("Минимальный ежемесячный взнос");
    cell = row.createCell(3);
    cell.setCellStyle(style);
    cell.setCellValue("Погашение");
    cell = row.createCell(4);
    cell.setCellStyle(style);
    cell = row.createCell(5);
    cell.setCellStyle(style);
    cell.setCellValue("Остаток на текущий месяц");
    row = sheet.createRow(1);
    cell = row.createCell(0);
    cell.setCellStyle(style);
    cell = row.createCell(1);
    cell.setCellStyle(style);
    cell = row.createCell(2);
    cell.setCellStyle(style);
    cell = row.createCell(3);
    cell.setCellStyle(style);
    cell.setCellValue("Основного долга");
    cell = row.createCell(4);
    cell.setCellStyle(style);
    cell.setCellValue("Процентов по кредиту");
    cell = row.createCell(5);
    cell.setCellStyle(style);
  }
  private void addRow(Sheet sheet, CellStyle style, int num, Payment payment) {
    //  private Integer number;                  // Номер значения
    //  private String month;                    // Месяц
    //  private Integer monthlyContribution;     // Ежемесячный взнос
    //  private Integer principalRepayment;      // Погашение основного долга
    //  private Integer interestPayment;         // Погашение процентов по кредиту
    //  private Double remainingBalance;         // Остаток кредита
    Row row = sheet.createRow(num);
    Cell cell = row.createCell(0);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getNumber());

    cell = row.createCell(1);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getMonth());

    cell = row.createCell(2);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getMonthlyContribution());

    cell = row.createCell(3);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getPrincipalRepayment());

    cell = row.createCell(4);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getInterestPayment());

    cell = row.createCell(5);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getRemainingBalance());

   /*
    if (par instanceof String) {
      cell.setCellValue((String) par);
    } else if (par instanceof Number) { // Это охватывает Integer, Double и другие
      cell.setCellValue(((Number) par).doubleValue()); // Преобразуем в double
    } else if (par instanceof Boolean) {
      cell.setCellValue((Boolean) par);
    }
  */
  }
  private void addResult(Sheet sheet, CellStyle style, int num, Payment payment) {
    Row row = sheet.createRow(num);
    Cell cell = row.createCell(0);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getMonth());
    cell = row.createCell(1);
    cell.setCellStyle(style);
    cell = row.createCell(2);
    cell.setCellStyle(style);

    cell = row.createCell(3);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getPrincipalRepayment());

    cell = row.createCell(4);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getInterestPayment());

    cell = row.createCell(5);
    cell.setCellStyle(style);
    cell.setCellValue(payment.getRemainingBalance());
  }
}



