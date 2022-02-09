package portlet2TestResourceBundle.portlet;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.stream.Stream;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import portlet2TestResourceBundle.constants.Portlet2TestResourceBundlePortletKeys;

/**
 * @author carlos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=Portlet2TestResourceBundle",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + Portlet2TestResourceBundlePortletKeys.PORTLET2TESTRESOURCEBUNDLE,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class Portlet2TestResourceBundlePortlet extends MVCPortlet {
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {
		
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String []languageKeys={"portlet2testresourcebundle.caption","test"};
		System.out.println("Testing language keys:");
		Stream.of(languageKeys).forEach(k->System.out.println(String.format("   %s->%s",k,LanguageUtil.get(themeDisplay.getLocale(),k))));

		include(viewTemplate, renderRequest, renderResponse);
	}
}