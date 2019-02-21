package ajax;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ajax {

	@CrossOrigin(origins="")
	@RequestMapping("/callback")
	public String ajax(String callback,String ceshi) {
		
		return callback+"({name:'nihaoshuai"+ceshi+"'})";
	}
	@RequestMapping("/test")
	public String ajax() {
		
		return "OK";
	}
}
