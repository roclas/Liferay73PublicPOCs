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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		try { 
			_log.info("AssetListAssetentryProviderWrapper customization (1)");
			return getAssetEntries( assetListEntry, segmentsEntryId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}catch(Throwable e){ 
			_log.error("customization's side effect, getting back to default (1)");
			return _wrapped.getAssetEntries( assetListEntry, segmentsEntryId);
		}
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long segmentsEntryId, int start, int end) {
		try{
			_log.info("AssetListAssetentryProviderWrapper customization (2)");
			return getAssetEntries( assetListEntry, new long[] {segmentsEntryId}, start, end);
		}catch(Throwable e) {
			_log.error("customization's side effect, getting back to default (2)");
			return _wrapped.getAssetEntries( assetListEntry, new long[] {segmentsEntryId}, start, end);
		}
		
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds) {
		try{
			_log.info("AssetListAssetentryProviderWrapper customization (3)");
			return getAssetEntries( assetListEntry, segmentsEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}catch(Throwable e){
			_log.error("customization's side effect, getting back to default (3)");
			return _wrapped.getAssetEntries( assetListEntry, segmentsEntryIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, int start, int end) {
		try{
			_log.info("AssetListAssetentryProviderWrapper customization (4)");
			return getAssetEntries( assetListEntry, segmentsEntryIds, StringPool.BLANK, start, end);
		}catch(Throwable e){
			_log.error("customization's side effect, getting back to default (4)");
			return _wrapped.getAssetEntries( assetListEntry, segmentsEntryIds, StringPool.BLANK, start, end);
		}
	}

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId) {
		try{
			_log.info("AssetListAssetentryProviderWrapper customization (5)");
			return getAssetEntries( assetListEntry, segmentsEntryIds, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}catch(Throwable e){
			_log.error("customization's side effect, getting back to default (5)");
			return _wrapped.getAssetEntries( assetListEntry, segmentsEntryIds, userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}
	}
	
	@Override
	public int getAssetEntriesCount( AssetListEntry assetListEntry, long segmentsEntryId) {
		try{
			_log.info("AssetListAssetentryProviderWrapper customization (6)");
			return getAssetEntriesCount( assetListEntry, new long[] {segmentsEntryId});
		}catch(Throwable e){
			_log.error("customization's side effect, getting back to default (6)");
			return _wrapped.getAssetEntriesCount( assetListEntry, new long[] {segmentsEntryId});}
	}

	@Override
	public int getAssetEntriesCount( AssetListEntry assetListEntry, long[] segmentsEntryIds) {
			return _wrapped.getAssetEntriesCount( assetListEntry, segmentsEntryIds);
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

	private int maxNumberOfElements=10;

	@Override
	public List<AssetEntry> getAssetEntries( AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId, int start, int end) {
		
		List<AssetEntry> result=new ArrayList();
		start=0;end=maxNumberOfElements;//TODO: hardcoded, setting the limits, put the real limit here

		/*
		System.out.println(String.format("\n\n\n\ngetting Asset Entries (start=%s,end=%s)!!!!\n\n\n\n\n",start,end));//TODO:remove
		for(long s:segmentsEntryIds) { 
			System.out.println(String.format("segmentEntryId=%s",s)); 
			long[] entries= {s};
			List<AssetEntry> r= _wrapped.getAssetEntries(assetListEntry,entries,userId,start,end);
			r.sort((a,b)->b.getModifiedDate().compareTo(a.getModifiedDate()));//TODO:hardcoded 
			result.addAll(r);
		}//TODO:remove
		*/
		
		System.out.println(String.format("\n\n\n\ngetting indexed Asset Entries (start=%s,end=%s)!!!!\n\n\n\n\n",start,end));//TODO:remove
		for(long s:segmentsEntryIds) { 
			System.out.println(String.format("segmentEntryId=%s",s)); 
			long[] entries= {s};
			AssetEntryQuery q = _wrapped.getAssetEntryQuery(assetListEntry, entries,userId);
			q.setOrderByCol1("modifiedDate");
			q.setOrderByCol2("title");
			q.setStart(start);
			q.setEnd(end);
			List<AssetEntry> r = _search(assetListEntry.getCompanyId(),q);
			result.addAll(r);
		}//TODO:remove
	
		/*
		//PERFORMANCE-OPTIMIZED
		AssetEntryQuery q = _wrapped.getAssetEntryQuery(assetListEntry, segmentsEntryIds,userId);
		q.setOrderByCol1("modifiedDate");
		q.setOrderByCol2("title");
		q.setStart(start);
		q.setEnd(end);
		List<AssetEntry> r = _search(assetListEntry.getCompanyId(),q);
		*/

		//NOT OPTIMIZED FOR PERFORMANCE
		//List<AssetEntry> result = _wrapped.getAssetEntries(assetListEntry,segmentsEntryIds,userId,start,end);
		result.sort((a,b)->b.getModifiedDate().compareTo(a.getModifiedDate()));//TODO:hardcoded 
		//return result;


		System.out.println(String.format("\n\n\n\nwe have %s elements !!!!\n\n\n\n\n",result.size()));//TODO:remove

		return result.stream().limit(maxNumberOfElements).collect(Collectors.toList());
	
	}

	/*
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
	
	*/

	
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
			searchContext.setKeywords(assetEntryQuery.getKeywords());
			searchContext.setStart(assetEntryQuery.getStart());
			searchContext.setEnd(assetEntryQuery.getEnd());

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
	
    //@Inject private AssetHelper _assetHelper;
	@Reference private AssetHelper _assetHelper;
	@Reference(target = "(component.name=com.liferay.asset.list.internal.asset.entry.provider.AssetListAssetEntryProviderImpl)")
	private AssetListAssetEntryProvider _wrapped;
	
	private static final Log _log = LogFactoryUtil.getLog( AssetListAssetEntryProviderWrapper.class);
	


}

