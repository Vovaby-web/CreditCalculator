package example.models;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class Payment {

  private Integer number;                  // Номер значения
  private String month;                    // Месяц
  private Integer monthlyContribution;     // Ежемесячный взнос
  private Integer principalRepayment;      // Остаток на текущий месяц
  private Integer interestPayment;         // Проценты по кредиту
  private Double remainingBalance;         // Остаток Основного долга
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
  public Double getRemainingBalance() {
    return remainingBalance;
  }
  public String getRemainingBalanceStr() {
      return String.format("%.2f",remainingBalance);
  }
  public void setRemainingBalance(Double remainingBalance) {
    this.remainingBalance = remainingBalance;
  }
}
