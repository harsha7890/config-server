package hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class MainController {

	@Value("${message}")
	private String message;
	
	@Value("${username}")
	private String username;
	
	@Value("${password}")
	private String password;
	
//	@Value("${db.url}")
//	private String dburl;

	
	 @RequestMapping("/showConfig")
	    @ResponseBody
	    public String showConfig() {
	        String configInfo = "message="+message+"\nusername=" + username +"\npassword=" + password+"db.url";
	        return configInfo;
	    }
}
