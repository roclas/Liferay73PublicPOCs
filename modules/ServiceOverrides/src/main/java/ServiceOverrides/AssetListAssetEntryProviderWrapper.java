package ServiceOverrides;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.util.AssetHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author carlos
 */
@Component(
		//configurationPid = "com.liferay.asset.list.internal.configuration.AssetListConfiguration",
		immediate = true
		,property = { "service.ranking:Integer=100" }
		,service = AssetListAssetEntryProvider.class
	)
public class AssetListAssetEntryProviderWrapper implements AssetListAssetEntryProvider{

	@Activate
	@Modified
	public void activate(BundleContext bundleContext) {
		String[] names={
				"com.liferay.asset.publisher.web",
				"com.liferay.asset.list.item.selector.web",
				"com.liferay.layout.type.controller.collection",
				"com.liferay.asset.list.web",
				"com.liferay.headless.delivery.impl"
		};
    
		List<Bundle> refresh=Stream.of(bundleContext.getBundles()).filter(bundle->
			Stream.of(names).anyMatch(b->b.equals(bundle.getSymbolicName()))
		).collect(Collectors.toList());

		bundleContext.getBundles()[0].adapt(FrameworkWiring.class).refreshBundles(refresh);
	}
	
	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long segmentsEntryId) {
		return getAssetEntries( assetListEntry, segmentsEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long segmentsEntryId, int start, int end) {
		return getAssetEntries( assetListEntry, new long[] {segmentsEntryId}, start, end);
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds) {
		return getAssetEntries( assetListEntry, segmentsEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, int start, int end) {
		return getAssetEntries( assetListEntry, segmentsEntryIds, StringPool.BLANK, start, end);
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId) {
		return getAssetEntries( assetListEntry, segmentsEntryIds, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}
	
	@Override
	public int getAssetEntriesCount( AssetListEntry assetListEntry, long segmentsEntryId) {
		return getAssetEntriesCount( assetListEntry, new long[] {segmentsEntryId});
	}

	@Override
	public int getAssetEntriesCount( AssetListEntry assetListEntry, long[] segmentsEntryIds) {
		return getAssetEntriesCount( assetListEntry, segmentsEntryIds, StringPool.BLANK);
	}
	@Override
	public AssetEntryQuery getAssetEntryQuery(AssetListEntry assetListEntry, long segmentsEntryId) {
		return _wrapped.getAssetEntryQuery(assetListEntry, segmentsEntryId) ;
	}
	@Override
	public AssetEntryQuery getAssetEntryQuery(AssetListEntry assetListEntry, long[] segmentsEntryIds) {
		return _wrapped.getAssetEntryQuery(assetListEntry, segmentsEntryIds) ;
	}
	

	
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////


	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId, int start, int end) {
		String newUserId = String.valueOf(PrincipalThreadLocal.getUserId());
		userId=newUserId;
		System.out.println("Overriding getAssetEntries( assetListEntry,segmentsEntryIds,userId,start,end) => Breakpoint in AssetListAssetEntryProviderImpl.java)");
		System.out.println(String.format("userId=%s start=%s end=%s",userId,start,end));
		
		Set entriesSet=new HashSet<AssetEntry>();
		for(long seid:segmentsEntryIds) { 
			System.out.println("segmentEntryId=>"+seid);
			//if(seid==0)continue;//ignore default variation
			long[] seids= {seid};
			System.out.println("segmentEntryId===>"+seids[0]);
			AssetEntryQuery q = _wrapped.getAssetEntryQuery(assetListEntry, seids,userId);
			_search(assetListEntry.getCompanyId(),q).forEach(a->{
				if(entriesSet.contains(a))entriesSet.add(a);
				else entriesSet.add(a);
			});
			System.out.println("----------------------\n"+q+"----------------------");
		}

		List<AssetEntry>result = (List<AssetEntry>)entriesSet.stream().collect(Collectors.toList());
		result.sort((a,b)->b.getModifiedDate().compareTo(a.getModifiedDate()));//TODO:hardcoded 
		return result;
	
	}
	

	
	private List<AssetEntry> _search( long companyId, AssetEntryQuery assetEntryQuery) {

			SearchContext searchContext = new SearchContext();

			String ddmStructureFieldName = GetterUtil.getString(
				assetEntryQuery.getAttribute("ddmStructureFieldName"));
			Serializable ddmStructureFieldValue = assetEntryQuery.getAttribute(
				"ddmStructureFieldValue");

			if (Validator.isNotNull(ddmStructureFieldName) &&
				Validator.isNotNull(ddmStructureFieldValue)) {

				searchContext.setAttribute( "ddmStructureFieldName", ddmStructureFieldName);
				searchContext.setAttribute( "ddmStructureFieldValue", ddmStructureFieldValue);
			}

			searchContext.setClassTypeIds(assetEntryQuery.getClassTypeIds());
			searchContext.setCompanyId(companyId);
			searchContext.setEnd(assetEntryQuery.getEnd());
			searchContext.setKeywords(assetEntryQuery.getKeywords());
			searchContext.setStart(assetEntryQuery.getStart());

			try {
				Hits hits = _assetHelper.search(
					searchContext, assetEntryQuery, assetEntryQuery.getStart(),
					assetEntryQuery.getEnd());

				return _assetHelper.getAssetEntries(hits);
			}
			catch (Exception exception) {
				_log.error("Unable to get asset entries", exception);
			}

			return Collections.emptyList();
	}
	
	@Reference private AssetHelper _assetHelper;
	@Reference(target = "(component.name=com.liferay.asset.list.internal.asset.entry.provider.AssetListAssetEntryProviderImpl)")
	private AssetListAssetEntryProvider _wrapped;
	
	private static final Log _log = LogFactoryUtil.getLog( AssetListAssetEntryProviderWrapper.class);
	

}

