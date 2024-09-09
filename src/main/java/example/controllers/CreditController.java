package example.controllers;
import example.models.SourceData;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import example.services.CreditService;

@Controller
public class CreditController {
  private final CreditService creditService;
  public CreditController(CreditService creditService) {
    this.creditService = creditService;
  }
  @GetMapping("/")
  public String getCredit(Model model) {
    model.addAttribute("source", creditService.getSourceData());
    return "main";
  }
  @PostMapping("/")
  public String setCredit(@ModelAttribute SourceData sourceData, Model model) {
    creditService.payNull();
    creditService.setSource(sourceData);
    if (creditService.exam(sourceData)) {
      model.addAttribute("payments", creditService.resultData());
      model.addAttribute("showPressure", true);
    }
    model.addAttribute("source", sourceData);
    return "main";
  }
  @PostMapping("/export")
  public ResponseEntity<byte[]> exportData() {
    return creditService.exportXls();
  }
}
