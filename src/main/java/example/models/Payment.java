package example.models;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
public class Payment {
  private Integer number;                  // Номер значения
  private String month;                    // Месяц
  private Integer monthlyContribution;     // Ежемесячный взнос
  private Integer principalRepayment;      // Погашение основного долга
  private Integer interestPayment;         // Погашение процентов по кредиту
  private Integer remainingBalance;         // Остаток кредита
  public Integer getNumber() {
    return number;
  }
  public void setNumber(Integer number) {
    this.number = number;
  }
  public String getMonth() {
    return month;
  }
  public void setMonth(String month) {
    this.month = month;
  }
  public Integer getMonthlyContribution() {
    return monthlyContribution;
  }
  public void setMonthlyContribution(Integer monthlyContribution) {
    this.monthlyContribution = monthlyContribution;
  }
  public Integer getPrincipalRepayment() {
    return principalRepayment;
  }
  public void setPrincipalRepayment(Integer principalRepayment) {
    this.principalRepayment = principalRepayment;
  }
  public Integer getInterestPayment() {
    return interestPayment;
  }
  public void setInterestPayment(Integer interestPayment) {
    this.interestPayment = interestPayment;
  }
  public Integer getRemainingBalance() {
    return remainingBalance;
  }
  public void setRemainingBalance(Integer remainingBalance) {
    this.remainingBalance = remainingBalance;
  }
}
