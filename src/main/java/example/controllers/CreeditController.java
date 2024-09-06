package example.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CreeditController {
    @GetMapping("/")
    public String getCredit(){
       return "main";
    }
    @PostMapping("/")
    public String setCredit(Model model){
        model.addAttribute("showPressure",true);
        return "main";
    }
    @PostMapping("/result")
    public String setResult(){
        return "main";
    }
}
