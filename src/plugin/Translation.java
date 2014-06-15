package plugin;

import jp.go.nict.langrid.service_1_2.translation.TranslationService;
import jp.go.nict.langrid.client.soap.SoapClientFactory;
import java.net.MalformedURLException;
import java.net.URL;

public class Translation {
	static public String execute(String word) throws Exception{
		String translated="";
		try{
			TranslationService service = new SoapClientFactory().create(
					TranslationService.class,
					new URL("http://langrid.org/service_manager/invoker/KyotoUJServer"),
					"b4training",
					"IshidaLab"
					);
			translated = service.translate("ja", "en", word);
		} catch(MalformedURLException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return translated;
	}
}
