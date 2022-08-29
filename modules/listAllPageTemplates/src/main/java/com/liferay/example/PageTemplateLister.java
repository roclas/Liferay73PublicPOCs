/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.layout.page.template.importer.LayoutPageTemplatesImporter;
import com.liferay.layout.page.template.importer.LayoutPageTemplatesImporterResultEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;

import java.io.File;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;


@Component(
	immediate = true,
	property = { },
	service = PageTemplateLister.class
)
public class PageTemplateLister {
	
	@Activate
	@Modified
	public void activate(BundleContext bundleContext) {
		//collectionService.getLayoutPageTemplateCollections(0,0,0,(a,b)->b.getModifiedDate().compareTo(a.getModifiedDate()));
		
		long groupId = 20121;
		

		/*
		collectionService.getLayoutPageTemplateCollections(0,Integer.MAX_VALUE) .forEach(c->{
			System.out.println(String.format("Listing entries for collection %s:",c.getName())); 
			entryService.getLayoutPageTemplateEntries(c.getGroupId(), c.getPrimaryKey()).forEach(e->{
				System.out.println(String.format(" collection %s => page template %s ( %s, %s )", 
						c.getName(),e.getName(),c.getPrimaryKey(),e.getPrimaryKey()));
			});
		});
		*/
		//entryService.fetchLayoutPageTemplateEntry(groupId, name, templateType);
		//entryService.addLayoutPageTemplateEntry(userId, groupId, layoutPageTemplateCollectionId, classNameId, classTypeId, name, type, masterLayoutPlid, status, serviceContext);

		long companyId =0;
		long collectionId=50442;
		try {
			companyId =CompanyLocalServiceUtil.getCompanies().get(0).getCompanyId();
		}catch(Exception e) {
			_log.error("No company id!");
			return;
		}
		_log.info(String.format("company id=%s",companyId));
		System.out.println(String.format("company id=%s",companyId));
		User testUser = userService.fetchUserByEmailAddress(companyId, "test@liferay.com");
		PermissionChecker permissionOriginalChecker = PermissionThreadLocal.getPermissionChecker();

		ServiceContext serviceContext = new ServiceContext();
		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		if(testUser==null) {
			_log.error("No test user?!!!");
			System.out.println("No test user?!!!");
			return;
		}

		String principalOriginalName = PrincipalThreadLocal.getName();
		PrincipalThreadLocal.setName(testUser.getUserId());
		serviceContext.setUserId(testUser.getUserId());
		ServiceContextThreadLocal.pushServiceContext( serviceContext );
		_log.info("test user id="+testUser.getUserId());
		System.out.println("test user id="+testUser.getUserId());
		
		CompanyThreadLocal.setCompanyId(companyId);
		PermissionThreadLocal.setPermissionChecker( _liberalPermissionCheckerFactory.create(testUser));

		try {
			File file=new File("/home/myuser/projects/myproject/automation/page2.zip");
			List<LayoutPageTemplatesImporterResultEntry> result = importer.importFile(testUser.getUserId(), groupId, collectionId, file, true);
			System.out.println("File's path = "+file.getAbsolutePath());
			System.out.println("File's space = "+file.getTotalSpace());
			result.forEach(el->System.out.println("page template =>"+el.getName()));
		} catch (Throwable e1) {
			System.out.println("horrible exception!!!");
			e1.printStackTrace();
		}
		ServiceContextThreadLocal.popServiceContext();
		PermissionThreadLocal.setPermissionChecker(permissionOriginalChecker);
		PrincipalThreadLocal.setName(principalOriginalName);
		
	}

	@Reference private LayoutPageTemplateCollectionLocalService collectionService;
	@Reference private LayoutPageTemplateEntryLocalService entryService;
	@Reference private UserLocalService userService;
	@Reference private LayoutPageTemplatesImporter importer;
	//@Reference private GroupLocalService groupService;
	@Reference(target = "(permission.checker.type=liberal)")
	private PermissionCheckerFactory _liberalPermissionCheckerFactory;





	/*
	com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalServiceUtil util;
	util.getLayoutPageTemplateCollections(
			long groupId, int start, int end,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
			*/

	//public UserLocalService getUserLocalService() { return _userLocalService; }
	//@Reference public void setUserLocalService(UserLocalService _userLocalService) { this._userLocalService = _userLocalService; }
	//public void usercount() { System.out.println( "# of users: " + getUserLocalService().getUsersCount()); }
	//private UserLocalService _userLocalService;
	
	
    private static String MAIN_ARRAY_ATTRIBUTE = "page-templates";

    private static String PAGE_DEFINITION_FILE = "page-definition.json";

    private static String PAGE_TEMPLATE_COLLECTION_FILE =
            "page-template-collection.json";

    private static String PAGE_TEMPLATE_COLLECTION_FOLDER_ATTRIBUTE =
            "page-template-collection-folder";

    private static String PAGE_TEMPLATE_FILE = "page-template.json";

    private static String PAGE_TEMPLATE_FOLDER = "page-templates";

    private static String SETUP_FILE = "page-templates-import.json";

    private static final Log _log = LogFactoryUtil.getLog(PageTemplateLister.class);

    private static final ObjectMapper _objectMapper = new ObjectMapper();

    //private final Bundle _bundle;
    //private final GroupLocalService _groupLocalService;
    private LayoutPageTemplateCollectionLocalService _layoutPageTemplateCollectionLocalService;

}
