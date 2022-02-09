package ServiceOverrides;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.asset.list.asset.entry.provider.AssetListAssetEntryProvider;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.List;
import java.util.Objects;
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

	
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////

	@Override
	public AssetEntryQuery getAssetEntryQuery(AssetListEntry assetListEntry, long segmentsEntryId) {
		System.out.println("overridding service");
		_log.error("Overriding service!!!");
		return _wrapped.getAssetEntryQuery(assetListEntry, segmentsEntryId) ;
	}

	@Override
	public AssetEntryQuery getAssetEntryQuery(AssetListEntry assetListEntry, long[] segmentsEntryIds) {
		AssetEntryQuery query = _wrapped.getAssetEntryQuery(assetListEntry, segmentsEntryIds) ;
		System.out.println("overridding service getAssetEntryQuery(AssetListEntry assetListEntry, long[] segmentsEntryIds)"); 
		_log.error("Overriding service!!!");
		System.out.println(query);
		return query;
	}

	@Reference(target = "(component.name=com.liferay.asset.list.internal.asset.entry.provider.AssetListAssetEntryProviderImpl)")
	private AssetListAssetEntryProvider _wrapped;
	
	private static final Log _log = LogFactoryUtil.getLog( AssetListAssetEntryProviderWrapper.class);
	
	
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	
	
	@Override
	public List<AssetEntry> getAssetEntries(
		AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId,
		int start, int end) {
		System.out.println("Overriding getAssetEntries( assetListEntry,segmentsEntryIds,userId,start,end)");
		return _wrapped.getAssetEntries( assetListEntry,segmentsEntryIds,userId,start,end);

	}

	@Override
	public int getAssetEntriesCount( AssetListEntry assetListEntry, long[] segmentsEntryIds, String userId) {
		System.out.println("overridding COUNT service");
		return _wrapped.getAssetEntriesCount(assetListEntry, segmentsEntryIds, userId) ;
	}



}

