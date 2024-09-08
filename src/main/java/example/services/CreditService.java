package example.services;
import example.models.Payment;
import example.models.SourceData;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Service
@RequestScope
public class CreditService {
  private final SourceData sourceData;
  private final List<Payment> pay;
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
    // pay.add(new Payment(1, "Январь", 1000,500, 50, 4500.00));
    int month = sourceData.getInDate()
      .getMonthValue();
    int year = sourceData.getInDate()
      .getYear();
    int curDays = sourceData.getInDate()
      .getDayOfMonth();

    YearMonth curMonth = YearMonth.of(year, month);
    int days = curMonth.lengthOfMonth() - curDays;
    System.out.println("Дней в сентябре: " + days);

    Double interest = sourceData.getInterestRate() / 365.0;

    // Добавляем 12 месяцев к inDate
    LocalDate datePlus12Months = sourceData.getInDate().plusMonths(12);

    // Вычисляем количество дней между inDate и datePlus12Months
    long daysBetween = ChronoUnit.DAYS.between(sourceData.getInDate(), datePlus12Months);

    for (int i = 0; i < sourceData.getLoanTerm(); i++) {


    }
    for (int i = 0; i < sourceData.getLoanTerm(); i++) {
      // private Integer number;                  // Номер значения
      //  private String month;                    // Месяц
      //  private Integer monthlyContribution;     // Ежемесячный взнос
      //  private Integer principalRepayment;      // Остаток на текущий месяц
      //  private Integer interestPayment;         // Проценты по кредиту
      //  private Double remainingBalance;         // Остаток Основного долга
      Payment payment = new Payment();
      payment.setNumber(i + 1);
      payment.setMonth(monthSelect[(month + i) % 12]);
      payment.setMonthlyContribution(100);
      payment.setPrincipalRepayment(100);
      payment.setInterestPayment(100);
      payment.setRemainingBalance(100.0);
      pay.add(payment);
    }
    return pay;
  }
  public List<Payment> getPay() {
    return pay;
  }
}
