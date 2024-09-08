package example.models;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDate;
@Service
@SessionScope
public class SourceData {
  private Integer totalSum;        // Сумма кредитования
  private Double interestRate;     // Процентная ставка
  private Integer loanTerm;        // Срок кредитования
  private String message;          // Сообщение об ошибке
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate inDate;        // Дата оформления кредита
  public LocalDate getInDate() {
    return inDate;
  }
  public void setInDate(LocalDate inDate) {
    this.inDate = inDate;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public Integer getTotalSum() {
    return totalSum;
  }
  public void setTotalSum(Integer totalSum) {
    this.totalSum = totalSum;
  }
  public Double getInterestRate() {
    return interestRate;
  }
  public void setInterestRate(Double interestRate) {
    this.interestRate = interestRate;
  }
  public Integer getLoanTerm() {
    return loanTerm;
  }
  public void setLoanTerm(Integer loanTerm) {
    this.loanTerm = loanTerm;
  }
}
